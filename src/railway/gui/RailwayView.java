package railway.gui;
//verson 1.0
import javax.swing.*;

import railway.Route;
import railway.Segment;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.ResourceBundle.Control;

/**
 * The view for the Railway Manager.
 */
@SuppressWarnings("serial")
public class RailwayView extends JFrame {

    // the model of the Railway Manager
    private RailwayModel model; 
    //the field to enter route file
    private JTextField fileName;
    //the fields to enter offsets
    private JTextField startOffset;
    private JTextField endOffset;
    //the fields to update offsets
    private JTextField nStartOffset;
    private JTextField nEndOffset;
    //button to load route file
    private JButton loadFile;
    //button to enter offsets
    private JButton enterOffset;
    //button to view selected route
    private JButton view;
    //button to update offsets
    private JButton update;
    //list to display routes
    private JList<Integer> display;
    private DefaultListModel<Integer> displayList;
    //labels of text
    private JLabel label1;
    private JLabel label2;
    private Font font = new Font("SanSerif", Font.BOLD, 12);
    private RailwayController control;
    ArrayList<Route> routelist = new ArrayList<Route>();
  

    /**
     * Creates a new Railway Manager window.
     */
    public RailwayView(RailwayModel model) {
        this.model = model;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setTitle("Railway Management System");
        this.setSize(500,600);
        Container c = getContentPane();
        this.setResizable(false);
        //separate container into 2 panels
        addUpperPanel(c);
        addLowerPanel(c);
       
    }

    private void addUpperPanel(Container c){
        //upper panel consists of 3 panels
    	JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	p1.setPreferredSize(new Dimension(400,100));
    	addFile(p1);    	
    	addOffsets(p1);    	
    	addButtons(p1);
    	c.add(p1,"North");
    	
    }

    private void addLowerPanel(Container c){
        //lower panel consist of 2 panels
    	JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	p2.setLayout(new GridLayout(2,1));;
    	p2.setPreferredSize(new Dimension(400,450));
    	addListArea(p2);
    	addNewOffsets(p2);
    	c.add(p2,"South");
    	
    	
    }

    private void addFile(JPanel p3){
    	//text field to enter file name
    	JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
    	p.add(new JLabel("Add route file:"));
    	fileName = new JTextField();
    	fileName.setPreferredSize(new Dimension(100, 20));
    	p.add(fileName);
    	p3.add(p,"North");
        }
    

    private void addButtons(JPanel p1){
    	//buttons panel consists of 3 buttons, load, enter, update
    	JPanel p = new JPanel();
    	p.setLayout(new GridLayout(1,4));
    	addLoadButton(p);
    	addEnterButton(p);
    	addViewButton(p);
    	addUpdateButton(p);
    	p1.add(p,"South");
	    		
    }
    
    private void addLoadButton(JPanel p){
    	//add load button
    	loadFile = new JButton("Load");
    	loadFile.setPreferredSize(new Dimension(120,30));
    	loadFile.setBackground(Color.WHITE);
    	loadFile.setForeground(Color.BLUE);
    	p.add(loadFile);   	
    }
    
    private void addEnterButton(JPanel p){
    	//add enter button
    	enterOffset = new JButton("Enter");
    	enterOffset.setPreferredSize(new Dimension(120,30));
    	enterOffset.setBackground(Color.WHITE);
    	enterOffset.setForeground(Color.BLUE);
    	p.add(enterOffset);
    	
    }
    
	//add view button
    private void addViewButton(JPanel p){
    	view = new JButton("View routes");
    	view.setPreferredSize(new Dimension(120,30));
    	view.setBackground(Color.WHITE);
    	view.setForeground(Color.BLUE);
    	p.add(view);
    			
    }
    
	//add update button
    private void addUpdateButton(JPanel p){
    	update = new JButton("Update routes");
    	update.setPreferredSize(new Dimension(120,30));
    	update.setBackground(Color.WHITE);
    	update.setForeground(Color.BLUE);
    	p.add(update);
    	
    }
    
	//the offsets panel consists of 2 offsets  
    private void addOffsets(JPanel p1){
    	JPanel p = new JPanel();
    	p.setLayout(new GridLayout(1,4));
    	addStartOffset(p);   	
    	addEndOffset(p);    	
    	p1.add(p);
    }
    
	//add start offset text field   
    private void addStartOffset(JPanel p){
    	label1 = new JLabel("Start offset");
    	startOffset = new JTextField();
    	p.add(label1);
    	p.add(startOffset);
    	
    }
    
	//add end offset text field   
    private void addEndOffset(JPanel p){
    	label2 = new JLabel("End Offset");
    	endOffset = new JTextField();
      	p.add(label2);
    	p.add(endOffset);
  
    	
    }
    
	//the panel to enter new offsets   
    private void addNewOffsets(JPanel p1){
    	JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        //add new start offset
    	p.add(new JLabel("Update Start offset: "));
    	nStartOffset = new JTextField();
    	nStartOffset.setPreferredSize(new Dimension(80, 20));
    	p.add(nStartOffset,"West");
    	//add new end offset
    	p.add(new JLabel("Update End offset: "));
    	nEndOffset = new JTextField();
    	nEndOffset.setPreferredSize(new Dimension(80, 20));
    	p.add(nEndOffset,"East");
    	p1.add(p,"South");
    	
    }
    
	//the list area to show routes
    private void addListArea(JPanel p1){
    	JPanel p = new JPanel();
    	displayList = new DefaultListModel<Integer>();
    	//displayList.addElement(1);
    //	routelist = model.getRouteList();
    	
    	/*for (int i = 0; i<routelist.size(); i++){
    		
        	//Route route = model.openRouteFile("route0.txt");
        	displayList.addElement(i);
        	//displayList.addElement(123);
        	
    	}*/
    	//DefaultListModel<Integer> displayList = new DefaultListModel<Integer>();
    	//displayList.addElement(1);
    	display = new JList<Integer>(displayList);
    	display.setFont(font);
    	display.setBackground(Color.WHITE);
    	display.setForeground(Color.BLUE);
    	p.add(display);
    	p1.add(p,"LEFT");
    }

    
    public String getFile(){
    	return fileName.getText();
    }
    
    public String getStartOffset(){
    	return startOffset.getText();
    }
    
    public String getEndOffset(){
    	return endOffset.getText();
    }
    
    public int getDisplay(){
    	return display.getSelectedIndex();
    }
    
    public String getNewStartOffset(){
    	return nStartOffset.getText();
    }
    
    public String getNewEndOffset(){
    	return nEndOffset.getText();
    }
    
    public int getChosenTrain(){
    	return display.getSelectedIndex();
    }
    
    public int getRoute(){
    	return display.getSelectedIndex();
    }
    
    public void addRoute(int id){
    	displayList.addElement(id);
    }

    public void addLoadListener(ActionListener pl){
    	loadFile.addActionListener(pl);
    }
    
    public void addEnterOffsetListener(ActionListener pl){
    	enterOffset.addActionListener(pl);
    }
    
    public void addViewListener(ActionListener pl){
    	view.addActionListener(pl);
    }
    
    public void addUpdateListener(ActionListener pl){
    	update.addActionListener(pl);
    }
    
}




























