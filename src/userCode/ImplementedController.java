package userCode;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.*;
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
	private Double viewOffset = new Double(512,512);
	private final int maxDrawArea=2048;
	private double viewableArea;

	private Point startClick;
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
		switch(myState){
		case DRAW:
			shapeB.secondPointUpdate(e.getPoint());
			model.emptyModelUpdate();
			break;
		case SELECT:
			if(selectedShape!=null){
				Point2D.Double delta= new Double(e.getX()-startClick.x,e.getY()-startClick.y);
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
					double rotation=angleFromATan(selectedShape, startClick, e.getPoint())+objSpaceRotateStart;
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
			if(myState==State.DRAW){
				selectedShape=null;
				drawPress(e.getPoint());
			}
			else if(myState==State.SELECT){
				Point2D.Double usablePoint=new Point2D.Double(e.getX(),e.getY());
				startClick=e.getPoint();
				if(model.isSelectedShapeHandle(usablePoint, 6.0/viewZoomScale)){
					if(selectedShape instanceof Line){
						draggingReason=dragMode.LINEMOVE;
						Line selected =((Line)selectedShape);
						usablePoint=model.transformWorldtoObjectPoint(selectedShape, usablePoint);
						double distanceA=new Double().distance(usablePoint);
						double distanceB=selected.getEnd().distance(usablePoint);
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

	private void drawPress(Point point) {
		if(currentShapeClass==Triangle.class){
			if(shapeB.isTriangleInProgess()){
				shapeB.addTrianglePoint(point);
				model.emptyModelUpdate();
				return;}
		}
		Shape newShape=shapeB.startShape(currentShapeClass,point,currentColor);
		if(newShape!=null)
			model.addShape(newShape);
		model.emptyModelUpdate();

	}

	private void selectShape(Point2D.Double usablePoint) {
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

	private double angleFromATan(Shape targetShape, Point first, Point second){
		Point2D.Double a=model.transformWorldtoObjectPoint(targetShape,new Point2D.Double(first.x,first.y));
		Point2D.Double b=model.transformWorldtoObjectPoint(targetShape,new Point2D.Double(second.x,second.y));
		return Math.atan2(b.y,b.x)-Math.atan2(a.y,a.x);
	}
	// Zooming.

	/**
	 * Called when the user hits the zoom in button.
	 */
	public void zoomInButtonHit(){		
		if(viewZoomScale<4)
			viewZoomScale*=2.0;
		GUIFunctions.setZoomText(viewZoomScale);
		GUIFunctions.setHScrollBarKnob((int) (512/viewZoomScale));
		GUIFunctions.setVScrollBarKnob((int) (512/viewZoomScale));
		GUIFunctions.setHScrollBarPosit((int)viewOffset.x);
		GUIFunctions.setVScrollBarPosit((int)viewOffset.y);
		
	}

	/**
	 * Called when the user hits the zoom out button.
	 */
	public void zoomOutButtonHit(){
		if(viewZoomScale>.25)
			viewZoomScale/=2.0;
		
		GUIFunctions.setZoomText(viewZoomScale);
		
		GUIFunctions.setHScrollBarMin(0);
		GUIFunctions.setHScrollBarMax(maxDrawArea);
		GUIFunctions.setHScrollBarKnob((int) (512/viewZoomScale));
		GUIFunctions.setHScrollBarPosit((int)viewOffset.x);
		
		GUIFunctions.setVScrollBarKnob((int) (512/viewZoomScale));
		GUIFunctions.setVScrollBarPosit((int)viewOffset.y);
		GUIFunctions.setVScrollBarMin(0);
		GUIFunctions.setVScrollBarMax(maxDrawArea);

	}

	/**
	 * Called when the horizontal scrollbar position changes.
	 * 
	 * @param value
	 *            = the new position.
	 */
	public void hScrollbarChanged(int value){
		System.out.println(value);
	}

	/**
	 * Called when the vertical scrollbar position changes.
	 * 
	 * @param value
	 *            = the new position.
	 */
	public void vScrollbarChanged(int value){

		System.out.println(value);
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
