package javasrc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseUtil {
    public static Connection dbConn = null;
    public static Statement dbStatement = null;
    public static ResultSet resultSet = null;

    public static Connection getDbConn(String url,String user,String passwd) {
        return null;
    }

    public static ResultSet getResultSet(Connection connection,String sql) {
        return null;
    }

    public static Statement getDbStatement() {
        return null;
    }
}
