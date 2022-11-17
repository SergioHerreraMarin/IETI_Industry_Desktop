import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UtilsSQLite {

    static void iniciarDB(String filePath) {
        Connection conn = connect(filePath);

        queryUpdate(conn, "DROP TABLE IF EXISTS user;");
        queryUpdate(conn, "CREATE TABLE IF NOT EXISTS user ("
                + "	id integer PRIMARY KEY AUTOINCREMENT,"
                + "	nom varchar(15) NOT NULL,"
                + "	contrasenya varchar(50) NOT NULL );");
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
            System.out.println("BBDD SQLite connectada");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
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