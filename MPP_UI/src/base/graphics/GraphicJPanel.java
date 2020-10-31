package base.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
/**
* GraphicJPanel
* <p>
* this class draws the given shape and makes it to a JPanel
* */
public class GraphicJPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	I_Drawable shape;

	/**
	* this method is the constructor
	* @param shape is the shape to put on the JPanel
	*/
	public GraphicJPanel(I_Drawable shape) {
		super();
		this.shape = shape;
		this.setPreferredSize(new Dimension(shape.getSizeX(), shape.getSizeY()));
		this.setMinimumSize(new Dimension(shape.getSizeX(), shape.getSizeY()));
		this.repaint();
	}
	
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.shape.draw(g);
    }
    
    public void setShapeColorActive() {
    	this.shape.setColor(Color.GREEN);
    	this.repaint();
    }
    
    public void setShapeColorInactive() {
    	this.shape.setColor(Color.GRAY);
    	this.repaint();
    }
    
    public void clear() {
    	this.shape.clear();
    	this.repaint();
    }
    
    public I_Drawable getShape() {
    	return this.shape;
    }
}
