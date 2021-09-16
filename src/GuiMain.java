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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
    public SplitPane rightPane;
    public AnchorPane bottomPane;
    public VBox infoBox;
    public Label positionLabel;
    public TextField heightTextField;
    public Slider heightSlider;
    public HBox hbox;
    public RangeSlider rangeSlider;

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
        hbox.getChildren().add(rangeSliderNode);
        borderPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                borderPane.setPrefWidth((double)newValue);

                if (controller!=null){
                    double newX = (double)newValue-rightPane.getWidth();
                    double newY = borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight();
                    float yDimension = controller.getyDimension();
                    float xDimension = controller.getxDimension();
                    if ((newX/xDimension) <  (newY/yDimension) ){
                        canvasPane.setPrefWidth(newX);
                        canvasPane.setPrefHeight((yDimension*newX)/xDimension);
                    }
                }
            }
        });
        borderPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                borderPane.setPrefHeight((double) newValue);

                if (controller!=null){
                    double newY = (double)newValue-bottomPane.getHeight()-menuBar.getHeight();
                    double newX = borderPane.getWidth()-rightPane.getWidth();
                    float yDimension = controller.getyDimension();
                    float xDimension = controller.getxDimension();
                    if ((newY/yDimension) < (newX/xDimension) ){
                        canvasPane.setPrefHeight(newY);
                        canvasPane.setPrefWidth((xDimension*newY)/yDimension);
                    }
                }
            }
        });

        canopySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (controller!=null){
                    controller.changeCanopyOpacity(newValue.floatValue());
                }
            }
        });
        heightSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (controller!=null){
                    // float temp = controller.sliderToHeight(newValue.floatValue());
                    // System.out.println(newValue.floatValue());
                    // controller.heightFilter(temp);
                    // //DecimalFormat df = new DecimalFormat("#.##");
                    // heightTextField.setText(Float.toString(temp));
                    // // df.format(temp)
                }
            }
        });
        heightTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //if (Pattern.matches("[0-9]*.[0-9]*", newValue)) {
                    //float temp = controller.heightToSlider(Float.parseFloat(newValue));
                    //controller.heightFilter(Float.parseFloat(newValue));
                    //heightSlider.setValue(temp);
               // }
            
                    
                
            }
        });
        undergrowthSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (controller!=null){
                    controller.changeUndergrowthOpacity(newValue.floatValue());
                }
            }
        });


        canvasPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("MOUSE PRESSED");
                controller.setPan((float) event.getX(), (float) event.getY());
            }
        });

        canvasPane.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                canvasPane.setCursor(Cursor.MOVE);
                dragging = true;
                controller.panning((float) event.getX(), (float) event.getY());

            }

        });
        canvasPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                canvasPane.setCursor(Cursor.DEFAULT);
            }
        });
        canvasPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(controller!=null && !dragging){
                    controller.getPlant((float)event.getX(), (float)event.getY());
                }
                dragging = false;
            }
        });
        canvasPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(controller!=null){
                    float[] pos = controller.screenToWorld((float)event.getX(),(float)event.getY());
                    positionLabel.setText(pos[0]+" , "+pos[1]);
                }
            }
        });

        canvasPane.setOnScroll(new EventHandler<ScrollEvent>(){
            @Override
            public void handle(ScrollEvent event) {
                //System.out.println("Scroll Event Y: " + event.getDeltaY());
                controller.zooming(event);
            }

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
            controller = new Controller(selectedFile, rangeSlider);
            controller.addCanvases(canvasPane);
            controller.generateMinimap(miniMap);

            double newY = borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight();
            double newX = borderPane.getWidth()-rightPane.getWidth();
            float yDimension = controller.getyDimension();
            float xDimension = controller.getxDimension();
            if ((newY/yDimension) < (newX/xDimension) ){
                canvasPane.setPrefHeight(newY);
                canvasPane.setPrefWidth((xDimension*newY)/yDimension);
            }
            else if ((newX/xDimension) <  (newY/yDimension) ){
                canvasPane.setPrefWidth(newX);
                canvasPane.setPrefHeight((yDimension*newX)/xDimension);
            }
            else{
                canvasPane.setPrefWidth(newX);
                canvasPane.setPrefHeight(newY);
            }
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

    public void openFilter(){
        infoBox.getChildren().clear();
        if (controller!=null){
            for(int i=0; i<controller.getNumSpecies();i++){
                controller.addFilter(i,infoBox);
            }
        }
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(rangeSlider);
            }
        });
    }

}