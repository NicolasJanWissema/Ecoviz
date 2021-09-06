import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Plants {

    // private variables
    private Plant[][] undergrowth;
    private Plant[][] canopy;
    private Plant[][] unfilteredCanopy;
    private Plant[][] unfilteredUndergrowth;
    private PlantCanvas canopyCanvas;
    private PlantCanvas undergrowthCanvas;
    private Color[] plantColors;
    private int dimx,dimy;
    private float xDimension, yDimension;

    class PlantCanvas extends Canvas {
        private Plant[][] unfilteredPlants;

        public PlantCanvas(Plant[][] unfilteredPlants){
            this.unfilteredPlants=unfilteredPlants;

            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
        }
        public void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());

            if (unfilteredPlants==null){
                unfilteredPlants=canopy.clone();
                //filterUndergrowth();
            }
            for(int i=0; i<unfilteredPlants.length;i++){
                gc.setFill(plantColors[i]);
                for (int j=0;j<unfilteredPlants[i].length;j++){
                    float x = (float) (unfilteredPlants[i][j].getPosition()[0]*getWidth()/xDimension);
                    float y = (float)(unfilteredPlants[i][j].getPosition()[1]*getHeight()/yDimension);

                    double rad = (double) (unfilteredPlants[i][j].getCanopyRadius()*getWidth()/xDimension);
                    gc.fillOval((double) x-rad, (double) y-rad, (double)rad*2, rad*2);
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

    //Constructors
    Plants(int numSpecies){
        undergrowth = new Plant[numSpecies][0];
        canopy = new Plant[numSpecies][0];
        generateColors(numSpecies);
    }
    Plants(){}

    public void addPlantCanvas(StackPane stackPane, float xDimension, float yDimension){
        this.xDimension = xDimension;
        this.yDimension = yDimension;
        canopyCanvas = new PlantCanvas(unfilteredCanopy);
        undergrowthCanvas = new PlantCanvas(unfilteredUndergrowth);
        canopyCanvas.widthProperty().bind(stackPane.widthProperty());
        canopyCanvas.heightProperty().bind(stackPane.heightProperty());
        undergrowthCanvas.widthProperty().bind(stackPane.widthProperty());
        undergrowthCanvas.heightProperty().bind(stackPane.heightProperty());

        stackPane.getChildren().addAll(undergrowthCanvas,canopyCanvas);
    }

    //Data generating methods
    public void addSpeciesNumToCanopy(int speciesID,int speciesNum){
        canopy[speciesID]=new Plant[speciesNum];
    }
    public void addSpeciesNumToUndergrowth(int speciesID,int speciesNum){
        undergrowth[speciesID]=new Plant[speciesNum];
    }
    public void addPlantToCanopy(int speciesPos, Plant newPlant){
        canopy[newPlant.getSpeciesID()][speciesPos]=newPlant;
    }
    public void addPlantToUndergrowth(int speciesPos, Plant newPlant){
        undergrowth[newPlant.getSpeciesID()][speciesPos]=newPlant;
    }


    //Filter specific canopy plant. Not currently implemented or useful.
    public void filterCanopyPlant(float[] position){
        for(int i=0; i<unfilteredCanopy.length;i++){
            for (int j=0;j<unfilteredCanopy[i].length;j++){
                if (unfilteredCanopy[i][j].getPosition()==position){
                    unfilteredCanopy[i][j]=null;
                }
            }
        }
    }
    //Filter specific undergrowth plant. Not currently implemented or useful.
    public void filterUndergrowthPlant(float[] position){
        for(int i=0; i<unfilteredUndergrowth.length;i++){
            for (int j=0;j<unfilteredUndergrowth[i].length;j++){
                if (unfilteredUndergrowth[i][j].getPosition()==position){
                    unfilteredUndergrowth[i][j]=null;
                }
            }
        }
    }

    //filter specific species from images.
    public void filterSpecies(int speciesID){
        float[] temp = {dimx,dimy};
        Plant[] emptyArray = { new Plant(0, temp ,0, 0)};
        unfilteredUndergrowth[speciesID]= emptyArray;
        unfilteredCanopy[speciesID]=emptyArray;
    }
    //unfilter specific species from images.
    public void unFilterSpecies(int speciesID){
        unfilteredUndergrowth[speciesID]=undergrowth[speciesID];
        unfilteredCanopy[speciesID]=canopy[speciesID];
    }

    public void getCanopyImageCanvas(int dimx, int dimy, float gridSpacing, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        this.dimx = dimx;
        this.dimy = dimy;
        if (unfilteredCanopy==null){
            unfilteredCanopy=canopy.clone();
            //filterUndergrowth();
        }
        //long startTime = System.nanoTime();
        for(int i=0; i<unfilteredCanopy.length;i++){
            for (int j=0;j<unfilteredCanopy[i].length;j++){
                int x = (int)(unfilteredCanopy[i][j].getPosition()[0]/gridSpacing);
                int y = (int)(unfilteredCanopy[i][j].getPosition()[1]/gridSpacing);
                if (x<dimx && y<dimy){
                    gc.setFill(plantColors[i]);
                    double rad = (double) (unfilteredCanopy[i][j].getCanopyRadius()/gridSpacing);
                    gc.fillOval((double) x-rad, (double) y-rad, (double)rad*2, rad*2);
                }
            }
        }
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
    }


    public void getUndergrowthImageCanvas(int dimx, int dimy, float gridSpacing, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        if (unfilteredUndergrowth==null){
            unfilteredUndergrowth=undergrowth.clone();
            //filterUndergrowth();
        }
        //long startTime = System.nanoTime();
        for(int i=0; i<unfilteredUndergrowth.length;i++){
            for (int j=0;j<unfilteredUndergrowth[i].length;j++){
                int x = (int)(unfilteredUndergrowth[i][j].getPosition()[0]/gridSpacing);
                int y = (int)(unfilteredUndergrowth[i][j].getPosition()[1]/gridSpacing);
                if (x<dimx && y<dimy){
                    gc.setFill(plantColors[i]);
                    double rad = (double) (unfilteredUndergrowth[i][j].getCanopyRadius()/gridSpacing);
                    gc.fillOval((double) x-rad, (double) y-rad, (double)rad*2, rad*2);
                }
            }
        }
    }

    public void generateUnfiltered() {
        if (unfilteredCanopy==null){
            unfilteredCanopy=canopy.clone();
            //filterUndergrowth();
        }
        if (unfilteredUndergrowth==null){
            unfilteredUndergrowth=undergrowth.clone();
            //filterUndergrowth();
        }
    }

    //Generate colors with a gaussian distribution around green.
    private void generateColors(int numSpecies){
        int gaussMultiple=1000;
        plantColors = new Color[numSpecies];
        Random random = new Random();
        double[] gaussian = new double[numSpecies*gaussMultiple];
        for (int i=0; i<gaussian.length;i++){
            double check = random.nextGaussian();
            if (Math.abs(check)>1){
                i--;
            }
            else {
                //System.out.println(check);
                gaussian[i]=check;
            }
        }
        Arrays.sort(gaussian);
        for (int i=0; i<numSpecies; i++){
            //System.out.println(gaussian[i*gaussMultiple]);
            plantColors[i] = new Color(Math.max(0,gaussian[i*gaussMultiple]),1-Math.abs(gaussian[i*gaussMultiple]),Math.max(0,-gaussian[i*gaussMultiple]),1);
        }
        List<Color> colorList = Arrays.asList(plantColors);
        Collections.shuffle(colorList);
        colorList.toArray(plantColors);
    }

    public Plant getUnfilteredUndergrowth(int x, int y) {
        return unfilteredUndergrowth[x][y];
    }

    public Plant getUnfilteredCanopy(int x, int y) {
        return unfilteredCanopy[x][y];
    }

    public int getUndergrowthLength() {
        return unfilteredUndergrowth.length;
    }

    public int getCanopyLength() {
        return unfilteredCanopy.length;
    }

    public int getUndergrowthLength(int i) {
        return unfilteredUndergrowth[i].length;
    }

    public int getCanopyLength(int i) {
        return unfilteredCanopy[i].length;
    }

    public Color getColor(int i) {
        return plantColors[i];
    }
}
