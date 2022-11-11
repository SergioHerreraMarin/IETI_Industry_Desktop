import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Model {
    
    private File currentFile;
    private HashMap<String, InterfaceComponentData> mapData = new HashMap<String, InterfaceComponentData>();

    public Model(){

    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile(File currentFile) {
        this.currentFile = currentFile;
        readXML();
    }

    public HashMap<String, InterfaceComponentData> getMapData() {
        return mapData;
    }

    public void setMapData(HashMap<String, InterfaceComponentData> mapData) {
        this.mapData = mapData;
    }

    
    public void readXML(){

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
                    
                    //Bucle de switches
                    NodeList nodelistSwitch = elementControls.getElementsByTagName("switch");   
                    for(int j = 0; j < nodelistSwitch.getLength(); j++){
                        Element elementSwitch = (Element)nodelistSwitch.item(j); //Current switch    
                        mapData.put("switch-" + elementSwitch.getAttribute("id"), new SwitchData(elementSwitch.getAttribute("id"), elementControls.getAttribute("name"), elementSwitch.getTextContent(), elementSwitch.getAttribute("default")));
                    }

                    //Bucle de slider
                    NodeList nodelistSlider = elementControls.getElementsByTagName("slider");   
                    for(int k = 0; k < nodelistSlider.getLength(); k++){
                        Element elementSlider = (Element)nodelistSlider.item(k); //Current switch    
                        mapData.put("slider-" + elementSlider.getAttribute("id"), new SliderData(elementSlider.getAttribute("id"), elementControls.getAttribute("name"), elementSlider.getTextContent(), Float.parseFloat(elementSlider.getAttribute("default")), Float.parseFloat(elementSlider.getAttribute("min")), Float.parseFloat(elementSlider.getAttribute("max")), Float.parseFloat(elementSlider.getAttribute("step"))));
                    }
                    
                    //Bucle sensor
                    NodeList nodelistSensor = elementControls.getElementsByTagName("sensor");
                    for(int l = 0; l < nodelistSensor.getLength(); l++){
                        Element elementSensor = (Element)nodelistSensor.item(l); //Current switch  
                        mapData.put("sensor-" + elementSensor.getAttribute("id"), new SensorData(elementSensor.getAttribute("id"), elementControls.getAttribute("name"), elementSensor.getTextContent(), elementSensor.getAttribute("units"), elementSensor.getAttribute("thresholdlow"), elementSensor.getAttribute("thresholdhigh")));
                    }
                    
                    //Bucle dropdown
                    NodeList nodelistDropdown = elementControls.getElementsByTagName("dropdown");
                    for(int m = 0; m < nodelistDropdown.getLength(); m++){
                        ArrayList<String> optionsArrayList =  new ArrayList<String>();
                        Element elementDropdown = (Element)nodelistDropdown.item(m);
                        //Bucle options
                        NodeList nodelistOptions = elementDropdown.getElementsByTagName("option");
                        for(int n = 0; n < nodelistOptions.getLength(); n++){
                            Element elementOption = (Element)nodelistOptions.item(n);
                            optionsArrayList.add(elementOption.getTextContent());
                        }

                        mapData.put("dropdown-" + elementDropdown.getAttribute("id"), new DropdownData(elementDropdown.getAttribute("id"), elementControls.getAttribute("name"), elementDropdown.getTextContent(), Integer.parseInt(elementDropdown.getAttribute("default")), optionsArrayList));
                    }
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            readCurrentData();          
        }
        else{
            System.out.println("\n**No hay un XML cargado.**");
        }
    }


    private void readCurrentData(){

        for(String key : mapData.keySet()){

            switch(returnIDType(key)){
                case "switch":
                    System.out.println("\nSwitch - ID: " + ((SwitchData)mapData.get(key)).getId() + ", Default: " + ((SwitchData)mapData.get(key)).getDefaultValue() + ", Label: " + ((SwitchData)mapData.get(key)).getLabel() + ", Block: " + ((SwitchData)mapData.get(key)).getBlock());
                break;
                case "slider":
                    System.out.println("\nSlider - ID: " + ((SliderData)mapData.get(key)).getId() + ", Default: " + ((SliderData)mapData.get(key)).getDefaultValue() + ", Min: " + ((SliderData)mapData.get(key)).getMin() + ", Max: " + ((SliderData)mapData.get(key)).getMax() + ", Step: " + ((SliderData)mapData.get(key)).getStep() + ", Label: " + ((SliderData)mapData.get(key)).getLabel() + ", Block: " + ((SliderData)mapData.get(key)).getBlock());
                break;
                case "dropdown":
                    System.out.println("\nDropdown - ID: " + ((DropdownData)mapData.get(key)).getId() + ", Default: " + ((DropdownData)mapData.get(key)).getDefaultValue() + ", Block: " + ((DropdownData)mapData.get(key)).getBlock() + ", Options:");
                    for(String option : ((DropdownData)mapData.get(key)).getOptions()){
                        System.out.println(option);
                    }
                break;
                case "sensor":
                    System.out.println("\nSensor - ID: " + ((SensorData)mapData.get(key)).getId() + ", Units: " + ((SensorData)mapData.get(key)).getUnits() + ", Thresholdlow: " + ((SensorData)mapData.get(key)).getThresholdlow() + ", Thresholdhigh: " + ((SensorData)mapData.get(key)).getThresholdhigh() + ", Label: " + ((SensorData)mapData.get(key)).getLabel() + ", Block: " + ((SensorData)mapData.get(key)).getBlock());
                break;
                default:
                    System.out.println("\n**No se encuentra la clave**");
                break;
            }

        }

    }

    /**Función que devuelve el tipo de componente a través de la ID*/
    private String returnIDType(String id){
        String type = "-1";
        String[] stringArray  = id.split("-");
        if(stringArray.length != 2){
            System.out.println("\n**Formato de key incorrecto");
        }else{
            type = stringArray[0]; //Se queda con el tipo
        }
        return type;
    }

}
