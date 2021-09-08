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
