package base.graphics;

import java.awt.Color;
import java.awt.Graphics;

public interface I_Drawable {
	public void draw(Graphics g);
	public void setColor(Color color);
	public int getSizeX();
	public int getSizeY();
	public void clear();
}
