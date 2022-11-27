import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Model {

    private File currentFile;

    private static ArrayList<CustomControlPanel> customControls = new ArrayList<CustomControlPanel>();
    private static ArrayList<JComponent> customComponents = new ArrayList<JComponent>();

    public Model() {

    }

    public File getCurrentFile() {
        return currentFile;
    }

    public boolean setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        return readXML();
    }

    public ArrayList<JComponent> getCustomComponents() {
        return customComponents;
    }

    public void setCustomComponents(ArrayList<JComponent> customComponents) {
        this.customComponents = customComponents;
    }

    public ArrayList<CustomControlPanel> getControlBlockData() {
        return this.customControls;
    }

    public boolean readXML() throws IllegalArgumentException {

        if (currentFile != null) {

            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                Document doc = documentBuilder.parse(currentFile);
                doc.getDocumentElement().normalize();

                // Bucle de controles
                NodeList nodelistControls = doc.getElementsByTagName("controls");
                for (int i = 0; i < nodelistControls.getLength(); i++) {

                    Element elementControls = (Element) nodelistControls.item(i);

                    if (elementControls.getAttribute("name").isEmpty()) {
                        JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The name tag is not configured correctly!!!");
                        return false;
                    } else {
                        Border border = BorderFactory.createTitledBorder(elementControls.getAttribute("name"));
                        CustomControlPanel controlPanel = new CustomControlPanel(elementControls.getAttribute("name"));
                        controlPanel.setBorder(border);
                        customControls.add(controlPanel);
                    }

                    // Bucle de switches
                    NodeList nodelistSwitch = elementControls.getElementsByTagName("switch");
                    for (int j = 0; j < nodelistSwitch.getLength(); j++) {
                        Element elementSwitch = (Element) nodelistSwitch.item(j); // Current switch
                        try {
                            Integer.parseInt(elementSwitch.getAttribute("id"));
                            if (elementSwitch.getAttribute("default").equals("on")
                                    || elementSwitch.getAttribute("default").equals("off")) {
                                CustomSwitch customSwitch = new CustomSwitch(elementSwitch.getAttribute("id"),
                                        elementControls.getAttribute("name"), elementSwitch.getTextContent(),
                                        elementSwitch.getAttribute("default"));
                                customComponents.add(customSwitch);
                            } else {
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame,
                                        "The default option is not correct: Switch default option!!!");
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame,
                                    "The ID must be a number: Switch ID!!!\n" + e.getMessage());
                            return false;
                        }
                    }

                    // Bucle de slider
                    NodeList nodelistSlider = elementControls.getElementsByTagName("slider");
                    for (int k = 0; k < nodelistSlider.getLength(); k++) {
                        Element elementSlider = (Element) nodelistSlider.item(k); // Current switch

                        try {
                            Integer.parseInt(elementSlider.getAttribute("id"));
                            if (Float.parseFloat(elementSlider.getAttribute("default")) > Float
                                    .parseFloat(elementSlider.getAttribute("max"))
                                    || Float.parseFloat(elementSlider.getAttribute("default")) < Float
                                            .parseFloat(elementSlider.getAttribute("min"))) {
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame,
                                        "The default number is not correct: Slider default number!!!");
                                return false;
                            } else {
                                if (Float.parseFloat(elementSlider.getAttribute("step")) > Float
                                        .parseFloat(elementSlider.getAttribute("max"))
                                        || Float.parseFloat(elementSlider.getAttribute("step")) < 0) {
                                    JFrame jFrame = new JFrame();
                                    JOptionPane.showMessageDialog(jFrame,
                                            "The step number is not correct: Slider step number!!!");
                                    return false;
                                } else {
                                    CustomSlider customSlider = new CustomSlider(elementSlider.getAttribute("id"),
                                            elementControls.getAttribute("name"), elementSlider.getTextContent(),
                                            Float.parseFloat(elementSlider.getAttribute("default")),
                                            Float.parseFloat(elementSlider.getAttribute("min")),
                                            Float.parseFloat(elementSlider.getAttribute("max")),
                                            Float.parseFloat(elementSlider.getAttribute("step")));

                                    customComponents.add(customSlider);
                                }
                            }
                        } catch (NumberFormatException e) {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame,
                                    "The ID must be a number: Slider ID!!!\n" + e.getMessage());
                            return false;
                        }
                    }

                    // Bucle sensor
                    NodeList nodelistSensor = elementControls.getElementsByTagName("sensor");
                    for (int l = 0; l < nodelistSensor.getLength(); l++) {

                        Element elementSensor = (Element) nodelistSensor.item(l); // Current switch
                        try {
                            Integer.parseInt(elementSensor.getAttribute("id"));
                        } catch (NumberFormatException e) {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame,
                                    "The ID must be a number: Sensor ID!!!\n" + e.getMessage());
                            return false;
                        }
                        if (Integer.parseInt(elementSensor.getAttribute("thresholdlow")) < Integer
                                .parseInt(elementSensor.getAttribute("thresholdhigh"))) {
                            if (elementSensor.getAttribute("units").equals("ºC")
                                    || elementSensor.getAttribute("units").equals("ºF")
                                    || elementSensor.getAttribute("units").equals("K")) {
                                CustomSensor customSensor = new CustomSensor(elementSensor.getAttribute("id"),
                                        elementControls.getAttribute("name"), elementSensor.getTextContent(),
                                        elementSensor.getAttribute("units"), elementSensor.getAttribute("thresholdlow"),
                                        elementSensor.getAttribute("thresholdhigh"));

                                customComponents.add(customSensor);
                            } else {
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame,
                                        "Allowable temperature units are Kelvin(K), Celsius(ºC) and Fahrenheit(ºF)!!!");
                                return false;
                            }
                        } else {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame, "Thresholdlow is bigger than thresholdhigh!!!");
                            return false;
                        }

                    }

                    // Bucle dropdown
                    try {
                        NodeList nodelistDropdown = elementControls.getElementsByTagName("dropdown");
                        for (int m = 0; m < nodelistDropdown.getLength(); m++) {
                            ArrayList<String> optionsArrayList = new ArrayList<String>();
                            Element elementDropdown = (Element) nodelistDropdown.item(m);
                            try {
                                Integer.parseInt(elementDropdown.getAttribute("id"));
                            } catch (NumberFormatException e) {
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame,
                                        "The ID must be a number: Dropdown ID!!!\n" + e.getMessage());
                                return false;
                            }
                            // Bucle options
                            NodeList nodelistOptions = elementDropdown.getElementsByTagName("option");
                            for (int n = 0; n < nodelistOptions.getLength(); n++) {
                                Element elementOption = (Element) nodelistOptions.item(n);
                                optionsArrayList.add(elementOption.getTextContent());
                            }

                            if (Integer.parseInt(elementDropdown.getAttribute("default")) < nodelistOptions
                                    .getLength()) {
                                CustomDropdown customDropdown = new CustomDropdown(elementDropdown.getAttribute("id"),
                                        elementControls.getAttribute("name"), elementDropdown.getTextContent(),
                                        Integer.parseInt(elementDropdown.getAttribute("default")), optionsArrayList);

                                customComponents.add(customDropdown);
                            } else {
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame,
                                        "The default number is not correct: Dropdown default number");
                                return false;
                            }

                        }
                    } catch (IllegalArgumentException e) {
                        JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame,
                                "Be careful with the characters you put!\n" + e.getMessage());
                        return false;
                    }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return false;
            } catch (SAXException e) {
                JFrame jFrame = new JFrame();
                JOptionPane.showMessageDialog(jFrame,
                        "Remember to put the labels right! It may also be that you have not selected an XML file\n"
                                + e.getMessage());
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (NumberFormatException e) {
                JFrame jFrame = new JFrame();
                JOptionPane.showMessageDialog(jFrame,
                        "Aware of! Don't put letters and special characters where they don't belong, also be careful with the titles of the variables.\n"
                                + e.getMessage());
                return false;
            }
        } else {
            JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "There is no XML loaded");
            return false;
        }
        return true;
    }

    public static String currentComponentValuesToApp() {
        String currentControlId = "";
        String data = "";

        for (CustomControlPanel control : customControls) {
            currentControlId = control.getControlId(); // block1

            for (JComponent component : customComponents) {
                if (component instanceof CustomSlider) {
                    if (((CustomSlider) component).getBlock().equals(currentControlId)) {
                        data += component.toString();
                    }

                } else if (component instanceof CustomSwitch) {
                    if (((CustomSwitch) component).getBlock().equals(currentControlId)) {
                        data += component.toString();
                    }

                } else if (component instanceof CustomDropdown) {
                    if (((CustomDropdown) component).getBlock().equals(currentControlId)) {
                        data += component.toString();
                    }

                } else if (component instanceof CustomSensor) {
                    if (((CustomSensor) component).getBlock().equals(currentControlId)) {
                        data += component.toString();
                    }

                }
            }
            data += "¿" + currentControlId + ";";
        }

        return data;
    }

    public static void updateComponent(String message){

        String idBlock = "", idComponent = "",  currentComponentValue = "";

        String[] componentProperties = message.split("!");

        for(String propert : componentProperties){
            String[] nameValue = propert.split(":"); //[0] value name, [1] value

            switch(nameValue[0]){
                case "blockID":
                    idBlock = nameValue[1];
                    break;
                case "id":
                    idComponent = nameValue[1];
                    break;
                case "current":
                    currentComponentValue = nameValue[1];
                    break;
                default:
                    System.out.println("Valor no encontrado");
                break;

            }
        }

        for(Object comp : Model.customComponents){
            if(comp instanceof CustomSlider){
                if(((CustomSlider)comp).getId().equals(idComponent)){
                    ((CustomSlider)comp).setDefaultValue(Float.valueOf(currentComponentValue));

                }
            }else if(comp instanceof CustomDropdown){
                if(((CustomDropdown)comp).getId().equals(idComponent)){
                    ((CustomDropdown)comp).setDefaultValue(Integer.valueOf(currentComponentValue));
                }
            }else if(comp instanceof CustomSwitch){
                if(((CustomSwitch)comp).getId().equals(idComponent)){
                    ((CustomSwitch)comp).setDefaultValue(currentComponentValue);
                }
            }
        }

    }

    public static void resetData() {
        customComponents.clear();
        customControls.clear();
    }
}
