package com.nhom17.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DBConnection {

    private static String url;
    private static String user;
    private static String password;
    private static boolean useH2Fallback = false;

    static {
        loadConfig();
    }

    private DBConnection() {}

    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream is = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is != null) {
                props.load(is);
                url = props.getProperty("db.url");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");
            } else {
                url = "jdbc:mysql://localhost:3306/fastfood_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8&createDatabaseIfNotExist=true";
                user = "root";
                password = "";
            }
        } catch (Exception e) {
            System.err.println("[WARN] Khong doc duoc file database.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (useH2Fallback) {
            return DriverManager.getConnection("jdbc:h2:mem:fastfood_db;DB_CLOSE_DELAY=-1;MODE=MySQL", "sa", "");
        }

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            if (!useH2Fallback) {
                System.out.println("[INFO] Khong ket noi duoc MySQL (" + e.getMessage() + ").");
                System.out.println("[INFO] Tu dong chuyen sang H2 In-Memory Database (MySQL Mode) de chay tiep.");
                useH2Fallback = true;
                return DriverManager.getConnection("jdbc:h2:mem:fastfood_db;DB_CLOSE_DELAY=-1;MODE=MySQL", "sa", "");
            }
            throw e;
        }
    }

    public static boolean isH2Mode() {
        return useH2Fallback;
    }
}
