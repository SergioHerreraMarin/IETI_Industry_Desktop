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
    private ArrayList<CustomControlPanel> controlBlockData = new ArrayList<CustomControlPanel>();
    private ArrayList<JComponent> customComponents = new ArrayList<JComponent>();


    public Model(){

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


    public ArrayList<CustomControlPanel> getControlBlockData(){
        return this.controlBlockData;
    }

    
    public boolean readXML() throws IllegalArgumentException{

        if(currentFile != null){
            
            try {
                DocumentBuilderFactory documentBuilderFactory= DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                
                Document doc = documentBuilder.parse(currentFile);
                doc.getDocumentElement().normalize();
            
                //Bucle de controles
                NodeList nodelistControls = doc.getElementsByTagName("controls");   
                for(int i = 0; i < nodelistControls.getLength(); i++){

                    Element elementControls = (Element) nodelistControls.item(i);
                    if (elementControls.getAttribute("name").isEmpty()){
                        JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The name tag is not configured correctly!!!");
                        return false;
                    }
                    else{
                        Border border = BorderFactory.createTitledBorder(elementControls.getAttribute("name"));
                        CustomControlPanel controlPanel = new CustomControlPanel(elementControls.getAttribute("name"));
                        controlPanel.setBorder(border);
                        controlBlockData.add(controlPanel);
                    }
                                
                    //Bucle de switches
                    NodeList nodelistSwitch = elementControls.getElementsByTagName("switch");   
                    for(int j = 0; j < nodelistSwitch.getLength(); j++){
                        Element elementSwitch = (Element)nodelistSwitch.item(j); //Current switch   
                        try {
                            Integer.parseInt(elementSwitch.getAttribute("id"));
                            if (elementSwitch.getAttribute("default").equals("on") || elementSwitch.getAttribute("default").equals("on")){
                                CustomSwitch customSwitch = new CustomSwitch(elementSwitch.getAttribute("id"), elementControls.getAttribute("name"), elementSwitch.getTextContent(), elementSwitch.getAttribute("default"));
                                customComponents.add(customSwitch);
                            }
                            else{
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame, "The default option is not correct: Switch default option!!!");
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame, "The ID must be a number: Switch ID!!!\n"+e.getMessage());
                            return false;
                        } 
                    }
                    
                    //Bucle de slider
                    NodeList nodelistSlider = elementControls.getElementsByTagName("slider");     
                    for(int k = 0; k < nodelistSlider.getLength(); k++){
                        Element elementSlider = (Element)nodelistSlider.item(k); //Current switch    
                        try {
                            Integer.parseInt(elementSlider.getAttribute("id"));
                            if (Integer.parseInt(elementSlider.getAttribute("default")) > Float.parseFloat(elementSlider.getAttribute("max")) || Integer.parseInt(elementSlider.getAttribute("default")) < Float.parseFloat(elementSlider.getAttribute("min"))){
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame, "The default number is not correct: Slider default number!!!");
                                return false;
                            }
                            else {
                                CustomSlider customSlider = new CustomSlider(elementSlider.getAttribute("id"), elementControls.getAttribute("name"), elementSlider.getTextContent(),Float.parseFloat(elementSlider.getAttribute("default")), Float.parseFloat(elementSlider.getAttribute("min")), Float.parseFloat(elementSlider.getAttribute("max")), Float.parseFloat(elementSlider.getAttribute("step")));

                                customComponents.add(customSlider);
                            }
                        } catch (NumberFormatException e) {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame, "The ID must be a number: Slider ID!!!\n"+e.getMessage());
                            return false;
                        }
                        
                    }
                    
                    //Bucle sensor
                    NodeList nodelistSensor = elementControls.getElementsByTagName("sensor");
                    for(int l = 0; l < nodelistSensor.getLength(); l++){
                        Element elementSensor = (Element)nodelistSensor.item(l); //Current switch
                        try {
                            Integer.parseInt(elementSensor.getAttribute("id"));
                        } catch (NumberFormatException e) {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame, "The ID must be a number: Sensor ID!!!\n"+e.getMessage());
                            return false;
                        }  
                        if (Integer.parseInt(elementSensor.getAttribute("thresholdlow")) < Integer.parseInt(elementSensor.getAttribute("thresholdhigh"))){
                            CustomSensor customSensor = new CustomSensor(elementSensor.getAttribute("id"), elementControls.getAttribute("name"), elementSensor.getTextContent(), elementSensor.getAttribute("units"), elementSensor.getAttribute("thresholdlow"), elementSensor.getAttribute("thresholdhigh"));
                        
                            customComponents.add(customSensor);
                        }
                        else {
                            JFrame jFrame = new JFrame();
                            JOptionPane.showMessageDialog(jFrame, "Thresholdlow is bigger than thresholdhigh!!!");
                            return false;
                        }
                        
                    
                    }
                    
                    //Bucle dropdown
                    try {
                        NodeList nodelistDropdown = elementControls.getElementsByTagName("dropdown");
                        for(int m = 0; m < nodelistDropdown.getLength(); m++){
                            ArrayList<String> optionsArrayList =  new ArrayList<String>();
                            Element elementDropdown = (Element)nodelistDropdown.item(m);
                            try {
                                Integer.parseInt(elementDropdown.getAttribute("id"));
                            } catch (NumberFormatException e) {
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame, "The ID must be a number: Dropdown ID!!!\n"+e.getMessage());
                                return false;
                            }
                            //Bucle options
                            NodeList nodelistOptions = elementDropdown.getElementsByTagName("option");
                            for(int n = 0; n < nodelistOptions.getLength(); n++){
                                Element elementOption = (Element)nodelistOptions.item(n);
                                optionsArrayList.add(elementOption.getTextContent());
                            }

                            if (Integer.parseInt(elementDropdown.getAttribute("default")) < nodelistOptions.getLength()) {
                                CustomDropdown customDropdown = new CustomDropdown(elementDropdown.getAttribute("id"), elementControls.getAttribute("name"), elementDropdown.getTextContent(), Integer.parseInt(elementDropdown.getAttribute("default")), optionsArrayList);
                            
                                customComponents.add(customDropdown);
                            }
                            else{
                                JFrame jFrame = new JFrame();
                                JOptionPane.showMessageDialog(jFrame, "The default number is not correct: Dropdown default number");
                                return false;
                            }
                            
                        }
                    } catch (IllegalArgumentException e){
                        JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "Be careful with the characters you put!\n"+e.getMessage());
                        return false;
                    } 
                }  
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return false;
            } catch (SAXException e) {
            	JFrame jFrame = new JFrame();
                JOptionPane.showMessageDialog(jFrame, "Remember to put the labels right! It may also be that you have not selected an XML file\n"+e.getMessage());
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } catch (NumberFormatException e) {
            	JFrame jFrame = new JFrame();
                JOptionPane.showMessageDialog(jFrame, "Aware of! Don't put letters and special characters where they don't belong, also be careful with the titles of the variables.\n"+e.getMessage());
                return false;
            } 
         
        }
        else{
        	JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "There is no XML loaded");
            return false;
        }
        return true;
    }

