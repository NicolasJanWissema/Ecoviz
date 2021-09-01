import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;

public class SpeciesInfo {

    // Global Variables
    private float[] height;
    private float avgCanopyRad;
    private String commmonName;
    private String lantinName;
    public CheckBox filterBox;

    public SpeciesInfo(String commmonName, String lantinName){
        this.commmonName=commmonName;
        this.lantinName=lantinName;
    }
    public SpeciesInfo(String speciesInfo){
        commmonName = speciesInfo.split("“")[1].split("”")[0];
        lantinName = speciesInfo.split("“")[2].split("”")[0];
    }

    // Sets
    public void setHeight(float minHeight, float maxHeight){
        height = new float[]{minHeight,maxHeight};
    }
    public void setAvgCanopyRad(float avgCanopyRad){
        this.avgCanopyRad=avgCanopyRad;
    }
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