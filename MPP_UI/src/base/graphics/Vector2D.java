package base.graphics;

import java.awt.Color;
/**
* Vector2D
* <p>
* this class is used to draw a a line with a color
*/
public class Vector2D {
	
	private double x;
	private double y;
    private Color color;
	
	public Vector2D(double x, double y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}
	
	public Vector2D(Color color) {
		this.x = 0;
		this.y = 0;
		this.color = color;
	}
	
	public void setXYbyPolar(double abs, double arg) {
		this.x = abs*Math.cos(arg);
		this.y = abs*Math.sin(arg);
	}
	
	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public double getAbs() {
		return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2));
	}

}
