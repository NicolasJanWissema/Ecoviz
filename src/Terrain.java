import java.awt.Color;
import java.awt.image.BufferedImage;

public class Terrain {

    // Global Varibles
    private float[][] height;
    int dimx, dimy;
    private Color terrainColor;
    private float gridSpacing;
    private float latitude;

    public BufferedImage img;

    //Constructor
    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        this.dimx = dimX;
        this.dimy = dimY;
        height = new float[dimx][dimy];
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
    void deriveImage() {
        img = new BufferedImage(dimx, dimy, BufferedImage.TYPE_INT_ARGB);
        float maxh = -10000.0f, minh = 10000.0f;

        // determine range of heights
        for(int x=0; x < dimx; x++)
            for(int y=0; y < dimy; y++) {
                float h = height[x][y];
                if(h > maxh)
                    maxh = h;
                if(h < minh)
                    minh = h;
            }

        for(int x=0; x < dimx; x++)
            for(int y=0; y < dimy; y++) {
                // find normalized height value in range
                float val = (height[x][y] - minh) / (maxh - minh);
                Color col = new Color(val, val, val, 1.0f);
                img.setRGB(x, y, col.getRGB());
            }
    }

    // get greyscale image
    public BufferedImage getImage() {
        return img;
    }

    public float[][] getHeight() {
        return height;
    }

    public float getHeight(int x, int y) {
        return height[x][y];
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