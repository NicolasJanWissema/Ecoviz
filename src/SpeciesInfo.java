import javafx.scene.control.CheckBox;

/**
 * Stores information on feach species
 * 
 * @author WSSNIC008 KRNHAN003 JCBSHA028
 */
public class SpeciesInfo {

    // Global Variables
    private float[] height;
    private float avgCanopyRad;
    private String commmonName;
    private String lantinName;
    public CheckBox filterBox;

    /**
     * Constructor 
     * 
     * @param commmonName common name in string
     * @param lantinName latin name in string
     */
    public SpeciesInfo(String commmonName, String lantinName){
        this.commmonName=commmonName;
        this.lantinName=lantinName;
    }

    /**
     * Constructor
     * 
     * @param speciesInfo gets common name and lantin name from a single string
     */
    public SpeciesInfo(String speciesInfo){
        commmonName = speciesInfo.split("“")[1].split("”")[0];
        lantinName = speciesInfo.split("“")[2].split("”")[0];
    }

    /**
     * Set max height of a species
     * 
     * @param minHeight
     * @param maxHeight
     */
    public void setHeight(float minHeight, float maxHeight){
        height = new float[]{minHeight,maxHeight};
    }

    /**
     * Sets the average canopy radius
     * 
     * @param avgCanopyRad average canopy radius
     */
    public void setAvgCanopyRad(float avgCanopyRad){
        this.avgCanopyRad=avgCanopyRad;
    }

    /**
     * Returns height in metres
     * 
     * @return height in metres
     */
    public float[] getHeight() {
        return height;
    }

    /**
     * Returns average canopy radius
     * 
     * @return average canopy radius
     */
    public float getAvgCanopyRad() {
        return avgCanopyRad;
    }

    /**
     * Returns common name
     * 
     * @return common name
     */
    public String getCommmonName() {
        return commmonName;
    }

    /**
     * Returns latin name
     * 
     * @return latin name
     */
    public String getLantinName() {
        return lantinName;
    }

}