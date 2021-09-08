import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import java.io.File;

import javafx.scene.Cursor;


public class GuiMain extends Application {
    @FXML
    public Menu fileMenu;
    public StackPane canvasPane;
    public StackPane miniMap;
    public BorderPane borderPane;
    public SplitPane sidePane;
    public AnchorPane bottomPane;
    public MenuBar menuBar;
    public Slider canopySlider;
    public Slider undergrowthSlider;

    private Controller controller;

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void initialize(){
        borderPane.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                borderPane.setPrefWidth((double)newValue);

                if (controller!=null){
                    double newX = (double)newValue-sidePane.getWidth();
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
                    double newX = borderPane.getWidth()-sidePane.getWidth();
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
                canvasPane.setCursor(Cursor.CLOSED_HAND);
                controller.setPan((float) event.getSceneX(), (float) event.getSceneY());
            }
        });

        canvasPane.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                controller.panning((float) event.getSceneX(), (float) event.getSceneY());

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
            controller = null;
            controller = new Controller(selectedFile);
            canvasPane.getChildren().clear();
            controller.addCanvases(canvasPane);
            controller.generateMinimap(miniMap);

            double newY = borderPane.getHeight()-bottomPane.getHeight()-menuBar.getHeight();
            double newX = borderPane.getWidth()-sidePane.getWidth();
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
    }

    public void closeFile(){
        canvasPane.getChildren().clear();
        controller=null;
    }

}