package edu.auburn.cardiomri;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import edu.auburn.cardiomri.gui.*;

public class RunMVC {
	
	public RunMVC() {
		
		GUIController guiController = new GUIController();
		
		// StudyStructure bit
		StudyStructureModel studyStructModel = new StudyStructureModel();
		StudyStructureView studyStructView = new StudyStructureView();
		
		studyStructModel.addObserver(studyStructView);
		
		guiController.setStudyStruct(studyStructModel, studyStructView);
		
		studyStructView.addController(guiController);
		
		// Grid bit
		GridModel gridModel = new GridModel();
		GridView gridView = new GridView();
		
		gridModel.addObserver(gridView);
		
		guiController.setGrid(gridModel, gridView);
		
		//gridView.addController(guiController);
		
		// MetaData bit
		MetaDataModel metaDataModel = new MetaDataModel();
		MetaDataView metaDataView = new MetaDataView();
		
		studyStructModel.addObserver(studyStructView);
		metaDataModel.addObserver(metaDataView);
		
		guiController.setMetaData(metaDataModel, metaDataView);
		
		//metaDataView.addController(guiController);
		
		// Image bit
		ImageModel imageModel = new ImageModel();
		ImageModel imageModelSmallLeft = new ImageModel();
		ImageModel imageModelSmallRight = new ImageModel();
		
		ImageView imageView = new ImageView();
		ImageView imageViewSmallLeft = new ImageView();
		ImageView imageViewSmallRight = new ImageView();
		
		imageModel.addObserver(imageView);
		imageModelSmallLeft.addObserver(imageViewSmallLeft);
		imageModelSmallRight.addObserver(imageViewSmallRight);
		
		guiController.setImageViewer(imageModel, imageView);
		guiController.setImageViewerSmallLeft(imageModelSmallLeft,  imageViewSmallLeft);
		guiController.setImageViewerSmallRight(imageModelSmallRight, imageViewSmallRight);
				
		
		//imageView.addController(guiController);
		
		// Defining GUI
		JFrame frame = new JFrame("Cardio MRI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 600);
		
		JSplitPane structAndGridPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				studyStructView.getPanel(), gridView.getPanel());
		structAndGridPane.setDividerLocation(270);
		
		// set the size of the grid view
		gridView.setSize(new Dimension(300, 200));

		//JSplitPane structGridAndAttr = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				//structAndGridPane, metaDataView.getPanel());
		//structGridAndAttr.setDividerLocation(200);
		
		JSplitPane structImagePane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				structAndGridPane, imageViewSmallLeft.getPanel());
		structImagePane2.setDividerLocation(200);
		
		JSplitPane splitPaneWithImage = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				structImagePane2, imageView.getPanel());
		splitPaneWithImage.setDividerLocation(600);
		
		JSplitPane structImagePane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				splitPaneWithImage, imageViewSmallRight.getPanel());
		//structImagePane3.setSize(new Dimension(300, 200));
		//structImagePane3.setDividerLocation(300);
		//structImagePane3.setLeftComponent(structImagePane2);
		
		
		frame.add(splitPaneWithImage);
		//frame.add(structImagePane2);
		//frame.add(structImagePane3);
		
		
		guiController.setMainComponent(splitPaneWithImage);
		//guiController.setMainComponent(structImagePane2);
		//guiController.setMinorComponent(structImagePane3);
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		// New menu
		JMenu newMenu = new JMenu("New Study");
		
			JMenuItem newFromSingle = new JMenuItem("From Single DICOM");
			newFromSingle.setActionCommand("Load Single DICOM");
			newFromSingle.addActionListener(guiController);
			
			JMenuItem newFromFileStruct = new JMenuItem("From File Structure");
			newFromFileStruct.setActionCommand("Create New Study");
			newFromFileStruct.addActionListener(guiController);
			
			newMenu.add(newFromSingle);
			newMenu.add(newFromFileStruct);
		
		// Open study
		JMenuItem openExisting = new JMenuItem("Open Existing (Ctrl+O)");
		openExisting.setActionCommand("Load Existing Study");
		openExisting.addActionListener(guiController);
		
		// Save
		JMenuItem saveStudy = new JMenuItem("Save (Ctrl+S)");
		saveStudy.setActionCommand("Save Study");
		saveStudy.addActionListener(guiController);
		
		JMenuItem saveAsStudy = new JMenuItem("Save as (Ctrl+Shift+S)");
		saveAsStudy.setActionCommand("Save As Study");
		saveAsStudy.addActionListener(guiController);
		
		// Import
		JMenuItem importDicom = new JMenuItem("Import DICOM");
		importDicom.setActionCommand("Import DICOM");
		importDicom.addActionListener(guiController);
		
		JMenu view = new JMenu("View");
		
		JMenuItem zoom = new JMenuItem("Zoom");
		
		
		JMenu contours = new JMenu("Contours");
		
		JMenuItem saveContours = new JMenuItem("Save Contours (.txt File)");
		saveContours.setActionCommand("Save Contours");
		saveContours.addActionListener(guiController);
		
		JMenuItem loadContours = new JMenuItem("Load Contours");
		loadContours.setActionCommand("Load Contours");
		loadContours.addActionListener(guiController);
		
		JMenuItem deleteContourAxis = new JMenuItem("Delete Contour Axis");
		deleteContourAxis.setActionCommand("Delete Contour Axis");
		deleteContourAxis.addActionListener(guiController);
		
		JMenuItem deleteContour = new JMenuItem("Delete Contour");
		deleteContour.setActionCommand("Delete Contour");
		deleteContour.addActionListener(guiController);
		
		JMenuItem deleteAllContours = new JMenuItem("Delete All Contours");
		deleteAllContours.setActionCommand("Delete All Contours");
		deleteAllContours.addActionListener(guiController);
		
		contours.add(saveContours);
		contours.add(loadContours);
		contours.add(deleteContourAxis);
		contours.add(deleteContour);
		contours.add(deleteAllContours);
		view.add(zoom);
		
		menuBar.add(contours);
		menuBar.add(view);
		
		fileMenu.add(newMenu);
		fileMenu.add(openExisting);
		fileMenu.add(saveStudy);
		fileMenu.add(saveAsStudy);
		fileMenu.add(importDicom);
		
		guiController.setAppFrame(frame);
		
		frame.setVisible(true);
	}
	
}