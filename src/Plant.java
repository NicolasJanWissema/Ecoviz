public class Plant {

    // Global Variables
    private float[] position;
    private int speciesID;
    private float height;
    private float canopyRadius;
    private boolean burnt;

    // Constructors
    public Plant() {

    }
    public Plant(int speciesID, float[] position,float height, float canopyRadius){
        this.position=position;
        this.height = height;
        this.canopyRadius = canopyRadius;
        this.speciesID=speciesID;
    }

    // Getter & Setters
    public float[] getPosition() {
        return position;
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
