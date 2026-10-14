package vn.edu.crud.dao;

import vn.edu.crud.database.DBConnection;
import vn.edu.crud.model.StudentClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    public List<StudentClass> findAll() {
        String sql = """
                SELECT id, class_code, class_name
                FROM classes
                ORDER BY class_code
                """;

        List<StudentClass> classes = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                classes.add(new StudentClass(
                        rs.getInt("id"),
                        rs.getString("class_code"),
                        rs.getString("class_name")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[SQL ERROR] " + e.getMessage());
        }

        return classes;
    }
}