/* 
    private void readCurrentData(){

        for(String key : mapData.keySet()){

            switch(returnIDType(key)){
                case "switch":
                	try {
                		Integer.parseInt(((CustomSwitch)mapData.get(key)).getId());
                		
					} catch (NumberFormatException e) {
						// TODO: handle exception
						JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The ID must be a number!!!\n"+e.getMessage());
                        break;
					}
                	try {
                		String control = ((CustomSwitch)mapData.get(key)).getDefaultValue();
						if(control.equals("off") || control.equals("on")) {
							System.out.println("\nSwitch - ID: " + ((CustomSwitch)mapData.get(key)).getId() + ", Default: " + ((CustomSwitch)mapData.get(key)).getDefaultValue() + ", Label: " + ((CustomSwitch)mapData.get(key)).getLabel() + ", Block: " + ((CustomSwitch)mapData.get(key)).getBlock());
						}
						else {
							throw new Exception();
						}
					} catch (Exception e) {
						JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The default value inside the switch must be on or off");
                        break;
					}
                    break;
                case "slider":
                	try {
                		Integer.parseInt(((CustomSlider)mapData.get(key)).getId());
					} catch (NumberFormatException e) {
						// TODO: handle exception
						JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "Watch the variables, you have to put numbers!!!\n"+e.getMessage());
                        break;
					}
                	if (((CustomSlider)mapData.get(key)).getDefaultValue()<((CustomSlider)mapData.get(key)).getMin() || ((CustomSlider)mapData.get(key)).getDefaultValue() > ((CustomSlider)mapData.get(key)).getMax()) {
                		JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The default number of the slider is not within the established parameters");
                	}	
                	else{
                		System.out.println("\nSlider - ID: " + ((CustomSlider)mapData.get(key)).getId() + ", Default: " + ((CustomSlider)mapData.get(key)).getDefaultValue() + ", Min: " + ((CustomSlider)mapData.get(key)).getMin() + ", Max: " + ((CustomSlider)mapData.get(key)).getMax() + ", Step: " + ((CustomSlider)mapData.get(key)).getStep() + ", Label: " + ((CustomSlider)mapData.get(key)).getLabel() + ", Block: " + ((CustomSlider)mapData.get(key)).getBlock());

                	}
                    break;
                case "dropdown":
                    try {
                        Integer.parseInt(((CustomDropdown)mapData.get(key)).getId());
                    } catch (NumberFormatException e) {
                        // TODO: handle exception
                        JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The ID must be a number!!!\n"+e.getMessage());
                        break;
                    }
                    System.out.println("\nDropdown - ID: " + ((CustomDropdown)mapData.get(key)).getId() + ", Default: " + ((CustomDropdown)mapData.get(key)).getDefaultValue() + ", Block: " + ((CustomDropdown)mapData.get(key)).getBlock() + ", Options:");
                    for(String option : ((CustomDropdown)mapData.get(key)).getOptions()){
                        System.out.println(option);
                    }
                break;
                case "sensor":
                	try {
                		Integer.parseInt(((CustomSensor)mapData.get(key)).getId());
					} catch (NumberFormatException e) {
						// TODO: handle exception
						JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The ID must be a number!!!\n"+e.getMessage());
                        break;
					}
                	try {
						String control = ((CustomSensor)mapData.get(key)).getUnits();
						if (control.equals("ºC")) {
							System.out.println("\nSensor - ID: " + ((CustomSensor)mapData.get(key)).getId() + ", Units: " + ((CustomSensor)mapData.get(key)).getUnits() + ", Thresholdlow: " + ((CustomSensor)mapData.get(key)).getThresholdlow() + ", Thresholdhigh: " + ((CustomSensor)mapData.get(key)).getThresholdhigh() + ", Label: " + ((CustomSensor)mapData.get(key)).getLabel() + ", Block: " + ((CustomSensor)mapData.get(key)).getBlock());
						}
						else {
							throw new Exception();
						}
					} catch (Exception e) {
						// TODO: handle exception
						JFrame jFrame = new JFrame();
                        JOptionPane.showMessageDialog(jFrame, "The temperature measurement unit is incorrect!");
                        break;
					}
                    break;
                default:
                	JFrame jFrame = new JFrame();
                    JOptionPane.showMessageDialog(jFrame, "Key not found!");
                break;
            }

        }

    }
    */

    public void resetData(){
        customComponents.clear();
        controlBlockData.clear();
    }

    /**Función que devuelve el tipo de componente a través de la ID*/
    /*public String returnIDType(String id){
        String type = "-1";
        String[] stringArray  = id.split("-");
        if(stringArray.length != 2){
        	JFrame jFrame = new JFrame();
            JOptionPane.showMessageDialog(jFrame, "The format of the key is wrong!");
        }else{
            type = stringArray[0]; //Se queda con el tipo
        }
        return type;
    }*/

    
}
