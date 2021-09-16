public class Plant implements Comparable<Plant> {

    // Global Variables
    private float xPos, yPos, zPos;
    private int speciesID;
    private float height;
    private float canopyRadius;
    private boolean burnt;
    private boolean speciesEnabled, plantEnabled, heightEnabled;

    // Constructors
    public Plant() {

    }
    public Plant(int speciesID, float xPos, float yPos, float zPos, float height, float canopyRadius){
        this.xPos = xPos;
        this.yPos = yPos;
        this.zPos = zPos;
        this.height = height;
        this.canopyRadius = canopyRadius;
        this.speciesID=speciesID;
        speciesEnabled=true;
        plantEnabled=true;
        heightEnabled = true;
    }
    public float distanceFromPlant(float x,float y){
        return (float)(Math.sqrt(Math.pow(xPos-x,2)+Math.pow(yPos-y,2)));
    }
    public boolean containedIn(float xOffset, float yOffset, float width, float height){
        if (enabled()){
            return (xOffset < xPos && xPos - xOffset < width && yOffset < yPos && yPos - yOffset < height);
        }
        return (false);
    }
    @Override public int compareTo(Plant otherPlant){
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

    // Getter & Setters
    public float[] getPosition() {
        return new float[]{xPos,yPos,zPos};
    }

    public int getSpeciesID() {
        return speciesID;
    }

    public float getHeight() {
        return height;
    }

    public float getCanopyRadius() {
        return canopyRadius;
    }

    public boolean isBurnt() {
        return burnt;
    }

    public void setBurnt(boolean burnt) {
        this.burnt = burnt;
    }

    public void disablePlant(){
        plantEnabled=false;
    }
    public void enablePlant(){
        plantEnabled=true;
    }
    public void disableSpecies(){
        speciesEnabled=false;
    }
    public void enableSpecies(){
        speciesEnabled=true;
    }
    public void enableHeight() {
        heightEnabled = true;
    }
    public void disableHeight() {
        heightEnabled = false;
    }
    public boolean enabled(){
        return (speciesEnabled && plantEnabled && heightEnabled);
    }

    public void checkHeight(float min, float max){
        if(height > min && height < max) {
            enableHeight();
        } else {
            disableHeight();
        }
    }
}
