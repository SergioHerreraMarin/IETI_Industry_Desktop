import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.ResultSet;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Servidor extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    String basePath = System.getProperty("user.dir");

    // Paths de los archivos
    String filePath = basePath + "/src/" + "database.db";
    String saltPath = basePath + "/src/" + "salt.db";
    String pepperingPath = basePath + "/src/" + "peppering.db";


    public Servidor(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));

        try{

            boolean running = true;

            // Deshabilitar SSLv3 per clients Android
            java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

            this.start();
            System.out.println("Servidor funciona al port: " + this.getPort());

            while (running) {
                String line = in.readLine();
                this.broadcast(line);
                if (line.equals("exit")) {
                    running = false;
                }
            }

            System.out.println("Aturant Servidor");
            this.stop(1000);

        }catch(Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        // Saludem personalment al nou client
        conn.send("Benvingut a IETI Industry");

        broadcast("Nova connexió: " + handshake.getResourceDescriptor());

        // Mostrem per pantalla (servidor) la nova connexió
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println(host + " s'ha connectat");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        // Informem a tothom que el client s'ha desconnectat
        broadcast(conn + " s'ha desconnectat");

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println(conn + " s'ha desconnectat");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    
        try {
            if (message.contains("UC")) {
                
                UtilsSQLite.iniciarDB(filePath);
                Connection connection = UtilsSQLite.connect(filePath);

                String[] userInfo = message.split("#");
                String username = userInfo[1];
                String password = userInfo[2];

                ResultSet rs = UtilsSQLite.querySelect(connection, "SELECT * FROM user");
                

                broadcast("V");
                

                /*while (rs.next()) {
                    if (rs.getString("name").equals(username) && rs.getString("password").equals(password)) {
                        broadcast("V");
                    } else {
                        broadcast("NV");
                    }
                }*/


            } else if (message.contains("XML")) {
                //System.out.println("Model data servidor: " + Model.modelData);
                broadcast(Model.modelData);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
        System.out.println("\nERROR\n");
    }

    @Override
    public void onStart() {
        // S'inicia el servidor
        System.out.println("Escriu 'exit' per aturar el servidor");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public String getConnectionId(WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }
}