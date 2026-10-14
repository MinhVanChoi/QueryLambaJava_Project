package vn.edu.crud.dao;

import vn.edu.crud.database.DBConnection;
import vn.edu.crud.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAO {

    private static final String SELECT_BASE = """
            SELECT s.id, s.name, s.email, s.age, s.class_id, c.class_code
            FROM students s
            LEFT JOIN classes c ON s.class_id = c.id
            """;

    public boolean insert(Student student) {
        String sql = """
                INSERT INTO students(name, email, age, class_id)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getAge());

            if (student.getClassId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, student.getClassId());
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            printSQLException("INSERT student", e);
            return false;
        }
    }

    public List<Student> findAll() {
        String sql = SELECT_BASE + " ORDER BY s.id";

        List<Student> students = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                students.add(mapRow(rs));
            }

        } catch (SQLException e) {
            printSQLException("SELECT all students", e);
        }

        return students;
    }

    public Optional<Student> findById(int id) {
        String sql = SELECT_BASE + " WHERE s.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            printSQLException("SELECT student by id", e);
        }

        return Optional.empty();
    }

    public boolean update(Student student) {
        String sql = """
                UPDATE students
                SET name = ?, email = ?, age = ?, class_id = ?
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getAge());

            if (student.getClassId() == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, student.getClassId());
            }

            ps.setInt(5, student.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            printSQLException("UPDATE student", e);
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM students WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            printSQLException("DELETE student", e);
            return false;
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        int classIdValue = rs.getInt("class_id");
        Integer classId = rs.wasNull() ? null : classIdValue;

        return new Student(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("age"),
                classId,
                rs.getString("class_code")
        );
    }

    private void printSQLException(String action, SQLException e) {
        System.err.println("[SQL ERROR] " + action);
        System.err.println("Message: " + e.getMessage());
        System.err.println("SQLState: " + e.getSQLState());
        System.err.println("ErrorCode: " + e.getErrorCode());
    }
}
