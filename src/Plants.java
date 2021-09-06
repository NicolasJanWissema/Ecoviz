import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Random;

public class Plants {

    // private variables
    private Plant[][] undergrowth;
    private Plant[][] canopy;
    private Plant[][] unfilteredCanopy;
    private Plant[][] unfilteredUndergrowth;
    private Color[] plantColors;
    private int dimx,dimy;

    //Constructors
    Plants(int numSpecies){
        undergrowth = new Plant[numSpecies][0];
        canopy = new Plant[numSpecies][0];
        generateColors(numSpecies);
    }
    Plants(){}

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
        //long startTime = System.nanoTime();
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW ONE CIRCLE: " + ((endTime-startTime)/1000000));
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
        this.dimx = dimx;
        this.dimy = dimy;
        //long startTime = System.nanoTime();
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW ONE CIRCLE: " + ((endTime-startTime)/1000000));
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
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
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
        plantColors = new Color[numSpecies];
        Random random = new Random();
        for (int i=0; i<numSpecies; i++){
            double deviation = random.nextGaussian()/3;
            if (Math.abs(deviation)>1){
                i--;
            }
            else{
                //System.out.println(deviation);
                plantColors[i] = new Color(Math.max(0,deviation),Math.abs(1-1.2*Math.abs(deviation)),Math.max(0,-deviation),1);
            }
        }
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
