
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface extends JFrame {

    private final int WIDTH = 1300;
    private final int HEIGHT = 600;
    private Model model;

    JPanel controlsPanel, panelSliderComponent, panelTogglesComponent, panelComboComponent, panelExterior;

    public UserInterface() {
        super("DESKTOP SERVER");

        model = new Model();
        initInterface();
        createMenuBar();

        Border emptyBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        panelExterior = new JPanel(new GridLayout(0, 1));
        panelExterior.setBorder(emptyBorder);
        panelExterior.setPreferredSize(new Dimension(1000, 600));
        JScrollPane scrollPanel = new JScrollPane(panelExterior);

        this.add(scrollPanel, BorderLayout.CENTER);
        this.setVisible(true);
        
    }

    private void createMenuBar() {

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArxiu = new JMenu("Arxiu");
        JMenu menuVisualitzacion = new JMenu("Visualitzacions");
        JMenuItem itemLoadConfig = new JMenuItem("Carregar configuració");

        itemLoadConfig.addActionListener(new ActionListener() {
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

    private void openFile() {

        int chooserStatus;

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter extensionFilter = new FileNameExtensionFilter("XML", "xml");
        fileChooser.setFileFilter(extensionFilter);
        chooserStatus = fileChooser.showOpenDialog(this);

        switch (chooserStatus) {
            case JFileChooser.APPROVE_OPTION:
                
                resetPanels();

                if (model.setCurrentFile(fileChooser.getSelectedFile())) {
                    loadControls();
                }
                break;
        }
    }

    private void loadControls() {

        for(JComponent component : model.getCustomComponents()) {

            if(component instanceof CustomSlider) {

                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomSlider) component).getBlock())) {
                        controlPanel.addSlidersToPanel(((CustomSlider) component));
                    }
                }
            
                ((CustomSlider) component).addMouseListener(new MouseInputAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        ((CustomSlider) component).setDefaultValue(((CustomSlider) component).getValue());
                        String data;
                        data = "blockID:" + ((CustomSlider)component).getBlock() + "!id:" + ((CustomSlider)component).getId() + "!current:" + ((CustomSlider)component).getDefaultValue();
                        Servidor.updateClientComponents(data);   
                    }
                });

            }else if(component instanceof CustomSwitch) {


                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomSwitch) component).getBlock())) {
                        controlPanel.addToggleToPanel(((CustomSwitch) component));
                    }
                }

                ((CustomSwitch) component).addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {

                        String toggleValue;
                        if (((CustomSwitch) component).isSelected()) {
                            toggleValue = "on";
                        } else {
                            toggleValue = "off";
                        }

                        ((CustomSwitch) component).setDefaultValue(toggleValue);
                        
                        String data;
                        data = "blockID:" + ((CustomSwitch)component).getBlock() + "!id:" + ((CustomSwitch)component).getId() + "!current:" + ((CustomSwitch)component).getDefaultValue();
                        Servidor.updateClientComponents(data);
                    }
                });

            }else if(component instanceof CustomDropdown) {

                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomDropdown) component).getBlock())) {
                        controlPanel.addDropdownToPanel(((CustomDropdown) component));
                    }
                }
                    
                ((CustomDropdown) component).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ((CustomDropdown) component).setDefaultValue(((CustomDropdown) component).getSelectedIndex());

                        String data;
                        data = "blockID:" + ((CustomDropdown)component).getBlock() + "!id:" + ((CustomDropdown)component).getId() + "!current:" + ((CustomDropdown)component).getDefaultValue();
                        Servidor.updateClientComponents(data);
                    }
                });

            }else if(component instanceof CustomSensor) {

                ArrayList<JLabel> sensorData = ((CustomSensor) component).createCustomSensor();

                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomSensor) component).getBlock())) {
                        for (JLabel data : sensorData) {
                            controlPanel.addSensorToPanel(data);
                        }
                    }
                }

            }else{
                System.out.println("Clase no encontrada");
            }
        }

        // Añade los paneles de control al panel exterior.
        for (CustomControlPanel controlPanel : model.getControlBlockData()) {
            panelExterior.add(controlPanel);
        }

        panelExterior.repaint();
        panelExterior.revalidate();
    }


    private void initInterface() {

        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    private void resetPanels(){
        Model.resetData();
        panelExterior.removeAll();
        panelExterior.revalidate();
        panelExterior.repaint();
    }

}
