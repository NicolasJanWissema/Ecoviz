import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.ScrollEvent;

import javax.swing.plaf.ColorChooserUI;

public class Controller {
    //Variables
    Plants plantData;
    SpeciesInfo[] speciesInfo;
    Terrain terrainData;
    private float xDimension, yDimension;
    TerrainCanvas terrainCanvas;
    PlantCanvas canopyCanvas;
    PlantCanvas undergrowthCanvas;
    private Plant selectedPlant;
    enum DRAWTYPE{Terrain,Undergrowth,Canopy,Minimap,Fire}


    // Panning and Zooming Variables
    float fOffsetX = 0.0f;
    float fOffsetY = 0.0f;
    float fStartPanX = 0;
    float fStartPanY = 0;
    float scaleX = 1.0f;
    float scaleY = 1.0f;
    float sizeX = 0;
    float sizeY = 0;


    //Constructor
    public Controller(File file){
        String filename = file.getAbsoluteFile().toString();
        filename = filename.replaceAll(".elv","");
        filename = filename.replaceAll(".spc.txt","");
        filename = filename.replaceAll("_canopy.pdb","");
        filename = filename.replaceAll("_undergrowth.pdb","");
        readFiles(filename);
    }

    public void setPan(float fStartPanX, float fStartPanY ) {
        this.fStartPanX = fStartPanX;
        this.fStartPanY = fStartPanY;
    }

    public void panning(float mouseX, float mouseY) {
        updateSize();
        fOffsetX += (mouseX - fStartPanX)/(scaleX*sizeX);
        fOffsetY += (mouseY - fStartPanY)/(scaleY*sizeY);
        //System.out.println(fOffsetX + " - " + fOffsetY);
        fStartPanX = mouseX;
        fStartPanY = mouseY;
        terrainCanvas.drawCanvas();
        undergrowthCanvas.drawCanvas();
        canopyCanvas.drawCanvas();
    }

