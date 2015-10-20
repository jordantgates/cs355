package cs355.solution;

import cs355.GUIFunctions;
import userCode.DrawingModel;
import userCode.ImplementedController;
import userCode.ImplementedViewRefresher;

/**
 * This is the main class. The program starts here. Make you add code below to
 * initialize your model, view, and controller and give them to the app.
 */
public class CS355 {

	/**
	 * This is where it starts.
	 * 
	 * @param args
	 *            = the command line arguments
	 */
	public static void main(String[] args) {

		// Fill in the parameters below with your controller and view.
		ImplementedController ic =new ImplementedController();
		GUIFunctions.createCS355Frame(ic, new ImplementedViewRefresher(ic));

		GUIFunctions.refresh();
	}
}
