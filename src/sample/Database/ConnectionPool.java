package sample.Database;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;


public class ConnectionPool {

    private static final BasicDataSource dataSource;
    private static final String DB_URL = "jdbc:mysql://XXXX:3306/XXXX";
    private static final String DB_USER = "XXXX";
    private static final String DB_PASSWORD = "XXXX";

    static {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        importConnectionInfo();
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(10);
        dataSource.setMaxTotal(50);
        dataSource.setMaxWaitMillis(10000);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void importConnectionInfo() {
        try {
            dataSource.setUrl(DB_URL);
            dataSource.setUsername(DB_USER);
            dataSource.setPassword(DB_PASSWORD);
        } catch (Exception ignored) {}
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
