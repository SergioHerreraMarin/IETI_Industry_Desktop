
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
                    Model.modelData = Model.readCurrentComponents();
                    System.out.println("Model data: " +Model.modelData);
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
                System.out.println("Clase no encontrada");
            }
        }

        //Añade los paneles de control al panel exterior. 
        for(CustomControlPanel controlPanel : model.getControlBlockData()){
            panelExterior.add(controlPanel);   
        }

        panelExterior.repaint();
        panelExterior.revalidate();
    }

    private void initInterface(){
        
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    



}
