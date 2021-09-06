import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Terrain {

    // Global Varibles
    private float[][] height;
    int dimx, dimy;
    private float gridSpacing;
    private float latitude;
    private TerrainCanvas terrainCanvas;
    private float xDimension, yDimension;

    //Constructor
    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        this.dimx = dimX;
        this.dimy = dimY;
        height = new float[dimx][dimy];
        this.gridSpacing = gridSpacing;
        this.latitude = latitude;
    }
    class TerrainCanvas extends Canvas {
        float maxh = -10000.0f, minh = 10000.0f;

        public TerrainCanvas(){
            for(int x=0; x < dimx; x++){
                for(int y=0; y < dimy; y++) {
                    float h = height[x][y];
                    if(h > maxh)
                        maxh = h;
                    if(h < minh)
                        minh = h;
                }
            }
            // Redraw canvas when size changes.
            widthProperty().addListener(evt -> drawCanvas());
            heightProperty().addListener(evt -> drawCanvas());
            getGraphicsContext2D().setEffect(new Blend(BlendMode.DARKEN));
        }

        public void drawCanvas() {
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight());
            PixelWriter pw = gc.getPixelWriter();

            //Redraw canvas
            for(int x=0; x < dimx; x++){
                for(int y=0; y < dimy; y++) {
                    // find normalized height value in range
                    float val = (height[x][y] - minh) / (maxh - minh);
                    Color color = new Color(val,val,val,1.0f);

                    pw.setColor((int)(x*getWidth()/xDimension), (int)(y*getHeight()/yDimension), color);
                }
            }
            //long endTime = System.nanoTime();
            //System.out.println("TIME TO DRAW CIRCLE: " + ((endTime-startTime)/1000000));
        }

        @Override
        public boolean isResizable() {
            return true;
        }

        @Override
        public double prefWidth(double height) {
            return getWidth();
        }

        @Override
        public double prefHeight(double width) {
            return getHeight();
        }
    }

    public void addTerrainCanvas(StackPane stackPane, float xDimension, float yDimension){
        this.xDimension = xDimension;
        this.yDimension = yDimension;
        terrainCanvas = new TerrainCanvas();
        terrainCanvas.widthProperty().bind(stackPane.widthProperty());
        terrainCanvas.heightProperty().bind(stackPane.heightProperty());

        stackPane.getChildren().add(terrainCanvas);
    }

    // Get & Sets


    public void deriveImageCanvas(Canvas img) {
        float maxh = -10000.0f, minh = 10000.0f;
        GraphicsContext gc = img.getGraphicsContext2D();
        PixelWriter pw = gc.getPixelWriter();

        // determine range of heights
        for(int x=0; x < dimx; x++){
            for(int y=0; y < dimy; y++) {
                float h = height[x][y];
                if(h > maxh)
                    maxh = h;
                if(h < minh)
                    minh = h;
            }
        }
        for(int x=0; x < dimx; x++){
            for(int y=0; y < dimy; y++) {
                // find normalized height value in range
                float val = (height[x][y] - minh) / (maxh - minh);
                Color color = new Color(val,val,val,1.0f);
                pw.setColor(x, y, color);
            }
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