    public void zooming(ScrollEvent event) {
        float mouseX = (float) event.getX();
        float mouseY = (float) event.getY();
        float[] beforeZoom = screenToWorld((int) mouseX, (int) mouseY);
        if (event.getDeltaY()>0) {
            scaleX *= 1.1f;
            scaleY *= 1.1f;
        } else if (scaleX>=1){
            scaleX *= 0.9f;
            scaleY *= 0.9f;
        }
        float mouseX1 = (float) event.getX();
        float mouseY1 = (float) event.getY();
        float[] afterZoom = screenToWorld((int) mouseX1, (int) mouseY1);
        fOffsetX -= (beforeZoom[0] - afterZoom[0]);
        fOffsetY -= (beforeZoom[1] - afterZoom[1]);
        terrainCanvas.drawCanvas();
        undergrowthCanvas.drawCanvas();
        canopyCanvas.drawCanvas();
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
                    plantData.addPlantToCanopy(j, new Plant(speciesID, plantInfo[0], plantInfo[1], plantInfo[2], plantInfo[3], plantInfo[4]));
                }
            }

            //reading undergrowth plant file
            bufferedReader = new BufferedReader(new FileReader(filename+"_undergrowth.pdb"));
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
                    plantData.addPlantToUndergrowth(j, new Plant(speciesID, plantInfo[0], plantInfo[1], plantInfo[2], plantInfo[3], plantInfo[4]));
                }
            }
            //finished reading files
            bufferedReader.close();
            //generate sorted arrays of data.
            plantData.completeGeneration();
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
        addTerrainCanvas(stackPane);
        addPlantCanvas(stackPane);
    }

    public float getxDimension() {
        return xDimension;
    }

    public float getyDimension() {
        return yDimension;
    }

    public float[] worldToScreen(float fWorldX, float fWorldY) {
        updateSize();
        float nScreenX = (fWorldX + fOffsetX)*sizeX*scaleX;
        float nScreenY = (fWorldY + fOffsetY)*sizeY*scaleY;
        return new float[]{nScreenX,nScreenY};
    }

    public float[] screenToWorld(float nScreenX, float nScreenY) {
        updateSize();
        float fWorldX = nScreenX/(scaleX*sizeX) - fOffsetX;
        float fWorldY = nScreenY/(scaleY*sizeY) - fOffsetY;
        return new float[]{fWorldX,fWorldY};
    }

    public void updateSize() {
        sizeX = (float) (terrainCanvas.getWidth()/xDimension);
        sizeY = (float) (terrainCanvas.getHeight()/yDimension);
    }

    public void changeCanopyOpacity(double value){
        canopyCanvas.setOpacity(value);
    }
    public void changeUndergrowthOpacity(double value){
        undergrowthCanvas.setOpacity(value);
    }


    class TerrainCanvas extends Canvas {
        float maxh = -10000.0f, minh = 10000.0f;
        int[] dim = terrainData.getDimensions();
        private final DRAWTYPE drawtype;
        public TerrainCanvas(DRAWTYPE drawtype){
            this.drawtype = drawtype;

            for(int x=0; x < dim[0]; x++){
                for(int y=0; y < dim[1]; y++) {
                    float h = terrainData.getHeight(x, y);
                    if(h > maxh)
                        maxh = h;
                    if(h < minh)
                        minh = h;
                }
            }
            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
        }
        public void drawThread(){
            Drawing drawing = new Drawing(drawtype);
            drawing.start();
        }

        public synchronized void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            PixelWriter pw = gc.getPixelWriter();

            //Redraw canvas
            for(int x=0; x < dim[0]; x++){
                for(int y=0; y < dim[1]; y++) {
                    // find normalized height value in range
                    float val = (terrainData.getHeight(x, y) - minh) / (maxh - minh);
                    Color color = new Color(val,val,val,1.0f);
                    float[] pos = worldToScreen(x*terrainData.getGridSpacing(), y*terrainData.getGridSpacing());
                   // pw.setColor((int)(x*getWidth()/xDimension), (int)(y*getHeight()/yDimension), color);
                    //pw.setColor((int)pos[0], (int)pos[1], color);
                    gc.setFill(color);
                    gc.fillRect(pos[0], pos[1], 1*scaleX*sizeX+1, 1*scaleX*sizeX+1);
                }
            }
            //long endTime = System.nanoTime();
            //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
        }

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

    class PlantCanvas extends Canvas {
        private final Plant[] plants;
        private final DRAWTYPE drawtype;

        public PlantCanvas(Plant[] plants, DRAWTYPE drawtype){
            this.plants=plants;
            this.drawtype=drawtype;

            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
        }
        public void drawThread(){
            Drawing drawing = new Drawing(drawtype);
            drawing.start();
        }
        public synchronized void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());

            if (selectedPlant!=null && selectedPlant.enabled()){
                for (Plant plant : plants){
                    if (plant.enabled() && plant!=selectedPlant){
                        Color color= plantData.getColor(plant.getSpeciesID());
                        gc.setFill(color);
                        //gc.setStroke(color.darker());
                        float[] pos = worldToScreen(plant.getPosition()[0], plant.getPosition()[1]);
                        double rad = (double) (plant.getCanopyRadius()*(sizeX*scaleX));
                        gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                        //gc.strokeOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                    }
                }
                gc.setFill(Color.BLACK);
                float[] pos = worldToScreen(selectedPlant.getPosition()[0], selectedPlant.getPosition()[1]);
                double rad = (double) (selectedPlant.getCanopyRadius()*(sizeX*scaleX));
                gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
            }
            else{
                for (Plant plant : plants){
                    if (plant.enabled()){
                        gc.setFill(plantData.getColor(plant.getSpeciesID()));
                        float[] pos = worldToScreen(plant.getPosition()[0], plant.getPosition()[1]);
                        double rad = (double) (plant.getCanopyRadius()*(sizeX*scaleX));
                        gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                    }
                }
            }
            //long endTime = System.nanoTime();
            //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
        }

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


    class Drawing extends Thread{
        //int drawingType;
        DRAWTYPE drawingType;
        public Drawing(DRAWTYPE drawingType){
            this.drawingType=drawingType;
        }
        public void run(){
            switch (drawingType){
                case Terrain:{
                    terrainCanvas.drawCanvas();
                    break;
                }
                case Undergrowth:{
                    undergrowthCanvas.drawCanvas();
                    break;
                }
                case Canopy:{
                    canopyCanvas.drawCanvas();
                    break;
                }
                case Minimap:{
                    System.out.println("minimap");
                    break;
                }
                case Fire:{
                    System.out.println("Fire");
                    break;
                }
                default:{
                    System.out.println("Runtime error");
                }
            }
        }
    }

    public void addTerrainCanvas(StackPane stackPane){
        terrainCanvas = new TerrainCanvas(DRAWTYPE.Terrain);
        terrainCanvas.widthProperty().bind(stackPane.widthProperty());
        terrainCanvas.heightProperty().bind(stackPane.heightProperty());
        stackPane.getChildren().add(terrainCanvas);
    }

    public void addPlantCanvas(StackPane stackPane){
        canopyCanvas = new PlantCanvas(plantData.getCanopy(), DRAWTYPE.Canopy);
        undergrowthCanvas = new PlantCanvas(plantData.getUndergrowth(), DRAWTYPE.Undergrowth);
        canopyCanvas.widthProperty().bind(stackPane.widthProperty());
        canopyCanvas.heightProperty().bind(stackPane.heightProperty());
        undergrowthCanvas.widthProperty().bind(stackPane.widthProperty());
        undergrowthCanvas.heightProperty().bind(stackPane.heightProperty());

        stackPane.getChildren().addAll(undergrowthCanvas,canopyCanvas);
    }

    public void generateMinimap(StackPane miniPane){
        Canvas minimapCanvas = new Canvas(200,200);
        GraphicsContext gc = minimapCanvas.getGraphicsContext2D();

        //Terrain drawing..
        float minh = terrainCanvas.minh;
        float maxh = terrainCanvas.maxh;
        gc.clearRect(0, 0, minimapCanvas.getWidth(), minimapCanvas.getHeight());
        PixelWriter pw = gc.getPixelWriter();
        for(int x=0; x < terrainData.dimx; x++){
            for(int y=0; y < terrainData.dimy; y++) {
                // find normalized height value in range
                float val = (terrainData.getHeight(x, y) - minh) / (maxh - minh);
                Color color = new Color(val,val,val,1.0f);
                pw.setColor((int)(x*minimapCanvas.getWidth()/xDimension), (int)(y*minimapCanvas.getHeight()/yDimension), color);
            }
        }

        //Undergrowth Drawing..
        for (Plant plant: plantData.getUndergrowth()){
            gc.setFill(plantData.getColor(plant.getSpeciesID()));
            float x = (float) (plant.getPosition()[0]*minimapCanvas.getWidth()/xDimension);
            float y = (float)(plant.getPosition()[1]*minimapCanvas.getHeight()/yDimension);
            double rad = (double) (plant.getCanopyRadius()*minimapCanvas.getWidth()/xDimension);
            gc.fillOval((double) x-rad, (double) y-rad, rad *2, rad*2);
        }
        //Canopy Drawing..
        for (Plant plant: plantData.getCanopy()){
            gc.setFill(plantData.getColor(plant.getSpeciesID()));
            float x = (float) (plant.getPosition()[0]*minimapCanvas.getWidth()/xDimension);
            float y = (float)(plant.getPosition()[1]*minimapCanvas.getHeight()/yDimension);
            double rad = (double) (plant.getCanopyRadius()*minimapCanvas.getWidth()/xDimension);
            gc.fillOval((double) x-rad, (double) y-rad, rad *2, rad*2);
        }




        miniPane.getChildren().add(minimapCanvas);
    }

    public void addFilter(int speciesID, VBox filterBox){
        HBox hBox = new HBox();
        ColorPicker colorPicker = new ColorPicker(plantData.getColor(speciesID));
        colorPicker.getStyleClass().add("button");
        colorPicker.valueProperty().addListener(new ChangeListener<Color>() {
            @Override
            public void changed(ObservableValue<? extends Color> observable, Color oldValue, Color newValue) {
                plantData.setColor(speciesID,observable.getValue());
                canopyCanvas.drawCanvas();
                undergrowthCanvas.drawCanvas();
            }
        });

        CheckBox checkBox = new CheckBox(speciesInfo[speciesID].getCommmonName());
        checkBox.setSelected(true);
        checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (observable.getValue()){
                    plantData.unFilterSpecies(speciesID);
                }
                else {
                    plantData.filterSpecies(speciesID);
                }
                canopyCanvas.drawCanvas();
                undergrowthCanvas.drawCanvas();
            }
        });

        hBox.getChildren().addAll(colorPicker,checkBox);
        filterBox.getChildren().add(hBox);
    }
    public int getNumSpecies(){
        return (speciesInfo.length);
    }
    public void getPlant(float x, float y){
        float[] pos = screenToWorld(x,y);
        selectedPlant = plantData.selectPlant(pos[0],pos[1]);
        if (selectedPlant!=null){
            System.out.println(speciesInfo[selectedPlant.getSpeciesID()].getCommmonName());
            System.out.println(speciesInfo[selectedPlant.getSpeciesID()].getLantinName());
        }
        canopyCanvas.drawCanvas();
        undergrowthCanvas.drawCanvas();
    }

}

