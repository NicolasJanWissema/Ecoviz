import java.util.ArrayList;
import java.lang.Math;

public class FireSim {

    // Global Variable
    private ArrayList<Integer>[][] grid;
    private Plant[][] firePlant;
    private int windDirection;
    private int[] seedPoint;
    private int size;

    public FireSim(int size, Plants plantData){
        this.size = size;
        grid = new ArrayList[size][size];
        float [] tempPos;
        float rad;
        Plant [] ug, can;
        ug = plantData.getUndergrowth();
        can = plantData.getCanopy();
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                grid[i][j] = new ArrayList<Integer>();
            }
        }
        for(Plant p : ug){
            fillInPlant(p);
        }
        for(Plant p : can){
            fillInPlant(p);
        }
    }
    // public synchronized void drawCanvas() {
    //     GraphicsContext gc = getGraphicsContext2D();
    //     gc.clearRect(0, 0, getWidth(), getHeight());
    //     float width = screenToWorld((float)getWidth(), (float)getHeight())[0]-fOffsetX;
    //     float height = screenToWorld((float)getWidth(), (float)getHeight())[1]-fOffsetY;

    //     if (selectedPlant!=null && selectedPlant.enabled()){
    //         for (Plant plant : plants){
    //             if (plant.enabled() && plant!=selectedPlant && plant.containedIn(fOffsetX,fOffsetY,width,height, plantData.getGreatestRadius())){
    //                 gc.setFill(plantData.getColor(plant.getSpeciesID()));
    //                 //gc.setStroke(plantData.getColor(plant.getSpeciesID()).darker());
    //                 float[] pos = worldToScreen(plant.getPosition()[0], plant.getPosition()[1]);
    //                 double rad = plant.getCanopyRadius()*(sizeX*scaleX);
    //                 gc.fillOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
    //                 //gc.strokeOval((double) pos[0]-rad, (double) pos[1]-rad, rad *2, rad*2);
    //             }
    //         }
    public void fillInPlant(Plant p){
        int r = Math.round(p.getCanopyRadius());
        int x = Math.round(p.getPosition()[0]);
        int y = Math.round(p.getPosition()[1]);
        int xstart = x - r;
        int ystart = y - r;
        int yend = y+r;
        int xend = x+r;
        for (int i = ystart; i<yend+1 ; i++){
            for(int j = xstart; j<xend+1; j++){
                grid[i][j].add(p.getPlantID());
            }
        }
    }

}