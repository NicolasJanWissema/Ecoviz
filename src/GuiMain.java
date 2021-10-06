import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import javax.swing.*;

import javafx.embed.swing.SwingNode;
import javafx.scene.Cursor;

/**
 * This class is the main method and controls the GUI
 * 
 * @author WSSNIC008 KRNHAN003 JCBSHA028
 */
public class GuiMain extends Application {
    @FXML
    //public Menu fileMenu;
    public StackPane canvasPane;
    public StackPane miniMap;
    public BorderPane borderPane;
    public ProgressBar loadingBar;
    public MenuBar menuBar;
    public AnchorPane rightPane;
    public AnchorPane leftPane;
    public AnchorPane bottomPane;
    public Separator leftSeparator;
    public Separator rightSeparator;
    public TextArea plantText;
    public VBox infoBox;
    public Label positionLabel;
    public HBox hbox;
    public RangeSlider rangeSlider;
    public HBox textFieldHbox;
    public Menu fileMenu;
    TextField tfLow;
    TextField tfHigh;
    public Slider canopySlider;
    public Slider undergrowthSlider;

    // Private Varibles
    private Controller controller;
    private boolean dragging;
    
    /**
     * Main Method
     * Launches the GUI
     * 
     * @param args default argument
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @FXML
    /**
     * This method runs right before the GUI is launched
     * Set the default varibles for the GUI and handles the event listeners
     */
    public void initialize(){
        rangeSlider = new RangeSlider();
        rangeSlider.setPreferredSize(new Dimension(240, rangeSlider.getPreferredSize().height));
        rangeSlider.setMinimum(0);
        rangeSlider.setMaximum(50);
        rangeSlider.setValue(0);
        rangeSlider.setUpperValue(50);
        SwingNode rangeSliderNode = new SwingNode();
        createSwingContent(rangeSliderNode);
        tfLow = new TextField();
        tfHigh = new TextField();
        Pane spacerPane = new Pane();
        spacerPane.setPrefHeight(tfLow.getHeight());
        spacerPane.setPrefWidth(200);
        hbox.getChildren().addAll(rangeSliderNode);
        textFieldHbox.getChildren().addAll(tfLow,spacerPane,tfHigh);


        borderPane.widthProperty().addListener((observable, oldValue, newValue) -> {
            borderPane.setPrefWidth((double)newValue);

            if (controller!=null){
                canvasPane.setPrefWidth((double)newValue-rightPane.getWidth()-leftPane.getWidth());
                canvasPane.setPrefHeight(borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight());

                controller.updateZoom();
            }
        });

        borderPane.heightProperty().addListener((observable, oldValue, newValue) -> {
            borderPane.setPrefHeight((double) newValue);
            rightPane.setPrefHeight(borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight());
            leftPane.setPrefHeight(borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight());

            if (controller!=null){
                canvasPane.setPrefWidth(borderPane.getWidth()-rightPane.getWidth()-leftPane.getWidth());
                canvasPane.setPrefHeight((double)newValue-bottomPane.getHeight()-menuBar.getHeight());

                controller.updateZoom();
            }
        });
        
        canopySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (controller!=null){
                controller.changeCanopyOpacity(newValue.floatValue());
            }
        });
        undergrowthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (controller!=null){
                controller.changeUndergrowthOpacity(newValue.floatValue());
            }
        });


        canvasPane.setOnMousePressed(event -> {
            //System.out.println("MOUSE PRESSED");
            controller.setPan((float) event.getX(), (float) event.getY());
        });
        canvasPane.setOnMouseDragged(event -> {
            if (canvasPane.getCursor() != Cursor.MOVE) {
                canvasPane.setCursor(Cursor.MOVE);
                //System.out.println("SET COURSER MOVE");
            }

            dragging = true;
            controller.panning((float) event.getX(), (float) event.getY());

        });
        canvasPane.setOnMouseReleased(event -> {
            canvasPane.setCursor(Cursor.DEFAULT);
            //System.out.println("SET COURSER DEFFAULT");
        });
        canvasPane.setOnMouseClicked(event -> {
            if(controller!=null && !dragging){
                controller.getPlant((float)event.getX(), (float)event.getY());
                plantText.setText(controller.getSelectedPlantText());
            }
            dragging = false;
        });
        canvasPane.setOnMouseMoved(event -> {
            if(controller!=null){
                float[] pos = controller.screenToWorld((float)event.getX(),(float)event.getY());
                positionLabel.setText(pos[0]+" , "+pos[1]);
            }
        });
        canvasPane.setOnScroll(event -> {
            //System.out.println("Scroll Event Y: " + event.getDeltaY());
            controller.zooming(event);
        });

        leftSeparator.setOnMouseDragged(event -> {
            leftPane.setPrefWidth(event.getSceneX());
            if (controller!=null){
                setCanvasPane();
            }
        });

        rightSeparator.setOnMouseDragged(event -> {
            double newWidth = borderPane.getWidth()-event.getSceneX();
            rightPane.setPrefWidth(newWidth);
            if (controller!=null){
                setCanvasPane();
            }
        });
    }


    @Override
    /**
     * This method starts the GUI using the fxml file
     * 
     * @param primaryStage Main Stage
     */
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("guiMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //primaryStage.setResizable(false);

        primaryStage.setTitle("EcoViz");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile==null){
            System.out.println("null");
        }
        else {
            closeFile();
            try{
                controller = new Controller(selectedFile,loadingBar, rangeSlider,tfLow,tfHigh);
                canvasPane.setPrefWidth(borderPane.getWidth()-rightPane.getWidth()-leftPane.getWidth());
                canvasPane.setPrefHeight(borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight());
                controller.addCanvases(canvasPane);
                controller.generateMinimap(miniMap);
            }
            catch (FileNotFoundException e){
                AnchorPane anchorPane = new AnchorPane();
                Text text = new Text(e.getMessage());
                anchorPane.getChildren().add(text);
                text.setLayoutY(20);
                Stage stage =  new Stage();
                stage.setResizable(false);
                stage.setAlwaysOnTop(true);
                stage.centerOnScreen();
                stage.setScene(new Scene(anchorPane));
                stage.setTitle("File Error");
                stage.show();
            }

        }
        openFilter();
        rangeSlider.setController(controller);
        rangeSlider.addListener();
    }

    /**
     * Closes a file
     */
    public void closeFile(){
        controller=null;
        canvasPane.getChildren().clear();
        infoBox.getChildren().clear();
        miniMap.getChildren().clear();
    }

    /**
     * Sets the width and height of the canvas
     */
    private void setCanvasPane(){
        canvasPane.setPrefWidth(borderPane.getWidth()-rightPane.getWidth()-leftPane.getWidth());
        canvasPane.setPrefHeight(borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight());
        controller.updateZoom();
    }

    /**
     * Adds filter to gui
     */
    public void openFilter(){
        infoBox.getChildren().clear();
        if (controller!=null){
            for(int i=0; i<controller.getNumSpecies();i++){
                controller.addFilter(i,infoBox);
            }
        }
    }

    /**
     * This remove the selected plant
     */
    public void deleteSelectedPlant(){
        if (controller!=null){
            controller.deleteSelectedPlant();
            plantText.setText("");
        }
    }

    public void openEditor() throws Exception {
        FileEditor fileEditor = new FileEditor();
        if(controller!=null){
            fileEditor = new FileEditor(controller);
        }
        fileEditor.start(new Stage());
    }
    public void openHelpMenu() throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HelpMenu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();

        stage.setTitle("Help Menu");
        stage.setScene(scene);
        stage.show();
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> swingNode.setContent(rangeSlider));
    }

}