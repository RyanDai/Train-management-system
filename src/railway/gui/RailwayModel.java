package railway.gui;
import railway.TrackReader;
import java.util.ArrayList;

import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.text.View;

import railway.FormatException;
import railway.Track;
import railway.Route;
import railway.RouteReader;
//verson 1.0
/**
 * The model for the Railway Manager.
 */
public class RailwayModel {
	private Track track;
	private Route route;
	private ArrayList<Route> routes;
	private RailwayController control;
	private ArrayList<Route> routeList;
	

    /**
     * Initializes the model for the Railway Manager.
     */
    public RailwayModel() {
 //       routes = new ArrayList<Route>();
    }
    
    //read the track file
	public Track openTackFile(String filename) {
		try{
			track = TrackReader.read(filename);
		}
		//Identity the input/output error reading
		catch(IOException e){
			JOptionPane.showMessageDialog(null, "Input/Output error", 
					"IOException",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		} 
		//Identity the error with the input format
		catch (FormatException e) {
			JOptionPane.showMessageDialog(null, "An error with input format", 
					"FormatException",JOptionPane.WARNING_MESSAGE);
			System.exit(0);	
		}
		return track;
	}
	
	//read the route file
	public Route openRouteFile(String filename){
		try{
			 route = RouteReader.read(filename);
			
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "Route cannot be read!",
					"Warning", JOptionPane.WARNING_MESSAGE);
		}
		return route;
	}
	
	//get routes from route list
	public Route getRoute(){
		return route;
	}
	
	//get added routes from controller
	public ArrayList<Route> getRouteList(){
		return control.getRouteList();
	}
	
	
	//add route into list
	public void addRoute(){
		routes.add(route);
	}
	
	
	//check the route is on track
	public boolean OnTrack(Track track, Route route){
		if(route.onTrack(track)){
			return true;
		}
		else{
			return false;
		}		
	}
	
    //check the route with valid offsets
	public boolean validOffset(int startOffset, int endOffset, int length){
		if(startOffset >= 0 && startOffset < endOffset && endOffset <= length){
			return true;
		}
		else
			return false;
	}
	
	//check the sub-routes' intersection
	public boolean validIntersection(Route route1, Route route2){
		if(route1.intersects(route2)){
			return false;
		}
		else
			return true;
		
	}
	
	//check if a string is numeric
	public boolean isNumeric(String s) {  
	    return s.matches("[-+]?\\d*\\.?\\d+");  
	} 
	

	
	
	

	

    // REMOVE THIS LINE AND ADD YOUR OWN METHODS ETC HERE

}

