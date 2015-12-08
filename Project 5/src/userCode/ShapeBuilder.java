package userCode;

import java.awt.Color;
import java.awt.geom.Point2D.Double;

import cs355.model.drawing.*;

public class ShapeBuilder {
	Shape currentShape;
	
	Double firstPoint;
	Boolean triangleInProgess;
	public Shape startShape(Class<?> shape, Double usablePoint, Color color) {
		firstPoint=new Double(usablePoint.x,usablePoint.y);
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
			currentShape= new Line(color, firstPoint, new Double(0,0));
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
	public void secondPointUpdate(Double updatePoint){
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
			Double delta=new Double(updatePoint.getX()-currentShape.getCenter().x,updatePoint.getY()-currentShape.getCenter().y);
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
	public void addTrianglePoint(Double point) {
		Triangle tri= (Triangle)(currentShape);
		if(tri.getB().equals(tri.getC())){
			tri.setB(new Double(point.x,point.y));
		}else{
			
			Double a= tri.getA();
			Double b= tri.getB();
			Double c=new Double(point.x,point.y);
			double centerX=(a.x+b.x+c.x)/3;
			double centerY=(a.y+b.y+c.y)/3;
			Double center= new Double(centerX,centerY);
			tri.setCenter(center);
			tri.setA(new Double(a.x-center.x,a.y-center.y));
			tri.setB(new Double(b.x-center.x,b.y-center.y));
			tri.setC(new Double(c.x-center.x,c.y-center.y));
			triangleInProgess=false;
		}
			

	}
	private Bounds getSquaredBounds(Double second){
		return new Bounds(firstPoint, second, true);

	}
	private Bounds getBounds(Double second){
		return new Bounds(firstPoint, second, false);
	}
	private class Bounds{
		double topLeftX;
		double topLeftY;
		double width;
		double height;
		private Bounds(Double first,Double second, boolean isSquare){
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
			return new Double(topLeftX+width/2,topLeftY+height/2);
		}
	}
	public Shape abortShape() {
		triangleInProgess=false;
		return currentShape;
			
	}

}
