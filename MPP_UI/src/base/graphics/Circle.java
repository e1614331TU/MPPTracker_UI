package base.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
/**
* Circle
* <p>
* this class is used to draw a circle and set the color of it and is needed by the config-panel
*/
public class Circle implements I_Drawable {

    private int x;
    private int y;
    private int radius;
    private Color color;

    public Circle(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = Color.GRAY;
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Ellipse2D.Double circle = new Ellipse2D.Double(x, y, radius, radius);

        g2d.setColor(this.color);
        g2d.fill(circle);
    }
    
    @Override
    public void setColor(Color color) {
    	this.color = color;
    }

	@Override
	public int getSizeX() {
		return this.x+this.radius;
	}

	@Override
	public int getSizeY() {
		return this.y+this.radius;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

}