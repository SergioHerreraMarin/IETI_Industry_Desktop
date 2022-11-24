import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import com.password4j.Password;

public class UtilsSQLite {

    static String basePath = System.getProperty("user.dir");

    static void iniciarDB(String filePath, String saltPath, String pepperPath, String snapshotPath) {
        Connection conn = connect(filePath);
        Connection connSalt = connect(saltPath);
        Connection connPepper = connect(pepperPath);
        Connection connSnapshot = connect(snapshotPath);

        queryUpdate(conn, "DROP TABLE IF EXISTS user;");
        queryUpdate(conn, "CREATE TABLE IF NOT EXISTS user ("
                + "	id integer PRIMARY KEY AUTOINCREMENT,"
                + "	name varchar(15) NOT NULL,"
                + "	password varchar(50) NOT NULL );");

        queryUpdate(connSalt, "DROP TABLE IF EXISTS salts;");
        queryUpdate(connSalt, "CREATE TABLE IF NOT EXISTS salts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, salt text NOT NULL);");

        queryUpdate(connPepper, "DROP TABLE IF EXISTS peppers;");
        queryUpdate(connPepper, "CREATE TABLE IF NOT EXISTS peppers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, pepper text NOT NULL);");

        queryUpdate(connSnapshot, "DROP TABLE IF EXISTS snapshot;");
        queryUpdate(connSnapshot, "CREATE TABLE IF NOT EXISTS snapshot ("
                + "	id integer PRIMARY KEY AUTOINCREMENT,"
                + "	state varchar(10000) NOT NULL,"
                + "	date TEXT NOT NULL, "
                + "	user varchar(50) NOT NULL" +
                ");");

        queryUpdate(conn,
                "INSERT INTO user (name, password) VALUES (\"admin\",  \"" + encriptar("hola123")
                        + "\");");
        queryUpdate(conn,
                "INSERT INTO user (name, password) VALUES (\"isma\",  \"" + encriptar("prueba")
                        + "\");");
        queryUpdate(conn,
                "INSERT INTO user (name, password) VALUES (\"sergio\",  \"" + encriptar("prueba")
                        + "\");");
        queryUpdate(conn,
                "INSERT INTO user (name, password) VALUES (\"erik\",  \"" + encriptar("prueba")
                        + "\");");

        disconnect(conn);
        disconnect(connSalt);
        disconnect(connPepper);
        disconnect(connSnapshot);
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

    public static String encriptar(String input) {
        String saltPath = basePath + "/src/" + "salt.db";
        Connection cSalt = connect(saltPath);

        String pepperingPath = basePath + "/src/" + "peppering.db";
        Connection cPepper = connect(pepperingPath);

        String pwdSalt = randomChars();
        String pwdPepper = randomChars();

        UtilsSQLite.queryUpdate(cSalt, "INSERT INTO salts (salt) VALUES (\"" + pwdSalt + "\");");
        UtilsSQLite.queryUpdate(cPepper,
                "INSERT INTO peppers (pepper) VALUES (\"" + pwdPepper + "\");");

        disconnect(cSalt);
        disconnect(cPepper);

        return Password.hash(input).addSalt(pwdSalt).addPepper(pwdPepper).withArgon2().getResult();
    }

    public static boolean decriptar(int id, String input, String hash)
            throws SQLException {
        String saltPath = basePath + "/src/" + "salt.db";
        Connection cSalt = connect(saltPath);

        String pepperingPath = basePath + "/src/" + "peppering.db";
        Connection cPepper = connect(pepperingPath);

        String pwdSalt = UtilsSQLite.querySelect(cSalt, "SELECT * FROM salts where id = " + id + ";").getString(2);
        String pwdPepper = UtilsSQLite.querySelect(cPepper, "SELECT * FROM peppers where id = " + id + ";")
                .getString(2);

        disconnect(cSalt);
        disconnect(cPepper);

        return Password.check(input, hash).addSalt(pwdSalt).addPepper(pwdPepper).withArgon2();
    }

    public static boolean login(Connection conn, Connection cSalt, Connection cPepper, String username, String password)
            throws SQLException {

        ResultSet rs = UtilsSQLite.querySelect(conn, "SELECT id, password from user where name='" + username + "';");
        String hash = "";
        int idUser = 0;

        while (rs.next()) {
            idUser = rs.getInt("id");
            hash = rs.getString("password");
        }

        if (decriptar(idUser, password, hash)) {
            return true;
        }
        return false;

    }

    public static Connection connect(String filePath) {
        Connection conn = null;

        try {
            String url = "jdbc:sqlite:" + filePath;
            conn = DriverManager.getConnection(url);
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