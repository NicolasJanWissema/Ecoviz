import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.ScrollEvent;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.plaf.ColorUIResource;

/**
 * Thic class is the bridge between the data and the GUI
 * It controls all the logic of the program
 * 
 * @author WSSNIC008 KRNHAN003 JCBSHA028
 */
public class Controller {
    //Variables
    Plants plantData;
    SpeciesInfo[] speciesInfo;
    Terrain terrainData;
    FireSim firesim;
    private float xDimension, yDimension;
    TerrainCanvas terrainCanvas;
    PlantCanvas canopyCanvas;
    PlantCanvas undergrowthCanvas;
    MiniMapCanvas miniMapSquare;
    ProgressBar loadingBar;
    private Plant selectedPlant;
    public float maxHeight;
    public float filterHeightUpper;
    public float filterHeightLower;
    enum DRAWTYPE{Terrain,Undergrowth,Canopy,Minimap,Fire}
    RangeSlider rangeSlider;
    TextField tfLow;
    TextField tfHigh;

    // Panning and Zooming Variables
    float fOffsetX = 0.0f;
    float fOffsetY = 0.0f;
    float fStartPanX = 0;
    float fStartPanY = 0;
    float scale = 1.0f;
    float sizeX = 0;
    float sizeY = 0;


    //Constructor
    public Controller(File file) throws FileNotFoundException {
        String filename = file.getAbsoluteFile().toString();
        filename = filename.replaceAll(".elv","");
        filename = filename.replaceAll(".spc.txt","");
        filename = filename.replaceAll("_canopy.pdb","");
        filename = filename.replaceAll("_undergrowth.pdb","");
        readFiles(filename);
    }

    public Controller(File file, ProgressBar loadingBar, RangeSlider slider, TextField tfLow, TextField tfHigh) throws FileNotFoundException {
        this.loadingBar = loadingBar;
        String filename = file.getAbsoluteFile().toString();
        filename = filename.replaceAll(".elv","");
        filename = filename.replaceAll(".spc.txt","");
        filename = filename.replaceAll("_canopy.pdb","");
        filename = filename.replaceAll("_undergrowth.pdb","");
        readFiles(filename);
        // int[] sp = {101, 107}; // for firesim testing
        // firesim = new FireSim(terrainData.getDimensions()[0] , terrainData.getGridSpacing(), plantData, sp); //for firesim testing
        // firesim.shouldBurn(sp[0], sp[1]);
        rangeSlider = slider;
        this.tfLow = tfLow;
        this.tfHigh = tfHigh;
        Platform.runLater(()->{
            tfLow.setText(Float.toString(0));
            tfHigh.setText(Float.toString(maxHeight));
        });
        //loadingBar.progressProperty().set(0);
    }

    /**
     * This runs the height filter method and sending the upper and lower bounds of the height
     * 
     * @param tempSlider 
     */
    public void movedSlider(RangeSlider tempSlider) {
        //System.out.println("Upper: " + sliderToHeight(tempSlider.getUpperValue()));
        //System.out.println("Lower: " + sliderToHeight(tempSlider.getValue()));
        heightFilter(sliderToHeight(tempSlider.getUpperValue()), sliderToHeight(tempSlider.getValue()));

    }

    /**
     * Sets the panning positions
     * 
     * @param fStartPanX x position
     * @param fStartPanY y position
     */
    public void setPan(float fStartPanX, float fStartPanY ) {
        this.fStartPanX = fStartPanX;
        this.fStartPanY = fStartPanY;
    }

    /**
     * Updates the panning varibles when a user attempts to pan
     * 
     * @param mouseX mouse position x
     * @param mouseY mouse position y
     */
    public void panning(float mouseX, float mouseY) {
        updateSize();
        float[] dimensions = screenToWorld((float)terrainCanvas.getWidth(), (float)terrainCanvas.getHeight());
        //X offset calculation.
        if (fOffsetX-(fStartPanX - mouseX)/(scale*sizeX)>=0){
            fOffsetX+=0;
        }
        else if(dimensions[0]+(fStartPanX - mouseX)/(scale*sizeX) <= xDimension){
            fOffsetX-=(fStartPanX - mouseX)/(scale*sizeX);
        }

        //Y offset calculation.
        if (fOffsetY - (fStartPanY - mouseY)/(scale*sizeY)>=0){
            fOffsetY=0;
        }
        else if(dimensions[1]+(fStartPanY - mouseY)/(scale*sizeY) <= yDimension){
            fOffsetY-=(fStartPanY - mouseY)/(scale*sizeY);
        }

        fStartPanX = mouseX;
        fStartPanY = mouseY;
        terrainCanvas.drawCanvas();
        undergrowthCanvas.drawCanvas();
        canopyCanvas.drawCanvas();
        miniMapSquare.drawSquare();
    }

