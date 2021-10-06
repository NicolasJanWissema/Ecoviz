/**
 * Stores the data for an individual plant
 * 
 * @author WSSNIC008 KRNHAN003 JCBSHA028
 */
public class Plant implements Comparable<Plant> {

    // Global Variables
    private float xPos, yPos, zPos;
    private int speciesID;
    private float height;
    private float canopyRadius;
    private boolean burnt;
    private boolean speciesEnabled, plantEnabled, heightEnabled;
    private int plantID;

    /**
     * Default Constructor
     */
    public Plant() {

    }
    
    /**
     * Constructor
     * 
     * @param speciesID ID for species
     * @param xPos x position for plant
     * @param yPos y position for plant
     * @param zPos z posiiton for plant
     * @param height height of plant
     * @param canopyRadius canopy radius for plant
     * @param ID plant ID
     */
    public Plant(int speciesID, float xPos, float yPos, float zPos, float height, float canopyRadius, int ID){
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.height = height;
        this.canopyRadius = canopyRadius;
        this.speciesID=speciesID;
        speciesEnabled=true;
        plantEnabled=true;
        heightEnabled = true;
        plantID = ID;
    }

    /**
     * Calculates the distane from this plant to a position
     * 
     * @param x x position
     * @param y y position
     * @return return the distance from the plant
     */
    public float distanceFromPlant(float x,float y){
        return (float)(Math.sqrt(Math.pow(xPos-x,2)+Math.pow(yPos-y,2)));
    }

    /**
     * Checks if a plant is contained into the users view
     * 
     * @param xOffset panning x offset
     * @param yOffset panning y offset
     * @param width 
     * @param height
     * @param greatestRadius the highest radius in the files
     * @return
     */
    public boolean containedIn(float xOffset, float yOffset, float width, float height, float greatestRadius){
        if (enabled()){
            return (xOffset-greatestRadius < xPos && xPos-greatestRadius-xOffset < width && yOffset-greatestRadius < yPos && yPos-greatestRadius-yOffset < height);
        }
        return (false);
    }

    /**
     * Overides the compare method
     * 
     * @param Plant Plant to be compared to
     * @return returns in that is this comparison
     */
    @Override
    public int compareTo(Plant otherPlant){
        if (this.height > otherPlant.getHeight()) {
            // if current object is greater,then return 1
            return 1;
        }
        else if (this.height < otherPlant.getHeight()) {
            // if current object is greater,then return -1
            return -1;
        }
        else {
            // if current object is equal to o,then return 0
            return 0;
        }
    }

    /**
     * Gets position of this plant
     * 
     * @return position in a float array
     */
    public float[] getPosition() {
        return new float[]{xPos,yPos,zPos};
    }

    /**
     * Get the species ID of this plant
     * 
     * @return returns int id
     */
    public int getSpeciesID() {
        return speciesID;
    }

    /**
     * Get the height of this plant
     * 
     * @return returns height in metres
     */
    public float getHeight() {
        return height;
    }

    /**
     * Gets the Plant ID of this plant
     * 
     * @return returns plant ID
     */
    public int getPlantID() {
        return plantID;
    }

    /**
     * Gets the canopy radius of this plant
     * 
     * @return returns canopy radius
     */
    public float getCanopyRadius() {
        return canopyRadius;
    }

    /**
     * Gets if this plant is burnt or not
     * 
     * @return returns boolean that indicated if the plant is burnt
     */
    public boolean isBurnt() {
        return burnt;
    }

    /**
     * Sets the status of the tree
     *
     * @param burnt either burnt (true) or not burnt (false)
     */
    public void setBurnt(boolean burnt) {
        this.burnt = burnt;
    }

    /**
     * Disables a plant. If true the plant won't be disabled on the screen
     */
    public void disablePlant(){
        plantEnabled=false;
    }

    /**
     * Enables plant to indicate it needs to be drawn 
     */
    public void enablePlant(){
        plantEnabled=true;
    }

    /**
     * Disables a spieces. Boolean to keep track if filtered by species
     */
    public void disableSpecies(){
        speciesEnabled=false;
    }

    /**
     * Enable a spieces. Boolean to keep track if filtered by species
     */
    public void enableSpecies(){
        speciesEnabled=true;
    }

    /**
     * Enable height boolean. To keep track if this plant is filtered by height
     */
    public void enableHeight() {
        heightEnabled = true;
    }

    /**
     * Disable height boolean. To keep track if this plant is filtered by height
     */
    public void disableHeight() {
        heightEnabled = false;
    }

    /**
     * Check if a speices in enabled or note
     *
     * @return true if species must be drawin 
     */
    public boolean enabled(){
        return (speciesEnabled && plantEnabled && heightEnabled);
    }

    /**
     * Check if a plant is in a particular range of heights
     * 
     * @param min minimal height
     * @param max maximum height
     */
    public void checkHeight(float min, float max){
        if(height > min && height < max) {
            enableHeight();
        } else {
            disableHeight();
        }
    }

    /**
     * Deletes a plant by disabling it
     */
    public void delete(){
        plantEnabled=false;
    }
}
