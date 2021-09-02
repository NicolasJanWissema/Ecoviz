import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Terrain {

    // Global Varibles
    private float[][] height;
    int dimx, dimy;
    private float gridSpacing;
    private float latitude;
    public WritableImage img;

    //Constructor
    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        this.dimx = dimX;
        this.dimy = dimY;
        height = new float[dimx][dimy];
        this.gridSpacing = gridSpacing;
        this.latitude = latitude;
    }

    // Get & Sets


    public void deriveImageCanvas(Canvas img) {
        float maxh = -10000.0f, minh = 10000.0f;
        GraphicsContext gc = img.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

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
                Color color = new Color(val,val,val,1.0f);
                pw.setColor(x, y, color);
            }

    }

    // get greyscale image

    public int[] getDimensions(){
        return( new int[]{height.length, height[0].length});
    }
    public void setHeight(int x, int y, float height){
        this.height[x][y] = height;
    }

    public float getYDimension(){
        return (dimy*gridSpacing);
    }
    public float getXDimension(){
        return (dimx*gridSpacing);
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

    public float getGridSpacing() {
        return gridSpacing;
    }

}