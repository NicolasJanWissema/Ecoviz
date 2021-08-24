public class Plants {

    // Global
    private Plant[][] undergrowth;
    private Plant[][] canopy;
    private Plant[][] unfiltered;

    //Constructors
    Plants(int numSpecies){
        undergrowth = new Plant[numSpecies][0];
    }
    Plants(){}

    //Data generating methods
    public void addPlantToCanopy(Plant newPlant){
    }
    public void addPlantToUndergrowth(Plant newPlant){
    }

    //Filtering Methods, assuming data generation has been completed.
    public Plant[][] filterPlant(float[] position){
        for(int i=0; i<unfiltered.length;i++){
            for (int j=0;j<unfiltered[i].length;j++){
                if (unfiltered[i][j].getPosition()==position){
                    unfiltered[i][j]=null;
                }
            }
        }
        return (unfiltered);
    }
    public Plant[][] filterSpecies(int speciesID){
        unfiltered[speciesID]=new Plant[0];
        return (unfiltered);
    }
    public Plant[][] filterCanopy(){
        unfiltered=undergrowth;
        return (unfiltered);
    }
    public Plant[][] filterUndergrowth(){
        unfiltered=canopy;
        return (unfiltered);
    }
}
