package cs355.model.drawing;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Add your square code here. You can add fields, but you cannot
 * change the ones that already exist. This includes the names!
 */
public class Square extends Shape {

	// The size of this Square.
	private double size;

	/**
	 * Basic constructor that sets all fields.
	 * @param color the color for the new shape.
	 * @param center the center of the new shape.
	 * @param size the size of the new shape.
	 */
	public Square(Color color, Point2D.Double center, double size) {

		// Initialize the superclass.
		super(color, center);

		// Set the field.
		this.size = size;
	}
	
	public Square(double size) {
		this(Color.white, new Point2D.Double(),size);
	}

	/**
	 * Getter for this Square's size.
	 * @return the size as a double.
	 */
	public double getSize() {
		return size;
	}
	/**
	 * Setter for this Square's size.
	 * @param size the new size.
	 */
	public void setSize(double size) {
		this.size = size;
	}

	/**
	 * Add your code to do an intersection test
	 * here. You shouldn't need the tolerance.
	 * @param pt = the point to test against.
	 * @param tolerance = the allowable tolerance.
	 * @return true if pt is in the shape,
	 *		   false otherwise.
	 */
	@Override
	public boolean pointInShape(Point2D.Double pt, double tolerance) {
		if((Math.abs(pt.x)>(size*0.5))|(Math.abs(pt.y)>(size*0.5)))
				return false;
			return true;
	}

	@Override
	public List<Circle> getHandles(double circleRadius,double handleDistance) {
		double halfSize=size/2.0;
		List<Circle> newElements=new ArrayList<Circle>();
		
		Point2D.Double rotateControl=new Point2D.Double(0, (halfSize)+handleDistance);
		newElements.add(new Circle(rotateControl, circleRadius));
		
		return newElements;
	}

}
