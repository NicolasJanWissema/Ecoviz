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

    //Generate colors with a gaussian distribution around green.
    private void generateColors(int numSpecies){
        plantColors = new Color[numSpecies];
        Random random = new Random();
        for (int i=0; i<numSpecies; i++){
            double deviation = random.nextGaussian()/3;
            if (deviation>1){
                i--;
            }
            else{
                //System.out.println(deviation);
                plantColors[i] = new Color(Math.max(0,deviation),Math.abs(1-Math.abs(deviation)),Math.max(0,-deviation),1);
            }
        }
    }
}