    /**
     * Updates the scrolling variables when the user scolls
     * 
     * @param event the mouse event.
     */
    public void zooming(ScrollEvent event) {
        float mouseX = (float) event.getX();
        float mouseY = (float) event.getY();
        float[] beforeZoom = screenToWorld((int) mouseX, (int) mouseY);
        if (event.getDeltaY()>0) {
            scale *= 1.1f;
        } else if (scale*0.9f>1){
            scale *= 0.9f;
        }
        else{
            scale=1.0f;
        }
        float mouseX1 = (float) event.getX();
        float mouseY1 = (float) event.getY();
        float[] afterZoom = screenToWorld((int) mouseX1, (int) mouseY1);
        //fOffsetX += (afterZoom[0] - beforeZoom[0]);
        float[] dimensions = screenToWorld((float)terrainCanvas.getWidth(), (float)terrainCanvas.getHeight());
        float[] offsets = screenToWorld(0,0);
        if (fOffsetX+(afterZoom[0] - beforeZoom[0])>=0){
            fOffsetX=0;
        }
        else if(dimensions[0]+(afterZoom[0] - beforeZoom[0]) <= xDimension){
            fOffsetX+=(afterZoom[0] - beforeZoom[0]);
        }
        else{
            fOffsetX = dimensions[0]-xDimension-offsets[0];
        }
        if (fOffsetY+(afterZoom[1] - beforeZoom[1])>=0){
            fOffsetY=0;
        }
        else if(dimensions[1]+(afterZoom[1] - beforeZoom[1]) <= yDimension){
            fOffsetY+=(afterZoom[1] - beforeZoom[1]);
        }
        else {
            fOffsetY = dimensions[1]-yDimension-offsets[1];
        }
        //fOffsetY += (afterZoom[1] - beforeZoom[1]);

        miniMapSquare.drawSquare();
        terrainCanvas.drawCanvas();
        long temp = System.nanoTime();
        undergrowthCanvas.drawCanvas();
        canopyCanvas.drawCanvas();
        //System.out.println("Time To Draw: " + (System.nanoTime() - temp)/1000000);
    }

    /**
     * Updates the zooming variables when the user scolls
     * 
     * @param event the mouse event.
     */
    public  void updateZoom(){
        float[] dimensions = screenToWorld((float)terrainCanvas.getWidth(), (float)terrainCanvas.getHeight());
        float[] offsets = screenToWorld(0,0);
        if (fOffsetX>=0){
            fOffsetX=0;
        }
        else if(dimensions[0] >= xDimension){
            fOffsetX = dimensions[0]-xDimension-offsets[0];
        }
        if (fOffsetY>=0){
            fOffsetY=0;
        }
        else if(dimensions[1] >= yDimension){
            fOffsetY = dimensions[1]-yDimension-offsets[1];
        }
        miniMapSquare.drawSquare();
        terrainCanvas.drawCanvas();
        undergrowthCanvas.drawCanvas();
        canopyCanvas.drawCanvas();
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
            loadingBar.progressProperty().set(0.25);
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
            loadingBar.progressProperty().set(0.5);
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
            loadingBar.progressProperty().set(0.75);
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
            loadingBar.progressProperty().set(1);

        } catch (IOException e) {
            //e.printStackTrace();
            throw new FileNotFoundException("undergrowth file not found or incorrectly formatted.");
        }

