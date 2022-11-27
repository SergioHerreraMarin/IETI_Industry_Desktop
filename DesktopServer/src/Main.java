import java.net.UnknownHostException;

public class Main {

    public static void main(String[] args) {
        
        try {
            new UserInterface();
            new Servidor(8888);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
