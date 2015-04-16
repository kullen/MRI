/**
 * 
 */
package edu.auburn.cardiomri.gui.views;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.auburn.cardiomri.dataimporter.DICOM3Importer;
import edu.auburn.cardiomri.dataimporter.DICOMFileTreeWalker;
import edu.auburn.cardiomri.datastructure.Contour;
import edu.auburn.cardiomri.datastructure.DICOMImage;
import edu.auburn.cardiomri.datastructure.Slice;
import edu.auburn.cardiomri.datastructure.Study;
import edu.auburn.cardiomri.datastructure.Time;
import edu.auburn.cardiomri.datastructure.Contour.Type;
import edu.auburn.cardiomri.datastructure.Study.NotInStudyException;
import edu.auburn.cardiomri.gui.GUIController;
import edu.auburn.cardiomri.util.SerializationManager;
import edu.auburn.cardiomri.util.StudyUtilities;

/**
 * @author Moniz
 *
 */
public class WorkspaceView extends View {
    protected JFileChooser fileChooser;
    protected JComponent mainComponent;
    protected JFrame appFrame;

    public WorkspaceView() {
        super();
        fileChooser = new JFileChooser();
        fileChooser
                .setCurrentDirectory(new File(System.getProperty("user.dir")));
    }

    /**
     * Sets the class' appFrame attribute.
     *
     * @param f : JFrame object that will be used as the class' appFrame
     *            attribute.
     */
    public void setAppFrame(JFrame f) {
        this.appFrame = f;
    }

    public void addView(View view) {
        // panel.add(view)
    }

    /**
     * Sets the class' mainComponent attribute. The class' KeyBindings will be
     * attached to the mainComponent.
     * 
     * @param c : JComponent object will be used as the class' mainComponent
     *            attribute.
     */
    public void setMainComponent(JComponent c) {
        this.mainComponent = c;

        this.addKeyBindings();
    }

    /**
     * Returns the class' mainComponent attribute.
     *
     * @return The GUIController's mainComponent attribute.
     */
    public JComponent getMainComponent() {
        return this.mainComponent;
    }

