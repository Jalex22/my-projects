import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;


public class Render 
{
	private BufferedImage view;
	private Rectangle camera;
	private int[] pixels;
	private int maxWidth, maxHeight;

	public Render(int width, int height) 
	{
		GraphicsDevice[] graphicsDevices = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

		for(int i = 0; i < graphicsDevices.length; i++)
		{
			if(maxWidth < graphicsDevices[i].getDisplayMode().getWidth())
				maxWidth = graphicsDevices[i].getDisplayMode().getWidth();

			if(maxHeight < graphicsDevices[i].getDisplayMode().getHeight())
				maxHeight = graphicsDevices[i].getDisplayMode().getHeight();
		}

		//buffImage for the view
		view = new BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_RGB);

		camera = new Rectangle(0, 0, width, height);

		//pixels array
		pixels = ((DataBufferInt) view.getRaster().getDataBuffer()).getData();

	}

	//render pixels to screen
	public void render(Graphics graphics)
	{
		graphics.drawImage(view.getSubimage(0, 0, camera.w, camera.h), 0, 0, camera.w, camera.h, null);
	}

	//render image to pixels
	public void renderImage(BufferedImage image, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed)
	{
		int[] imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		renderArray(imagePixels, image.getWidth(), image.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);
	}

	//render sprite
	public void renderSprite(Sprite sprite, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) 
	{
		renderArray(sprite.getPixels(), sprite.getWidth(), sprite.getHeight(), xPosition, yPosition, xZoom, yZoom, fixed);
	}

	//render rect
	public void renderRectangle(Rectangle rectangle, int xZoom, int yZoom, boolean fixed)
	{
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null)
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x, rectangle.y, xZoom, yZoom, fixed);	
	}

	//render offset rect
	public void renderRectangle(Rectangle rectangle, Rectangle offset, int xZoom, int yZoom, boolean fixed)
	{
		int[] rectanglePixels = rectangle.getPixels();
		if(rectanglePixels != null)
			renderArray(rectanglePixels, rectangle.w, rectangle.h, rectangle.x + offset.x, rectangle.y + offset.y, xZoom, yZoom, fixed);	
	}

	public void renderArray(int[] renderPixels, int renderWidth, int renderHeight, int xPosition, int yPosition, int xZoom, int yZoom, boolean fixed) 
	{
		for(int y = 0; y < renderHeight; y++)
			for(int x = 0; x < renderWidth; x++)
				for(int yZoomPosition = 0; yZoomPosition < yZoom; yZoomPosition++)
					for(int xZoomPosition = 0; xZoomPosition < xZoom; xZoomPosition++)
						setPixel(renderPixels[x + y * renderWidth], (x * xZoom) + xPosition + xZoomPosition, ((y * yZoom) + yPosition + yZoomPosition), fixed);
	}

	private void setPixel(int pixel, int x, int y, boolean fixed) 
	{
		int pixelIndex = 0;
		if(!fixed) 
		{
			if(x >= camera.x && y >= camera.y && x <= camera.x + camera.w && y <= camera.y + camera.h)
				pixelIndex = (x - camera.x) + (y - camera.y) * view.getWidth();
		}
		else
		{
			if(x >= 0 && y >= 0 && x <= camera.w && y <= camera.h)
				pixelIndex = x + y * view.getWidth();
		}

		if(pixels.length > pixelIndex && pixel != Game.alpha) //transparency 
			pixels[pixelIndex] = pixel;
	}

	public Rectangle getCamera() 
	{
		return camera;
	}

	public int getMaxWidth() 
	{
		return maxWidth;
	}

	public int getMaxHeight() 
	{
		return maxHeight;
	}

	//clear pixels
	public void clear()
	{
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = 0;
	}

}