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

    // Panning and zooming
    float fOffsetX = 0.0f;
    float fOffsetY = 0.0f;

    float scaleX = 1.0f;
    float scaleY = 1.0f;

    //Constructor
    public Terrain(int dimX , int dimY, float gridSpacing, float latitude){
        this.dimx = dimX;
        this.dimy = dimY;
        height = new float[dimx][dimy];
        this.gridSpacing = gridSpacing;
        this.latitude = latitude;
    }
    
    public int[] worldToScreen(float fWorldX, float fWorldY) {
        int nScreenX = (int) ((fWorldX - fOffsetX)*scaleX);
        int nScreenY = (int) ((fWorldY - fOffsetY)*scaleY);
        int[] temp = {nScreenX,nScreenY};
        return temp;
    }

    public float[] screenToWorld(int nScreenX, int nScreenY) {
        float fWorldX = (float) (nScreenX/scaleX + fOffsetX);
        float fWorldY = (float) (nScreenY/scaleY + fOffsetY);
        float[] temp = {fWorldX,fWorldY};
        return temp;

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
                int[] pos = worldToScreen(x, y);
                //pw.setColor(pos[0], pos[1], color);
                gc.setFill(color);
                gc.fillRect(pos[0], pos[1], scaleX+1, scaleX+1);
                
            }

    }

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