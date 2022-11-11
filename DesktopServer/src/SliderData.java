public class SliderData extends InterfaceComponentData {
    
    private float defaultValue;
    private float min;
    private float max;
    private float step;
    
    public SliderData(String id, String block, String label, float defaultValue, float min, float max, float step) {
        super(id, block, label);
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
    }


    public float getDefaultValue() {
        return defaultValue;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public float getStep() {
        return step;
    }

    public void setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setStep(int step) {
        this.step = step;
    }

    
    @Override
    public String toString() {
        return "SliderData, defaultValue=" + defaultValue + ", min=" + min + ", max=" + max + ", step="
                + step + "]";
    }
}
