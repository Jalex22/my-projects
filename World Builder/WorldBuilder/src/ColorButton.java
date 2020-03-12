public class ColorButton extends GUIButton
{
	private Game game;
	private int tileID;
	private boolean isSelected = false;

	public ColorButton(Game game, int tileID, Sprite tileSprite, Rectangle rect) 
	{
		super(tileSprite, rect, true);
		this.game = game;
		this.tileID = tileID;
		rect.generateGraphics(0xD3D3D3); //grey
	}

	@Override
	public void update(Game game)  //color buttons grey and selected button yellow
	{
		if(tileID == game.getTile())
		{
			if(!isSelected) 
			{
				rect.generateGraphics(0xFCE205); //yellow
				isSelected = true;
			}
		}
		else
		{
			if(isSelected)
			{
				rect.generateGraphics(0xD3D3D3); //grey
				isSelected = false;
			}
		}
	}

	@Override
	public void render(Render renderer, int xZoom, int yZoom, Rectangle interfaceRect)
	{
		renderer.renderRectangle(rect, interfaceRect, 1, 1, fixed);
		renderer.renderSprite(sprite, rect.x + interfaceRect.x + (xZoom - (xZoom - 1))*rect.w/2/xZoom, rect.y + interfaceRect.y + (yZoom - (yZoom - 1))*rect.h/2/yZoom, 
							  xZoom - 1, yZoom - 1, fixed);
	}

	public void activate()
	{
		game.changeTile(tileID);
	}

}