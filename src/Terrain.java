public class Terrain {

    // Global Varibles
    private final float[][] height;
    private final float gridSpacing;
    int dimx, dimy;
    public final float latitude;

    //Constructor
    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        this.dimx = dimX;
        this.dimy = dimY;
        height = new float[dimx][dimy];
        this.gridSpacing = gridSpacing;
        this.latitude = latitude;
    }

    // Set height method for generation
    public void setHeight(int x, int y, float height){
        this.height[x][y] = height;
    }


    //Get methods
    public int[] getDimensions(){
        return( new int[]{height.length, height[0].length});
    }

    public float getYDimension(){
        return (dimy*gridSpacing);
    }

    public float getXDimension(){
        return (dimx*gridSpacing);
    }

    public float getHeight(int x, int y) {
        return height[x][y];
    }

    public float getGridSpacing() {
        return gridSpacing;
    }

}