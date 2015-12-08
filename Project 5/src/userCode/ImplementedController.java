package userCode;

import java.awt.Color;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.File;
import java.util.Iterator;

import cs355.GUIFunctions;
import cs355.controller.CS355Controller;
import cs355.model.drawing.*;

public class ImplementedController implements CS355Controller, MouseListener, MouseMotionListener {

	private Class<?> currentShapeClass;
	private DrawingModel model;
	private Color currentColor;


	private double viewZoomScale=1;
	private Double viewOffset = new Double(768,768);
	private final int maxDrawArea=2048;
	private boolean ignoreBarEvents=false;


	private Double  usablePoint=new Double();
	private Double originalCenter;
	private double objSpaceRotateStart;

	private ShapeBuilder shapeB;
	private Shape selectedShape;

	private boolean lineFront;
	private State myState;
	private dragMode draggingReason;
	private Double originalEnd;
	private enum State {DRAW,SELECT};
	private enum dragMode {MOVE,ROTATE,LINEMOVE};

	public ImplementedController() {
		this.model=new DrawingModel();
		shapeB=new ShapeBuilder();
		currentColor=new Color(128,128,128);


	}

	// Color.

	/**
	 * Called when the user hits the color button.
	 * 
	 * @param c
	 *            = the new <i>drawing</i> color.
	 */
	public void colorButtonHit(Color c){
		currentColor=c;
		GUIFunctions.changeSelectedColor(c);
		if(selectedShape!=null){
			selectedShape.setColor(c);
			model.emptyModelUpdate();
		}
	}
	// File menu.

	/**
	 * Called to save a drawing.
	 * 
	 * @param file
	 *            = the file to save the drawing to.
	 */
	public void saveDrawing(File file){
		model.save(file);
	}

	/**
	 * Called to open a drawing.
	 * 
	 * @param file
	 *            = the file to open the drawing from.
	 */
	public void openDrawing(File file){
		selectedShape=null;
		model.setSelectedShape(null);
		model.open(file);

	}

	// Shapes.

	/**
	 * Called when the user hits the line button.
	 */
	public void lineButtonHit(){
		abortShapeBuilding();
		currentShapeClass=Line.class;
		myState=State.DRAW;
	}

	/**
	 * Called when the user hits the square button.
	 */
	public void squareButtonHit(){
		abortShapeBuilding();
		currentShapeClass=Square.class;
		myState=State.DRAW;
	}

	/**
	 * Called when the user hits the rectangle button.
	 */
	public void rectangleButtonHit(){
		abortShapeBuilding();
		currentShapeClass=Rectangle.class;
		myState=State.DRAW;
	}

	/**
	 * Called when the user hits the circle button.
	 */
	public void circleButtonHit(){
		abortShapeBuilding();
		currentShapeClass=Circle.class;
		myState=State.DRAW;
	}

	/**
	 * Called when the user hits the ellipse button.
	 */
	public void ellipseButtonHit(){
		abortShapeBuilding();
		currentShapeClass=Ellipse.class;
		myState=State.DRAW;
	}

	/**
	 * Called when the user hits the triangle button.
	 */
	public void triangleButtonHit(){
		abortShapeBuilding();
		currentShapeClass=Triangle.class;
		myState=State.DRAW;
	}

