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
        for(int p = 0; p<can.length; p++){
            fillInPlant(can[p]);
        }
    }
 
    public void fillInPlant(Plant p){
        int r = Math.round(p.getCanopyRadius()); // gets canopy radius and rounds it as int
        int x = Math.round(p.getPosition()[0]); // gets x position and rounds it as int
        int y = Math.round(p.getPosition()[1]); // gets y position and rounds it as int
        int xstart = x - r; // calculates xstart for the plant canopy
        if(xstart < 0){ // edge case checking incase plant is on border
            xstart = 0;
        }
        int ystart = y - r; // calculates ystart for the plant canopy
        if(ystart < 0){ // edge case checking incase plant is on border
            ystart = 0;
        }
        int yend = y+r; // calculates yend for the plant canopy
        if(yend > size-1){ // edge case checking incase plant is on border
            yend = size-1;
        }
        int xend = x+r; // calculates xend for the plant canopy
        if(xend > size-1){ // edge case checking incase plant is on border
            xend = size-1;
        }
        for (int i = ystart; i<yend+1 ; i++){ // loops through square of canopy size surrounding plant 
            for(int j = xstart; j<xend+1; j++){
                grid[i][j].add(p.getPlantID()); // appends arraylist with the plantID of plant occupying space
            }
        }
    }

}