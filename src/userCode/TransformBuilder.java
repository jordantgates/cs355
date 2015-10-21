package userCode;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Double;

import cs355.model.drawing.Shape;

public class TransformBuilder {

	//Mi Transformation
	public static AffineTransform objectToView(Shape reference,ScreenDim dimensions){
		AffineTransform result=worldToView(dimensions);
		result.concatenate(objectToWorld(reference));
		return result;
	}
	//Mi inverse
	public static AffineTransform viewToObject(Shape reference,ScreenDim dimensions){
		AffineTransform result=worldToObject(reference);
		result.concatenate(viewToWorld(dimensions));
		return result;
	}

	public static AffineTransform objectToWorld(Shape reference){
		AffineTransform result=translate(reference.getCenter());
		result.concatenate(rotate(reference.getRotation()));
		return result;
	}
	
	public static AffineTransform worldToObject(Shape reference){
		AffineTransform result=rotate(reference.getRotation()*-1.0);
		result.concatenate(translate(negatePoint(reference.getCenter())));
		return result;
	}

	public static AffineTransform worldToView(ScreenDim dimensions){
		AffineTransform result=zoom(dimensions.getMagnificationLevel());
		result.concatenate(translate(negatePoint(dimensions.getScreenOffset())));
		return result;
	}

	public static AffineTransform viewToWorld(ScreenDim dimensions){
		AffineTransform result=translate((dimensions.getScreenOffset()));
		result.concatenate(zoom(1/dimensions.getMagnificationLevel()));
		return result;
	}
	
	
	
	public static AffineTransform translate(Double delta){
		return new AffineTransform(1,0,0, 1, delta.x, delta.y);
	}

	public static AffineTransform rotate(double theta){
		theta=theta/-1;
		return new AffineTransform(Math.cos(theta),-Math.sin(theta),Math.sin(theta), Math.cos(theta), 0, 0);
	}

	public static AffineTransform zoom(double scale){
		return new AffineTransform(scale,0,0, scale, 0, 0);
	}

	public static Double negatePoint(Double original){
		return new Double(original.x*-1.0,original.y*-1.0);
	}
}
