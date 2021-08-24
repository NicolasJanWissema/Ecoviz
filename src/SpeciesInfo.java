public class SpeciesInfo {

    // Global Variables
    private float[] height;
    private float avgCanopyRad;
    private String commmonName;
    private String lantinName;

    public SpeciesInfo(String commmonName, String lantinName){
        this.commmonName=commmonName;
        this.lantinName=lantinName;
    }
    public SpeciesInfo(String speciesInfo){
        commmonName = speciesInfo.split("“")[1].split("”")[0];
        lantinName = speciesInfo.split("“")[2].split("”")[0];
    }

    // Sets
    public float[] getHeight() {
        return height;
    }

    public float getAvgCanopyRad() {
        return avgCanopyRad;
    }

    public String getCommmonName() {
        return commmonName;
    }

    public String getLantinName() {
        return lantinName;
    }

}