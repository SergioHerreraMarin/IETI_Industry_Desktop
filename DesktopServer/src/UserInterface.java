
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface extends JFrame {

    private final int WIDTH = 1300;
    private final int HEIGHT = 600;
    private Model model;

    JPanel controlsPanel,panelSliderComponent, panelTogglesComponent, panelComboComponent, panelExterior;

    public UserInterface(){
        super("DESKTOP SERVER");
        
        model = new Model();
        initInterface();
        createMenuBar();
        
        Border emptyBorder = BorderFactory.createEmptyBorder(20,20,20,20);
        panelExterior = new JPanel(new GridLayout(0,1));
        panelExterior.setBorder(emptyBorder);
        panelExterior.setPreferredSize(new Dimension(1000, 600));
        JScrollPane scrollPanel = new JScrollPane(panelExterior);

        this.add(scrollPanel, BorderLayout.CENTER);
        this.setVisible(true);
    }


    private void createMenuBar(){

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArxiu = new JMenu("Arxiu");
        JMenu menuVisualitzacion = new JMenu("Visualitzacions");
        JMenuItem itemLoadConfig = new JMenuItem("Carregar configuració");

        itemLoadConfig.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }   
        });
        menuArxiu.add(itemLoadConfig);

        menuBar.add(menuArxiu);
        menuBar.add(menuVisualitzacion);

        this.setJMenuBar(menuBar);
    }


    private void openFile(){

        int chooserStatus;

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("XML", "xml");
        fileChooser.setFileFilter(extensionFilter);
        chooserStatus = fileChooser.showOpenDialog(this);

        switch(chooserStatus){
            case JFileChooser.APPROVE_OPTION:
                model.resetData();
                if(model.setCurrentFile(fileChooser.getSelectedFile())){
                    loadControls();
                }
            break;
        }
    }
    
    
    private void loadControls(){

        for(JComponent component : model.getCustomComponents()){

            if(component instanceof CustomSlider){

                JSlider slider = ((CustomSlider)component).createCustomSlider();           
                for(CustomControlPanel controlPanel : model.getControlBlockData()){
                    if(controlPanel.getControlId().equals(((CustomSlider) component).getBlock())){
                        controlPanel.addSlidersToPanel(slider);
                    }     
                }

            }else if(component instanceof CustomSwitch){

                JToggleButton tggleButtonn = ((CustomSwitch)component).createCustomSwitch();
                for(CustomControlPanel controlPanel : model.getControlBlockData()){
                    if(controlPanel.getControlId().equals(((CustomSwitch) component).getBlock())){
                        controlPanel.addToggleToPanel(tggleButtonn);
                    }     
                }

            }else if(component instanceof CustomDropdown){

                JComboBox dropdown = ((CustomDropdown)component).createCustomDropdown();
                for(CustomControlPanel controlPanel : model.getControlBlockData()){
                    if(controlPanel.getControlId().equals(((CustomDropdown) component).getBlock())){
                        controlPanel.addDropdownToPanel(dropdown);
                    }     
                }

            }else if(component instanceof CustomSensor){

                ArrayList<JLabel> sensorData = ((CustomSensor)component).createCustomSensor();

                for(CustomControlPanel controlPanel : model.getControlBlockData()){
                    if(controlPanel.getControlId().equals(((CustomSensor) component).getBlock())){ 
                        for(JLabel data : sensorData){
                            controlPanel.addSensorToPanel(data);
                        }
                    }     
                }

            }else{
                JFrame jFrame = new JFrame();
                JOptionPane.showMessageDialog(jFrame, "Class not found");
            }
        }

        //Añade los paneles de control al panel exterior. 
        for(CustomControlPanel controlPanel : model.getControlBlockData()){
            panelExterior.add(controlPanel);   
        }

        /* 
        for(String key : model.getMapData().keySet()){
            switch(model.returnIDType(key)){
                
                case "switch":
                    JToggleButton toggleButton = new JToggleButton();
                    if(((CustomSwitch)model.getMapData().get(key)).getDefaultValue().equals("on")){
                        toggleButton.setSelected(true);
                    }else{
                        toggleButton.setSelected(false);
                    }
                    toggleButton.setText(((CustomSwitch)model.getMapData().get(key)).getLabel());

                    for(ControlPanel controlPanel : model.getControlBlockData()){
                        if(controlPanel.getControlId().equals(((CustomSwitch)model.getMapData().get(key)).getBlock())){
                            controlPanel.addToggleToPanel(toggleButton);
                        }     
                    }
            
                break;
                case "slider":
                    JSlider slider = new JSlider();
                    slider.setValue((int)((CustomSlider)model.getMapData().get(key)).getDefaultValue());
                    slider.setMaximum((int)((CustomSlider)model.getMapData().get(key)).getMax());
                    slider.setMinimum((int)((CustomSlider)model.getMapData().get(key)).getMin());
                    slider.setMajorTickSpacing((int)((CustomSlider)model.getMapData().get(key)).getStep());

                    for(ControlPanel controlPanel : model.getControlBlockData()){
                        if(controlPanel.getControlId().equals(((CustomSlider)model.getMapData().get(key)).getBlock())){
                            controlPanel.addSlidersToPanel(slider);
                        }     
                    }

                break;
                case "dropdown":
                    JComboBox dropdown = new JComboBox<>();
                    for(String option : ((CustomDropdown)model.getMapData().get(key)).getOptions()){
                        dropdown.addItem(option);
                    }
                    dropdown.setSelectedIndex((int)((CustomDropdown)model.getMapData().get(key)).getDefaultValue());

                    for(ControlPanel controlPanel : model.getControlBlockData()){
                        if(controlPanel.getControlId().equals(((CustomDropdown)model.getMapData().get(key)).getBlock())){
                            controlPanel.addDropdownToPanel(dropdown);
                        }     
                    }
                break;
                case "sensor":
                    JLabel unitsData = new JLabel();
                    JLabel thresholdlow = new JLabel();
                    JLabel thresholdhigh = new JLabel();
                    JLabel sensorLabel = new JLabel();

                    unitsData.setText("Units: " + ((CustomSensor)model.getMapData().get(key)).getUnits());
                    thresholdlow.setText("Thresholdlow: " + ((CustomSensor)model.getMapData().get(key)).getThresholdlow());
                    thresholdhigh.setText("Thresholdhigh: " + ((CustomSensor)model.getMapData().get(key)).getThresholdhigh());
                    sensorLabel.setText("Label: " + ((CustomSensor)model.getMapData().get(key)).getLabel());

                    for(ControlPanel controlPanel : model.getControlBlockData()){
                        if(controlPanel.getControlId().equals(((CustomSensor)model.getMapData().get(key)).getBlock())){
                            controlPanel.addSensorToPanel(unitsData);
                            controlPanel.addSensorToPanel(thresholdlow);
                            controlPanel.addSensorToPanel(thresholdhigh);
                            controlPanel.addSensorToPanel(sensorLabel);
                        }     
                    }
                break;
            }
        }

        //Añade los paneles de control al panel exterior. 
        for(ControlPanel controlPanel : model.getControlBlockData()){
            panelExterior.add(controlPanel);   
        }
        */
    }

    private void initInterface(){
        
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
