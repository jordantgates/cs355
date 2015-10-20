/**
 * 
 */
package userCode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import cs355.GUIFunctions;
import cs355.model.drawing.*;
import cs355.view.ViewRefresher;

/**
 * @author Jordan Gates
 *
 */
public class ImplementedViewRefresher implements ViewRefresher {
	List<Shape> currentShapes;
	Shape currentSelected;
	ImplementedController theController;
	public ImplementedViewRefresher(ImplementedController ic) {
		theController=ic;
		theController.getModel().addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		if(o instanceof DrawingModel){
			currentShapes=((DrawingModel)o).getShapes();
			currentSelected=((DrawingModel)o).getSelectedShape();
			GUIFunctions.refresh();
		}
	}

	@Override
	public void refreshView(Graphics2D canvas) {
		canvas.clearRect(0, 0, 3000, 3000);
		if((canvas!=null)&(currentShapes!=null)){
			for(Shape currentShape:currentShapes){
				drawShape(currentShape,canvas,!(currentShape instanceof Line), false, new Double());
			}
			if(currentSelected!=null)
				drawSelectionOutlines(canvas);
		}

	}
	private void drawSelectionOutlines(Graphics2D canvas) {
		double circleRadius=5.0;
		List<Shape> newElements= new ArrayList<Shape>();
		newElements.addAll(currentSelected.getHandles(circleRadius));
		if(currentSelected instanceof Square){
			newElements.add(new Square(((Square) currentSelected).getSize()));
		}
		if(currentSelected instanceof Circle){
			newElements.add(new Square(((Circle) currentSelected).getRadius()*2.0));
		}
		else if(currentSelected instanceof Rectangle){
			Rectangle rect=(Rectangle) currentSelected;
			newElements.add(new Rectangle(rect.getWidth(),rect.getHeight()));
		}
		else if(currentSelected instanceof Ellipse){
			Ellipse ellip=(Ellipse) currentSelected;
			newElements.add(new Rectangle(ellip.getWidth(),ellip.getHeight()));
		}
		else if(currentSelected instanceof Triangle){
			Triangle tri=(Triangle) currentSelected;
			newElements.add(new Triangle(tri.getA(), tri.getB(),tri.getC()));

		}

		for(Shape shape:newElements){
			Double initalOffset=shape.getCenter();
			shape.setCenter(currentSelected.getCenter());
			shape.setRotation(currentSelected.getRotation());
			drawShape(shape, canvas, false, true, initalOffset);
		}

	}

	private void drawShape(Shape currentShape, Graphics2D canvas, boolean filled, boolean highlighting, Double initialOffset){
		AffineTransform transform;
		java.awt.Shape convertedShape=ShapeConverter.convert(currentShape);
		//set-up transform
		transform = generateTransform(currentShape, initialOffset);
		canvas.setTransform(transform);
		canvas.setStroke(new BasicStroke(2));

		if(convertedShape!=null){
			Color currentColor =currentShape.getColor();
			if(highlighting){
				canvas.setColor(Color.CYAN);}
			else{
				canvas.setColor(currentColor);}

			if(filled)
				canvas.fill(convertedShape);
			else
				canvas.draw(convertedShape);}

	}

	private AffineTransform generateTransform(Shape currentShape, Double initialOffset) {
		AffineTransform objToWorld = new AffineTransform();
		Double worldOffset = currentShape.getCenter();
		// translate to its position in the world (last transformation)
		objToWorld.translate(worldOffset.x, worldOffset.y);
		// rotate to its orientation (first transformation)
		objToWorld.rotate(currentShape.getRotation());
		// set the drawing transformation
		objToWorld.translate(initialOffset.x, initialOffset.y);
		return objToWorld;
	}

}
