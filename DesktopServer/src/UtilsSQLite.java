import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import com.password4j.Password;

public class UtilsSQLite {

    static void iniciarDB(String filePath) {
        Connection conn = connect(filePath);

        queryUpdate(conn, "DROP TABLE IF EXISTS user;");
        queryUpdate(conn, "CREATE TABLE IF NOT EXISTS user ("
                + "	id integer PRIMARY KEY AUTOINCREMENT,"
                + "	name varchar(15) NOT NULL,"
                + "	password varchar(50) NOT NULL );");

        queryUpdate(conn,
                "INSERT INTO user (name, password) VALUES (\"admin\",  \"hola123\");");

        queryUpdate(conn, "CREATE TABLE IF NOT EXISTS salts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, salt varchar(500));");

        queryUpdate(conn, "CREATE TABLE IF NOT EXISTS peppers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, pepper varchar(500));");
    }

    public static int randomInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static String randomChars() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        String cadena = "";
        for (int i = 0; i < 6; i++) {
            int index = randomInt(0, chars.length() - 1);
            char randomChar = chars.charAt(index);
            cadena += randomChar;
        }
        return cadena;
    }

    public static String encriptar(Connection conn, String input) {
        String pwdSalt = randomChars();
        String pwdPepper = randomChars();

        queryUpdate(conn, "INSERT INTO salts (salt) VALUES (\"" + pwdSalt + "\");");
        queryUpdate(conn, "INSERT INTO peppers (pepper) VALUES (\"" + pwdPepper + "\");");

        return Password.hash(input).addSalt(pwdSalt).addPepper(pwdPepper).withArgon2().getResult();
    }

    public static boolean decriptar(Connection conn, int num, String input, String text) throws SQLException {
        String pwdSalt = querySelect(conn, "SELECT * FROM salts where id = " + num + ";").getString(2);
        String pwdPepper = querySelect(conn, "SELECT * FROM pepers where id = " + num + ";").getString(2);

        return Password.check(input, text).addSalt(pwdSalt).addPepper(pwdPepper).withArgon2();
    }

    public static Connection connect(String filePath) {
        Connection conn = null;

        try {
            String url = "jdbc:sqlite:" + filePath;
            conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("BBDD driver: " + meta.getDriverName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static ArrayList<String> listTables(Connection conn) {
        ArrayList<String> list = new ArrayList<>();

        try {
            ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
            while (rs.next()) {
                list.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return list;
    }

    public static void disconnect(Connection conn) {
        try {
            if (conn != null) {
                conn.close();
                System.out.println("DDBB SQLite desconnectada");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static int queryUpdate(Connection conn, String sql) {
        int result = 0;
        try {
            Statement stmt = conn.createStatement();
            result = stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ResultSet querySelect(Connection conn, String sql) {
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Opció invàlida");
        }
        return rs;
    }
}