package userCode;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import cs355.model.drawing.*;

public class ShapeBuilder {
	Shape currentShape;
	
	Point2D.Double firstPoint;
	Boolean triangleInProgess;
	public Shape startShape(Class<?> shape, Point point, Color color) {

		firstPoint=new Point2D.Double(point.getX(),point.getY());
		if(isTriangleInProgess()){
			triangleInProgess=false;
		}

		if(shape==Circle.class){
			currentShape=new Circle(color, firstPoint, 0);
		}
		else if(shape==Ellipse.class){
			currentShape=new Ellipse(color, firstPoint, 0 , 0);
		}
		else if(shape==Line.class){
			currentShape= new Line(color, firstPoint, new Point2D.Double(0,0));
		}
		else if(shape==Rectangle.class){
			currentShape= new Rectangle(color, firstPoint, 0, 0);
		}
		else if(shape==Square.class){
			currentShape= new Square(color, firstPoint, 0);
		}
		else if(shape==Triangle.class){
			currentShape= new Triangle(color, firstPoint, firstPoint, firstPoint, firstPoint);
			triangleInProgess = true;

		}
		else
			return null;
		return currentShape;
	}
	public void secondPointUpdate(Point point){
		Point2D.Double updatePoint=new Point2D.Double(point.getX(),point.getY());
		Bounds bounds;

		if(currentShape instanceof Circle){
			bounds=getSquaredBounds(updatePoint);
			((Circle) currentShape).setRadius(bounds.width/2);
			((Circle) currentShape).setCenter(bounds.getCenter());
		}
		else if(currentShape instanceof Ellipse){
			bounds=getBounds(updatePoint);
			((Ellipse)currentShape).setCenter(bounds.getCenter());
			((Ellipse)currentShape).setHeight(bounds.height);
			((Ellipse)currentShape).setWidth(bounds.width);
		}
		else if(currentShape instanceof Line){
			Point2D.Double delta=new Point2D.Double(point.getX()-currentShape.getCenter().x,point.getY()-currentShape.getCenter().y);
			((Line)currentShape).setEnd(delta);
		}
		else if(currentShape instanceof Rectangle){
			bounds=getBounds(updatePoint);
			((Rectangle)currentShape).setCenter(bounds.getCenter());
			((Rectangle)currentShape).setHeight(bounds.height);
			((Rectangle)currentShape).setWidth(bounds.width);
		}
		else if(currentShape instanceof Square){
			bounds=getSquaredBounds(updatePoint);
			((Square)currentShape).setCenter(bounds.getCenter());
			((Square)currentShape).setSize(bounds.width);
		}
	}
	public boolean isTriangleInProgess() {
		if(triangleInProgess==null)
			triangleInProgess=false;
		return triangleInProgess;
	}
	public void addTrianglePoint(Point point) {
		Triangle tri= (Triangle)(currentShape);
		if(tri.getB().equals(tri.getC())){
			tri.setB(new Double(point.x,point.y));
		}else{
			Point2D.Double c=new Point2D.Double(point.x,point.y);
			Point2D.Double a= tri.getA();
			Point2D.Double b= tri.getB();
			double centerX=(a.x+b.x+c.x)/3;
			double centerY=(a.y+b.y+c.y)/3;
			Point2D.Double center= new Point2D.Double(centerX,centerY);
			tri.setCenter(center);
			tri.setA(new Point2D.Double(a.x-center.x,a.y-center.y));
			tri.setB(new Point2D.Double(b.x-center.x,b.y-center.y));
			tri.setC(new Point2D.Double(c.x-center.x,c.y-center.y));
			triangleInProgess=false;
		}
			

	}
	private Bounds getSquaredBounds(Point2D.Double second){
		return new Bounds(firstPoint, second, true);

	}
	private Bounds getBounds(Point2D.Double second){
		return new Bounds(firstPoint, second, false);
	}
	private class Bounds{
		double topLeftX;
		double topLeftY;
		double width;
		double height;
		private Bounds(Point2D.Double first,Point2D.Double second, boolean isSquare){
			width=Math.abs(first.x-second.x);
			height=Math.abs(first.y-second.y);
			if(isSquare){
				if(width<height)
					height=width;
				else 
					width=height;
				if(second.x<first.x)
					second.x=first.x-width;
				else
					second.x=first.x+width;
				if(second.y<first.y)
					second.y=first.y-height;
				else
					second.y=first.y+height;
			}

			if(first.x<second.x){
				topLeftX=first.x;
			}	else
				topLeftX=second.x;
			if(first.y<second.y){
				topLeftY=first.y;
			}	else
				topLeftY=second.y;
		}
		private Double getCenter(){
			return new Point2D.Double(topLeftX+width/2,topLeftY+height/2);
		}
	}
	public Shape abortShape() {
		triangleInProgess=false;
		return currentShape;
			
	}

}