    // ActionListener methods
    public void actionPerformed(java.awt.event.ActionEvent e) {

        String actionCommand = e.getActionCommand();

        // System.out.println("GUIController : actionPerformed - " +
        // actionCommand);

        if (actionCommand.equals("Create New Study")) {
            this.createNewStudy(e);
        } else if (actionCommand.equals("Load Existing Study")) {
            this.loadExistingStudy(e);
        } else if (actionCommand.equals("Load Single DICOM")) {
            try {
                this.loadSingleDicom(e);
            } catch (NotInStudyException e1) {
                e1.printStackTrace();
            }
        } else if (actionCommand.equals("Save Study")) {
            this.saveStudy();
        } else if (actionCommand.equals("Save As Study")) {
            this.saveAsStudy();
        } else if (actionCommand.equals("Import DICOM")) {
            // Check to see if there is a Study to add things to
            if (this.studyStructModel.getStudy() != null) {
                this.importDicom(e);
            } else {
                // System.out.println("GUIController : failed attempt to import DICOM");
            }
        } else if (actionCommand.equals("Save Contours")) {
            this.saveContour();
        } else if (actionCommand.equals("Load Contours")) {
            try {
                this.setUpLoad();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        } else if (actionCommand.equals("Default Type")) {
            this.mainImageModel.addContourToImage(new Contour(Type.DEFAULT));
        } else if (actionCommand.equals("Closed Type")) {
            this.mainImageModel.addContourToImage(new Contour(
                    Type.DEFAULT_CLOSED));
        } else if (actionCommand.equals("Open Type")) {
            this.mainImageModel
                    .addContourToImage(new Contour(Type.DEFAULT_OPEN));
        } else if (actionCommand.equals("Delete All Contours")) {
            this.deleteAllContoursForImage();
        } else if (actionCommand.equals("Delete Selected Contour")) {
            this.deleteSelectedContour();
        } else if (actionCommand.equals("Hide Selected Contour")) {
            this.hideSelectedContour();
        } else if (actionCommand.equals("Hide Contours")) {
            this.hideContours();
        } else if (actionCommand.equals("Delete All Contours")) {
            this.deleteAllContoursForImage();
        } else if (actionCommand.equals("Show Contours")) {
            this.showContours();
        }
    }

    /**
     * Method that will close the GUIController's applicationFrame variable.
     */
    private void closeWindow() {
        // System.out.println("Close Window");
        this.appFrame.dispatchEvent(new WindowEvent(this.appFrame,
                WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Opens a JFileChooser that allows the user to select where they would like
     * to save the currently displayed Study object and what name they would
     * like to give it.
     */
    public void saveAsStudy(Study study) {
        if (study != null) {
            // System.out.println("Saving Study As...");

            JFileChooser saveFC = fileChooser;

            FileFilter studyFileFilter = new FileNameExtensionFilter(
                    "Study file (.smc)", "smc");
            saveFC.setFileFilter(studyFileFilter);

            int response = saveFC.showSaveDialog(this.mainComponent);

            if (response == JFileChooser.APPROVE_OPTION) {
                // System.out.println("Choose to save");
                String newFilename = saveFC.getSelectedFile().getAbsolutePath();
                // Incorrect file extension
                if (!newFilename.endsWith(".smc")) {
                    newFilename = newFilename.concat(".smc");
                }
                // System.out.println("filename : " + newFilename);
                try {
                    SerializationManager.save(this.studyStructModel.getStudy(),
                            newFilename);
                    this.filename = newFilename;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response == JFileChooser.CANCEL_OPTION) {
                // System.out.println("Choose to Cancel");
            }
        }
    }

    /**
     * Opens a JFileChooser that allows the user to select a Study model file
     * (.smc).
     */
    public void loadStudy() {
        FileFilter studyFilter = new FileNameExtensionFilter(
                "Study file (.smc)", "smc");
        fileChooser.setFileFilter(studyFilter);

        int response = fileChooser.showOpenDialog(this.mainComponent);
        if (response == JFileChooser.APPROVE_OPTION) {
            String fileName = fileChooser.getSelectedFile().getAbsolutePath();

            Study study = StudyUtilities.loadStudy(fileName);
            // TODO: Send this to the grid view/model
        }
    }

    /**
     * Opens a JFileChooser that allows the user to select a Directory, which
     * will then be iterated through to generate a new Study object.
     *
     * @param e : ActionEvent object that is was used to originally used to call
     *            the GUIController's actionPerformed method. (currently unused)
     */
    public void createNewStudy(ActionEvent e) {
        // System.out.println("GUIController : Create New Study");

        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int returnVal = fileChooser.showOpenDialog(this.mainComponent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            // System.out.println("FileChooser : Load File Structure");

            String directory = fileChooser.getSelectedFile().getAbsolutePath();
            Path path = Paths.get(directory);

            DICOMFileTreeWalker fileTreeWalker = new DICOMFileTreeWalker();

            Study s = fileTreeWalker.addFileTreeToStudy(path, new Study());

            // update study structure Model
            this.updateNewStudy(s);
            this.updateNewDicom();
        } else {
            // System.out.println("FileChooser : Canceled choosing directory");
        }
    }

    /**
     * Opens a JFileChooser that allows the user to select a single Dicom file
     * and generate a Study object with the Dicom as the only image in it.
     *
     * @param e : ActionEvent object that is was used to originally used to call
     *            the GUIController's actionPerformed method. (currently unused)
     */
    private void loadSingleDicom(ActionEvent e) throws NotInStudyException {
        // System.out.println("GUIController : Load Single DICOM");

        FileFilter dicomType = new FileNameExtensionFilter("DICOM file (.dcm)",
                "dcm");
        fileChooser.addChoosableFileFilter(dicomType);

        int returnVal = fileChooser.showOpenDialog(this.mainComponent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            // System.out.println("File selected");

            // filechooser.getSelectedFile() returns a file object
            String filename = fileChooser.getSelectedFile().getPath();

            // System.out.println("StudyStructureController : Chose file - " +
            // filename);

            DICOMImage dImage = DICOM3Importer.makeDICOMImageFromFile(filename);

            Study s = new Study();
            s.addImage(dImage);

            // update study structure Model
            this.updateNewStudy(s);
            this.updateNewDicom();
        } else {
            // System.out.println("GUIController : Cancel choosing file");
        }
    }

    /**
     * Opens a JFileChooser that allows the user to select a single Dicom file
     * and adds it to the existing Study object.
     *
     * @param e : ActionEvent object that is was used to originally used to call
     *            the GUIController's actionPerformed method. (currently unused)
     */
    private void importDicom(ActionEvent e) {
        // System.out.println("GUIController : Import DICOM");

        FileFilter dicomFilter = new FileNameExtensionFilter(
                "DICOM file (.dcm)", "dcm");
        fileChooser.setFileFilter(dicomFilter);

        int returnVal = fileChooser.showOpenDialog(this.mainComponent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            // System.out.println("File selected");

            // filechooser.getSelectedFile() returns a file object
            String filename = fileChooser.getSelectedFile().getPath();

            // System.out.println("StudyStructureController : Chose file - " +
            // filename);

            DICOMImage dImage = DICOM3Importer.makeDICOMImageFromFile(filename);

            Study existingStudy = this.getStudyStructModel().getStudy();
            try {
                existingStudy.addImage(dImage);
            } catch (NotInStudyException e1) {
                e1.printStackTrace();
            }

            // update study structure Model
            this.updateNewStudy(existingStudy);
            this.updateNewDicom();
        } else {
            // System.out.println("GUIController : Cancel choosing file");
        }
    }

    /**
     * deletes all of the Contours for the displayed image
     */
    private void deleteAllContoursForImage() {
        DICOMImage dImage = this.studyStructModel.getImage();
        dImage.getContours().clear();
    }

    /**
     * Saves current image's contour lines into a .txt file containing a header
     * and the X and Y coordinates of all the points along the contour
     * 
     * @param contour : Contour object to be saved
     */

    public void saveContour() {
        String path = System.getProperty("user.dir") + File.separator
                + "contourPoints.txt";
        writeContoursToFile(this.studyStructModel.getStudy()
                .getSOPInstanceUIDToDICOMImage(), path);

    }

    /**
     * reads in a text file containing lists of coordinates for Contour objects
     * for one or more images and assigns the Contours to their corresponding
     * images.
     * 
     * @throws IOException
     */

    public void setUpLoad() throws IOException {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = fileChooser.showOpenDialog(this.mainComponent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getPath());
            loadContour(file, this.studyStructModel.getStudy()
                    .getSOPInstanceUIDToDICOMImage());
        }
    }

    private void hideSelectedContour() {
        DICOMImage dImage = this.studyStructModel.getImage();
        Contour selected = this.mainImageModel.getSelectedContour();
        dImage.getContours().remove(selected);
        this.mainImageModel.getHiddenContours().add(selected);

    }

    private void hideContours() {
        DICOMImage dImage = this.studyStructModel.getImage();
        this.mainImageModel.getHiddenContours().addAll(dImage.getContours());
        dImage.getContours().clear();
    }

    private void showContours() {
        if (this.mainImageModel.getHiddenContours().size() != 0) {
            DICOMImage dImage = this.studyStructModel.getImage();
            dImage.getContours().addAll(
                    (Vector<Contour>) this.mainImageModel.getHiddenContours());
            this.mainImageModel.getHiddenContours().clear();
        }
    }

    private void deleteSelectedContour() {
        DICOMImage dImage = this.studyStructModel.getImage();
        dImage.getContours().remove(this.mainImageModel.getSelectedContour());
    }

    // TODO: move key binding to GridView
    // TODO: move key binding to GridView
    // TODO: move key binding to GridView
    // TODO: move key binding to GridView
    // TODO: move key binding to GridView
    // TODO: move key binding to GridView
    // TODO: move key binding to GridView
    // TODO: move key binding to GridView

    /**
     * Adds common KeyBindings (Ctrl+S, Ctrl+Shift+S, Ctrl+O, etc.) to the
     * class' mainComponent attribute.
     */
    private void addKeyBindings() {
        // Need to map KeyBindings
        this.mainComponent.getInputMap().put(KeyStroke.getKeyStroke("LEFT"),
                "left");
        this.mainComponent.getActionMap().put("left", new LeftKeyAction(this));

        this.mainComponent.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"),
                "right");
        this.mainComponent.getActionMap()
                .put("right", new RightKeyAction(this));

        this.mainComponent.getInputMap().put(KeyStroke.getKeyStroke("DOWN"),
                "down");
        this.mainComponent.getActionMap().put("down", new DownKeyAction(this));

        this.mainComponent.getInputMap()
                .put(KeyStroke.getKeyStroke("UP"), "up");
        this.mainComponent.getActionMap().put("up", new UpKeyAction(this));

        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask());
        this.mainComponent.getInputMap().put(ctrlS, "save");
        this.mainComponent.getActionMap().put("save", new CtrlSAction(this));

        KeyStroke ctrlShiftS = KeyStroke.getKeyStroke(KeyEvent.VK_S, 21);
        this.mainComponent.getInputMap().put(ctrlShiftS, "save as");
        this.mainComponent.getActionMap().put("save as",
                new CtrlShiftSAction(this));

        KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask());
        this.mainComponent.getInputMap().put(ctrlO, "open existing");
        this.mainComponent.getActionMap().put("open existing",
                new CtrlOAction(this));

        KeyStroke ctrlW = KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit
                .getDefaultToolkit().getMenuShortcutKeyMask());
        this.mainComponent.getInputMap().put(ctrlW, "close");
        this.mainComponent.getActionMap().put("close", new CtrlWAction(this));
    }

