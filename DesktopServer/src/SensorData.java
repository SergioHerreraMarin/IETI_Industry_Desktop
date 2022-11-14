public class SensorData extends InterfaceComponentData {
    
    private String units;
    private String thresholdlow;
    private String thresholdhigh;

    public SensorData(String id, String block, String label, String units, String thresholdlow, String thresholdhigh) {
        super(id, block, label);
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

    
    @Override
    public String toString() {
        return "SensorData, units=" + units + ", thresholdlow=" + thresholdlow + ", thresholdhigh="
                + thresholdhigh + "]";
    }

}
