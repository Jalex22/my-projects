public interface GameObject 
{

	public void render(Render renderer, int xZoom, int yZoom);

	public void update(Game game);

	//for mouse click on canvas
	//return true to stop checking clicks.
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom);

	public int getLayer();

	public Rectangle getRectangle();
}