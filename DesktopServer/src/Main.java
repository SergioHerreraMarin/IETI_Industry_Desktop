import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        try {
            new UserInterface();
            new Servidor(8888);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
