package pr3;

import java.util.ArrayList;

public class DropdownData extends InterfaceComponentData {
    
    private int defaultValue;
    private ArrayList<String> options = new ArrayList<String>();
   
    public DropdownData(String id, String block, String label, int defaultValue, ArrayList<String> options) {
        super(id, block, label);
        this.defaultValue = defaultValue;
        this.options = options;
    }

    
    public int getDefaultValue() {
        return defaultValue;
    }
    public ArrayList<String> getOptions() {
        return options;
    }
    
    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }
    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }


    @Override
    public String toString() {
        return "DropdownData, defaultValue=" + defaultValue + ", options=" + options + "]";
    }
}
