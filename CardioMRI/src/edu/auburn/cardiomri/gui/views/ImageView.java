package edu.auburn.cardiomri.gui.views;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import com.pixelmed.display.SingleImagePanel;

import edu.auburn.cardiomri.datastructure.Contour;
import edu.auburn.cardiomri.datastructure.Landmark;
import edu.auburn.cardiomri.datastructure.Landmark.LandmarkType;
import edu.auburn.cardiomri.datastructure.Point;
import edu.auburn.cardiomri.datastructure.TensionPoint;
import edu.auburn.cardiomri.datastructure.DICOMImage;
import edu.auburn.cardiomri.datastructure.Vector3d;
import edu.auburn.cardiomri.gui.ConstructImage;
import edu.auburn.cardiomri.gui.models.ImageModel;
import edu.auburn.cardiomri.gui.models.Model;
import edu.auburn.cardiomri.popupmenu.view.ContourContextMenu;
import edu.auburn.cardiomri.popupmenu.view.LandmarkContextMenu;
import edu.auburn.cardiomri.popupmenu.view.SelectContextMenu;
import edu.auburn.cardiomri.util.Mode;
import edu.auburn.cardiomri.datastructure.ControlPoint;

public class ImageView extends SingleImagePanel implements ActionListener,
        ViewInterface, Observer {
    protected Model model;
    protected JPanel imageContourPanel, panel, cPanel;
    private static final long serialVersionUID = -6920775905498293695L;
    private boolean lmrkMode = false;
    private Point clickedPoint = null;
    private Vector<Shape> redShapes = new Vector<Shape>();
    private Vector<Shape> orangeShapes = new Vector<Shape>();
    private Vector<Shape> blueShapes = new Vector<Shape>();
    private Vector<Shape> whiteShapes = new Vector<Shape>();
    /*
    private Vector<Shape> visibleShapes = new Vector<Shape>();
    private Vector<Shape> selectedShapes = new Vector<Shape>();
    private Vector<Shape> visibleTensionPointShapes = new Vector<Shape>();
    */
    private ContourControlView contourPanel; // testing
    
    public ContourContextMenu contourCM;// = ContourContextMenu.popupContextMenu(); //kw
    public LandmarkContextMenu landmarkCM; //LandmarkContextMenu()
    public SelectContextMenu selectCM; //SelectContextMenu();
    

    
    /**
     * Redraws the DICOMImage, updates the selected contour's control points,
     * and updates the set of visible contours.
     */
    public void update(Observable obs, Object obj) {
        if (obj.getClass() == DICOMImage.class) {
            this.redraw();
        }
    }
    
    public void redraw() {
    	DICOMImage dImage = getImageModel().getImage();

        dirtySource(new ConstructImage(dImage));
        /*visibleShapes.clear();
        selectedShapes.clear();
        visibleTensionPointShapes.clear();
        
        */
        
        clearShapes();
        
        updateContours(getImageModel().getContours());
        updateLandmarks(getImageModel().getLandmarks());
        
        /*updateSelectedContour(getImageModel().getSelectedContour());
        updateVisibleContours(getImageModel().getVisibleContours());
        
        updateVisibleLandmarks(getImageModel().getVisibleLandmarks());
        updateSelectedLandmark(getImageModel().getSelectedLandmark());*/
        
        colorShapes();
        
        /*this.setPreDefinedShapes(visibleShapes);
        this.setLocalizerShapes(selectedShapes);
        this.setPreDefinedShapes(visibleTensionPointShapes);
        this.setPreDefinedShapes(visibleTensionPointShapes);*/
        refresh();
    }
    
    private void clearShapes() {
    	this.redShapes.clear();
    	this.blueShapes.clear();
    	this.orangeShapes.clear();
    	this.whiteShapes.clear();
    }
    
    private void colorShapes() {
    	this.setSelectedDrawingShapes(redShapes);
    	this.setPreDefinedShapes(blueShapes);
    	this.setPersistentDrawingShapes(orangeShapes);
    	this.setLocalizerShapes(whiteShapes);
    }

    /**
     * Updates the set of control points that should be drawn onto the screen.
     * Control points are drawn as 2x2 ellipses.
     * 
     * @param contour The currently selected contour
     */
    /*private void updateSelectedContour(Contour contour) {
        
        if (contour != null) {
            for (ControlPoint controlPoint : contour.getControlPoints()) {
            	Ellipse2D ellipse = new Ellipse2D.Double(controlPoint.getX(),
                        controlPoint.getY(), 2, 2);
                selectedShapes.add(ellipse);
            }
            updateTensionPoints(contour);
        }
    }*/
    
    private void updateLandmarks(Vector<Landmark> landmarks) {
    	for (Landmark landmark: landmarks) {
    		if (landmark != null) {	
    			if (landmark.isVisible()) {
    				double x = landmark.getX();
	        		double y = landmark.getY();
	        		GeneralPath cross = new GeneralPath();
	        		//horizontal component
	        		cross.moveTo(x-1, y);
	        		cross.lineTo(x+1, y);
	        		//vertical component
	        		cross.moveTo(x,y-1);
	        		cross.lineTo(x,y+1);
	        		
	        		colorShape(cross, landmark.getColor());
    			}
        	}
    	}
    }
    
<<<<<<< HEAD
    private void updateTensionPoints(Contour contour) {
    	
    	if(contour != null) {
    		for (ControlPoint controlPoint : contour.getControlPoints()) {
    			if(contour.getControlPoints().size() > 1) {
	    			Ellipse2D ellipse = new Ellipse2D.Double(controlPoint.getTension1().getX(), controlPoint.getTension1().getY(), 2, 2);
	    			Ellipse2D ellipse2 = new Ellipse2D.Double(controlPoint.getTension2().getX(), controlPoint.getTension2().getY(), 2, 2);
	    			visibleShapes.add(ellipse);
	    			visibleShapes.add(ellipse2);
    			}
    		}
        }   
=======
    private void updateContours(Vector<Contour> contours) {
    	for (Contour contour: contours) {
    		updateContour(contour);
    	}
    }
    
    private void updateContour(Contour contour) {
    	if (contour != null) {
    		if (contour.isVisible()) {
    			
    			colorShape(contour, contour.getColor());
    		
	    		if (contour.isSelected()) {
	    			
		            for (ControlPoint controlPoint : contour.getControlPoints()) {
		            	
		            	Ellipse2D controlPointEllipse = new Ellipse2D.Double(controlPoint.getX(),
		                        controlPoint.getY(), 2, 2);
		            	
		            	TensionPoint tensionPoint1 = controlPoint.getTension1();
		            	TensionPoint tensionPoint2 = controlPoint.getTension2();
		            	
		            	Ellipse2D tensionPoint1Ellipse = new Ellipse2D.Double(tensionPoint1.getX(), tensionPoint1.getY(), 2, 2);
		    			Ellipse2D tensionPoint2Ellipse = new Ellipse2D.Double(tensionPoint2.getX(), tensionPoint2.getY(), 2, 2);
		    			
		            	colorShape(controlPointEllipse, controlPoint.getColor());
		            	colorShape(tensionPoint1Ellipse, tensionPoint1.getColor());
		            	colorShape(tensionPoint2Ellipse, tensionPoint2.getColor());
		            }
	    		}
    		}
        }
>>>>>>> dev
    }
    
    private void colorShape(Shape shape, Color color) {
    	if (color == Color.BLUE) {
    		this.blueShapes.add(shape);
    	}
    	else if (color == Color.ORANGE) {
    		this.orangeShapes.add(shape);
    	}
    	else if (color == Color.RED) {
    		this.redShapes.add(shape);
    	}
    	else if (color == Color.WHITE) {
    		this.whiteShapes.add(shape);
    	}
    }
    
    /*private void updateSelectedLandmark(Landmark landmark) {
    	if (landmark != null) {	
    		double x = landmark.getCoordinates().getX();
    		double y = landmark.getCoordinates().getY();
    		GeneralPath cross = new GeneralPath();
    		//horizontal component
    		cross.moveTo(x-1, y);
    		cross.lineTo(x+1, y);
    		//vertical component
    		cross.moveTo(x,y-1);
    		cross.lineTo(x,y+1);
    		selectedShapes.add(cross);
    	}
    }*/
    

    /*private void updateVisibleLandmarks(Vector<Landmark> landmarks){
    	for (Landmark l:landmarks){
    		double x = l.getCoordinates()[0];
    		double y = l.getCoordinates()[1];
    		GeneralPath cross = new GeneralPath();
    		//horizontal component
    		cross.moveTo(x-1, y);
    		cross.lineTo(x+1, y);
    		//vertical component
    		cross.moveTo(x,y-1);
    		cross.lineTo(x,y+1);
    		visibleTensionPointShapes.add(cross);
    	}
    }*/
    

    /**
     * Updates the list of contours to be drawn onto the screen
     * 
     * @param contours List of Contour objects
     */
    private void updateVisibleContours(Vector<Contour> contours) {
        this.setSelectedDrawingShapes(contours);
        //contourPanel.refreshView(contours); //KW
    }


    /**
     * This is copy/pasted from the View class.
     */
    public void setModel(Model model) {
        this.model = model;
        this.model.addObserver(this);
    }

    protected static ImageModel imageModel; //kw
    
    public ImageModel getImageModel() {
    	imageModel = (ImageModel) this.model;
        return (ImageModel) this.model;
    }
    
    public static ImageModel getImageModelStatic(){ //kw
    	return imageModel;
    }

    public Model getModel() {
        return this.model;
    }

    /**
     * Returns a JPanel with this ImageView as its only element
     */
    public JPanel getPanel() {
        panel = new JPanel();
        panel.setSize(200, 200);
        panel.setLayout(new GridLayout(1, 1));
        panel.setBackground(Color.BLACK);
        panel.add(this);
        panel.setFocusable(true);
        addKeyBindings(panel);
        return panel;
    }
    

    public void refresh() {
        this.revalidate();
        this.repaint();
    }

    public ImageView(ConstructImage sImg) {
        super(sImg);
    }

    /**************************************************************************
     * Handle mouse click events. 
     * Depending on which ever mode you are in the mouse click will react
     * differently.
     * Mode 1: ContourMode
     * Mode 2: LandmarkMode
     * Mode 3: SelectMode
     *************************************************************************/
    public void mouseClicked(MouseEvent e) {

    	int mode = Mode.getMode(); //kw
    	
    	//System.out.println("MODE : " + mode);
    	
    	java.awt.geom.Point2D mouseClick =  getImageCoordinateFromWindowCoordinate(e.getX(), e.getY());
    	
    	if(mode == 1){ 
    		// CONTOUR MODE
    		if(SwingUtilities.isLeftMouseButton(e)){
	    		if (!getImageModel().addControlPoint(mouseClick.getX(),mouseClick.getY())) {
	                System.err.println("currentContour is null");
	            }
    		}
    		else if(SwingUtilities.isRightMouseButton(e)){
    			contourCM = new ContourContextMenu(getImageModel()); //open context menu
    		}
    	} 
    	else if (mode == 2){ 
    		//landmark mode
    		if (SwingUtilities.isLeftMouseButton(e)) {    			
    			Mode.setMode(Mode.selectMode());
            	
            	getImageModel().addLandmarkToImage(new Landmark(Mode.getNextLandmarkType(), mouseClick.getX(), mouseClick.getY()));
            	lmrkMode = false;
    		}
    		else if(SwingUtilities.isRightMouseButton(e)){
    			landmarkCM = new LandmarkContextMenu(getImageModel());
    		}
    		
    	}
    	else { 
    		// SELECT MODE
    		 if (SwingUtilities.isLeftMouseButton(e)) {
    			 getImageModel().selectClosestAnnotation(mouseClick.getX(), mouseClick.getY());
    	     } 
    		 else if(SwingUtilities.isRightMouseButton(e)){
    			 selectCM = new SelectContextMenu(getImageModel());
    		 }

    	} 
        this.panel.requestFocusInWindow();
    } // END MOUSE CLICK
    
    /**************************************************************************
     * MouseMoved checks and see if cursor is in the contextmenus.
     * if mouse is not the in the same range as a menu close menu
     * @param MouseEvent
     *************************************************************************/
    public void mouseMoved(MouseEvent e){

    	if(contourCM != null && !(contourCM.isInBox())){
    		contourCM.setVisible(false);
    	}
    	else if( landmarkCM != null && !(landmarkCM.isInBox())){
    		landmarkCM.setVisible(false);
    	}
    	else if( selectCM != null && !(selectCM.isInBox())){
    		selectCM.setVisible(false);
    	}
    }
    
    
    
    public void mouseDragged(MouseEvent e) {
    	java.awt.geom.Point2D mouseClick = getImageCoordinateFromWindowCoordinate(e.getX(), e.getY());
    	
    	
        if(!lmrkMode) {
        	if (clickedPoint != null && clickedPoint.getClass() == ControlPoint.class) {
        		getImageModel().getSelectedContour().moveContourPoint(mouseClick.getX(), mouseClick.getY(), (ControlPoint)clickedPoint);
        	}
        	else if (clickedPoint != null && clickedPoint.getClass() == TensionPoint.class) {
        		getImageModel().getSelectedContour().moveTensionPoint(mouseClick.getX(), mouseClick.getY(), (TensionPoint)clickedPoint);
        	}
        	else if (clickedPoint != null && clickedPoint.getClass() == Landmark.class) {
        		((Landmark) clickedPoint).moveLandmark(mouseClick.getX(),mouseClick.getY());
        		//add this code to the landmark drag when created
        	}
        	else {
        		super.mouseDragged(e);
        	}
        	/*if(cPointD >= 0) {
        		getImageModel().getSelectedContour().moveContourPoint(mouseClick.getX(), mouseClick.getY(), cPointD);
        	}
        	else if(tPointD >= 0) {
        		getImageModel().getSelectedContour().moveTensionPoint(mouseClick.getX(), mouseClick.getY(), tPointD);	
        	}
        	else {
        		super.mouseDragged(e);
        		//add this code to the landmark drag when created
        	}*/
        	
        	// Forces updating of control and tension points during dragging
        	this.redraw();
        }
        this.panel.requestFocusInWindow();
    }

    /**
     * This is so every time a mouse event is processed through the Image
     * panels, the arrow keys will work as well.
     * 
     */
    public void mouseReleased(MouseEvent e) {
    	clickedPoint = null;
        this.panel.requestFocusInWindow();
    }

 
  
    
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

    public void mousePressed(MouseEvent e) {

    	java.awt.geom.Point2D mouseClick = getImageCoordinateFromWindowCoordinate(e.getX(), e.getY());
    	
    	clickedPoint = getImageModel().findNearestPointWithinRange(mouseClick.getX(), mouseClick.getY(), 3);
    	getImageModel().selectClosestAnnotation(mouseClick.getX(), mouseClick.getY());
    	super.mousePressed(e);
    	
    	this.panel.requestFocusInWindow();
    }

    private void addKeyBindings(JPanel panel) {
        panel.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "left");
        panel.getActionMap().put("left", this.new ArrowKeyAction("left"));

        panel.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "right");
        panel.getActionMap().put("right", this.new ArrowKeyAction("right"));

        panel.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
        panel.getActionMap().put("down", this.new ArrowKeyAction("down"));

        panel.getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
        panel.getActionMap().put("up", this.new ArrowKeyAction("up"));

        int commandKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        panel.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_E, commandKey), "LV ENDO");
        panel.getActionMap().put("LV ENDO",
                this.new ControlKeyAction("LV ENDO", this));

        panel.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_R, commandKey), "LV EPI");
        panel.getActionMap().put("LV EPI",
                this.new ControlKeyAction("LV EPI", this));

        panel.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, commandKey),
                "Delete Contour");
        panel.getActionMap().put("Delete Contour",
                this.new ControlKeyAction("Delete Contour", this));

        panel.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_D, commandKey
                        | InputEvent.SHIFT_MASK), "Delete Contours");
        panel.getActionMap().put("Delete Contours",
                this.new ControlKeyAction("Delete All Contours", this));

        panel.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_G, commandKey
                        | InputEvent.SHIFT_MASK), "Hide Contours");
        panel.getActionMap().put("Hide Contours",
                this.new ControlKeyAction("Hide Contours", this));

        panel.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_G, commandKey),
                "Hide Contour");
        panel.getActionMap().put("Hide Contour",
                this.new ControlKeyAction("Hide Contour", this));

        panel.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, commandKey),
                "Show Contours");
        panel.getActionMap().put("Show Contours",
                this.new ControlKeyAction("Show Contours", this));
    }

    /**
     * Action key event for all of the arrow buttons. Has to be sent over to
     * workspaceView
     * 
     * @author Ben Gustafson
     *
     */
    public class ArrowKeyAction extends AbstractAction {
        private static final long serialVersionUID = 6612132766001531904L;
        private String comand;

        public ArrowKeyAction(String cmd) {
            this.comand = cmd;
        }

        public void actionPerformed(ActionEvent e) {
            getImageModel().arrowAction(
                    new ActionEvent(panel, (int) ActionEvent.ACTION_PERFORMED,
                            this.comand));
        }
    }

    /**
     * Action key event for all of the arrow buttons. Has to be sent over to
     * workspaceView
     * 
     * @author Ben Gustafson
     *
     */
    public class ControlKeyAction extends AbstractAction {
        private static final long serialVersionUID = 6612132766001531904L;
        private String comand;
        private ImageView imageView;

        public ControlKeyAction(String cmd, ImageView imageView) {
            this.comand = cmd;
            this.imageView = imageView;
        }

        public void actionPerformed(ActionEvent e) {
            imageView.actionPerformed(new ActionEvent(panel,
                    (int) ActionEvent.ACTION_PERFORMED, this.comand));
        }
    }



}