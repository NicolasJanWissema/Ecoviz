import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.io.File;

import javafx.scene.control.Label;
import javafx.scene.control.Button;


public class GUI extends Application {
    @FXML
    // regular grid of height values
    Group root;

    // Main Class Globals
    private Plants plants;
    private Terrain terrain;
    private SpeciesInfo[] speciesInfo;
    private FireSim fireSim;
    public Label label;
    public Button button;
    public StackPane anchorPane;
    public ResizableCanvas terrainCanvas;
    public ResizableCanvas undergrowthCanvas;
    public ResizableCanvas canopyCanvas;
    public Slider undergrowthSlider;
    public Slider canopySlider;
    public Slider zoomSlider;
    public VBox filterPlaceholder;
    public CheckBox[] filterBoxes;

    int dimx, dimy; // data dimension density

    class ResizableCanvas extends Canvas {

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void dataGen() {
        long startTime = System.nanoTime();
        readFiles("Data/S2000-2000-512");
        long endTime = System.nanoTime();
        //System.out.println("TIME TO READ FILES: " + ((endTime-startTime)/1000000));
        dimx = terrain.dimx;
        dimy = terrain.dimy;
    }

    @FXML
    public void initialize() {
        dataGen();

        anchorPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        anchorPane.setPrefWidth(dimx);
        anchorPane.setPrefHeight(dimy);
        terrainCanvas = new ResizableCanvas();
        undergrowthCanvas = new ResizableCanvas();
        canopyCanvas = new ResizableCanvas();

        anchorPane.getChildren().addAll(terrainCanvas, undergrowthCanvas, canopyCanvas);
        terrain.deriveImageCanvas(terrainCanvas);
        plants.getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
        plants.getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);
        //System.out.println(speciesInfo.length);

