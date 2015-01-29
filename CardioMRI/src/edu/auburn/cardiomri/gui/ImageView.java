package edu.auburn.cardiomri.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.MouseListener;
import java.util.Observable;

import javax.swing.JPanel;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.display.SingleImagePanel;
import com.pixelmed.display.SourceImage;

import edu.auburn.cardiomri.datastructure.DICOMImage;

public class ImageView implements java.util.Observer {
	
	private JPanel panel;
	private MouseListener mouseListener;
	private SingleImagePanel sIP;

	// Observer methods
	public void update(Observable obs, Object obj) {
		if (obj.getClass() == DICOMImage.class) {
//System.out.println("ImageView : update with DICOMImage");
			
			this.panel.removeAll();
			
			this.sIP = null;
			
			DICOMImage dImage = ((DICOMImage) obj);
			
			SourceImage sImage = null;
			try {
				sImage = new SourceImage(dImage);
				
				this.sIP = new SingleImagePanel(sImage);
				
				this.panel.revalidate();
				
				
			} catch (DicomException e) {
				e.printStackTrace();
			}
			
			this.sIP.addMouseListener(this.mouseListener);
			
			SingleImagePanel.deconstructAllSingleImagePanelsInContainer(this.panel);
			this.panel.removeAll();
			this.panel.add(sIP);
			
			this.panel.revalidate();
		}
	}
	
	// Setters
	/*
	 * Sets the class' mouseListener attribute.
	 * 
	 *  @param mL : MouseListener object that is used as the class'
	 *  mouseListener attribute.
	 */
	public void setMouseListener(MouseListener mL) {
		this.mouseListener = mL;
	}
	
	// Getters
	/*
	 * Returns the class panel attribute.
	 * 
	 *  @return		The class' panel attribute.
	 */
	public JPanel getPanel() { return this.panel; }
	
	// Constructors
	public ImageView() {
//System.out.println("ImageView()");
		
		this.panel = new JPanel();
		this.panel.setLayout(new GridLayout(1,1));
		this.panel.setBackground(Color.BLACK);
		this.panel.setOpaque(true);
		
		this.panel.setVisible(true);
	}
	
}