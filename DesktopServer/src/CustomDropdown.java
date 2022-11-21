import java.util.ArrayList;

import javax.swing.JComboBox;

public class CustomDropdown extends JComboBox {
    
    private String id;
    private String block;
    private String label;
    private int defaultValue;
    private ArrayList<String> options = new ArrayList<String>();
 
    public CustomDropdown(String id, String block, String label, int defaultValue, ArrayList<String> options) {
        
        this.id = id;
        this.block = block;
        this.label = label;
        this.defaultValue = defaultValue;
        this.options = options;
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

    public int getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(int defaultValue) {
        this.defaultValue = defaultValue;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }


    public JComboBox createCustomDropdown(){

        JComboBox dropdown = new JComboBox<>();

        for(String option : this.options){
            dropdown.addItem(option);
        }

        dropdown.setSelectedIndex(defaultValue);   
        return dropdown;
    }


    private String readDropdownOptions(){
        String opt = "";
        
        for(int i = 0; i < options.size(); i++){
            if(i < options.size() - 1){
                opt += options.get(i) + "!";
            }else{
                opt += options.get(i);
            }
        }
        
        return opt;
    }


    @Override
    public String toString() {
        return "%CustomDropdown:id=" + id + ", block=" + block + ", label=" + label + ", defaultValue=" + defaultValue
                + ", options=" + readDropdownOptions();
    }

}
