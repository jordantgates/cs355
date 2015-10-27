package userCode;

import java.awt.geom.Point2D.Double;

public class ScreenDim {

	private double magnificationLevel;


	private Double screenOffset;

	public ScreenDim(double magnificationLevel, Double screenOffset) {
		this.magnificationLevel = magnificationLevel;
		this.screenOffset = screenOffset;
	}

	public double getMagnificationLevel() {
		return magnificationLevel;
	}

	public Double getScreenOffset() {
		return screenOffset;
	}
	
}
