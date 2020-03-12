
import java.awt.Canvas;
//import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics;

import java.lang.Runnable;
import java.lang.Thread;

import javax.swing.JFrame;

import javax.imageio.ImageIO;

import java.io.IOException;
import java.io.File;

public class Game extends JFrame implements Runnable
{

	public static int alpha = 0xFFFF00DC;

	private Canvas canvas = new Canvas();
	private Render renderer;

	private SpriteSheet sheet;
	private SpriteSheet playerSheet;

	private int TileID = 2;
	private int Layer = 0;

	private Tiles tiles;
	private Map map;

	private GameObject[] objects;
	private KeyBoardListener keyListener = new KeyBoardListener(this);
	private MouseEventListener mouseListener = new MouseEventListener(this);

	private Player player;

	private int xZoom = 3;
	private int yZoom = 3;

	AudioClip clip;
	
	public Game() 
	{
		//close/end game
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//make full screen
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		add(canvas);
		setVisible(true);
		
		//make obj for buffer strategy.
		canvas.createBufferStrategy(3);

		renderer = new Render(getWidth(), getHeight());

		//load tileset and character assets
		BufferedImage sheetImage = loadImage("/Tiles1.png");
		sheet = new SpriteSheet(sheetImage);
		sheet.loadSprites(16, 16);

		BufferedImage playerSheetImage = loadImage("/Sprite.png");
		playerSheet = new SpriteSheet(playerSheetImage);
		playerSheet.loadSprites(20, 26);

		//animations
		Animation playerAnimations = new Animation(playerSheet, 5);

		//load in tiles
		tiles = new Tiles(new File("Resources/Tiles.txt"),sheet);

		//load the map
		map = new Map(new File("Resources/Map.txt"), tiles);

		//load/play audio
		clip = new AudioClip("Resources/bkgMusic.wav");
		AudioPlayer.playLoopSound(clip);
		
		clip = new AudioClip("Resources/build.wav");
		
		
		//load the GUI
		GUIButton[] buttons = new GUIButton[tiles.size()];
		Sprite[] tileSprites = tiles.getSprites();
		
		//first column of GUI
		for(int i = 0; i < 16; i++)
		{
			Rectangle tileRectangle = new Rectangle(0, i*(16*xZoom + 2), 16*xZoom, 16*yZoom);

			buttons[i] = new ColorButton(this, i, tileSprites[i], tileRectangle);
		}
		
		//second column of GUI
		int column2 = 0;
		for(int j = 16; j < buttons.length; j++)
		{
			Rectangle tileRectangle = new Rectangle(64, column2*(16*xZoom + 2), 16*xZoom, 16*yZoom);
			column2++;
			buttons[j] = new ColorButton(this, j, tileSprites[j ], tileRectangle);
		}

		GUI gui = new GUI(buttons, 5, 5, true);

		//load game objects
		objects = new GameObject[2];
		player = new Player(playerAnimations, xZoom, yZoom);
		objects[0] = player;
		objects[1] = gui;

		//add listeners
		canvas.addKeyListener(keyListener);
		canvas.addFocusListener(keyListener);
		canvas.addMouseListener(mouseListener);
		canvas.addMouseMotionListener(mouseListener);
		addComponentListener(new ComponentListener() 
		{
			public void componentResized(ComponentEvent e) 
			{
				int newWidth = canvas.getWidth();
				int newHeight = canvas.getHeight();

				if(newWidth > renderer.getMaxWidth())
					newWidth = renderer.getMaxWidth();

				if(newHeight > renderer.getMaxHeight())
					newHeight = renderer.getMaxHeight();

				renderer.getCamera().w = newWidth;
				renderer.getCamera().h = newHeight;
				canvas.setSize(newWidth, newHeight);
				pack();
			}

			public void componentHidden(ComponentEvent e) 
			{	
			}
			public void componentMoved(ComponentEvent e) 
			{		
			}
			public void componentShown(ComponentEvent e) 
			{	
			}
		});  //all of this code is inside addComponentListener parameter
		canvas.requestFocus();
	}

	
	public void update() 
	{
		for(int i = 0; i < objects.length; i++) 
			objects[i].update(this);
	}


	private BufferedImage loadImage(String path)
	{
		try 
		{
			BufferedImage loadedImage = ImageIO.read(getClass().getResource(path));
			BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			formattedImage.getGraphics().drawImage(loadedImage, 0, 0, null);

			return formattedImage;
		}
		catch(IOException exception) 
		{
			exception.printStackTrace();
			return null;
		}
	}
	//CTRL+C will save progress
	public void handleCTRL(boolean[] keys) 
	{
		if(keys[KeyEvent.VK_C])
			map.saveMap();
	}
	
	//select and set tile
	public void leftClick(int x, int y)
	{
		AudioPlayer.playSound(clip);
		Rectangle mouseRect = new Rectangle(x, y, 1, 1);
		boolean stopCheck = false;

		for(int i = 0; i < objects.length; i++)
			if(!stopCheck)
				stopCheck = objects[i].handleMouseClick(mouseRect, renderer.getCamera(), xZoom, yZoom);

		if(!stopCheck) 
		{
			x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
			y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
			map.setTile(Layer, x, y, TileID);
		}
	}

	//removes tile
	public void rightClick(int x, int y)
	{
		AudioPlayer.playSound(clip);
		x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
		y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
		map.removeTile(Layer, x, y);
	}


	public void render() 
	{
			BufferStrategy bufferStrategy = canvas.getBufferStrategy();
			Graphics graphics = bufferStrategy.getDrawGraphics();
			super.paint(graphics);

			map.render(renderer, objects, xZoom, yZoom);

			renderer.render(graphics);

			graphics.dispose();
			bufferStrategy.show();
			renderer.clear();
	}

	public void changeTile(int tileID) 
	{
		TileID = tileID;
	}

	public int getTile()
	{
		return TileID;
	}

	public void run() 
	{

		long lastTime = System.nanoTime(); //long 2^63
		double nanoSecConversion = 1000000000.0 / 60; //60 frames per second
		double changeInSec = 0;

		while(true) 
		{
			long now = System.nanoTime();

			changeInSec += (now - lastTime) / nanoSecConversion;
			while(changeInSec >= 1) {
				update();
				changeInSec--;
			}

			render();
			lastTime = now;
		}

	}

	public static void main(String[] args) 
	{
		Game game = new Game();
		Thread gameThread = new Thread(game);
		gameThread.start();
	}

	public KeyBoardListener getKeyListener() 
	{
		return keyListener;
	}

	public MouseEventListener getMouseListener() 
	{
		return mouseListener;
	}

	public Render getRenderer()
	{
		return renderer;
	}

	public Map getMap() {
		return map;
	}

	public int getXZoom() {
		return xZoom;
	}

	public int getYZoom() {
		return yZoom;
	}
}