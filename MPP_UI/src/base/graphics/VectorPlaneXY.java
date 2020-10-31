package base.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
/**
* VectorPlaneXY
* <p>
* this class is used to draw vectors into a plane
*/
public class VectorPlaneXY implements I_Drawable {
	
	private List<Vector2D> vectors = new ArrayList<Vector2D>();
	private int height = 300;
	private int width = 300;
	
	public void clear() {
		this.vectors.clear();
	}
	
	public void addVector(Vector2D vec) {
		this.vectors.add(vec);
	}

	@Override
	public void draw(Graphics g) {
		
		// draw border and coordinate system
		Graphics2D g2d = (Graphics2D) g.create();
		
		
		g2d.drawString("beta",this.width/2+10,10); 
		g2d.drawString("alpha",this.width-10,this.height/2+10); 
		
		float[] dash1 = { 2f, 0f, 2f };
		
		BasicStroke bs1 = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1.0f, dash1, 2f);
		g2d.setStroke(bs1);
		g2d.setColor(Color.black);
		g2d.drawLine(0, this.height/2, this.width, this.height/2);
		g2d.drawLine(this.width/2, 0, this.width/2, this.height);
        
		
		
		double max = 1;
		for(Vector2D vec : this.vectors) {
			double abs = vec.getAbs();
			if(abs > max) max = abs;
		}
		
		
	    for (Vector2D vec : this.vectors) {
	    	
	    	double x = vec.getX()/max*this.width/2;
	    	double y = vec.getY()/max*this.height/2;
	    	
	    	g2d.setStroke(new BasicStroke(2));
	    	
	    	g2d.setColor(vec.getColor());
	    	g2d.drawLine(this.width/2, this.height/2, this.width/2+(int)x, this.height/2-(int)y);
	    }
	    g2d.dispose();
	}

	@Override
	public void setColor(Color color) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getSizeX() {
		return this.width;
	}

	@Override
	public int getSizeY() {
		return this.height;
	}

}
