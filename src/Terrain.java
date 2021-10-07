/**
 * Terrain Model Object
 * 
 * @author WSSNIC008 KRNHAN003 JCBSHA028
 */
public class Terrain {

    // Global Varibles
    private final float[][] height;
    private final float gridSpacing;
    int dimx, dimy;
    public final float latitude;

    /**
     * Constructor
     * 
     * @param dimX Is the x value deimention of the terrain
     * @param dimY Is the y value deimention of the terrain
     * @param gridSpacing The value in metres for each grid spacing
     */
    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        this.dimx = dimX;
        this.dimy = dimY;
        height = new float[dimx][dimy];
        this.gridSpacing = gridSpacing;
        this.latitude = latitude;
    }

    /**
     * Sets the height of the tree
     * 
     * @param x x position in metres
     * @param y y positionin metres
     * @param height height in metres
     */
    public void setHeight(int x, int y, float height){
        this.height[x][y] = height;
    }


    /**
     * Returns 2d array dimentions
     * 
     * @return int array with size of 2d array
     */
    public int[] getDimensions(){
        return( new int[]{height.length, height[0].length});
    }

    /**
     * Get y dimention in metres
     * 
     * @return y dimention in metres
     */
    public float getYDimension(){
        return (dimy*gridSpacing);
    }

    /**
     * Get x dimention in metres
     * 
     * @return x dimention in metres
     */
    public float getXDimension(){
        return (dimx*gridSpacing);
    }

    /**
     * Get height of a particular point
     * 
     * @param x x position in array
     * @param y y position in array
     * @return
     */
    public float getHeight(int x, int y) {
        return height[x][y];
    }

    /**
     * Gets the gridspacing number
     * 
     * @return give spacing in metres
     */
    public float getGridSpacing() {
        return gridSpacing;
    }

}