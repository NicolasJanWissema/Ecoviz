import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.SubScene;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
 
public class GUI extends Application {
    @FXML
    // regular grid of height values
	WritableImage img; // greyscale image for displaying the terrain top-down
    Group root;

    // Main Class Globals
    private Plants plants;
    private Terrain terrain;
    private SpeciesInfo[] speciesInfo;
    private FireSim fireSim;
    private Plant[][] displayed;
    public Group pane;
    public Label label;
    public Button button;

    float [][] height; // regular grid of height values
    int dimx, dimy; // data dimensions
    public static void main(String[] args) {
        //Controller controller = new Controller(gui);
        launch(args);
    }

    public GUI() {
        readFiles("Data/S6000-6000-256");
        dimx = terrain.dimx;
        dimy = terrain.dimy;
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));   
        //root = new Group() ;
        primaryStage.setTitle("EcoViz");
        long startTime = System.nanoTime();
        deriveImage();
        long endTime = System.nanoTime();
        System.out.println("TIME: " + ((endTime-startTime)/1000000));
        ImageView imageView = new ImageView(img);
        //root.getChildren().add(imageView);
        //pane.getChildren().add(imageView);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        
    }

    public void hanndleButtonClick() {
        label.setText("BUTTON PRESSED");
        ImageView imageView = new ImageView(deriveImage());
        pane.getChildren().add(imageView);
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

    public WritableImage deriveImage() {
        System.out.println(dimx + " " +dimy);
		img = new WritableImage(dimx, dimy);
        PixelWriter pw = img.getPixelWriter();
		float maxh = -10000.0f, minh = 10000.0f;
		
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
				Color col = new Color(val, val, val, 1.0f);
                pw.setColor(x, y, col);
				 
			}
        return img;
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
            System.out.println("canopy:");
            sc = new Scanner(new File(filename+"_canopy.pdb"));
            sc.useLocale(Locale.US);
            int speciesNum = sc.nextInt();
            for(int i=0; i<speciesNum; i++){
                int speciesID = sc.nextInt();
                System.out.println(speciesID);
                speciesInfo[speciesID].setHeight(sc.nextFloat(),sc.nextFloat());
                speciesInfo[speciesID].setAvgCanopyRad(sc.nextFloat());
                int plantNum = sc.nextInt();
                plants.addSpeciesNumToCanopy(speciesID,plantNum);
                for (int j=0; j<plantNum; j++){
                    plants.addPlantToCanopy(j, new Plant(speciesID, new float[]{sc.nextFloat(),sc.nextFloat(),sc.nextFloat()}, sc.nextFloat(),sc.nextFloat()));
                }
            }

            //reading undergrowth plant file
            System.out.println("undergrowth:");
            sc = new Scanner(new File(filename+"_undergrowth.pdb"));
            sc.useLocale(Locale.US);
            speciesNum = sc.nextInt();
            for(int i=0; i<speciesNum; i++){
                int speciesID = sc.nextInt();
                System.out.println(speciesID);
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