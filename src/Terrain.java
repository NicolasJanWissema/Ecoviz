import java.awt.Color;

public class Terrain {

    // Global Varibles
    private float[][] height;
    private Color terrainColor;
    private float gridSpacing;

    // Get & Sets
    public float[][] getHeight() {
        return height;
    }

    public void setHeight(float[][] height) {
        this.height = height;
    }

    public Color getTerrainColor() {
        return terrainColor;
    }

    public float getGridSpacing() {
        return gridSpacing;
    }

}