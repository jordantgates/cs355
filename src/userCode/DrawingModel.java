package userCode;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cs355.model.drawing.*;

public class DrawingModel extends CS355Drawing {
	ArrayList<Shape> shapes;
	int currentShapeIndex=-1;


	public DrawingModel() {
		shapes=new ArrayList<Shape>();
	}

	/**
	 * Get a shape at a certain index.
	 * 
	 * @param index
	 *            = the index of the desired shape.
	 * @return the shape at the provided index.
	 */
	public Shape getShape(int index){
		return shapes.get(index);
	}

	public void emptyModelUpdate(){
		this.setChanged();
		notifyObservers();
	}

	// Adding and deleting.

	/**
	 * Add a shape to the <b>FRONT</b> of the list.
	 * 
	 * @param s
	 *            = the shape to add.
	 * @return the index of the shape.
	 */
	public int addShape(Shape s){
		shapes.add(s);
		this.setChanged();
		notifyObservers();
		return shapes.indexOf(s);
	}

	/**
	 * Delete the shape at a certain index.
	 * 
	 * @param index
	 *            = the index of the shape to delete.
	 */
	public void deleteShape(int index){
		shapes.remove(index);
		this.setChanged();
		currentShapeIndex=-1;
		notifyObservers();
	}

	/**
	 * Delete the shape from the list if it exists
	 * 
	 * @param Shape
	 *            = the shape to delete.
	 */
	public void deleteShape(Shape unwantedShape) {
		if(unwantedShape!=null){
			shapes.remove(unwantedShape);
		}

	}
	// Moving commands.

	/**
	 * Move the shape at a certain index to the front of the list.
	 * 
	 * @param index
	 *            = the index of the shape to move to the front.
	 */
	public void moveToFront(int index){
		Shape movedShape= shapes.remove(index);
		shapes.add(movedShape);
		setSelectedShape(movedShape);
		this.setChanged();
		notifyObservers();
	}

	/**
	 * Move the shape at a certain index to the back of the list.
	 * 
	 * @param index
	 *            = the index of the shape to move to the back.
	 */
	public void movetoBack(int index){
		Shape movedshape= shapes.remove(index);
		shapes.add(0, movedshape);
		setSelectedShape(movedshape);
		this.setChanged();
		notifyObservers();
	}

	/**
	 * Move the shape at a certain index forward one slot.
	 * 
	 * @param index
	 *            = the index of the shape to move forward.
	 */
	public void moveForward(int index){

		if((index>-1)&(index<(shapes.size()-1))){
			Collections.swap(shapes, index, index+1);
			setSelectedShape(index+1);
			this.setChanged();
			notifyObservers();
		}
	}


	/**
	 * Move the shape at a certain index backward one slot.
	 * 
	 * @param index
	 *            = the index of the shape to move backward.
	 */
	public void moveBackward(int index){
		if((index>0)&(index<shapes.size())){
			Collections.swap(shapes, index, index-1);
		}
		setSelectedShape(index-1);
		this.setChanged();
		notifyObservers();
	}

	/**
	 * Returns the index of the first occurrence of the specified shape in this model, or -1 if this model does not contain the shape.
	 * @param shape
	 * @return the index of the shape
	 */
	public int getShapeIndex(Shape shape){
		return shapes.indexOf(shape);
	}

	// Whole list operations.

	/**
	 * Get the list of the shapes in this model.
	 * 
	 * @return the list of shapes.
	 */
	public List<Shape> getShapes(){
		return Collections.unmodifiableList(shapes);
	}

	/**
	 * Get the reversed list of the shapes in this model. This is for doing
	 * click tests (front first).
	 * 
	 * @return the reversed list of shapes.
	 */
	public List<Shape> getShapesReversed(){
		@SuppressWarnings("unchecked")
		ArrayList<Shape> inverted= (ArrayList<Shape>) shapes.clone();
		Collections.reverse(inverted);
		return Collections.unmodifiableList(inverted);
	}

	/**
	 * Sets the list of shapes in this model. This should overwrite the current
	 * list.
	 * 
	 * @param shapes
	 *            = the new list of shapes for the model.
	 */
	public void setShapes(List<Shape> shapes){
		this.shapes.clear();
		this.shapes.addAll(shapes);
		this.setChanged();
		notifyObservers();
	}
	public void setSelectedShape(int index) {

		currentShapeIndex=index;
	}
	public void setSelectedShape(Shape shape) {
		if(shape==null)
			currentShapeIndex=-1;
		else
			currentShapeIndex=shapes.indexOf(shape);
	}

	public int getSelectedIndex() {

		return currentShapeIndex;
	}
	public Shape getSelectedShape() {
		if(currentShapeIndex>-1)
			return shapes.get(currentShapeIndex);
		return null;
	}

	public boolean isSelectedShapeHandle(Point2D.Double worldPoint, double circleRadius){
		if(currentShapeIndex>-1){
			Shape targetShape=getSelectedShape();
			Point2D.Double objPoint= transformWorldtoObjectPoint(targetShape,worldPoint);
			return targetShape.pointInHandle(objPoint, circleRadius);}
		return false;
	}

	public Shape getTopShapeUnderPoint(Point2D.Double worldPoint, int tolerance){
		for(Shape shape:getShapesReversed()){
			Point2D.Double objPoint= transformWorldtoObjectPoint(shape,worldPoint);
			if(shape.pointInShape(objPoint, tolerance)){
				return shape;}
		}
		return null;

	}

	public Point2D.Double transformWorldtoObjectPoint(Shape targetShape, Point2D.Double worldPoint) {
		AffineTransform objToWorld = new AffineTransform();
		Point2D.Double objPoint=new Point2D.Double();
		Double translation = targetShape.getCenter();
		// rotate to its orientation (first transformation)
		objToWorld.rotate(-targetShape.getRotation());
		// translate to its position in the world (last transformation)
		objToWorld.translate(-translation.x, -translation.y);
		// set the point transformation
		objToWorld.transform(worldPoint, objPoint);
		return objPoint;
	}
	public Point2D.Double transformWorldtoObjectPoint(Shape targetShape, Point2D.Double worldPoint,Double centerOverload) {
		AffineTransform objToWorld = new AffineTransform();
		Double objPoint=new Double();
		Double translation = centerOverload;
		// rotate to its orientation (first transformation)
		objToWorld.rotate(-targetShape.getRotation());
		// translate to its position in the world (last transformation)
		objToWorld.translate(-translation.x, -translation.y);
		// set the point transformation
		objToWorld.transform(worldPoint, objPoint);
		return objPoint;
	}
}
