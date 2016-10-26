package railway.gui;

/**
 * This class provides the main method that runs the Railway Manager.
 * 
 * INSTRUCTIONS: DO NOT MODIFY THIS CLASS
 */
public class RailwayManager {

	/** Starts the GUI. */
	public static void main(String[] args) throws Exception {
		RailwayModel model = new RailwayModel();
		RailwayView view = new RailwayView(model);
		new RailwayController(model, view);
		view.setVisible(true);
	}

}
