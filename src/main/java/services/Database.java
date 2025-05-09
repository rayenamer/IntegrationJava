package services;

import utils.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final Config config = Config.getInstance();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            config.getDbUrl(),
            config.getDbUser(),
            config.getDbPassword()
        );
    }
}
