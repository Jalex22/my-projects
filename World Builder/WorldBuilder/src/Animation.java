import java.awt.image.BufferedImage;

public class Animation extends Sprite implements GameObject
{
	private Sprite[] sprites;
	private int currentSprite = 0;
	private int speed;
	private int counter = 0;

	private int startSprite = 0;
	private int endSprite;

	public Animation(SpriteSheet sheet, Rectangle[] positions, int speed) 
	{
		sprites = new Sprite[positions.length];
		this.speed = speed;
		this.endSprite = positions.length - 1;

		//add sprites to array
		for(int i = 0; i < positions.length; i++)
			sprites[i] = new Sprite(sheet, positions[i].getX(), positions[i].getY(), positions[i].getW(), positions[i].getH());
	}

	public Animation(SpriteSheet sheet, int speed) 
	{
		sprites = sheet.getLoadedSprites();
		this.speed = speed;
		this.endSprite = sprites.length - 1;
	}

	//speed >>> how many frames pass until the sprite changes
	public Animation(BufferedImage[] images, int speed)
	{
		sprites = new Sprite[images.length];
		this.speed = speed;
		this.startSprite = images.length - 1;

		for(int i = 0; i < images.length; i++)
			sprites[i] = new Sprite(images[i]);

	}

	public void render(Render renderer, int xZoom, int yZoom) {}

	//for mouse click on canvas
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) 
	{ 
		return false; 
	}

	public void update(Game game)
	{
		counter++;
		if(counter >= speed) 
		{
			counter = 0;
			incrementSprite();
		}
	}

	public void reset()
	{
		counter = 0;
		currentSprite = startSprite;
	}

	public void setAnimationRange(int startSprite, int endSprite)
	{
		this.startSprite = startSprite;
		this.endSprite = endSprite;
		reset();
	}

	public int getWidth()
	{
		return sprites[currentSprite].getWidth();
	}

	public int getHeight()
	{
		return sprites[currentSprite].getHeight();
	}

	public int[] getPixels()
	{
		return sprites[currentSprite].getPixels();
	}

	public void incrementSprite() 
	{
		currentSprite++;
		if(currentSprite >= endSprite)
			currentSprite = startSprite;
	}

	public int getLayer() 
	{
		return -1;
	}

	public Rectangle getRectangle() 
	{
		return null;
	}

}