import java.awt.Color;

public class Terrain {

    // Global Varibles
    private float[][] height;
    private Color terrainColor;
    private float gridSpacing;
    private float latitude;

    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        height = new float[dimX][dimY];
        this.gridSpacing = gridSpacing;
        this.latitude = latitude;
    }

    // Get & Sets
    public int[] getDimensions(){
        return( new int[]{height.length, height[0].length});
    }
    public void setHeight(int x, int y, float height){
        this.height[x][y] = height;
    }


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