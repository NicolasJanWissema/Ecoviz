import javafx.scene.paint.Color;

import java.util.*;

public class Plants {

    // private variables
    private Plant[][] tempUndergrowth;
    private Plant[][] tempCanopy;
    private Plant[] undergrowth;
    private Plant[] canopy;
    private Color[] plantColors;
    private float greatestRadius;
    private boolean completeGeneration;
    private float maxHeight;

    //Constructors
    Plants(int numSpecies){
        completeGeneration=false;
        tempUndergrowth = new Plant[numSpecies][0];
        tempCanopy = new Plant[numSpecies][0];
        generateColors(numSpecies);
    }

    //Data generating methods
    public void addSpeciesNumToCanopy(int speciesID,int speciesNum){
        tempCanopy[speciesID]=new Plant[speciesNum];
    }
    public void addPlantToCanopy(int speciesPos, Plant newPlant){
        tempCanopy[newPlant.getSpeciesID()][speciesPos]=newPlant;
        if (newPlant.getCanopyRadius()>greatestRadius){
            greatestRadius= newPlant.getCanopyRadius();
        }
    }

    public void addSpeciesNumToUndergrowth(int speciesID,int speciesNum){
        tempUndergrowth[speciesID]=new Plant[speciesNum];
    }
    public void addPlantToUndergrowth(int speciesPos, Plant newPlant){
        tempUndergrowth[newPlant.getSpeciesID()][speciesPos]=newPlant;
        if (newPlant.getCanopyRadius()>greatestRadius){
            greatestRadius= newPlant.getCanopyRadius();
        }
    }

    public void completeGeneration(){
        long startTime = System.nanoTime();
        int arrayLength=0;
        int count=0;
        for (Plant[] plants : tempUndergrowth) {
            arrayLength+=plants.length;
        }
        System.out.println("number of undergrowth plants: "+arrayLength);
        undergrowth=new Plant[arrayLength];
        for (Plant[] plants : tempUndergrowth){
            for (Plant plant : plants){
                undergrowth[count]=plant;
                count++;
            }
        }
        Arrays.sort(undergrowth);

        arrayLength=0;
        count=0;
        for (Plant[] plants : tempCanopy) {
            arrayLength+=plants.length;
        }
        System.out.println("number of canopy plants: "+arrayLength);
        canopy=new Plant[arrayLength];
        for (Plant[] plants : tempCanopy){
            for (Plant plant : plants){
                canopy[count]=plant;
                count++;
            }
        }
        Arrays.sort(canopy);
        tempCanopy=null;
        tempUndergrowth=null;
        completeGeneration=true;

        long endTime = System.nanoTime();
        System.out.println("TIME TO SORT: " + ((endTime-startTime)/1000000));
    }

    public Plant selectPlant(float posX, float posY){
        if (!completeGeneration){completeGeneration();}
        Plant selectedPlant = getEnabledPlant();
        if (selectedPlant!=null){
            for (Plant plant : undergrowth){
                if (plant.distanceFromPlant(posX,posY)<selectedPlant.distanceFromPlant(posX,posY) && plant.enabled()){
                    selectedPlant=plant;
                }
            }
            for (Plant plant : canopy){
                if (plant.distanceFromPlant(posX,posY)<selectedPlant.distanceFromPlant(posX,posY) && plant.enabled()){
                    selectedPlant=plant;
                }
            }
            return (selectedPlant);
        }
        return (null);
    }
    private Plant getEnabledPlant(){
        for (Plant plant : undergrowth){
            if (plant.enabled()){
                return (plant);
            }
        }
        for (Plant plant : canopy){
            if (plant.enabled()){
                return (plant);
            }
        }
        return (null);
    }

    //Filter Height
    public void filterHeight(float min, float max){
        if (!completeGeneration){completeGeneration();}
        for (Plant plant : undergrowth){
            plant.checkHeight(min, max);
        }
        for (Plant plant : canopy){
            plant.checkHeight(min, max);
        }
    }

   

    //filter specific species from images.
    public void filterSpecies(int speciesID){
        if (!completeGeneration){completeGeneration();}
        for (Plant plant : undergrowth){
            if(plant.getSpeciesID()==speciesID){
                plant.disableSpecies();
            }
        }
        for (Plant plant : canopy){
            if(plant.getSpeciesID()==speciesID){
                plant.disableSpecies();
            }
        }
    }
    //unfiltered specific species from images.
    public void unFilterSpecies(int speciesID){
        if (!completeGeneration){completeGeneration();}
        for (Plant plant : undergrowth){
            if(plant.getSpeciesID()==speciesID){
                plant.enableSpecies();
            }
        }
        for (Plant plant : canopy){
            if(plant.getSpeciesID()==speciesID){
                plant.enableSpecies();
            }
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
            plantColors[i] = new Color(Math.max(0,gaussian[i*gaussMultiple]),1-Math.abs(gaussian[i*gaussMultiple]),Math.max(0,-gaussian[i*gaussMultiple]),0.5);
        }
        List<Color> colorList = Arrays.asList(plantColors);
        Collections.shuffle(colorList);
        colorList.toArray(plantColors);
    }

    public Plant[] getUndergrowth(){
        if (!completeGeneration){completeGeneration();}
        return (undergrowth);
    }

    public Plant[] getCanopy() {
        if (!completeGeneration){completeGeneration();}
        return (canopy);
    }

    public Color getColor(int i) {
        return plantColors[i];
    }

    public void setColor(int speciesID, Color newColor){
        plantColors[speciesID]=newColor;
    }

    public float getHeight() {
        return maxHeight;
    }
}
