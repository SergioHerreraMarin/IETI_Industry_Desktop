
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
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
                model.setCurrentFile(fileChooser.getSelectedFile());
                loadControls();
            break;
        }
    }

    
    private void loadControls(){

        for(String key : model.getMapData().keySet()){
            switch(model.returnIDType(key)){
                
                case "switch":
                    JToggleButton toggleButton = new JToggleButton();
                    if(((SwitchData)model.getMapData().get(key)).getDefaultValue().equals("true")){
                        toggleButton.setSelected(true);
                    }else{
                        toggleButton.setSelected(false);
                    }
                    toggleButton.setText(((SwitchData)model.getMapData().get(key)).getLabel());

                    for(String key2: model.getControlBlockData().keySet()){
                        if(key2.equals(((SwitchData)model.getMapData().get(key)).getBlock())){
                            model.getControlBlockData().get(key2).addToggleToPanel(toggleButton);
                        }
                    }
            
                break;
                case "slider":
                    JSlider slider = new JSlider();
                    slider.setValue((int)((SliderData)model.getMapData().get(key)).getDefaultValue());
                    slider.setMaximum((int)((SliderData)model.getMapData().get(key)).getMax());
                    slider.setMinimum((int)((SliderData)model.getMapData().get(key)).getMin());
                    slider.setMajorTickSpacing((int)((SliderData)model.getMapData().get(key)).getStep());

                    for(String key2: model.getControlBlockData().keySet()){
                        if(key2.equals(((SliderData)model.getMapData().get(key)).getBlock())){
                            model.getControlBlockData().get(key2).addSlidersToPanel(slider);
                        }
                    }
                break;
                case "dropdown":
                    JComboBox dropdown = new JComboBox<>();
                    for(String option : ((DropdownData)model.getMapData().get(key)).getOptions()){
                        dropdown.addItem(option);
                    }
                    dropdown.setSelectedIndex((int)((DropdownData)model.getMapData().get(key)).getDefaultValue());

                    for(String key2: model.getControlBlockData().keySet()){
                        if(key2.equals(((DropdownData)model.getMapData().get(key)).getBlock())){
                            model.getControlBlockData().get(key2).addDropdownToPanel(dropdown);
                        }
                    }
                break;
                case "sensor":
                    JLabel unitsData = new JLabel();
                    JLabel thresholdlow = new JLabel();
                    JLabel thresholdhigh = new JLabel();
                    JLabel sensorLabel = new JLabel();

                    unitsData.setText("Units: " + ((SensorData)model.getMapData().get(key)).getUnits());
                    thresholdlow.setText("Thresholdlow: " + ((SensorData)model.getMapData().get(key)).getThresholdlow());
                    thresholdhigh.setText("Thresholdhigh: " + ((SensorData)model.getMapData().get(key)).getThresholdhigh());
                    sensorLabel.setText("Label: " + ((SensorData)model.getMapData().get(key)).getLabel());

                    for(String key2: model.getControlBlockData().keySet()){
                        if(key2.equals(((SensorData)model.getMapData().get(key)).getBlock())){
                            model.getControlBlockData().get(key2).addSensorToPanel(unitsData);
                            model.getControlBlockData().get(key2).addSensorToPanel(thresholdlow);
                            model.getControlBlockData().get(key2).addSensorToPanel(thresholdhigh);
                            model.getControlBlockData().get(key2).addSensorToPanel(sensorLabel);
                        }
                    }
                break;
            }
        }

        for(String key: model.getControlBlockData().keySet()){ //Añade los paneles de control al panel exterior. 
            panelExterior.add(((model.getControlBlockData().get(key))));     
        }
    }

    private void initInterface(){
        
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

}