    /**
     * Decrements the current time index and updates the models.
     */
    private void decrementTimeIndex() {
        // Check to see if it is possible
        if (this.studyStructModel.getStudy() == null) {
            return;
        }
        if ((this.tIndex - 1) >= 0) {
            // then decrement
            // System.out.println("GUIController : decrement time index");
            this.tIndex--;
            this.updateNewDicom();
        } else {
            this.tIndex = this.studyStructModel.getStudy().getGroups()
                    .get(gIndex).getSlices().get(sIndex).getTimes().size() - 1;
        }
    }

    /**
     * Increments the current time index and updates the models.
     */
    private void incrementTimeIndex() {
        // Check to see if it is possible
        if (this.studyStructModel.getStudy() == null) {
            return;
        }
        ArrayList<Time> curTimes = this.studyStructModel.getStudy().getGroups()
                .get(gIndex).getSlices().get(sIndex).getTimes();
        if ((this.tIndex + 1) < curTimes.size()) {
            // then increment
            // System.out.println("GUIController : increment time index");
            this.tIndex++;
            this.updateNewDicom();
        } else {
            // Loop
            this.tIndex = 0;
        }
    }

    /**
     * Decrements the current slice index and updates the models.
     */
    private void decrementSliceIndex() {
        // Check to see if it is possible
        if (this.studyStructModel.getStudy() == null) {
            return;
        }
        if (this.sIndex > 0) {
            // then increment
            // System.out.println("GUIController : decrement slice index");
            this.sIndex--;
            this.updateNewDicom();
        } else {
            // System.out.println("GUIController : failed attempt to decrement slice");
        }
    }

