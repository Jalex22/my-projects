public class Player implements GameObject
{
	private Rectangle playerRect;
	private Rectangle collisionRect;
	private int speed = 4;
	private int direction = 0;  //0 = Right, 1 = Left, 2 = Up, 3 = Down
	private int layer = 0;
	private Sprite sprite;
	private Animation spriteAnimate = null;
	private final int xOffset = 14;
	private final int yOffset = 20;
	
	public Player(Sprite sprite, int xZoom, int yZoom)
	{
		this.sprite = sprite;

		if(sprite != null && sprite instanceof Animation)
			spriteAnimate = (Animation) sprite;

		updateDirection();
		playerRect = new Rectangle(-90, 0, 20, 26);
		playerRect.generateGraphics(3, 0xFF00FF90);
		collisionRect = new Rectangle(0, 0, 10*xZoom, 15*yZoom);
	}
	//updates direction
	private void updateDirection()
	{
		if(spriteAnimate != null)
		{
			spriteAnimate.setAnimationRange(direction * 8, (direction * 8) + 7);
		}
	}

	//render the sprite
	public void render(Render renderer, int xZoom, int yZoom)
	{
		if(spriteAnimate != null)
			renderer.renderSprite(spriteAnimate, playerRect.x, playerRect.y, xZoom, yZoom, false);
		else if(sprite != null)
			renderer.renderSprite(sprite, playerRect.x, playerRect.y, xZoom, yZoom, false);
		else
			renderer.renderRectangle(playerRect, xZoom, yZoom, false);

	}

	public void update(Game game)
	{
		KeyBoardListener keyListener = game.getKeyListener();

		boolean didMove = false;
		int newDirection = direction;

		collisionRect.x = playerRect.x;
		collisionRect.y = playerRect.y;

		if(keyListener.left())
		{
			newDirection = 1; //left
			didMove = true;
			collisionRect.x -= speed;
		}
		if(keyListener.right())
		{
			newDirection = 0; //right
			didMove = true;
			collisionRect.x += speed;
		}
		if(keyListener.up()) 
		{
			newDirection = 2; //up
			collisionRect.y -= speed;
			didMove = true;	
		}
		if(keyListener.down()) 
		{
			newDirection = 3; //down
			didMove = true;
			collisionRect.y += speed;
		}

		if(newDirection != direction) 
		{
			direction = newDirection;
			updateDirection();
		}


		if(!didMove) 
		{
			spriteAnimate.reset();
		}

		if(didMove) 
		{
			collisionRect.x += xOffset;
			collisionRect.y += yOffset;

			Rectangle axisCheck = new Rectangle(collisionRect.x, playerRect.y + yOffset, collisionRect.w, collisionRect.h);

			//check x axis
			if(!game.getMap().checkCollision(axisCheck, layer, game.getXZoom(), game.getYZoom()) && 
				!game.getMap().checkCollision(axisCheck, layer + 1, game.getXZoom(), game.getYZoom())) 
			{
				playerRect.x = collisionRect.x - xOffset;
			}

			axisCheck.x = playerRect.x + xOffset;
			axisCheck.y = collisionRect.y;
			axisCheck.w = collisionRect.w;
			axisCheck.h = collisionRect.h;

			//check y axis
			if(!game.getMap().checkCollision(axisCheck, layer, game.getXZoom(), game.getYZoom()) && 
				!game.getMap().checkCollision(axisCheck, layer + 1, game.getXZoom(), game.getYZoom())) 
			{
				playerRect.y = collisionRect.y - yOffset;
			}


			spriteAnimate.update(game);
		}

		updateCamera(game.getRenderer().getCamera());
	}

	public void updateCamera(Rectangle camera) 
	{
		camera.x = playerRect.x - (camera.w / 2);
		camera.y = playerRect.y - (camera.h / 2);
	}

	public int getLayer() {
		return layer;
	}

	public Rectangle getRectangle() {
		return playerRect;
	}

	//for mouse click on canvas
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) 
	{ 
		return false; 
	}
}