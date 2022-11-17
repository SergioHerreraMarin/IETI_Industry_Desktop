import javax.swing.JSlider;

public class CustomSlider extends JSlider {
    
    private String id;
    private String block;
    private String label;
    private float defaultValue;
    private float min;
    private float max;
    private float step;
    
    public CustomSlider(String id, String block, String label, float defaultValue, float min, float max, float step) {
        this.id = id;
        this.block = block;
        this.label = label;
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.step = step;
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

    public float getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(float defaultValue) {
        this.defaultValue = defaultValue;
    }

    public float getMin() {
        return min;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public float getStep() {
        return step;
    }

    public void setStep(float step) {
        this.step = step;
    }


    public JSlider createCustomSlider(){

        JSlider slider = new JSlider();
        slider.setValue((int)this.defaultValue);
        slider.setMaximum((int)this.max);
        slider.setMinimum((int)this.min);
        slider.setMajorTickSpacing((int)this.step);

        return slider;
    }


    @Override
    public String toString() {
        return "CustomSlider [id=" + id + ", block=" + block + ", label=" + label + ", defaultValue=" + defaultValue
                + ", min=" + min + ", max=" + max + ", step=" + step + "]";
    }

}
