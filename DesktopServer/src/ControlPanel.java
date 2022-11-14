import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import java.awt.GridLayout;

public class ControlPanel extends JPanel {
    
    private String controlId;

    JPanel panelSliders = new JPanel(new GridLayout(1,0));
    JPanel panelDropdown = new JPanel(new GridLayout(1,0));
    JPanel panelToggles = new JPanel(new GridLayout(1,0));
    JPanel panelSensor = new JPanel(new GridLayout(1,0));
    JPanel panelSliderComponent = new JPanel();
    JPanel panelDropdownComponent = new JPanel();
    JPanel panelTogglesComponent = new JPanel();
    JPanel panelSensorComponent = new JPanel(new GridLayout(0,1));


    public ControlPanel(String controlId) {
        this.controlId = controlId;
        this.setLayout(new GridLayout(1,0));

        Border borderSliders = BorderFactory.createTitledBorder("Sliders");
        Border bordeDropdown = BorderFactory.createTitledBorder("Dropdown");
        Border borderToggles = BorderFactory.createTitledBorder("Toggle");
        Border borderSensor = BorderFactory.createTitledBorder("Sensor");

        panelSliders.setBorder(borderSliders);
        panelDropdown.setBorder(bordeDropdown);
        panelToggles.setBorder(borderToggles);
        panelSensor.setBorder(borderSensor);
        panelSliders.add(panelSliderComponent);
        panelDropdown.add(panelDropdownComponent);
        panelToggles.add(panelTogglesComponent);
        panelSensor.add(panelSensorComponent);

        this.add(panelSliders);
        this.add(panelToggles);
        this.add(panelDropdown);
        this.add(panelSensor);
    }

    public String getControlId() {
        return controlId;
    }

    public void setControlId(String controlId) {
        this.controlId = controlId;
    }

    public void addSlidersToPanel(JSlider slider){
        this.panelSliderComponent.add(slider);
    }

    public void addDropdownToPanel(JComboBox dropdown){
        this.panelDropdownComponent.add(dropdown);
    }

    public void addToggleToPanel(JToggleButton toggle){
        this.panelTogglesComponent.add(toggle);
    }

    public void addSensorToPanel(JLabel sensorLabel){
        this.panelSensorComponent.add(sensorLabel);
    }

}
