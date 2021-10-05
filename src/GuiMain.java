import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import java.awt.*;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;
import javax.swing.SwingUtilities;
import javafx.embed.swing.SwingNode;

import javafx.scene.Cursor;


public class GuiMain extends Application {
    @FXML
    //public Menu fileMenu;
    public StackPane canvasPane;
    public StackPane miniMap;

    public BorderPane borderPane;
    public MenuBar menuBar;
    public HBox rightPane;
    public HBox leftPane;
    public AnchorPane bottomPane;
    //public AnchorPane leftPane;
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

    private Controller controller;

    private boolean dragging;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
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
                //plantText.setText(controller.getSelectedPlantText());
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
        
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("guiMain.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //primaryStage.setResizable(false);

        primaryStage.setTitle("EcoViz");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void openFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile==null){
            System.out.println("null");
        }
        else {
            closeFile();
            controller = new Controller(selectedFile, rangeSlider,tfLow,tfHigh);
            controller.addCanvases(canvasPane);
            controller.generateMinimap(miniMap);

            setCanvasPane();
            controller.addCanvases(canvasPane);
            controller.generateMinimap(miniMap);
        }
        openFilter();
        rangeSlider.setController(controller);
        rangeSlider.addListener();
    }

    public void closeFile(){
        controller=null;
        canvasPane.getChildren().clear();
        infoBox.getChildren().clear();
        miniMap.getChildren().clear();
    }

    private void setCanvasPane(){
        canvasPane.setPrefWidth(borderPane.getWidth()-rightPane.getWidth()-leftPane.getWidth());
        canvasPane.setPrefHeight(borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight());

        controller.updateZoom();
    }

    public void openFilter(){
        infoBox.getChildren().clear();
        if (controller!=null){
            for(int i=0; i<controller.getNumSpecies();i++){
                controller.addFilter(i,infoBox);
            }
        }
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> swingNode.setContent(rangeSlider));
    }

}