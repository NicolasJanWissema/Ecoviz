import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.io.File;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
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
    public Group pane;
    public ImageView terrainView;
    public ImageView plantView;
    public Label label;
    public Button button;
    public AnchorPane anchorPane;

    int dimx, dimy; // data dimensions

    public static void main(String[] args) {
        launch(args);
    }

    public void start() {
        long startTime = System.nanoTime();
        readFiles("Data/S2000-2000-512");
        long endTime = System.nanoTime();
        System.out.println("TIME TO READ FILES: " + ((endTime-startTime)/1000000));
        dimx = terrain.dimx;
        dimy = terrain.dimy;
    }

    @FXML
    public void initialize() {
        start();
        terrainView.setImage(terrain.deriveImage());
        //plantView.setImage(plants.getPlantImage(dimx,dimy, terrain.getGridSpacing()));
        plantView.setImage(plants.getPlantImageCircle(dimx,dimy, terrain.getGridSpacing()));
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

    public void hanndleButtonClick() {

        if (!terrainView.visibleProperty().get()){
            terrainView.visibleProperty().set(true);
        }
        else{
            terrainView.visibleProperty().set(false);
        }
    }
    public void showPlants(){

        if (!plantView.visibleProperty().get()){
            plantView.visibleProperty().set(true);
        }
        else{
            plantView.visibleProperty().set(false);
        }
    }

    public void addPlants() {
        // for (int i = 0; i < plants.length; i++) {
        //     //float[] pos = plants[i].getPostion();
        //     //addPlant(pos[0],pos[1],plants[i].getHeight());
        // }
    }

    public void addPlant(float x, float y, float rad) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(rad);
        circle.setFill(Color.GREEN);
        root.getChildren().addAll(circle);
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