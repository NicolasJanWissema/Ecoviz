import javafx.scene.paint.Color;
import java.util.*;

/**
 * This class stores the bulk of the data for each plant
 * 
 * @author WSSNIC008 KRNHAN003 JCBSHA028
 */
public class Plants {

    // private variables
    private Plant[][] tempUndergrowth;
    private Plant[][] tempCanopy;
    private Plant[] undergrowth;
    private Plant[] canopy;
    private Color[] plantColors;
    private float greatestRadius;
    private boolean completeGeneration;
    private double opacity;
    private float maxHeight;

    /**
     * Constructor
     * 
     * @param numSpecies takes in the number of diffrent species in a file
     */
    Plants(int numSpecies){
        completeGeneration=false;
        tempUndergrowth = new Plant[numSpecies][0];
        tempCanopy = new Plant[numSpecies][0];
        greatestRadius=0;
        System.out.println(numSpecies);
        opacity = 0.7;
        generateColors(numSpecies);

    }

    /**
     * Adds the species number to canopy
     * 
     * @param speciesID
     * @param speciesNum
     */
    public void addSpeciesNumToCanopy(int speciesID,int speciesNum){
        tempCanopy[speciesID]=new Plant[speciesNum];
    }

    /**
     * Adds a plant to the canopy array
     * 
     * @param speciesPos species ID
     * @param newPlant plant to be added
     */
    public void addPlantToCanopy(int speciesPos, Plant newPlant){
        tempCanopy[newPlant.getSpeciesID()][speciesPos]=newPlant;
        if (newPlant.getCanopyRadius()>greatestRadius){
            greatestRadius= newPlant.getCanopyRadius();
        }
    }

    /**
     * Adds Species Number to Undergrowth
     * 
     * @param speciesID
     * @param speciesNum
     */
    public void addSpeciesNumToUndergrowth(int speciesID,int speciesNum){
        tempUndergrowth[speciesID]=new Plant[speciesNum];
    }

    /**
     * Adds a planty to the undergrowth array
     * 
     * @param speciesPos species ID
     * @param newPlant plant to be added
     */
    public void addPlantToUndergrowth(int speciesPos, Plant newPlant){
        tempUndergrowth[newPlant.getSpeciesID()][speciesPos]=newPlant;
        if (newPlant.getCanopyRadius()>greatestRadius){
            greatestRadius= newPlant.getCanopyRadius();
        }
    }

    /**
     * Generates and sorts the data
     */
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

    /**
     * Selects a plant closest to a certain position relative to a plants centre
     * 
     * @param posX x position
     * @param posY y position
     * @return
     */
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

    /**
     * Find the selected plant
     * 
     * @return returns the selected plant
     */
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

    /**
     * Filter the plants by height 
     * 
     * @param min minimal height
     * @param max macimum height
     */
    public void filterHeight(float min, float max){
        if (!completeGeneration){completeGeneration();}
        for (Plant plant : undergrowth){
            plant.checkHeight(min, max);
        }
        for (Plant plant : canopy){
            plant.checkHeight(min, max);
        }
    }

    /**
     * Filters a specices using species ID
     * 
     * @param speciesID species ID
     */
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


    /**
     * Unfilter a specices using species ID
     * 
     * @param speciesID species ID
     */
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

    /**
     * Generates Unique colours for species
     * 
     * @param numSpecies
     */
    private void generateColors(int numSpecies){
        // int gaussMultiple=1000;
        System.out.println("Opacity: "+opacity);
        Color[] pc = {Color.RED, Color.rgb(255, 128, 0, opacity), Color.rgb(153, 153, 0, opacity),
                        Color.rgb(255, 255, 0, opacity), Color.rgb(0, 204, 0, opacity), Color.rgb(0, 153, 76, opacity),
                        Color.rgb(0, 255, 255, opacity), Color.rgb(0, 128, 255, opacity), Color.rgb(128, 255, 0, opacity),
                        Color.rgb(127, 0, 255, opacity), Color.rgb(255, 0, 255, opacity), Color.rgb(255, 0, 127, opacity),
                        Color.rgb(153, 204, 255, opacity), Color.rgb(0, 153, 153, opacity), Color.rgb(102, 255, 178, opacity),
                        Color.rgb(255, 178, 102, opacity)};

        if(pc.length!= numSpecies){
            System.out.println("Fucked");
        }
        plantColors = pc;
        // for(int i = 0; i<numSpecies; i++){
        //     plantColors[i] = pc[i];
        // }
        
        // Random random = new Random();
        // double[] gaussian = new double[numSpecies*gaussMultiple];
        // for (int i=0; i<gaussian.length;i++){
        //     double check = random.nextGaussian()/2;
        //     if (Math.abs(check)>1){
        //         i--;
        //     }
        //     else {
        //         //System.out.println(check);
        //         gaussian[i]=check;
        //     }
        // }
        // Arrays.sort(gaussian);
        // for (int i=0; i<numSpecies; i++){
        //     //System.out.println(gaussian[i*gaussMultiple]);
        //     plantColors[i] = new Color(Math.max(0,gaussian[i*gaussMultiple]),1-Math.abs(gaussian[i*gaussMultiple]),Math.max(0,-gaussian[i*gaussMultiple]),0.4);
        //     plantColors[i] = new Colour();
        // }
        // List<Color> colorList = Arrays.asList(plantColors);
        // Collections.shuffle(colorList);
        // colorList.toArray(plantColors);
    }

    /**
     * Returns the undergrowth and if not generated it will generate it
     * 
     * @return returns array of undergrowth plants
     */
    public Plant[] getUndergrowth(){
        if (!completeGeneration){completeGeneration();}
        return (undergrowth);
    }

    /**
     * Returns the canopy and if not generated it will generate it
     * 
     * @return returns array of undergrowth plants
     */
    public Plant[] getCanopy() {
        if (!completeGeneration){completeGeneration();}
        return (canopy);
    }

    /**
     * Get's colour for specific species
     * 
     * @param i species ID
     * @return returns the colour
     */
    public Color getColor(int i) {
        return plantColors[i];
    }

    /**
     * Set's the colour for a specific species
     * 
     * @param speciesID Species ID
     * @param newColor New Color
     */
    public void setColor(int speciesID, Color newColor){
        plantColors[speciesID]=newColor;
    }

    /**
     * Get the maximum height of all the plants
     * 
     * @return gets height in metres
     */
    public float getHeight() {
        return maxHeight;
    }

    /**
     * Gets the maximum radius of a plant
     * 
     * @return gets radius in metres
     */
    public float getGreatestRadius() {
        return greatestRadius;
    }

    /**
     * Sets the opacity of all the plants
     * 
     * @param o opacity value
     */
    public void setOpacity(Double o){
        opacity=o;
    }
}