    /**
     * Increments the current slice index and updates the models.
     */
    private void incrementSliceIndex() {
        // Check to see if it is possible
        if (this.studyStructModel.getStudy() == null) {
            return;
        }
        ArrayList<Slice> curSlices = this.studyStructModel.getStudy()
                .getGroups().get(gIndex).getSlices();
        if ((this.sIndex + 1) < curSlices.size()) {
            // then increment
            // System.out.println("GUIController : increment slice index " +
            // curSlices.size());
            this.sIndex++;
            this.updateNewDicom();
        } else {
            // System.out.println("GUIController : failed attempt to increment slice");
        }
    }

    // Action classes
    private class LeftKeyAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.decrementTimeIndex();
        }

        // Constructor
        public LeftKeyAction(GUIController c) {
            this.controller = c;
        }
    }

    private class RightKeyAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.incrementTimeIndex();
        }

        // Constructor
        public RightKeyAction(GUIController c) {
            this.controller = c;
        }

    }

    private class UpKeyAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.decrementSliceIndex();
        }

        // Constructor
        public UpKeyAction(GUIController c) {
            this.controller = c;
        }
    }

    private class DownKeyAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.incrementSliceIndex();
        }

        // Constructor
        public DownKeyAction(GUIController c) {
            this.controller = c;
        }
    }

    private class CtrlSAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.saveStudy();
        }

        // Constructor
        public CtrlSAction(GUIController c) {
            this.controller = c;
        }
    }

    private class CtrlShiftSAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.saveAsStudy();
        }

        // Constructor
        public CtrlShiftSAction(GUIController c) {
            this.controller = c;
        }
    }

    private class CtrlOAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.loadExistingStudy(e);
            ;
        }

        // Constructor
        public CtrlOAction(GUIController c) {
            this.controller = c;
        }
    }

    private class CtrlWAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private GUIController controller;

        // AbstractAction methods
        public void actionPerformed(ActionEvent e) {
            this.controller.closeWindow();
        }

        // Constructor
        public CtrlWAction(GUIController c) {
            this.controller = c;
        }
    }
}
