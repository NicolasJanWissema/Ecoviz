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
    private final Plant[][] undergrowth;
    private final Plant[][] canopy;
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

    //Data generating methods
    public void addSpeciesNumToCanopy(int speciesID,int speciesNum){
        canopy[speciesID]=new Plant[speciesNum];
    }
    public void addPlantToCanopy(int speciesPos, Plant newPlant){
        canopy[newPlant.getSpeciesID()][speciesPos]=newPlant;
    }

    public void addSpeciesNumToUndergrowth(int speciesID,int speciesNum){
        undergrowth[speciesID]=new Plant[speciesNum];
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
    public Plant selectPlant(float posX, float posY){
        Plant selectedPlant = new Plant(0,0,0,0,0,0);
        for(int i=0; i<unfilteredUndergrowth.length;i++){
            for (int j=0;j<unfilteredUndergrowth[i].length;j++){
                if (unfilteredUndergrowth[i][j].distanceFrom(posX,posY)<selectedPlant.distanceFrom(posX,posY)){
                    selectedPlant = unfilteredUndergrowth[i][j];
                }
            }
        }
        for(int i=0; i<unfilteredCanopy.length;i++){
            for (int j=0;j<unfilteredCanopy[i].length;j++){
                if (unfilteredCanopy[i][j].distanceFrom(posX,posY)<selectedPlant.distanceFrom(posX,posY)){
                    selectedPlant = unfilteredCanopy[i][j];
                }
            }
        }
        return (selectedPlant);
    }

    //filter specific species from images.
    public void filterSpecies(int speciesID){
        Plant[] emptyArray = { new Plant()};
        unfilteredUndergrowth[speciesID]= emptyArray;
        unfilteredCanopy[speciesID]=emptyArray;
    }
    //unfilter specific species from images.
    public void unFilterSpecies(int speciesID){
        unfilteredUndergrowth[speciesID]=undergrowth[speciesID];
        unfilteredCanopy[speciesID]=canopy[speciesID];
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
            double check = random.nextGaussian()/2;
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

    public Plant[][] getUnfilteredUndergrowth() {
        return unfilteredUndergrowth;
    }

    public Plant[][] getUnfilteredCanopy() {
        return unfilteredCanopy;
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

    public void setColor(int speciesID, Color newColor){
        plantColors[speciesID]=newColor;
    }
}
