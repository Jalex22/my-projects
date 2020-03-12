public abstract class GUIButton implements GameObject
{
	protected Sprite sprite;
	protected Rectangle rect;
	protected boolean fixed;

	public GUIButton(Sprite sprite, Rectangle rect, Boolean fixed)
	{
		this.sprite = sprite;
		this.rect = rect;
		this.fixed = fixed;
	}

	
	public void render(Render renderer, int xZoom, int yZoom) {}

	//render the button
	public void render(Render renderer, int xZoom, int yZoom, Rectangle interfaceRect)
	{
		renderer.renderSprite(sprite, rect.x + interfaceRect.x, rect.y + interfaceRect.y, xZoom, yZoom, fixed);
	}

	
	public void update(Game game) {}

	//for mouse click on canvas
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom)
	{
		if(mouseRectangle.intersects(rect)) {
			activate();
			return true;
		}

		return false;
	}

	public abstract void activate();

	public int getLayer() {
		return Integer.MAX_VALUE;
	}

	public Rectangle getRectangle() {
		return rect;
	}

}