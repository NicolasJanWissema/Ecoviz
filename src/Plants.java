import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Random;

public class Plants {

    // Global
    private Plant[][] undergrowth;
    private Plant[][] canopy;
    private Plant[][] unfiltered;
    private Color[] plantColors;
    private WritableImage wImage;
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

    //Filtering Methods, assuming data generation has been completed.
    public Plant[][] filterPlant(float[] position){
        for(int i=0; i<unfiltered.length;i++){
            for (int j=0;j<unfiltered[i].length;j++){
                if (unfiltered[i][j].getPosition()==position){
                    unfiltered[i][j]=null;
                }
            }
        }
        return (unfiltered);
    }
    public Plant[][] filterSpecies(int speciesID){
        unfiltered[speciesID]=new Plant[0];
        return (unfiltered);
    }
    public Plant[][] filterCanopy(){
        unfiltered=undergrowth;
        return (unfiltered);
    }
    public Plant[][] filterUndergrowth(){
        unfiltered=canopy;
        return (unfiltered);
    }
    public void generateUnfiltered(){
        unfiltered=new Plant[canopy.length][0];
        for (int i=0; i< unfiltered.length; i++){
            unfiltered[i] = new Plant[canopy[i].length+ undergrowth[i].length];

            for (int j=0; j< canopy[i].length; j++){
                unfiltered[i][j]=canopy[i][j];
            }
            for (int j= canopy[i].length; j< unfiltered[i].length; j++){
                unfiltered[i][j]=undergrowth[i][j-canopy[i].length];
            }
        }
    }

    //Generate coloured map for plants
    public WritableImage getPlantImage(int dimx, int dimy, float gridSpacing){
        WritableImage img = new WritableImage(dimx, dimy);
        PixelWriter pw = img.getPixelWriter();
        //System.out.println("Generating plant map image.");
        if (unfiltered==null){
            generateUnfiltered();
        }

        for(int i=0; i<unfiltered.length;i++){
            for (int j=0;j<unfiltered[i].length;j++){
                int x = (int)(unfiltered[i][j].getPosition()[0]/gridSpacing);
                int y = (int)(unfiltered[i][j].getPosition()[1]/gridSpacing);
                if (x<dimx && y<dimy){
                    pw.setColor(x, y, plantColors[i]);
                }
            }
        }
        return (img);
    }

    public WritableImage getPlantImageCircle(int dimx, int dimy, float gridSpacing) {
        wImage = new WritableImage(dimx, dimy);
        this.dimx = dimx;
        this.dimy = dimy;
        //long startTime = System.nanoTime();
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW ONE CIRCLE: " + ((endTime-startTime)/1000000));
        if (unfiltered==null){
            generateUnfiltered();
            //filterUndergrowth();
        }
        //long startTime = System.nanoTime();
        for(int i=0; i<unfiltered.length;i++){
            for (int j=0;j<unfiltered[i].length;j++){
                int x = (int)(unfiltered[i][j].getPosition()[0]/gridSpacing);
                int y = (int)(unfiltered[i][j].getPosition()[1]/gridSpacing);
                if (x<dimx && y<dimy){
                    circleBres(x, y, (int)(unfiltered[i][j].getCanopyRadius()/gridSpacing), plantColors[i]);
                }
            }
        }
        //long endTime = System.nanoTime();
        //System.out.println("TIME TO DRAW ONE CIRCLE: " + ((endTime-startTime)/1000000));
        return wImage;
    }

    public void circleBres(int xc, int yc, int r, Color col){
        int x = 0, y = r;
        int d = 3 - 2 * r;
        drawCircle(xc, yc, x, y,col);
        while (y >= x) {
            x++;
            if (d > 0) {
                y--;
                d = d + 4 * (x -y) + 10;
            } else {
                d = d + 4 * x + 6;
            }
            drawCircle(xc, yc, x, y, col);
        }
    }

    public void drawCircle(int xc, int yc, int x, int y, Color col) {
        drawLine(xc-x, xc+x, yc+y, col);
        drawLine(xc-x, xc+x, yc-y, col);
        drawLine(xc-y, xc+y, yc+x, col);
        drawLine(xc-y, xc+y, yc-x, col);
    }

    public void drawLine(int x0, int x1, int y, Color col) {
        PixelWriter pw = wImage.getPixelWriter();
        if (y < dimy && y >= 0) {
            for (int i = x0; i <= x1; i++ ) {
                if (i < dimx && i >= 0) {
                    pw.setColor(i, y, col);
                } 
            }
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
}
