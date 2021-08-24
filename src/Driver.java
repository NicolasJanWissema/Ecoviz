import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Driver{

    // Global Variables
    private static Plants plants;
    private GUI gui;
    private static Terrain terrain;
    private static SpeciesInfo[] speciesInfo;
    private FireSim fireSim;
    private Plant[][] displayed;

    float [][] height; // regular grid of height values
    int dimx, dimy; // data dimensions
    BufferedImage img; // greyscale image for displaying the terrain top-down

    ArrayList<Integer> permute;	// permuted list of integers in range [0, dimx*dimy)

    public static void main(String[] args) {
        readFiles("Data/S6000-6000-256");
        terrain.deriveImage();
    }

    private static void readFiles(String filename) {
        try{
            Scanner sc;
            sc = new Scanner(new File(filename+".spc.txt"));
            ArrayList<SpeciesInfo> speciesInfoArrayList = new ArrayList<>();

            while(sc.hasNext()){
                speciesInfoArrayList.add(new SpeciesInfo(sc.nextLine()));
            }
            speciesInfo=new SpeciesInfo[speciesInfoArrayList.size()];
            speciesInfoArrayList.toArray(speciesInfo);
            plants = new Plants(speciesInfo.length);

            //read elevation file
            sc = new Scanner(new File(filename+".elv"));
            terrain = new Terrain(sc.nextInt(),sc.nextInt(),sc.nextFloat(),sc.nextFloat());
            for (int x=0; x<terrain.getDimensions()[0]; x++){
                for(int y=0; y< terrain.getDimensions()[1]; y++){
                    terrain.setHeight(x,y,sc.nextFloat());
                }
            }

            //reading canopy plant file
            System.out.println("canopy:");
            sc = new Scanner(new File(filename+"_canopy.pdb"));
            int speciesNum = sc.nextInt();
            for(int i=0; i<speciesNum; i++){
                int speciesID = sc.nextInt();
                System.out.println(speciesID);
                speciesInfo[speciesID].setHeight(sc.nextFloat(),sc.nextFloat());
                speciesInfo[speciesID].setAvgCanopyRad(sc.nextFloat());
                int plantNum = sc.nextInt();
                plants.addSpeciesNumToCanopy(speciesID,plantNum);
                for (int j=0; j<plantNum; j++){
                    plants.addPlantToCanopy(j, new Plant(speciesID, new float[]{sc.nextFloat(),sc.nextFloat(),sc.nextFloat()}, sc.nextFloat(),sc.nextFloat()));
                }
            }

            //reading undergrowth plant file
            System.out.println("undergrowth:");
            sc = new Scanner(new File(filename+"_undergrowth.pdb"));
            speciesNum = sc.nextInt();
            for(int i=0; i<speciesNum; i++){
                int speciesID = sc.nextInt();
                System.out.println(speciesID);
                speciesInfo[speciesID].setHeight(sc.nextFloat(),sc.nextFloat());
                speciesInfo[speciesID].setAvgCanopyRad(sc.nextFloat());
                int plantNum = sc.nextInt();
                plants.addSpeciesNumToUndergrowth(speciesID,plantNum);
                for (int j=0; j<plantNum; j++){
                    plants.addPlantToUndergrowth(j, new Plant(speciesID, new float[]{sc.nextFloat(),sc.nextFloat(),sc.nextFloat()}, sc.nextFloat(),sc.nextFloat()));
                }
            }

            sc.close();
        }
        catch (IOException e){
            System.out.println("Unable to open input file");
            e.printStackTrace();
        }
        catch (java.util.InputMismatchException e){
            System.out.println("Malformed input file");
            e.printStackTrace();
        }
    }
}