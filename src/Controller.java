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
        fOffsetX -= (mouseX - fStartPanX)/(scaleX*sizeX);
        fOffsetY -= (mouseY - fStartPanY)/(scaleY*sizeY);
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
        } else if (scaleX>1){
            scaleX *= 0.9f;
            scaleY *= 0.9f;
        }
        float mouseX1 = (float) event.getX();
        float mouseY1 = (float) event.getY();
        float[] afterZoom = screenToWorld((int) mouseX1, (int) mouseY1);
        fOffsetX += (beforeZoom[0] - afterZoom[0]);
        fOffsetY += (beforeZoom[1] - afterZoom[1]);
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
        addTerrainCanvas(stackPane);
        addPlantCanvas(stackPane);
    }

    public float getxDimension() {
        return xDimension;
    }

    public float getyDimension() {
        return yDimension;
    }

    public int[] worldToScreen(float fWorldX, float fWorldY) {
        updateSize();
        int nScreenX = (int) ((fWorldX - fOffsetX)*(scaleX*sizeX));
        //int nScreenX = (int) ((fWorldX - fOffsetX)*scaleX);
        int nScreenY = (int) ((fWorldY - fOffsetY)*(scaleY*sizeY));
        //int nScreenY = (int) ((fWorldY - fOffsetY)*scaleY);
        return new int[]{nScreenX,nScreenY};
    }

    public float[] screenToWorld(float nScreenX, float nScreenY) {
        updateSize();
        float fWorldX = nScreenX/(scaleX*sizeX) + fOffsetX;
        //float fWorldX = (float) (nScreenX/scaleX + fOffsetX);
        float fWorldY = nScreenY/(scaleY*sizeY) + fOffsetY;
        //float fWorldY = (float) (nScreenY/scaleY + fOffsetY);
        return new float[]{fWorldX,fWorldY};

    }

    public void updateSize() {
        sizeX = (float) (terrainCanvas.getWidth()/xDimension)*terrainData.getGridSpacing();
        sizeY = (float) (terrainCanvas.getHeight()/yDimension)*terrainData.getGridSpacing();
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
        public TerrainCanvas(){
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

        public void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            PixelWriter pw = gc.getPixelWriter();

            //Redraw canvas
            for(int x=0; x < dim[0]; x++){
                for(int y=0; y < dim[1]; y++) {
                    // find normalized height value in range
                    float val = (terrainData.getHeight(x, y) - minh) / (maxh - minh);
                    Color color = new Color(val,val,val,1.0f);
                    int[] pos = worldToScreen(x, y);
                   // pw.setColor((int)(x*getWidth()/xDimension), (int)(y*getHeight()/yDimension), color);
                    pw.setColor(pos[0], pos[1], color);
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
        private final Plant[][] unfilteredPlants;

        public PlantCanvas(Plant[][] unfilteredPlants){
            this.unfilteredPlants=unfilteredPlants;

            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
        }
        public void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());

            plantData.generateUnfiltered();
            if (selectedPlant!=null){
                gc.setFill(Color.BLACK);
                int[] pos1 = worldToScreen(selectedPlant.getPosition()[0]/terrainData.getGridSpacing(), selectedPlant.getPosition()[1]/terrainData.getGridSpacing());
                double rad1 = (double) (selectedPlant.getCanopyRadius()*scaleX*getHeight()/yDimension);
                gc.fillOval((double) pos1[0]-rad1, (double) pos1[1]-rad1, rad1 *2, rad1*2);

                for(int i=0; i<unfilteredPlants.length;i++){
                    gc.setFill(plantData.getColor(i));
                    for (int j=0;j<unfilteredPlants[i].length;j++){
                        if (unfilteredPlants[i][j]==selectedPlant){
                            gc.setFill(Color.BLACK);
                            int[] pos = worldToScreen(unfilteredPlants[i][j].getPosition()[0]/terrainData.getGridSpacing(), unfilteredPlants[i][j].getPosition()[1]/terrainData.getGridSpacing());
                            double rad = (double) (unfilteredPlants[i][j].getCanopyRadius()*scaleX*getHeight()/yDimension);
                            gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                            gc.setFill(plantData.getColor(i));
                        }
                        else{
                            int[] pos = worldToScreen(unfilteredPlants[i][j].getPosition()[0]/terrainData.getGridSpacing(), unfilteredPlants[i][j].getPosition()[1]/terrainData.getGridSpacing());
                            double rad = (double) (unfilteredPlants[i][j].getCanopyRadius()*scaleX*getHeight()/yDimension);
                            gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                        }
                    }
                }
            }
            else{
                for(int i=0; i<unfilteredPlants.length;i++){
                    gc.setFill(plantData.getColor(i));
                    for (int j=0;j<unfilteredPlants[i].length;j++){
                        int[] pos = worldToScreen(unfilteredPlants[i][j].getPosition()[0]/terrainData.getGridSpacing(), unfilteredPlants[i][j].getPosition()[1]/terrainData.getGridSpacing());
                        double rad = (double) (unfilteredPlants[i][j].getCanopyRadius()*scaleX*getHeight()/yDimension);
                        gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                    }
                }
            }
            //long endTime = System.nanoTime();
            //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
        }
        public void highLight(){
            GraphicsContext gc = getGraphicsContext2D();
            for(int i=0; i<unfilteredPlants.length;i++){
                for (int j=0;j<unfilteredPlants[i].length;j++){
                    if (unfilteredPlants[i][j]==selectedPlant){
                        System.out.println("found.");
                        gc.setFill(Color.BLACK);
                        int[] pos = worldToScreen(unfilteredPlants[i][j].getPosition()[0]/terrainData.getGridSpacing(), unfilteredPlants[i][j].getPosition()[1]/terrainData.getGridSpacing());
                        double rad = (double) (unfilteredPlants[i][j].getCanopyRadius()*scaleX*getHeight()/yDimension);
                        gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
                    }
                }
            }
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

    public void addTerrainCanvas(StackPane stackPane){
        terrainCanvas = new TerrainCanvas();
        terrainCanvas.widthProperty().bind(stackPane.widthProperty());
        terrainCanvas.heightProperty().bind(stackPane.heightProperty());
        stackPane.getChildren().add(terrainCanvas);
    }

    public void addPlantCanvas(StackPane stackPane){
        plantData.generateUnfiltered();
        canopyCanvas = new PlantCanvas(plantData.getUnfilteredCanopy());
        undergrowthCanvas = new PlantCanvas(plantData.getUnfilteredUndergrowth());
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
        plantData.generateUnfiltered();
        for(int i=0; i<plantData.getUnfilteredUndergrowth().length;i++){
            gc.setFill(plantData.getColor(i));
            for (int j=0;j<plantData.getUnfilteredUndergrowth()[i].length;j++){
                float x = (float) (plantData.getUnfilteredUndergrowth()[i][j].getPosition()[0]*minimapCanvas.getWidth()/xDimension);
                float y = (float)(plantData.getUnfilteredUndergrowth()[i][j].getPosition()[1]*minimapCanvas.getHeight()/yDimension);
                double rad = (double) (plantData.getUnfilteredUndergrowth()[i][j].getCanopyRadius()*minimapCanvas.getWidth()/xDimension);
                gc.fillOval((double) x-rad, (double) y-rad, rad *2, rad*2);
            }
        }
        //Canopy Drawing..
        plantData.generateUnfiltered();
        for(int i=0; i<plantData.getUnfilteredCanopy().length;i++){
            gc.setFill(plantData.getColor(i));
            for (int j=0;j<plantData.getUnfilteredCanopy()[i].length;j++){
                float x = (float) (plantData.getUnfilteredCanopy()[i][j].getPosition()[0]*minimapCanvas.getWidth()/xDimension);
                float y = (float)(plantData.getUnfilteredCanopy()[i][j].getPosition()[1]*minimapCanvas.getHeight()/yDimension);
                double rad = (double) (plantData.getUnfilteredCanopy()[i][j].getCanopyRadius()*minimapCanvas.getWidth()/xDimension);
                gc.fillOval((double) x-rad, (double) y-rad, rad *2, rad*2);
            }
        }


        miniPane.getChildren().add(minimapCanvas);
    }

    public void addFilter(int speciesID, VBox filterBox){
        HBox hBox = new HBox();
        ColorPicker colorPicker = new ColorPicker(plantData.getColor(speciesID));
        colorPicker.getStyleClass().add("button");
        //colorPicker.setStyle(ColorPicker.STYLE_CLASS_BUTTON);
        //colorPicker.styleProperty().setValue(ColorPicker.STYLE_CLASS_BUTTON);
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
        float[] pos = screenToWorld(256/terrainData.getGridSpacing(),256/terrainData.getGridSpacing());
        //selectedPlant = plantData.selectPlant(pos[0],pos[1]);
        selectedPlant = new Plant(0,pos[0],pos[1],0,1,10 );
        //System.out.println(speciesInfo[selectedPlant.getSpeciesID()].getCommmonName());
        //System.out.println(speciesInfo[selectedPlant.getSpeciesID()].getLantinName());
        System.out.println("clicked position: "+pos[0]+" , "+pos[1]);
        System.out.println("plant position: "+selectedPlant.getPosition()[0]+" , "+selectedPlant.getPosition()[1]);
        //System.out.println(selectedPlant.getCanopyRadius());
        canopyCanvas.drawCanvas();
        undergrowthCanvas.drawCanvas();
    }

}

