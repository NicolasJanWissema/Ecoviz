import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

public class FireSim {

    // Global Variable
    private ArrayList<Integer>[][] grid;
    private Plant[][] firePlant;
    private int windDirection;
    private int[] seedPoint;
    private int size;
    private float gspc;
    private ArrayList<Integer> burning;
    private ArrayList<Integer> burnt;

    public FireSim(int size, float gridSpacing, Plants plantData, int[] sp){
        gspc = gridSpacing;
        seedPoint = sp;
        this.size = size;
        grid = new ArrayList [size][size];
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
        int r = Math.round(p.getCanopyRadius()/gspc); // gets canopy radius and rounds it as int
        int x = Math.round(p.getPosition()[0]/gspc); // gets x position and rounds it as int
        int y = Math.round(p.getPosition()[1]/gspc); // gets y position and rounds it as int
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

    public void burn(int x, int y){
        // do something
    }

    public void shouldBurn(int x, int y){

        if(burning.contains(grid[x][y].get(0))){ // checks if plant is already burning
            // call another method
        }
        else{
            int burningNeighbours = 0;
            int [][] surrounding = {{x-1, y+1}, {x, y+1}, {x+1, y+1}, {x-1, y}, {x+1, y}, {x-1, y-1}, {x, y-1}, {x+1, y-1}};
            Random r = new Random();
            for(int [] s : surrounding){ // for every surrounding point
                for(int p = 0; p<grid[s[0]][s[1]].size(); p++){ // for every possible plant in arraylist
                    if(burning.contains(grid[s[0]][s[1]].get(p))){ // check if plant is burning
                        burningNeighbours++; // increment the number of burning neighbours
                        if(burningNeighbours>3){ // if plant has more than 3 burning neighbours, burn it
                            burn(x,y);
                            break;
                        }
                    }
                    else{
                        continue;
                    }
                }
            }
            switch (burningNeighbours) {
                case 1:
                    if(r.nextInt(8)==0){ // if has only 1 burning neighbour;
                        burn(x, y);
                    }
                    break;
                case 2:
                    if(r.nextInt(4)==0){ // if has only 2 burning neighbours;
                        burn(x, y);
                    }
                    break;
                case 3:
                    if(r.nextInt(3)==0){ //if has only 3 burning neighbours;
                        burn(x, y);
                    }
                    break;

            }
            
        }
        
    }

    public void setSeedPoint(int[] seedPoint) {
        this.seedPoint = seedPoint;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public int getWindDirection() {
        return windDirection;
    }
    public int[] getSeedPoint() {
        return seedPoint;
    }

    public ArrayList<Integer> getBurning() {
        return burning;
    }

    public ArrayList<Integer> getBurnt() {
        return burnt;
    }

}