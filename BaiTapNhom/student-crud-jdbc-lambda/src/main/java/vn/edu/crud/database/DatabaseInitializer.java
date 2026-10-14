package vn.edu.crud.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private DatabaseInitializer() {
    }

    public static void initialize() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String createClassesTable = """
                    CREATE TABLE IF NOT EXISTS classes (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        class_code VARCHAR(20) NOT NULL UNIQUE,
                        class_name VARCHAR(100) NOT NULL
                    )
                    """;

            String createStudentsTable = """
                    CREATE TABLE IF NOT EXISTS students (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(120) NOT NULL UNIQUE,
                        age INT NOT NULL,
                        class_id INT NULL,
                        CONSTRAINT chk_student_age CHECK (age BETWEEN 16 AND 100),
                        CONSTRAINT fk_students_class
                            FOREIGN KEY (class_id)
                            REFERENCES classes(id)
                            ON UPDATE CASCADE
                            ON DELETE SET NULL
                    )
                    """;

            stmt.execute(createClassesTable);
            stmt.execute(createStudentsTable);

            seedInitialData(stmt);

            System.out.println("[DB] Connection validated. Tables ensured: classes, students");

        } catch (SQLException e) {
            System.err.println("[DB] Failed to initialize database: " + e.getMessage());
            throw new IllegalStateException("Database initialization failed", e);
        }
    }

    private static void seedInitialData(Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM classes")) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("""
                        INSERT INTO classes(class_code, class_name) VALUES
                        ('EC2026A', 'E-Commerce 2026 - A'),
                        ('EC2026B', 'E-Commerce 2026 - B'),
                        ('IT2026A', 'Information Technology 2026 - A')
                        """);
            }
        }

        try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("""
                        INSERT INTO students(name, email, age, class_id) VALUES
                        ('Nguyen Van An', 'an@gmail.com', 20, 1),
                        ('Tran Thi Binh', 'binh@yahoo.com', 21, 1),
                        ('Le Minh Cuong', 'cuong@gmail.com', 19, 2),
                        ('Pham Thu Dung', 'dung@outlook.com', 22, 2),
                        ('Hoang Gia Huy', 'huy@gmail.com', 20, 3),
                        ('Do Minh Khoa', 'khoa@yahoo.com', 23, 3)
                        """);
            }
        }
    }
}