package userCode;

import java.awt.Color;
import java.awt.geom.*;
import java.util.*;

import cs355.model.drawing.*;

public class ShapeConverter {
	/*
	 * Converts a shape from the model construct to a java.awt Shape, so that it can be painted onscreen more easily
	 */
	public static java.awt.Shape convert(Shape currentShape) {
		if(currentShape instanceof Circle){
			double radius=((Circle) currentShape).getRadius();
			return new Ellipse2D.Double(-radius,-radius, radius*2, radius*2);
		}

		else if(currentShape instanceof Ellipse){
			double width=((Ellipse) currentShape).getWidth();
			double height=((Ellipse) currentShape).getHeight();
			return new Ellipse2D.Double(-(width/2),-(height/2), width, height);
		}

		else if(currentShape instanceof Line){
			return new Line2D.Double(new java.awt.geom.Point2D.Double(0,0), ((Line) currentShape).getEnd());
		}

		else if(currentShape instanceof Rectangle){
			Rectangle rect=(Rectangle)currentShape;
			return new Rectangle2D.Double(-rect.getWidth()/2,-rect.getHeight()/2,rect.getWidth(), rect.getHeight());
		}

		else if(currentShape instanceof Square){
			Square sqr=(Square)currentShape;
			return new Rectangle2D.Double(-sqr.getSize()/2,-sqr.getSize()/2,sqr.getSize(), sqr.getSize());
		}

		else if(currentShape instanceof Triangle){
			Triangle tri=(Triangle)currentShape;
			int x1=(int)(tri.getA().x);
			int x2=(int)(tri.getB().x);
			int x3=(int)(tri.getC().x);
			int y1=(int)(tri.getA().y);
			int y2=(int)(tri.getB().y);
			int y3=(int)(tri.getC().y);

			return new java.awt.Polygon(new int[]{x1,x2,x3},new int[]{y1,y2,y3},3);
		}
		return null;
	}

}