        //Generate filter buttons
        filterBoxes = new CheckBox[speciesInfo.length];
        for(int i=0; i<filterBoxes.length; i++){
            filterBoxes[i]= new CheckBox(speciesInfo[i].getCommmonName());
            filterBoxes[i].selectedProperty().set(true);

            filterBoxes[i].selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (observable.getValue().booleanValue()){
                        for (int j= 0; j < filterBoxes.length; j++) {
                            if (filterBoxes[j].isSelected()) {
                                plants.unFilterSpecies(j);
                            }
                        }
                        plants.getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
                        plants.getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);
                        //System.out.println("On");
                    }
                    else {
                        for (int j= 0; j < filterBoxes.length; j++) {
                            if (!filterBoxes[j].isSelected()) {
                                plants.filterSpecies(j);
                            }
                        }
                        plants.getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
                        plants.getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);
                        //System.out.println("Off");
                    }
                }
            });
            filterPlaceholder.getChildren().add(filterBoxes[i]);
        }

        undergrowthSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                undergrowthCanvas.opacityProperty().set(undergrowthSlider.getValue());
            }
        });

        canopySlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                canopyCanvas.opacityProperty().set(canopySlider.getValue());
            }
        });

        anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            }
        });

        anchorPane.setCursor(Cursor.OPEN_HAND);
        anchorPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                label.setText(event.getSceneX()+", "+event.getSceneY());
            }
        });
        anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorPane.setCursor(Cursor.CLOSED_HAND);
            }
        });
        anchorPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorPane.setCursor(Cursor.OPEN_HAND);
            }
        });

        ChangeListener<Number> stageSizeListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                //System.out.println("Height: " + anchorPane.getHeight() + " Width: " + anchorPane.getWidth());
                float ratioX = ((float)anchorPane.getWidth())/(terrain.getDimensions()[0]* terrain.getGridSpacing());
                System.out.println(ratioX);
                float ratioY = ((float)anchorPane.getHeight())/(terrain.getDimensions()[1]* terrain.getGridSpacing());
                if (ratioY < ratioX){
                    undergrowthCanvas.setHeight(anchorPane.getHeight());
                    undergrowthCanvas.setWidth(anchorPane.getHeight());
                    canopyCanvas.setHeight(anchorPane.getHeight());
                    canopyCanvas.setWidth(anchorPane.getHeight());
                    terrainCanvas.setHeight(anchorPane.getHeight());
                    terrainCanvas.setWidth(anchorPane.getHeight());
                    plants.getUndergrowthImageCanvas( (int)(dimx*ratioY), (int)(dimy),  terrain.getGridSpacing()/ratioY, undergrowthCanvas);
                    plants.getCanopyImageCanvas((int)(dimx*ratioY), (int)(dimy),  terrain.getGridSpacing()/ratioY, canopyCanvas);
                }
                else if(ratioY > ratioX){
                    undergrowthCanvas.setHeight(anchorPane.getWidth());
                    undergrowthCanvas.setWidth(anchorPane.getWidth());
                    canopyCanvas.setHeight(anchorPane.getWidth());
                    canopyCanvas.setWidth(anchorPane.getWidth());
                    terrainCanvas.setHeight(anchorPane.getWidth());
                    terrainCanvas.setWidth(anchorPane.getWidth());
                    plants.getUndergrowthImageCanvas( (int)(undergrowthCanvas.getWidth()), (int)(dimy),  terrain.getGridSpacing()/ratioX, undergrowthCanvas);
                    plants.getCanopyImageCanvas((int)(undergrowthCanvas.getWidth()), (int)(dimy),  terrain.getGridSpacing()/ratioX, canopyCanvas);
                }
                else{
                    //System.out.println("Equal");
                }
            }
        };
        anchorPane.widthProperty().addListener(stageSizeListener);
        anchorPane.heightProperty().addListener(stageSizeListener);

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //primaryStage.setResizable(false);

        primaryStage.setTitle("EcoViz");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void handleButtonClick() {
        if (!terrainCanvas.visibleProperty().get()){
            terrainCanvas.visibleProperty().set(true);
        }
        else{
            terrainCanvas.visibleProperty().set(false);
        }
    }
    public void showPlants(){
        if (!undergrowthCanvas.visibleProperty().get()){
            undergrowthCanvas.visibleProperty().set(true);
        }
        else{
            undergrowthCanvas.visibleProperty().set(false);
        }
    }


    private void readFiles(String filename) {
        try{
            Scanner sc;
            sc = new Scanner(new File(filename+".spc.txt"));
            sc.useLocale(Locale.US);
            ArrayList<SpeciesInfo> speciesInfoArrayList = new ArrayList<>();
            while(sc.hasNext()){
                speciesInfoArrayList.add(new SpeciesInfo(sc.nextLine()));
            }
            speciesInfo=new SpeciesInfo[speciesInfoArrayList.size()];
            speciesInfoArrayList.toArray(speciesInfo);
            plants = new Plants(speciesInfo.length);

            //read elevation file
            sc = new Scanner(new File(filename+".elv"));
            sc.useLocale(Locale.US);
            int one = sc.nextInt();
            int two = sc.nextInt();
            float three = sc.nextFloat();
            float four = sc.nextFloat();
            terrain = new Terrain(one,two,three,four);
            for (int x=0; x<terrain.getDimensions()[0]; x++){
                for(int y=0; y< terrain.getDimensions()[1]; y++){
                    terrain.setHeight(x,y,sc.nextFloat());
                }
            }

            //reading canopy plant file
            //System.out.println("canopy:");
            sc = new Scanner(new File(filename+"_canopy.pdb"));
            sc.useLocale(Locale.US);
            int speciesNum = sc.nextInt();
            for(int i=0; i<speciesNum; i++){
                int speciesID = sc.nextInt();
                //System.out.println(speciesID);
                speciesInfo[speciesID].setHeight(sc.nextFloat(),sc.nextFloat());
                speciesInfo[speciesID].setAvgCanopyRad(sc.nextFloat());
                int plantNum = sc.nextInt();
                plants.addSpeciesNumToCanopy(speciesID,plantNum);
                for (int j=0; j<plantNum; j++){
                    plants.addPlantToCanopy(j, new Plant(speciesID, new float[]{sc.nextFloat(),sc.nextFloat(),sc.nextFloat()}, sc.nextFloat(),sc.nextFloat()));
                }
            }

            //reading undergrowth plant file
            //System.out.println("undergrowth:");
            sc = new Scanner(new File(filename+"_undergrowth.pdb"));
            sc.useLocale(Locale.US);
            speciesNum = sc.nextInt();
            for(int i=0; i<speciesNum; i++){
                int speciesID = sc.nextInt();
                //System.out.println(speciesID);
                speciesInfo[speciesID].setHeight(sc.nextFloat(),sc.nextFloat());
                speciesInfo[speciesID].setAvgCanopyRad(sc.nextFloat());
                int plantNum = sc.nextInt();
                plants.addSpeciesNumToUndergrowth(speciesID,plantNum);
                for (int j=0; j<plantNum; j++){
                    plants.addPlantToUndergrowth(j, new Plant(speciesID, new float[]{sc.nextFloat(),sc.nextFloat(),sc.nextFloat()}, sc.nextFloat(),sc.nextFloat()));
                }
            }

            sc.close();
        }
        catch (IOException e){
            System.out.println("Unable to open input file");
            e.printStackTrace();
        }
        catch (java.util.InputMismatchException e){
            System.out.println("Malformed input file");
            e.printStackTrace();
        }
    }
    
    
}