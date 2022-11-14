package pr3;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UserInterface extends JFrame {
    
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private Model model;

    public UserInterface(){

        super("DESKTOP SERVER");
        model = new Model();
        initInterface();
        createMenuBar();


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
            break;
        }
        

    }


    private void initInterface(){
        
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
    }

}
