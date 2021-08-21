public class Plant {

    // Global Variables
    private float[] postion;
    private int speciesID;
    private float height;
    private float canopyRadius;
    private boolean burnt;

    // Constructors
    public Plant() {

    }

    // Getter & Setters
    public float[] getPostion() {
        return postion;
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