import java.util.ArrayList;

import javax.swing.JLabel;

public class CustomSensor extends JLabel {
    
    private String id;
    private String block;
    private String label;
    private String units;
    private String thresholdlow;
    private String thresholdhigh;

    public CustomSensor(String id, String block, String label, String units, String thresholdlow, String thresholdhigh) {
        this.id = id;
        this.block = block;
        this.label = label;
        this.units = units;
        this.thresholdlow = thresholdlow;
        this.thresholdhigh = thresholdhigh;
    }

    public String getUnits() {
        return units;
    }

    public String getThresholdlow() {
        return thresholdlow;
    }

    public String getThresholdhigh() {
        return thresholdhigh;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setThresholdlow(String thresholdlow) {
        this.thresholdlow = thresholdlow;
    }

    public void setThresholdhigh(String thresholdhigh) {
        this.thresholdhigh = thresholdhigh;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<JLabel> createCustomSensor(){

        ArrayList<JLabel> sensorLabels = new ArrayList<JLabel>();
        sensorLabels.add(new JLabel("Units: " + this.units));
        sensorLabels.add(new JLabel("Thresholdlow: " + thresholdlow)); 
        sensorLabels.add(new JLabel("Thresholdhigh: " + thresholdhigh));
        sensorLabels.add(new JLabel("Label: " + label));

        return sensorLabels;
    }


    @Override
    public String toString() {
        return "%CustomSensor:id=" + id + ", block=" + block + ", label=" + label + ", units=" + units
                + ", thresholdlow=" + thresholdlow + ", thresholdhigh=" + thresholdhigh;
    }

}
