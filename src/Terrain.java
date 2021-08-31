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

    // Panning and zooming
    float fOffsetX = 0.0f;
    float fOffsetY = 0.0f;

    //Constructor
    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        this.dimx = dimX;
        this.dimy = dimY;
        height = new float[dimx][dimy];
        this.gridSpacing = gridSpacing;
        this.latitude = latitude;
    }
    
    public int[] worldToScreen(float fWorldX, float fWorldY) {
        int nScreenX = (int) (fWorldX - fOffsetX);
        int nScreenY = (int) (fWorldY - fOffsetY);
        int[] temp = {nScreenX,nScreenY};
        return temp;
    }

    public float[] screenToWorld(int nScreenX, int nScreenY) {
        float fWorldX = (float) (nScreenX + fOffsetX);
        float fWorldY = (float) (nScreenY + fOffsetY);
        float[] temp = {fWorldX,fWorldY};
        return temp;

    }

    // Get & Sets
    public int[] getDimensions(){
        return( new int[]{height.length, height[0].length});
    }
    public void setHeight(int x, int y, float height){
        this.height[x][y] = height;
    }

    public void deriveImageCanvasOffset(Canvas img, float fOffsetX, float fOffsetY) {
        this.fOffsetX = fOffsetX;
        this.fOffsetY = fOffsetY;
        float maxh = -10000.0f, minh = 10000.0f;
        GraphicsContext gc = img.getGraphicsContext2D();
        gc.clearRect(0, 0, img.getWidth(), img.getHeight());
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
                int tempX = (int) (x - fOffsetX);
                int tempY = (int) (y - fOffsetY);
                //if (tempX >= 0 && tempX < dimx && tempY >= 0 && tempY < dimy) {
                    pw.setColor(tempX, tempY, color);
                //}
                
            }

    }

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
    public WritableImage getImage() {
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

    public float getGridSpacing() {
        return gridSpacing;
    }

}