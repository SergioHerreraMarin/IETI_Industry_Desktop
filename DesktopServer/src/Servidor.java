import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

public class Servidor extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws InterruptedException, IOException {
        int port = 8888;
        boolean running = true;

        // Deshabilitar SSLv3 per clients Android
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        Servidor socket = new Servidor(port);
        socket.start();
        System.out.println("Servidor funciona al port: " + socket.getPort());

        while (running) {
            String line = in.readLine();
            socket.broadcast(line);
            if (line.equals("exit")) {
                running = false;
            }
        }

        System.out.println("Aturant Servidor");
        socket.stop(1000);
    }

    public Servidor(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public Servidor(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        // Saludem personalment al nou client
        conn.send("Benvingut a IETI Industry");

        // Enviem la direcció URI del nou client a tothom
        System.out.println("\nOK\n");
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

        System.out.println(message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {

        // Enviem el missatge del client a tothom
        broadcast(message.array());

        // Mostrem per pantalla (servidor) el missatge
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
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