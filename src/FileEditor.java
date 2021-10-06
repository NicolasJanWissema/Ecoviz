import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;

public class FileEditor extends Application {
    @FXML
    Controller controller;
    SpeciesInfo[] speciesInfo;
    Plants plantData;
    Terrain terrainData;
    float xDimension, yDimension;
    private float maxHeight;

    public FileEditor(Controller controller){
        this.controller = controller;
    }
    public FileEditor(){}

    public void initialize(){
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileEditor.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        //primaryStage.setResizable(false);

        primaryStage.setTitle("File Editor");
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
            String filename = selectedFile.getAbsoluteFile().toString();
            filename = filename.replaceAll(".elv","");
            filename = filename.replaceAll(".spc.txt","");
            filename = filename.replaceAll("_canopy.pdb","");
            filename = filename.replaceAll("_undergrowth.pdb","");
            try{
                readFiles(filename);
                //generate sorted arrays of data.
                plantData.completeGeneration();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void closeFile(){
        controller=null;
    }

    private void readFiles(String filename) throws FileNotFoundException {
        long startTime = System.nanoTime();
        //Reading species file.
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename+".spc.txt"));
            String line;
            ArrayList<SpeciesInfo> speciesInfoArrayList = new ArrayList<>();
            while((line=bufferedReader.readLine())!=null){
                speciesInfoArrayList.add(new SpeciesInfo(line));
            }
            speciesInfo=new SpeciesInfo[speciesInfoArrayList.size()];
            speciesInfoArrayList.toArray(speciesInfo);
            plantData = new Plants(speciesInfo.length);
            //finished reading files
            bufferedReader.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new FileNotFoundException("species file not found or incorrectly formatted.");
        }
        //read elevation file
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename+".elv"));
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
            //finished reading files
            bufferedReader.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new FileNotFoundException("elevation file not found or incorrectly formatted.");
        }
        //reading canopy plant file
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename+"_canopy.pdb"));
            int speciesNum = Integer.parseInt(bufferedReader.readLine());
            int plantCount = 0;
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
                    if (maxHeight < plantInfo[3]) {
                        maxHeight = plantInfo[3];
                    }
                    plantData.addPlantToCanopy(j, new Plant(speciesID, plantInfo[0], plantInfo[1], plantInfo[2], plantInfo[3], plantInfo[4], plantCount));
                    plantCount++;
                }

            }
            //finished reading files
            bufferedReader.close();
        } catch (IOException e) {
            //e.printStackTrace();
            throw new FileNotFoundException("canopy file not found or incorrectly formatted.");
        }
        //reading undergrowth plant file
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename+"_undergrowth.pdb"));
            int speciesNum = Integer.parseInt(bufferedReader.readLine());
            int plantCount = 0;
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
                    if (maxHeight < plantInfo[3]) {
                        maxHeight = plantInfo[3];
                    }
                    plantData.addPlantToUndergrowth(j, new Plant(speciesID, plantInfo[0], plantInfo[1], plantInfo[2], plantInfo[3], plantInfo[4], plantCount));
                    plantCount++;
                }
            }
            //finished reading files
            bufferedReader.close();

        } catch (IOException e) {
            //e.printStackTrace();
            throw new FileNotFoundException("undergrowth file not found or incorrectly formatted.");
        }

        long endTime = System.nanoTime();
        System.out.println("TIME TO READ: " + ((endTime-startTime)/1000000));
    }
}
