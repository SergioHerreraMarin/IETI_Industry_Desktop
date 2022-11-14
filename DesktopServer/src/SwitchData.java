public class SwitchData extends InterfaceComponentData {
    
    private String defaultValue;
    
    public SwitchData(String id,String block, String label, String defaultValue) {
        super(id, block, label);
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "SwitchData, defaultValue=" + defaultValue + "]";
    }
}
