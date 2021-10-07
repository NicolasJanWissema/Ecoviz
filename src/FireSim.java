import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;

public class FireSim {

    private ArrayList<Integer>[][] grid;
    private Plant[][] firePlant; 
    private int windDirection;
    private int[] seedPoint;
    private int size;
    private Plant [] ug, can;
    private float gspc;
    private ArrayList<Integer> burning;
    private ArrayList<Integer> burnt;
    private ArrayList<Integer> nextPoint;
    private ArrayList<Integer> visited;

    public FireSim(int size, float gridSpacing, Plants plantData, int[] sp){
        gspc = gridSpacing;
        seedPoint = new int[2];
        seedPoint[0] = sp[0];
        seedPoint[1] = sp[1];
        this.size = size;
        burning = new ArrayList<Integer>();
        burnt = new ArrayList<Integer>();
        nextPoint = new ArrayList<Integer>();
        visited = new ArrayList<Integer>();
        grid = new ArrayList [size][size];
        // Plant [] ug, can;
        ug = plantData.getUndergrowth();
        can = plantData.getCanopy();
        for(int i = 0; i<size; i++){
            for(int j = 0; j<size; j++){
                // System.out.println("i: "+i+" j: "+j);
                grid[i][j] = new ArrayList<Integer>();
            }
        }
        // for(Plant p : ug){
        //     fillInPlant(p);
        // }
        grid[seedPoint[1]][seedPoint[0]].add(-1);
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

    public void print(){
        for (int i = 0; i<size ; i++){
            for(int j = 0; j<size; j++){
                System.out.print(grid[i][j]);
            }
            System.out.println("");
        }
    }

    public void burn(int x, int y){
        // System.out.println("at point "+x+" "+y+" grid has:"+grid[y][x]);
        for(int p = 0; p<grid[y][x].size(); p++){ // for every plant in arraylist
            if(!burning.contains(grid[y][x].get(p))){
                burning.add(grid[y][x].get(p));
                visited.add(grid[y][x].get(p));
                // need to set Plant object burning boolean
                setPlantOnFire(grid[y][x].get(p));

            }
        }
    }

    public void setPlantOnFire(int id){
        if(id>=ug.length){
            id=id-ug.length;
            can[id].setBurning(true);
        }
        else{
            ug[id].setBurning(true);
        }
    }


    public void shouldBurn(int x, int y){
        boolean stop = false;
        // if(!burning.isEmpty()){
            // s(grid[y][if(burning.containx].get(0))){ // checks if plant is already burning
            //     // call another method
            // }
            // else{
        int burnableNeighbours = 0;
        burning.add(-1);
        int [][] surrounding = {{x-1, y+1}, {x, y+1}, {x+1, y+1}, {x-1, y}, {x+1, y}, {x-1, y-1}, {x, y-1}, {x+1, y-1}};
        Random r = new Random();
        for(int [] s : surrounding){ // for every surrounding point
            // System.out.println("At point :"+x+","+y+" looking at neighbour "+s[0]+","+s[1]);
            if (s[0] < 0 || s[0] >= size || s[1] < 0 || s[1] >= size){ // accounts for border points
                //System.out.println("border");
                continue;
                
            }
            if(grid[s[1]][s[0]].isEmpty()){ // checks if surrounding point has empty arraylist
                continue;
            }
            else{
                nextPoint.add(pointToPos(s[0], s[1])); // adds point pos to nextpoint queue

            }
        }

        while(!nextPoint.isEmpty()){ // while we still have points to go through
            int tempx = posToPoint(nextPoint.get(0))[0];
            int tempy = posToPoint(nextPoint.get(0))[1];
            System.out.println("At point: "+nextPoint.get(0)+" a.k.a. "+tempx+", "+tempy);

            if(burning.contains(grid[tempy][tempx].get(0))){ // if one of the plants is burning which should mean they all are
                visited.add(nextPoint.get(0)); // adds point to list of visited ones
                nextPoint.remove(0); // pop off the first item in list
                continue; // move onto next iteration
            }
            else{
                burn(tempx, tempy);
                addSurroundingPoints(tempx, tempy);
                nextPoint.remove(0);
            }
            
            System.out.println("Burning plants: "+burning);
            System.out.println("Visited plants: "+visited);
        }

    }

    public void addSurroundingPoints(int x, int y){
        int [][] surrounding = {{x-1, y+1}, {x, y+1}, {x+1, y+1}, {x-1, y}, {x+1, y}, {x-1, y-1}, {x, y-1}, {x+1, y-1}};
        for(int [] s : surrounding){ // for every surrounding point
            // System.out.println("At point :"+x+","+y+" looking at neighbour "+s[0]+","+s[1]);
            if (s[0] < 0 || s[0] >= size || s[1] < 0 || s[1] >= size){ // accounts for border points
                continue;
            }
            else{
                // checks if surrounding point is empty, already burning or was visited
                if(visited.contains(pointToPos(s[0], s[1])) || grid[s[1]][s[0]].isEmpty() || burning.contains(grid[s[1]][s[0]].get(0))){ 
                    continue; // skip
                }
                else{
                    nextPoint.add(pointToPos(s[0], s[1])); // adds point to queue
                }
            }
        }
    }

    public int pointToPos(int x, int y){
        int pos = (size*y)+x;
        return pos;
    }

    public int[] posToPoint(int pos){
        int x = pos%size;
        int y = pos/size;
        int [] p = {x, y};
        return p;
    }

}