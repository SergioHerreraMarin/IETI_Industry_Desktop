import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class UserInterface extends JFrame {
    
    private final int WIDTH = 800;
    private final int HEIGHT = 600;

    public UserInterface(){

        super("DESKTOP SERVER");
        initInterface();
        createMenuBar();


        this.setVisible(true);
    }


    private void createMenuBar(){

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArxiu = new JMenu("Arxiu");
        JMenu menuVisualitzacion = new JMenu("Visualitzacions");

        menuBar.add(menuArxiu);
        menuBar.add(menuVisualitzacion);


        this.setJMenuBar(menuBar);
    }


    private void initInterface(){
        
        this.setSize(WIDTH, HEIGHT);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
    }


}
