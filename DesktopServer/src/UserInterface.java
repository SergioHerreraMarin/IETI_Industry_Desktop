
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface extends JFrame {
    static String basePath = System.getProperty("user.dir");
    static String filePath = basePath + "/src/snapshot.db";

    static ArrayList<JComponent> snapshotComponents = new ArrayList<JComponent>();

    static ArrayList<String> snapshotControls = new ArrayList<String>();

    private final int WIDTH = 1300;
    private final int HEIGHT = 600;
    private Model model;

    JPanel controlsPanel, panelSliderComponent, panelTogglesComponent, panelComboComponent, panelSnapshot,
            panelExterior;

    JButton snapshot = new JButton("Snapshot");
    JButton loadSnapshot = new JButton("Load Snapshot");
    static String modelSnapshot;

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

        for (JComponent component : model.getCustomComponents()) {

            if (component instanceof CustomSlider) {

                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomSlider) component).getBlock())) {
                        controlPanel.addSlidersToPanel(((CustomSlider) component));
                    }
                }

                ((CustomSlider) component).addMouseListener(new MouseInputAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        ((CustomSlider) component).setDefaultValue(((CustomSlider) component).getValue());
                    }
                });

            } else if (component instanceof CustomSwitch) {

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
                    }
                });

            } else if (component instanceof CustomDropdown) {

                for (CustomControlPanel controlPanel : model.getControlBlockData()) {
                    if (controlPanel.getControlId().equals(((CustomDropdown) component).getBlock())) {
                        controlPanel.addDropdownToPanel(((CustomDropdown) component));
                    }
                }

                ((CustomDropdown) component).addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ((CustomDropdown) component).setDefaultValue(((CustomDropdown) component).getSelectedIndex());
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

        GUISnapshot();
    }

    public void GUISnapshot() {
        panelSnapshot = new JPanel();

        // SNAPSHOTS
        panelSnapshot.add(snapshot);
        panelSnapshot.add(loadSnapshot);

        snapshot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarInstancia();
            }
        });

        loadSnapshot.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cargarInstancia();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
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

    public void guardarInstancia() {
        String snapshotName = "";
        // Se conecta a esa base de datos
        Connection conn = UtilsSQLite.connect(filePath);

        String data = Model.currentComponentValuesToApp();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        String dataInstancia = LocalDateTime.now().toString().replace("T", " ");

        while (snapshotName.equals("")) {
            snapshotName = JOptionPane.showInputDialog("Insert a name for the snapshot:");
        }

        UtilsSQLite.queryUpdate(conn,
                "INSERT INTO snapshot (name, stateData, date) VALUES (\"" + snapshotName
                        + "\",  \"" + data + "\", \"" + dataInstancia + "\");");

        JOptionPane.showMessageDialog(null, "Snapshot done", "Snapshot", JOptionPane.INFORMATION_MESSAGE);

        UtilsSQLite.disconnect(conn);
    }

    public void loadSnapshot(int id) {
        Connection conn = UtilsSQLite.connect(filePath);

        ResultSet rs = UtilsSQLite.querySelect(conn, "select stateData from snapshot where id = " + id + ";");

        try {
            while (rs.next()) {
                if (rs.getString(1).split(";") != null) {
                    loadControlsSnapshot(rs.getString(1).split(";"));
                }
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void loadControlsSnapshot(String[] controls) {
        resetPanels();

        for (String control : controls) { // Por cada control crea uno.

            String[] componentsBlockid = control.split("¿"); // [0] = componente [1] bloque id

            CustomControlPanel customControl = new CustomControlPanel(componentsBlockid[1]);

            String components[] = componentsBlockid[0].split("%");
            for (String component : components) {
                if (component.length() != 0) {
                    String keyValues[] = component.split(":");
                    switch (keyValues[0]) {

                        case "CustomSlider": // KeyValues[1] para sacar los valores por defecto del componente actual.

                            String sliderId = "", sliderBlock = "", sliderLabel = "";
                            float sliderDefaultValue = 5, sliderMin = 0, sliderMax = 10, sliderStep = 0;

                            String[] keyValuesSlider = keyValues[1].split(",");
                            for (String keyValue : keyValuesSlider) {
                                String keysValues[] = keyValue.split("=");

                                switch (keysValues[0].trim()) {
                                    case "id":
                                        sliderId = keysValues[1];
                                        break;
                                    case "block":
                                        sliderBlock = keysValues[1];
                                        break;
                                    case "label":
                                        sliderLabel = keysValues[1];
                                        break;
                                    case "defaultValue":
                                        sliderDefaultValue = Float.valueOf(keysValues[1]);
                                        break;
                                    case "min":
                                        sliderMin = Float.valueOf(keysValues[1]);
                                        break;
                                    case "max":
                                        sliderMax = Float.valueOf(keysValues[1]);
                                        break;
                                    case "step":
                                        sliderStep = Float.valueOf(keysValues[1]);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            // CREATE CUSTOM SLIDER
                            CustomSlider slider = new CustomSlider(sliderId, sliderBlock, sliderLabel,
                                    sliderDefaultValue, sliderMin, sliderMax, sliderStep);
                            snapshotComponents.add(slider);
                            break;

                        case "CustomSwitch":

                            String switchId = "", switchBlock = "", switchLabel = "", switchDefaultValue = "";
                            String[] keyValuesSwitch = keyValues[1].split(",");

                            for (String keyValue : keyValuesSwitch) {
                                String keysValues[] = keyValue.split("=");

                                switch (keysValues[0].trim()) {
                                    case "id":
                                        switchId = keysValues[1];
                                        break;
                                    case "block":
                                        switchBlock = keysValues[1];
                                        break;
                                    case "label":
                                        switchLabel = keysValues[1];
                                        break;
                                    case "defaultValue":
                                        switchDefaultValue = keysValues[1];
                                        break;
                                    default:
                                        break;
                                }
                            }
                            // CREATE CUSTOM SWITCH
                            CustomSwitch switchs = new CustomSwitch(switchId, switchBlock, switchLabel,
                                    switchDefaultValue);
                            snapshotComponents.add(switchs);
                            break;

                        case "CustomSensor":

                            String sensorId = "", sensorBlock = "", sensorLabel = "", sensorUnits = "",
                                    sensorThresholdlow = "", sensorThresholdhigh = "";
                            String[] keyValuesSensor = keyValues[1].split(",");

                            for (String keyValue : keyValuesSensor) {

                                String keysValues[] = keyValue.split("=");

                                switch (keysValues[0].trim()) {
                                    case "id":
                                        sensorId = keysValues[1];
                                        break;
                                    case "block":
                                        sensorBlock = keysValues[1];
                                        break;
                                    case "label":
                                        sensorLabel = keysValues[1];
                                        break;
                                    case "units":
                                        sensorUnits = keysValues[1];
                                        break;
                                    case "thresholdlow":
                                        sensorThresholdlow = keysValues[1];
                                        break;
                                    case "thresholdhigh":
                                        sensorThresholdhigh = keysValues[1];
                                    default:
                                        break;
                                }
                            }
                            // CREATE CUSTOM SENSOR
                            CustomSensor sensor = new CustomSensor(sensorId, sensorBlock, sensorLabel, sensorUnits,
                                    sensorThresholdlow, sensorThresholdhigh);
                            snapshotComponents.add(sensor);
                            customControl.addSensorToPanel(sensor);
                            break;

                        case "CustomDropdown":

                            String dropdownId = "", dropdownBlock = "", dropdownLabel = "";
                            int dropdownDefaultValue = 0;
                            ArrayList<String> dropdownOptions = new ArrayList<String>();

                            String[] keyValuesDropdown = keyValues[1].split(",");

                            for (String keyValue : keyValuesDropdown) {
                                String keysValues[] = keyValue.split("=");

                                switch (keysValues[0].trim()) {
                                    case "id":
                                        dropdownId = keysValues[1];
                                        break;
                                    case "block":
                                        dropdownBlock = keysValues[1];
                                        break;
                                    case "label":
                                        dropdownLabel = keysValues[1];
                                        break;
                                    case "defaultValue":
                                        dropdownDefaultValue = Integer.valueOf(keysValues[1]);
                                        break;
                                    case "options":
                                        String[] optionsValues = keysValues[1].split("!");
                                        for (int i = 0; i < optionsValues.length; i++) {
                                            dropdownOptions.add(optionsValues[i]);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                            }
                            // CREATE CUSTOM DROPDOWN
                            CustomDropdown dropdown = new CustomDropdown(dropdownId, dropdownBlock, dropdownLabel,
                                    dropdownDefaultValue, dropdownOptions);
                            snapshotComponents.add(dropdown);
                            break;
                        default:
                            break;
                    }
                }
            }
            model.customControls.add(customControl);
        }

        model.setCustomComponents(snapshotComponents);
        loadControls();
    }

    private void cargarInstancia() throws SQLException {
        Connection connSnapshot = UtilsSQLite.connect(filePath);
        ResultSet infoSnapshot = UtilsSQLite.querySelect(connSnapshot, "SELECT id,name FROM snapshot;");

        HashMap<Integer, String> snapshots = new HashMap<Integer, String>();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        ArrayList<String> values = new ArrayList<String>();

        try {
            while (infoSnapshot.next()) {
                snapshots.put(infoSnapshot.getInt(1), infoSnapshot.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Layout Snapshot
        JFrame snapshotSelection = new JFrame("Load Snapshot");
        int height = 0;
        int numSnapshots = 0;
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        snapshotSelection.setContentPane(contentPane);

        // Label
        JLabel labelSnapshot = new JLabel("Select a snapshot to load:  ");
        labelSnapshot.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(labelSnapshot, BorderLayout.NORTH);

        // Lista de snapshots
        JScrollPane scrollSnapshots = new JScrollPane();

        JPanel pSnapshots = new JPanel(new BorderLayout());
        scrollSnapshots.setViewportView(pSnapshots);
        pSnapshots.setLayout(new BoxLayout(pSnapshots, BoxLayout.Y_AXIS));

        for (Map.Entry<Integer, String> s : snapshots.entrySet()) {
            values.add(s.getValue());
            ids.add(s.getKey());
            numSnapshots++;
        }

        height = (numSnapshots * 70) - (numSnapshots - 10);

        JList listSnapshots = new JList();
        listSnapshots.setListData(values.toArray());
        listSnapshots.setFixedCellHeight(30);
        listSnapshots.setFixedCellWidth(this.getWidth() - 40);
        listSnapshots.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pSnapshots.add(listSnapshots);
        contentPane.add(scrollSnapshots, BorderLayout.CENTER);

        DefaultListCellRenderer renderer = (DefaultListCellRenderer) listSnapshots.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Botón
        JButton selection = new JButton("Select");
        selection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = ids.get(values.indexOf(listSnapshots.getSelectedValue()));
                    loadSnapshot(id);
                } catch (Exception e1) {
                    // TODO: handle exception
                } finally {
                    snapshotSelection.setVisible(false);
                }
            }
        });

        contentPane.add(selection, BorderLayout.SOUTH);
        snapshotSelection.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        snapshotSelection.setVisible(true);
        snapshotSelection.setSize(new Dimension(300, height));
    }

    private void resetPanels() {
        Model.resetData();
        panelExterior.removeAll();
        panelExterior.revalidate();
        panelExterior.repaint();
    }

}
