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
    String filePath = basePath + "/src/database.db";
    String saltPath = basePath + "/src/salt.db";
    String pepperingPath = basePath + "/src/peppering.db";
    String snapshotPath = basePath + "/src/snapshot.db";

    public Servidor(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));

        try {

            boolean running = true;

            // Deshabilitar SSLv3 per clients Android
            java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

            this.start();
            System.out.println("Server running in port: " + this.getPort());
            UtilsSQLite.checkDatabase();

            while (running) {
                String line = in.readLine();
                this.broadcast(line);
                if (line.equals("exit")) {
                    running = false;
                }
            }

            System.out.println("Stopping Server...");
            this.stop(1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        // Saludem personalment al nou client
        conn.send("Welcome to IETI Industry");

        broadcast("New connection: " + handshake.getResourceDescriptor());

        // Mostrem per pantalla (servidor) la nova connexió
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println(host + " has connected");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        // Informem a tothom que el client s'ha desconnectat
        broadcast(conn + " has disconnected");

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println(conn + " has disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {

        try {
            if (message.contains("UC")) {
                Connection connection = UtilsSQLite.connect(filePath);

                String[] userInfo = message.split("#");
                String username = userInfo[1];
                String password = userInfo[2];

                ResultSet rs = UtilsSQLite.querySelect(connection, "SELECT * FROM user;");

                while (rs.next()) {
                    if (UtilsSQLite.login(connection, UtilsSQLite.connect(saltPath),
                            UtilsSQLite.connect(pepperingPath), username, password)) {
                        broadcast("V");
                    } else {
                        broadcast("NV");
                    }
                }

            } else if (message.equals("XML")) {
                System.out.println("Components sent");
                broadcast(Model.currentComponentValuesToApp());
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
        System.out.println("Welcome to IETI Industry");
        System.out.println("Write 'exit' to stop the server");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public String getConnectionId(WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }
}