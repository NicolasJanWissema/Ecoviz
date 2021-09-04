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
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    public AnchorPane anchorPane;
    public Canvas terrainCanvas;
    public Canvas undergrowthCanvas;
    public Canvas canopyCanvas;
    public Slider undergrowthSlider;
    public Slider canopySlider;
    public Slider zoomSlider;
    public VBox filterPlaceholder;
    public CheckBox[] filterBoxes;

    // Panning and Zooming Variables
    float fOffsetX = 0.0f;
    float fOffsetY = 0.0f;
    // int nScreenX = 0;
    // int nScreenY = 0;
    // float fWorldX = 0;
    // float fWorldY = 0;
    float fStartPanX = 0;
    float fStartPanY = 0;

    float scaleX = 1.0f;
    float scaleY = 1.0f;

    int dimx, dimy; // data dimensions

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

    public int[] worldToScreen(float fWorldX, float fWorldY) {
        int nScreenX = (int) ((fWorldX - fOffsetX)*scaleX);
        int nScreenY = (int) ((fWorldY - fOffsetY)*scaleY);
        int[] temp = {nScreenX,nScreenY};
        return temp;
    }

    public float[] screenToWorld(int nScreenX, int nScreenY) {
        float fWorldX = (float) (nScreenX/scaleX + fOffsetX);
        float fWorldY = (float) (nScreenY/scaleY + fOffsetY);
        float[] temp = {fWorldX,fWorldY};
        return temp;

    }

    public void deriveImageCanvasOffset(Canvas img, float fOffsetX, float fOffsetY) {
        this.fOffsetX = fOffsetX;
        this.fOffsetY = fOffsetY;
        float maxh = -10000.0f, minh = 10000.0f;
        GraphicsContext gc = img.getGraphicsContext2D();
        gc.clearRect(0, 0, img.getWidth(), img.getHeight());
        PixelWriter pw = gc.getPixelWriter();

        // determine range of heights
        for(int x=0; x < dimx; x++)
            for(int y=0; y < dimy; y++) {
                float h = terrain.getHeight(x, y);
                if(h > maxh)
                    maxh = h;
                if(h < minh)
                    minh = h;
            }

        for(int x=0; x < dimx; x++)
            for(int y=0; y < dimy; y++) {
                // find normalized height value in range
                float val = (terrain.getHeight(x, y) - minh) / (maxh - minh);
                Color color = new Color(val,val,val,1.0f);
                int[] pos = worldToScreen(x, y);
                //pw.setColor(pos[0], pos[1], color);
                gc.setFill(color);
                gc.fillRect(pos[0], pos[1], scaleX+1, scaleX+1);
                
            }

    }

    public void getCanopyImageCanvas(int dimx, int dimy, float gridSpacing, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.dimx = dimx;
        this.dimy = dimy;
        //long startTime = System.nanoTime();
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW ONE CIRCLE: " + ((endTime-startTime)/1000000));
        plants.generateUnfiltered();
        //long startTime = System.nanoTime();
        for(int i=0; i<plants.getCanopyLength();i++){
            for (int j=0;j<plants.getCanopyLength(i);j++){
                Plant tempPlant = plants.getUnfilteredCanopy(i, j);
                // int x = (int)(tempPlant.getPosition()[0]/gridSpacing);
                // int y = (int)(tempPlant.getPosition()[1]/gridSpacing);
                int pos[] = worldToScreen(tempPlant.getPosition()[0]/gridSpacing, tempPlant.getPosition()[1]/gridSpacing);
                //if (x<dimx && y<dimy){
                    gc.setFill(plants.getColor(i));
                    double rad = (double)( (tempPlant.getCanopyRadius()/gridSpacing)*scaleX);
                    gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad*2, rad*2);
               // }
            }
        }
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
    }


    public void getUndergrowthImageCanvas(int dimx, int dimy, float gridSpacing, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.dimx = dimx;
        this.dimy = dimy;
        //long startTime = System.nanoTime();
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW ONE CIRCLE: " + ((endTime-startTime)/1000000));
        plants.generateUnfiltered();
        //long startTime = System.nanoTime();
        for(int i=0; i<plants.getUndergrowthLength();i++){
            for (int j=0;j<plants.getUndergrowthLength(i);j++){
                Plant tempPlant = plants.getUnfilteredUndergrowth(i, j);
                // int x = (int)(tempPlant.getPosition()[0]/gridSpacing);
                // int y = (int)(tempPlant.getPosition()[1]/gridSpacing);
                int pos[] = worldToScreen(tempPlant.getPosition()[0]/gridSpacing, tempPlant.getPosition()[1]/gridSpacing);
                //if (x<dimx && y<dimy){
                    gc.setFill(plants.getColor(i));
                    double rad = (double) ((tempPlant.getCanopyRadius()/gridSpacing)*scaleX);
                    gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad*2, rad*2);
                //}
            }
        }
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
    }
    
    
    @FXML
    public void initialize() {
        dataGen();

        anchorPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        deriveImageCanvasOffset(terrainCanvas, fOffsetX, fOffsetY);
        getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
        getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);
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
                        getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
                        getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);
                        //System.out.println("On");
                    }
                    else {
                        for (int j= 0; j < filterBoxes.length; j++) {
                            if (!filterBoxes[j].isSelected()) {
                                plants.filterSpecies(j);
                            }
                        }
                        getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
                        getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);
                        //System.out.println("Off");
                    }
                }
            });
            filterPlaceholder.getChildren().add(filterBoxes[i]);
        }

        anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("MOUSE PRESSED");
                anchorPane.setCursor(Cursor.CLOSED_HAND);
                fStartPanX = (float) event.getSceneX();
                fStartPanY = (float) event.getSceneY();
            }
        });

        anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event) {
                float mouseX = (float) event.getSceneX();
                float mouseY = (float) event.getSceneY();
                fOffsetX -= (mouseX - fStartPanX)/scaleX;
                fOffsetY -= (mouseY - fStartPanY)/scaleY;
                //System.out.println(fOffsetX + " - " + fOffsetY);
                fStartPanX = mouseX;
                fStartPanY = mouseY;
                deriveImageCanvasOffset(terrainCanvas, fOffsetX, fOffsetY);
                getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
                getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);

            }
            
        });

        anchorPane.setOnScroll(new EventHandler<ScrollEvent>(){
            @Override
            public void handle(ScrollEvent event) {
                //System.out.println("Scroll Event Y: " + event.getDeltaY());
                float mouseX = (float) event.getSceneX();
                float mouseY = (float) event.getSceneY();
                float[] beforeZoom = screenToWorld((int) mouseX, (int) mouseY);
                if (event.getDeltaY()>0) {
                    scaleX *= 1.1f;
                    scaleY *= 1.1f;
                } else {
                    scaleX *= 0.9f;
                    scaleY *= 0.9f;
                }
                float mouseX1 = (float) event.getSceneX();
                float mouseY1 = (float) event.getSceneY();
                float[] afterZoom = screenToWorld((int) mouseX1, (int) mouseY1);
                fOffsetX += (beforeZoom[0] - afterZoom[0]);
                fOffsetY += (beforeZoom[1] - afterZoom[1]);
                deriveImageCanvasOffset(terrainCanvas, fOffsetX, fOffsetY);
                getUndergrowthImageCanvas(dimx, dimy,  terrain.getGridSpacing(), undergrowthCanvas);
                getCanopyImageCanvas(dimx, dimy,  terrain.getGridSpacing(), canopyCanvas);
            }
            
        });
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

        // anchorPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
        //     @Override
        //     public void handle(MouseEvent event) {
        //     }
        // });

        anchorPane.setCursor(Cursor.OPEN_HAND);
        anchorPane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                label.setText(event.getSceneX()+", "+event.getSceneY());
            }
        });
        // anchorPane.setOnMousePressed(new EventHandler<MouseEvent>() {
        //     @Override
        //     public void handle(MouseEvent event) {
        //         anchorPane.setCursor(Cursor.CLOSED_HAND);
        //     }
        // });
        anchorPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                anchorPane.setCursor(Cursor.OPEN_HAND);
            }
        });

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setResizable(false);
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