package railway.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JOptionPane;

import railway.Route;
import railway.Track;
//verson 1.0
/**
 * The controller for the Railway Manager.
 */
public class RailwayController {

	// the model that is being controlled
	private RailwayModel model;
	// the view that is being controlled
	private RailwayView view;
	private String routeFile;
	private Route route;
	private Track track;
	private int trainId;
	private ArrayList<Route> routeList;
	private ArrayList<TrainTriplet<Integer, Integer, Route>> trains;
	// private ArrayList<>showRoute;
	
	
	
	public class TrainTriplet<Start, End, Name> { 
		  public Start start; 
		  public End end;
		  public Name name;
		  public TrainTriplet(Start start, End end, Name name) { 
		    this.name = name; 
		    this.start = start;
		    this.end = end;
		  }
		  
		  public Start getFirstParameter(){
			  return start;
		  }
		  
		  public End getSecondParameter(){
			  return end;
		  }
		  
		  public Name getThirdParameter(){
			  return name;
		  }
		  
		  public void setFirstParameter(Start start){
			  this.start = start;
		  }
		  
		  public void setSecondParameter(End end){
			  this.end = end;
		  }
		  
		  
		  
		}

	/**
	 * Initialises the Controller for the Railway Manager.
	 */

	// read track file when system executed
	public RailwayController(RailwayModel model, RailwayView view) {
		this.model = model;
		this.view = view;
		view.addLoadListener(new LoadActionListener());
		view.addEnterOffsetListener(new EnterOffsetActionListener());
		view.addViewListener(new ViewActionListener());
		view.addUpdateListener(new UpdateActionListener());
		
		track = model.openTackFile("track.txt");
		trainId = 0;
		routeList = new ArrayList<Route>();
		trains = new ArrayList<TrainTriplet<Integer, Integer, Route>>();
	    
	}

	// action of loading route file
	private class LoadActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			routeFile = view.getFile();
			route = model.openRouteFile(routeFile);
		}
	}

	// action of Entering offsets value
	private class EnterOffsetActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			if(view.getStartOffset().equals("") || !(model.isNumeric(view.getStartOffset()))){
				JOptionPane.showMessageDialog(null, "Invalid start offset!", 
						"Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			else if(view.getEndOffset().equals("") || !(model.isNumeric(view.getEndOffset()))){
				JOptionPane.showMessageDialog(null, "Invalid end offset!", 
						"Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int startOffset = Integer.parseInt(view.getStartOffset());
			int endOffset = Integer.parseInt(view.getEndOffset());
			int length = route.getLength();
			boolean start = true;

			// check route on track
			if (!model.OnTrack(track, route)) {
				JOptionPane.showMessageDialog(null, "The route is not on track", 
						"Warning",
						JOptionPane.WARNING_MESSAGE);
				start = false;
				return;

			}
			// check valid offsets
			if (!model.validOffset(startOffset, endOffset, length)) {
				JOptionPane.showMessageDialog(null, "Input invalid offsets ", 
						"Warning", JOptionPane.WARNING_MESSAGE);
				start = false;
				return;
			}
			// check intersection
			Route totalRoute = model.getRoute();
			
			Route subRoute = totalRoute.getSubroute(startOffset, endOffset);
			//compare subRoute to all the elements in the routeList
			for (int i = 0; i < routeList.size(); i++) {
				if (!model.validIntersection(routeList.get(i), subRoute)) {
					JOptionPane.showMessageDialog(null, "The route intersects ", 
							"Warning",
							JOptionPane.WARNING_MESSAGE);
					start = false;
					return;
				}
			}
			// if no problem above, route can be added into list
			if (start == true) {
				TrainTriplet<Integer, Integer, Route> train = new 
						TrainTriplet<Integer, Integer, Route>(startOffset, endOffset, totalRoute);
				
				
				trains.add(trainId, train);
				routeList.add(subRoute);
				//routeId back to view
				view.addRoute(trainId);
				trainId++;
				
				
			}

		}
	}

	public ArrayList<Route> getRouteList() {
		return routeList;
	
	}

	// action of viewing route list
	private class ViewActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int id = view.getChosenTrain();
			int start = trains.get(id).getFirstParameter();
			int end = trains.get(id).getSecondParameter();
			Route totalRoute = trains.get(id).getThirdParameter();
			JOptionPane.showMessageDialog(null, id + "\n" + "Start: " + start + " End: " + end + "\n" + totalRoute, 
					"This is the chosen train", JOptionPane.WARNING_MESSAGE);

		}
	}

	// action of updating offsets value
	private class UpdateActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int id = view.getChosenTrain();
			if(view.getNewStartOffset().equals("") || !(model.isNumeric(view.getNewStartOffset()))){
				JOptionPane.showMessageDialog(null, "Invalid start offset!", 
						"Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			else if(view.getNewEndOffset().equals("") || !(model.isNumeric(view.getNewEndOffset()))){
				JOptionPane.showMessageDialog(null, "Invalid end offset!", 
						"Warning",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int nStartOffset = Integer.parseInt(view.getNewStartOffset());
			int nEndOffset = Integer.parseInt(view.getNewEndOffset());
			
			TrainTriplet<Integer, Integer, Route> oldTrain = trains.get(id);
			Route totalRoute = oldTrain.getThirdParameter();
			int startOffset = oldTrain.getFirstParameter();
			int endOffset = oldTrain.getSecondParameter();
			
			Route newSubRoute = totalRoute.getSubroute(nStartOffset, nEndOffset);
			Route oldRoute = totalRoute.getSubroute(startOffset, endOffset);
			
			Iterator<Route> iterator = routeList.iterator();

			while (iterator.hasNext()) {
			    Route currentRoute = iterator.next();

			    if (currentRoute.equals(oldRoute))
			        iterator.remove();
			}
			for (int i = 0; i < routeList.size(); i++) {
				if (!model.validIntersection(routeList.get(i), newSubRoute)) {
					routeList.add(oldRoute);
					JOptionPane.showMessageDialog(null, "The route intersects ", 
							"Warning",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
			}
			TrainTriplet<Integer, Integer, Route> newTrain = new TrainTriplet<Integer, Integer, Route>(
					nStartOffset, nEndOffset, totalRoute);
			trains.remove(id);
			trains.add(id, newTrain);
			routeList.add(newSubRoute);
			
			

		}
	}

}
