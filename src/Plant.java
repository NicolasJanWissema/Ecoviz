public class Plant {

    // Global Variables
    private float xPos, yPos, zPos;
    private Plant NW, NE, SE, SW;
    private int speciesID;
    private float height;
    private float canopyRadius;
    private boolean burnt;

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
    }
    public float distanceFrom(float x,float y){
        return (float)(Math.sqrt(Math.pow(xPos-x,2)+Math.pow(yPos-y,2)));
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


}