	private void abortShapeBuilding() {
		selectedShape=null;
		model.setSelectedShape(-1);
		if(shapeB.isTriangleInProgess()){
			Shape abortedShape=shapeB.abortShape();
			if(abortedShape instanceof Triangle)
				model.deleteShape(abortedShape);
		}

	}
	//////Mouse Buttons
	@Override
	public void mouseDragged(MouseEvent e){
		Double eventPoint=new Double(e.getX(),e.getY());
		TransformBuilder.viewToWorld(getScreenDims()).transform(eventPoint, eventPoint);
		switch(myState){
		case DRAW:
			shapeB.secondPointUpdate(eventPoint);
			model.emptyModelUpdate();
			break;
		case SELECT:
			if(selectedShape!=null){
				Double delta= new Double(eventPoint.x-usablePoint.x,eventPoint.y-usablePoint.y);
				switch(draggingReason){
				case LINEMOVE:
					Line selected =((Line)selectedShape);
					if(lineFront){
						Point2D.Double newCenter=new Point2D.Double(originalCenter.x+(delta.x),originalCenter.y+(delta.y));
						selectedShape.setCenter(newCenter);
						selected.setEnd(new Point2D.Double(originalEnd.x-(delta.x),originalEnd.y-(delta.y)));
					}
					else{
						selected.setEnd(new Point2D.Double(originalEnd.x+(delta.x),originalEnd.y+(delta.y)));
					}
					break;
				case MOVE:
					Point2D.Double newCenter=new Point2D.Double(originalCenter.x+(delta.x),originalCenter.y+(delta.y));
					selectedShape.setCenter(newCenter);
					break;
				case ROTATE:
					double rotation=angleFromATan(selectedShape, eventPoint)+objSpaceRotateStart;
					selectedShape.setRotation(rotation);
					break;
				}
			}
			break;
		}
		model.emptyModelUpdate();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton()==MouseEvent.BUTTON1){
			TransformBuilder.viewToWorld(getScreenDims()).transform(new Point2D.Double(e.getX(),e.getY()),usablePoint);
			if(myState==State.DRAW){
				selectedShape=null;
				drawPress();
			}
			else if(myState==State.SELECT){
				if(model.isSelectedShapeHandle(usablePoint, 6.0/viewZoomScale, 10.0/viewZoomScale)){
					if(selectedShape instanceof Line){
						draggingReason=dragMode.LINEMOVE;
						Line selected =((Line)selectedShape);
						Double linePoint=new Double();
						TransformBuilder.worldToObject(selected).transform(usablePoint, linePoint);
						double distanceA=new Double().distance(linePoint);
						double distanceB=selected.getEnd().distance(linePoint);
						lineFront=(distanceA<distanceB);
						originalCenter=selected.getCenter();
						originalEnd=selected.getEnd();
					}
					else{
						draggingReason=dragMode.ROTATE;
						objSpaceRotateStart=selectedShape.getRotation();
					}
				}
				else{
					draggingReason=dragMode.MOVE;
					selectShape(usablePoint);
				}
			}
		}
		model.setSelectedShape(selectedShape);
		model.emptyModelUpdate();
	}

	private void drawPress() {
		if(currentShapeClass==Triangle.class){
			if(shapeB.isTriangleInProgess()){
				shapeB.addTrianglePoint(usablePoint);
				model.emptyModelUpdate();
				return;
				}
		}
		Shape newShape=shapeB.startShape(currentShapeClass,usablePoint,currentColor);
		if(newShape!=null)
			model.addShape(newShape);
		model.emptyModelUpdate();

	}

	private void selectShape(Double usablePoint) {
		selectedShape=model.getTopShapeUnderPoint(usablePoint, 4.0/viewZoomScale);

		if(selectedShape!=null){
			currentColor=selectedShape.getColor();
			originalCenter=selectedShape.getCenter();
			GUIFunctions.changeSelectedColor(currentColor);
			System.out.println("Selected Shape: "+selectedShape);
		}
		else
			System.out.println("No Shape Selected");

	}


	@Override
	public void mouseReleased(MouseEvent e) {
		//String note="Mouse Released";
		//GUIFunctions.printf(note,"");

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		//String note="Mouse Moved";
		//GUIFunctions.printf(note,"");

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		//String note="Mouse Clicked";
		//GUIFunctions.printf(note,"");

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
	/**
	 * Called when the user hits the select button.
	 */
	public void selectButtonHit(){
		myState=State.SELECT;
	}
	// Object menu.

	/**
	 * Called to move the currently selected shape one slot forward.
	 */
	public void doMoveForward(){
		if(selectedShape!=null){
			int index=model.getShapeIndex(selectedShape);
			model.moveForward(index);
		}
		model.setSelectedShape(selectedShape);
	}

	/**
	 * Called to move the currently selected shape one slot backward.
	 */
	public void doMoveBackward(){
		if(selectedShape!=null){
			int index=model.getShapeIndex(selectedShape);
			model.moveBackward(index);
		}
	}

	/**
	 * Called to move the currently selected shape to the front.
	 */
	public void doSendToFront(){
		if(selectedShape!=null){
			int index=model.getShapeIndex(selectedShape);
			model.moveToFront(index);
		}
	}

	/**
	 * Called to move the currently selected shape to the back.
	 */
	public void doSendtoBack(){
		if(selectedShape!=null){
			int index=model.getShapeIndex(selectedShape);
			model.movetoBack(index);
		}
	}

	/////ROTATING

	private double angleFromATan(Shape targetShape, Double b){
		Double a = new Double(usablePoint.x,usablePoint.y);
		AffineTransform viewToObj=TransformBuilder.worldToObject(targetShape);
		viewToObj.transform(a, a);
		viewToObj.transform(b, b);
		double angle=Math.atan2(b.y,b.x)-Math.atan2(a.y,a.x);
		return angle;
		
	}

	// Zooming.

	/**
	 * Called when the user hits the zoom in button.
	 */
	public void zoomInButtonHit(){		
		if(viewZoomScale>=4)
			return;

		viewZoomScale*=2.0;
		viewOffset.x+=(256/viewZoomScale);
		viewOffset.y+=(256/viewZoomScale);
		
		ignoreBarEvents=true;
		
		GUIFunctions.setZoomText(viewZoomScale);
		GUIFunctions.setHScrollBarKnob((int) (512/viewZoomScale));
		GUIFunctions.setVScrollBarKnob((int) (512/viewZoomScale));
		GUIFunctions.setHScrollBarPosit((int)(viewOffset.x));
		GUIFunctions.setVScrollBarPosit((int)(viewOffset.y));
		ignoreBarEvents=false;
		model.emptyModelUpdate();
		

	}

	/**
	 * Called when the user hits the zoom out button.
	 */
	public void zoomOutButtonHit(){

		if(viewZoomScale<=.25)
			return;

		viewZoomScale*=0.5;
		viewOffset.x-=(128/viewZoomScale);
		if(viewOffset.x<0)
			viewOffset.x=0;
		if(viewOffset.x>maxDrawArea-+((int) (512/viewZoomScale)))
			viewOffset.x=maxDrawArea-+((int) (512/viewZoomScale));
		viewOffset.y-=(128/viewZoomScale);
		if(viewOffset.y<0)
			viewOffset.y=0;
		if(viewOffset.y>maxDrawArea-((int) (512/viewZoomScale)))
			viewOffset.y=maxDrawArea-((int) (512/viewZoomScale));
		
		
		ignoreBarEvents=true;
		
		GUIFunctions.setZoomText(viewZoomScale);
		GUIFunctions.setHScrollBarPosit((int)(viewOffset.x));
		GUIFunctions.setVScrollBarPosit((int)(viewOffset.y));
		GUIFunctions.setHScrollBarKnob((int) (512/viewZoomScale));
		GUIFunctions.setVScrollBarKnob((int) (512/viewZoomScale));
		ignoreBarEvents=false;
		model.emptyModelUpdate();
		

	}

	/**
	 * Called when the horizontal scrollbar position changes.
	 * 
	 * @param value
	 *            = the new position.
	 */
	public void hScrollbarChanged(int value){
		if(ignoreBarEvents)
			return;
		viewOffset.x=value;
		model.emptyModelUpdate();
		
	}

	/**
	 * Called when the vertical scrollbar position changes.
	 * 
	 * @param value
	 *            = the new position.
	 */
	public void vScrollbarChanged(int value){
		if(ignoreBarEvents)
			return;
		viewOffset.y=value;
		model.emptyModelUpdate();
		

	}

	public ScreenDim getScreenDims() {
		return new ScreenDim(viewZoomScale, viewOffset);
	}

	// 3D Model.

	/**
	 * Called to load a scene from a file.
	 * 
	 * @param file
	 *            = the file containing the scene to load.
	 */
	public void openScene(File file){

	}

	/**
	 * Called to toggle the 3D OpenGL display.
	 */
	public void toggle3DModelDisplay(){

	}

	/**
	 * Called when the user presses keys. This is used for navigating in the 3D
	 * world.
	 * 
	 * @param iterator
	 *            = the iterator over the keys.
	 */
	public void keyPressed(Iterator<Integer> iterator){

	}

	// Image.

	/**
	 * Called to load a background image.
	 * 
	 * @param file
	 *            = the image file to load.
	 */
	public void openImage(File file){

	}

	/**
	 * Called to save the background image.
	 * 
	 * @param file
	 *            = the file to save the image to.
	 */
	public void saveImage(File file){

	}

	/**
	 * Called to toggle the background image display.
	 */
	public void toggleBackgroundDisplay(){

	}

	// Edit menu.

	/**
	 * Called to delete the currently selected shape.
	 */
	public void doDeleteShape(){
		if(selectedShape!=null){
			int index=model.getShapeIndex(selectedShape);
			model.deleteShape(index);
			abortShapeBuilding();
		}
	}

	// Image menu.

	/**
	 * Called to perform edge detection on the background image.
	 */
	public void doEdgeDetection(){

	}

	/**
	 * Called to perform sharpen on the background image.
	 */
	public void doSharpen(){

	}

	/**
	 * Called to perform median blur on the background image.
	 */
	public void doMedianBlur(){

	}

	/**
	 * Called to perform uniform blur on the background image.
	 */
	public void doUniformBlur(){

	}

	/**
	 * Called to change the background image to grayscale.
	 */
	public void doGrayscale(){

	}

	/**
	 * Called to change the contrast on the background image.
	 * 
	 * @param contrastAmountNum
	 *            = how much contrast to add.
	 */
	public void doChangeContrast(int contrastAmountNum){

	}

	/**
	 * Called to change the brightness on the background image.
	 * 
	 * @param brightnessAmountNum
	 *            = how much brightness to add.
	 */
	public void doChangeBrightness(int brightnessAmountNum){

	}

	public double getZoomLevel(){
		return viewZoomScale;
	}
	public DrawingModel getModel(){
		return model;
	}
}