        long endTime = System.nanoTime();
        System.out.println("TIME TO READ: " + ((endTime-startTime)/1000000));
    }

    /**
     * Calls the method to add Canvases to stack pane
     * 
     * @param stackPane main viewing stackpane
     */
    public void addCanvases(StackPane stackPane){
        addTerrainCanvas(stackPane);
        addPlantCanvas(stackPane);
    }

    /**
     * Get x dimension of data
     * 
     * @return returns dimention in float
     */
    public float getxDimension() {
        return xDimension;
    }

    /**
     * Get y dimension of data
     * 
     * @return returns dimention in float
     */
    public float getyDimension() {
        return yDimension;
    }

    /**
     * Changes a position from world view to screen view
     * 
     * @param fWorldX world view position x
     * @param fWorldY world view position y
     * @return
     */
    public float[] worldToScreen(float fWorldX, float fWorldY) {
        updateSize();
        float nScreenX = (fWorldX + fOffsetX)*sizeX*scale;
        float nScreenY = (fWorldY + fOffsetY)*sizeY*scale;
        return new float[]{nScreenX,nScreenY};
    }

    /**
     * Changes a position from screen view to world view
     * 
     * @param nScreenX screen view position x
     * @param nScreenY screen view position y
     * @return
     */
    public float[] screenToWorld(float nScreenX, float nScreenY) {
        updateSize();
        float fWorldX = nScreenX/(scale*sizeX) - fOffsetX;
        float fWorldY = nScreenY/(scale*sizeY) - fOffsetY;
        return new float[]{fWorldX,fWorldY};
    }

    /**
     * Updates the scaling of the window size
     */
    public void updateSize() {
        if (terrainCanvas.getWidth() < terrainCanvas.getHeight()){
            sizeX = (float) (terrainCanvas.getHeight()/xDimension);
            sizeY = (float) (terrainCanvas.getHeight()/yDimension);
        }
        else if (terrainCanvas.getWidth() > terrainCanvas.getHeight()){
            sizeX = (float) (terrainCanvas.getWidth()/xDimension);
            sizeY = (float) (terrainCanvas.getWidth()/yDimension);
        }
        else{
            sizeX = (float) (terrainCanvas.getWidth()/xDimension);
            sizeY = (float) (terrainCanvas.getHeight()/yDimension);
        }
    }

    /**
     * Changes canopy Opacity
     * 
     * @param value opacity value
     */
    public void changeCanopyOpacity(double value){
        canopyCanvas.setOpacity(value);
    }

    /**
     * Changes undergrowth Opacity
     * 
     * @param value opacity value
     */
    public void changeUndergrowthOpacity(double value){
        undergrowthCanvas.setOpacity(value);
    }

    /**
     * Class that define the terrain canvas view
     */
    class TerrainCanvas extends Canvas {
        float maxh = -10000.0f, minh = 10000.0f;
        int[] dim = terrainData.getDimensions();
        private final DRAWTYPE drawtype;

        /**
         * Constructor
         * 
         * @param drawtype Defines what this class will be used for
         */
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
        }
        public void drawThread(){
            Drawing drawing = new Drawing(drawtype);
            drawing.start();
        }
        public void addListeners(){
            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
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
                    gc.fillRect(pos[0], pos[1], 1*scale*sizeX+1, 1*scale*sizeX+1);
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
        }
        public void drawThread(){
            Drawing drawing = new Drawing(drawtype);
            drawing.start();
        }
        public void addListeners(){
            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
        }
        public synchronized void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            float width = screenToWorld((float)getWidth(), (float)getHeight())[0]-fOffsetX;
            float height = screenToWorld((float)getWidth(), (float)getHeight())[1]-fOffsetY;

            if (selectedPlant!=null && selectedPlant.enabled()){
                for (Plant plant : plants){
                    if (plant.enabled() && plant!=selectedPlant && plant.containedIn(fOffsetX,fOffsetY,width,height, plantData.getGreatestRadius())){
                        gc.setFill(plantData.getColor(plant.getSpeciesID()));
                        //gc.setStroke(plantData.getColor(plant.getSpeciesID()).darker());
                        float[] pos = worldToScreen(plant.getPosition()[0], plant.getPosition()[1]);
                        double rad = plant.getCanopyRadius()*(sizeX*scale);
                        gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                        //gc.strokeOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                    }
                }
                gc.setFill(Color.BLACK);
                float[] pos = worldToScreen(selectedPlant.getPosition()[0], selectedPlant.getPosition()[1]);
                double rad = selectedPlant.getCanopyRadius()*(sizeX*scale);
                gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
            }
            else{
                for (Plant plant : plants){
                    if (plant.enabled() && plant.containedIn(fOffsetX,fOffsetY,width,height, plantData.getGreatestRadius())){
                        gc.setFill(plantData.getColor(plant.getSpeciesID()));
                        //gc.setStroke(plantData.getColor(plant.getSpeciesID()).darker());
                        float[] pos = worldToScreen(plant.getPosition()[0], plant.getPosition()[1]);
                        double rad = plant.getCanopyRadius()*(sizeX*scale);
                        gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                        //gc.strokeOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
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

    class MiniMapCanvas extends Canvas{
        private DRAWTYPE drawtype;

        public MiniMapCanvas(DRAWTYPE drawtype){
            this.drawtype=drawtype;
        }
        public MiniMapCanvas(){
        }
        public void addListeners(){
            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
        }
        public void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());

            //Terrain drawing..
            float minh = terrainCanvas.minh;
            float maxh = terrainCanvas.maxh;
            gc.clearRect(0, 0, getWidth(), getHeight());
            PixelWriter pw = gc.getPixelWriter();
            for(int x=0; x < terrainData.dimx; x++){
                for(int y=0; y < terrainData.dimy; y++) {
                    // find normalized height value in range
                    float val = (terrainData.getHeight(x, y) - minh) / (maxh - minh);
                    Color color = new Color(val,val,val,1.0f);
                    pw.setColor((int)(x*getWidth()/xDimension), (int)(y*getHeight()/yDimension), color);
                }
            }

            //Undergrowth Drawing..
            for (Plant plant: plantData.getUndergrowth()){
                gc.setFill(plantData.getColor(plant.getSpeciesID()));
                float x = (float) (plant.getPosition()[0]*getWidth()/xDimension);
                float y = (float)(plant.getPosition()[1]*getHeight()/yDimension);
                double rad = plant.getCanopyRadius()*getWidth()/xDimension;
                gc.fillOval((double) x-rad, (double) y-rad, rad *2, rad*2);
            }
            //Canopy Drawing..
            for (Plant plant: plantData.getCanopy()){
                gc.setFill(plantData.getColor(plant.getSpeciesID()));
                float x = (float) (plant.getPosition()[0]*getWidth()/xDimension);
                float y = (float)(plant.getPosition()[1]*getHeight()/yDimension);
                double rad = plant.getCanopyRadius()*getWidth()/xDimension;
                gc.fillOval((double) x-rad, (double) y-rad, rad *2, rad*2);
            }
        }

        public void drawSquare(){
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());

            gc.setStroke(Color.BLACK);
            float[] dimensions = screenToWorld((float)terrainCanvas.getWidth(), (float)terrainCanvas.getHeight());
            gc.strokeRect(-fOffsetX*getWidth()/xDimension, -fOffsetY*getHeight()/yDimension, (dimensions[0]+fOffsetX)*getWidth()/xDimension,(dimensions[1]+fOffsetY)*getHeight()/yDimension);
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
        System.out.println("adding terrain");
        terrainCanvas = new TerrainCanvas(DRAWTYPE.Terrain);
        terrainCanvas.widthProperty().bind(stackPane.widthProperty());
        terrainCanvas.heightProperty().bind(stackPane.heightProperty());
        stackPane.getChildren().add(terrainCanvas);
        terrainCanvas.addListeners();
        System.out.println("done adding terrain");
    }

    public void addPlantCanvas(StackPane stackPane){
        System.out.println("adding canvases");
        canopyCanvas = new PlantCanvas(plantData.getCanopy(), DRAWTYPE.Canopy);
        undergrowthCanvas = new PlantCanvas(plantData.getUndergrowth(), DRAWTYPE.Undergrowth);
        canopyCanvas.widthProperty().bind(stackPane.widthProperty());
        canopyCanvas.heightProperty().bind(stackPane.heightProperty());
        undergrowthCanvas.widthProperty().bind(stackPane.widthProperty());
        undergrowthCanvas.heightProperty().bind(stackPane.heightProperty());

        stackPane.getChildren().addAll(undergrowthCanvas,canopyCanvas);
        canopyCanvas.addListeners();
        undergrowthCanvas.addListeners();
        System.out.println("done adding canvases");
    }

    public void generateMinimap(StackPane miniPane){
        MiniMapCanvas minimapCanvas = new MiniMapCanvas(DRAWTYPE.Minimap);
        minimapCanvas.widthProperty().bind(miniPane.widthProperty());
        minimapCanvas.heightProperty().bind(miniPane.heightProperty());
        minimapCanvas.addListeners();
        minimapCanvas.drawCanvas();

        miniMapSquare = new MiniMapCanvas();
        miniMapSquare.widthProperty().bind(miniPane.widthProperty());
        miniMapSquare.heightProperty().bind(miniPane.heightProperty());
        miniMapSquare.addListeners();

        miniPane.getChildren().addAll(minimapCanvas, miniMapSquare);
    }

    public float sliderToHeight(float sliderNum) {
        float temp = ((sliderNum)/50)*maxHeight;
        if (temp > maxHeight) {
            temp = maxHeight;
        }
        return temp;
    }

    public float heightToSlider(float height) {
        float temp = (height/maxHeight)*50;
        if (temp > maxHeight) {
            temp = maxHeight;
        }
        return temp;
    }

    public void heightFilter(float upper, float lower) {
        filterHeightUpper = upper;
        filterHeightLower = lower;
        Platform.runLater(()->{
            tfLow.setText(Float.toString(lower));
            tfHigh.setText(Float.toString(upper));
        });
        
        plantData.filterHeight(filterHeightLower, filterHeightUpper);
        // try {
        //     TimeUnit.MILLISECONDS.sleep(500);
        // } catch (Exception e) {
        //     System.out.println("OH OH");
        // }
        redrawPlants(); 
    }

    private void redrawUndergrowth() {
        Platform.runLater(()-> undergrowthCanvas.drawCanvas());

    }

    private void redrawCanopy() {
        Platform.runLater(()-> canopyCanvas.drawCanvas());
    }

    private void redrawTerrain() {
        Platform.runLater(()-> terrainCanvas.drawCanvas());
    }

    private void redrawPlants() {
        redrawUndergrowth();
        redrawCanopy();
    }

    private void redraw() {
        redrawUndergrowth();
        redrawCanopy();
        redrawTerrain();
    }

    public void addFilter(int speciesID, VBox filterBox){
        HBox hBox = new HBox();
        ColorPicker colorPicker = new ColorPicker(plantData.getColor(speciesID));
        colorPicker.getStyleClass().add("button");
        colorPicker.setStyle("-fx-color-label-visible: false ;");
        colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            plantData.setColor(speciesID,observable.getValue());
            canopyCanvas.drawCanvas();
            undergrowthCanvas.drawCanvas();
        });

        CheckBox checkBox = new CheckBox(speciesInfo[speciesID].getCommmonName());
        checkBox.setSelected(true);
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (observable.getValue()){
                plantData.unFilterSpecies(speciesID);
            }
            else {
                plantData.filterSpecies(speciesID);
            }
            canopyCanvas.drawCanvas();
            undergrowthCanvas.drawCanvas();
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
        canopyCanvas.drawCanvas();
        undergrowthCanvas.drawCanvas();
    }
    public String getSelectedPlantText(){
        String text = "";
        if (selectedPlant!=null){
            text += speciesInfo[selectedPlant.getSpeciesID()].getCommmonName()+"\n";
            text += speciesInfo[selectedPlant.getSpeciesID()].getLantinName()+"\n";
            text += "Height: "+ selectedPlant.getHeight()+"m\n";
            text += "Canopy radius: "+ selectedPlant.getCanopyRadius()+"m\n";
        }
        return (text);
    }
    public void deleteSelectedPlant(){
        if(selectedPlant!=null){
            selectedPlant.delete();
        }
        undergrowthCanvas.drawCanvas();
        canopyCanvas.drawCanvas();
    }

    public void startFireSim(){

    }
    public void addSeedPoint(){

    }
    public void fTimestep(){

    }
    public void bTimestep(){

    }
    public void endFireSim(){

    }

}

