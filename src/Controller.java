import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Controller {
    //Variables
    Plants plantData;
    SpeciesInfo speciesInfo[];
    Terrain terrainData;
    private float xDimension, yDimension;


    //Constructor
    public Controller(File file){
        String filename = file.getAbsoluteFile().toString();
        filename = filename.replaceAll(".elv","");
        filename = filename.replaceAll(".spc.txt","");
        filename = filename.replaceAll("_canopy.pdb","");
        filename = filename.replaceAll("_undergrowth.pdb","");
        readFiles(filename);
    }

    private void readFiles(String filename) {
        long startTime = System.nanoTime();
        try{
            BufferedReader bufferedReader;
            String line;

            //Reading species file.
            bufferedReader = new BufferedReader(new FileReader(filename+".spc.txt"));
            ArrayList<SpeciesInfo> speciesInfoArrayList = new ArrayList<>();
            while((line=bufferedReader.readLine())!=null){
                speciesInfoArrayList.add(new SpeciesInfo(line));
            }
            speciesInfo=new SpeciesInfo[speciesInfoArrayList.size()];
            speciesInfoArrayList.toArray(speciesInfo);
            plantData = new Plants(speciesInfo.length);

            //read elevation file
            bufferedReader = new BufferedReader(new FileReader(filename+".elv"));
            String[] elevationInfo = bufferedReader.readLine().split(" ");
            int dimX = Integer.parseInt(elevationInfo[0]);
            int dimY = Integer.parseInt(elevationInfo[1]);
            float gridSpacing = Float.parseFloat(elevationInfo[2]);
            float Latitude = Float.parseFloat(elevationInfo[3]);
            terrainData = new Terrain(dimX,dimY,gridSpacing,Latitude);
            for (int x=0; x<dimX; x++){
                String[] terrainRow = bufferedReader.readLine().trim().split(" ");
                for(int y=0; y< dimY; y++){
                    terrainData.setHeight(x,y,Float.parseFloat(terrainRow[y]));
                }
            }
            xDimension = terrainData.getXDimension();
            yDimension = terrainData.getYDimension();

            //reading canopy plant file
            bufferedReader = new BufferedReader(new FileReader(filename+"_canopy.pdb"));
            int speciesNum = Integer.parseInt(bufferedReader.readLine());
            for(int i=0; i<speciesNum; i++){
                String[] speciesData = bufferedReader.readLine().split(" ");
                int speciesID = Integer.parseInt(speciesData[0]);
                speciesInfo[speciesID].setHeight(Float.parseFloat(speciesData[1]),Float.parseFloat(speciesData[2]));
                speciesInfo[speciesID].setAvgCanopyRad(Float.parseFloat(speciesData[3]));
                int plantNum = Integer.parseInt(bufferedReader.readLine());
                plantData.addSpeciesNumToCanopy(speciesID,plantNum);
                for (int j=0; j<plantNum; j++){
                    String[] plantLine = bufferedReader.readLine().split(" ");
                    float[] plantInfo = new float[5];
                    for (int x=0;x<5;x++){
                        plantInfo[x] = Float.parseFloat(plantLine[x]);
                    }
                    plantData.addPlantToCanopy(j, new Plant(speciesID, new float[]{plantInfo[0],plantInfo[1],plantInfo[2]}, plantInfo[3],plantInfo[4]));
                }
            }

            //reading undergrowth plant file
            bufferedReader = new BufferedReader(new FileReader(filename+"_canopy.pdb"));
            speciesNum = Integer.parseInt(bufferedReader.readLine());
            for(int i=0; i<speciesNum; i++){
                String[] speciesData = bufferedReader.readLine().split(" ");
                int speciesID = Integer.parseInt(speciesData[0]);
                speciesInfo[speciesID].setHeight(Float.parseFloat(speciesData[1]),Float.parseFloat(speciesData[2]));
                speciesInfo[speciesID].setAvgCanopyRad(Float.parseFloat(speciesData[3]));
                int plantNum = Integer.parseInt(bufferedReader.readLine());
                plantData.addSpeciesNumToUndergrowth(speciesID,plantNum);
                for (int j=0; j<plantNum; j++){
                    String[] plantLine = bufferedReader.readLine().split(" ");
                    float[] plantInfo = new float[5];
                    for (int x=0;x<5;x++){
                        plantInfo[x] = Float.parseFloat(plantLine[x]);
                    }
                    plantData.addPlantToUndergrowth(j, new Plant(speciesID, new float[]{plantInfo[0],plantInfo[1],plantInfo[2]}, plantInfo[3],plantInfo[4]));
                }
            }

            bufferedReader.close();
        }
        catch (IOException e){
            System.out.println("Unable to open input file");
            e.printStackTrace();
        }
        catch (java.util.InputMismatchException e){
            System.out.println("Malformed input file");
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        System.out.println("TIME TO READ: " + ((endTime-startTime)/1000000));
    }

    public void addCanvases(StackPane stackPane){
        terrainData.addTerrainCanvas(stackPane, xDimension, yDimension);
        plantData.addPlantCanvas(stackPane, xDimension, yDimension);
    }

    public float getxDimension() {
        return xDimension;
    }

    public float getyDimension() {
        return yDimension;
    }
}

