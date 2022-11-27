import javax.swing.JToggleButton;

public class CustomSwitch extends JToggleButton {
    
    private String id;
    private String block;
    private String label;
    private String defaultValue;
    
    public CustomSwitch(String id,String block, String label, String defaultValue) {
        this.id = id;
        this.block = block;
        this.label = label;
        this.defaultValue = defaultValue;

        if(defaultValue.equals("on")){
            this.setSelected(true);
        }else{
            this.setSelected(false);
        }

        this.setText(this.label);
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;

        if(defaultValue.equals("on")){
            this.setSelected(true);
        }else{
            this.setSelected(false);
        }
    }


    @Override
    public String toString() {
        return "%CustomSwitch:id=" + id + ", block=" + block + ", label=" + label + ", defaultValue=" + defaultValue;
    }


}
