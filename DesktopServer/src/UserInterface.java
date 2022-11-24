
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface extends JFrame {
    static String basePath = System.getProperty("user.dir");
    String filePath = basePath + "/src/snapshot.db";

    private final int WIDTH = 1300;
    private final int HEIGHT = 600;
    private Model model;

    JPanel controlsPanel, panelSliderComponent, panelTogglesComponent, panelComboComponent, panelSnapshot,
            panelExterior;

    JButton snapshot = new JButton("Snapshot");
    JButton loadSnapshot = new JButton("Load Snapshot");

    public UserInterface() {
        super("DESKTOP SERVER");

        model = new Model();
        initInterface();
        createMenuBar();
        Border emptyBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        panelExterior = new JPanel(new GridLayout(0, 1));
        panelSnapshot = new JPanel(new FlowLayout());
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
                model.resetData();
                if (model.setCurrentFile(fileChooser.getSelectedFile())) {
                    loadControls();
                }
                break;
        }
    }

    private void loadControls() {

        for (JComponent component : model.getCustomComponents()) {

            if (component instanceof CustomSlider) {

                JSlider slider = ((CustomSlider) component).createCustomSlider();
                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomSlider) component).getBlock())) {
                        controlPanel.addSlidersToPanel(slider);
                    }
                }

                slider.addChangeListener(new ChangeListener() { // ----------------------------------------------Para
                                                                // actualizar componentes..
                    @Override
                    public void stateChanged(ChangeEvent e) {
                        ((CustomSlider) component).setDefaultValue(slider.getValue());
                    }
                });

            } else if (component instanceof CustomSwitch) {

                JToggleButton tggleButtonn = ((CustomSwitch) component).createCustomSwitch();
                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomSwitch) component).getBlock())) {
                        controlPanel.addToggleToPanel(tggleButtonn);
                    }
                }

                tggleButtonn.addChangeListener(new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent e) {

                        String toggleValue;
                        if (tggleButtonn.isSelected()) {
                            toggleValue = "on";
                        } else {
                            toggleValue = "off";
                        }

                        ((CustomSwitch) component).setDefaultValue(toggleValue);
                    }
                });

            } else if (component instanceof CustomDropdown) {

                JComboBox dropdown = ((CustomDropdown) component).createCustomDropdown();
                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomDropdown) component).getBlock())) {
                        controlPanel.addDropdownToPanel(dropdown);
                    }
                }

                dropdown.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ((CustomDropdown) component).setDefaultValue(dropdown.getSelectedIndex());
                    }
                });

            } else if (component instanceof CustomSensor) {

                ArrayList<JLabel> sensorData = ((CustomSensor) component).createCustomSensor();

                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomSensor) component).getBlock())) {
                        for (JLabel data : sensorData) {
                            controlPanel.addSensorToPanel(data);
                        }
                    }
                }

            } else {
                System.out.println("Clase no encontrada");
            }
        }

        // Añade los paneles de control al panel exterior.
        for (CustomControlPanel controlPanel : model.getControlBlockData()) {
            panelExterior.add(controlPanel);
        }

        // SNAPSHOTS
        panelSnapshot.add(snapshot);
        panelSnapshot.add(loadSnapshot);

        snapshot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarInstancia();
            }
        });

        panelExterior.add(panelSnapshot);

        panelExterior.repaint();
        panelExterior.revalidate();
    }

    private void initInterface() {

        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void guardarInstancia() {
        String userSnapshot = "";

        // Se conecta a esa base de datos
        Connection conn = UtilsSQLite.connect(filePath);

        String data = Model.currentComponentValuesToApp();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dataInstancia = LocalDateTime.now().toString().replace("T", " ");

        while (userSnapshot.equals("") || userSnapshot.length() < 3) {
            userSnapshot = JOptionPane.showInputDialog("Insert a name:");
        }

        UtilsSQLite.queryUpdate(conn,
                "INSERT INTO snapshot (state, date, user) VALUES (\"" + Model.currentComponentValuesToApp()
                        + "\",  \"" + dataInstancia + "\", \"" + userSnapshot + "\");");

        JOptionPane.showMessageDialog(null, "Snapshot done", "Snapshot", JOptionPane.INFORMATION_MESSAGE);

        UtilsSQLite.disconnect(conn);
    }

    private void cargarInstancia() {
        // Crea la base de datos de snapshot
        // UtilsSQLite.snapshot(filePath);

        // Se conecta a esa base de datos
        Connection conn = UtilsSQLite.connect(filePath);

    }

}
