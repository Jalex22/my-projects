public class Rectangle 
{
	public int x,y,w,h;
	private int[] pixels;

	Rectangle(int x, int y, int w, int h) 
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}

	Rectangle() 
	{
		this(0,0,0,0);
	}

	public void generateGraphics(int color) 
	{
		pixels = new int[w*h];
		for(int y = 0; y < h; y++)
			for(int x = 0; x < w; x++)
				pixels[x + y * w] = color;
	}

	public boolean intersects(Rectangle otherRectangle)
	{
		if(x > otherRectangle.x + otherRectangle.w || otherRectangle.x > x + w)
			return false;

		if(y > otherRectangle.y + otherRectangle.h || otherRectangle.y > y + h)
			return false;

		return true;
	}

	public void generateGraphics(int borderWidth, int color) {
		pixels = new int[w*h];
		
		//fill rect
		for(int i = 0; i < pixels.length; i++)
			pixels[i] = Game.alpha;
		
		//top of rect
		for(int y = 0; y < borderWidth; y++)
			for(int x = 0; x < w; x++)
				pixels[x + y * w] = color;

		//left side
		for(int y = 0; y < h; y++)
			for(int x = 0; x < borderWidth; x++)
				pixels[x + y * w] = color;

		//right side
		for(int y = 0; y < h; y++)
			for(int x = w - borderWidth; x < w; x++)
				pixels[x + y * w] = color;
		//bottom
		for(int y = h - borderWidth; y < h; y++)
			for(int x = 0; x < w; x++)
				pixels[x + y * w] = color;
		
	}

	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getW()
	{
		return w;
	}
	
	public int getH()
	{
		return h;
	}
	
	public int[] getPixels() 
	{
		if(pixels != null)
			return pixels;
		return null;
	}

}