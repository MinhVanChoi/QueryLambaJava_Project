package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.enums.TrangThaiMon;
import com.nhom17.model.MonAn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MonAnDAO {

    public List<MonAn> findAll() {
        List<MonAn> list = new ArrayList<>();
        String sql = "SELECT maMon, tenMon, giaBan, moTa, trangThai, maDM FROM mon_an ORDER BY maMon";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Optional<MonAn> findById(String maMon) {
        return findAll().stream()
                .filter(m -> m.getMaMon().equalsIgnoreCase(maMon))
                .findFirst();
    }

    public boolean insert(MonAn mon) {
        String sql = "INSERT INTO mon_an (maMon, tenMon, giaBan, moTa, trangThai, maDM) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mon.getMaMon());
            ps.setString(2, mon.getTenMon());
            ps.setBigDecimal(3, mon.getGiaBan());
            ps.setString(4, mon.getMoTa());
            ps.setString(5, mon.getTrangThai().name());
            ps.setString(6, mon.getMaDM());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(MonAn mon) {
        String sql = "UPDATE mon_an SET tenMon = ?, giaBan = ?, moTa = ?, trangThai = ?, maDM = ? WHERE maMon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, mon.getTenMon());
            ps.setBigDecimal(2, mon.getGiaBan());
            ps.setString(3, mon.getMoTa());
            ps.setString(4, mon.getTrangThai().name());
            ps.setString(5, mon.getMaDM());
            ps.setString(6, mon.getMaMon());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maMon) {
        String sql = "DELETE FROM mon_an WHERE maMon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maMon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private MonAn mapRow(ResultSet rs) throws SQLException {
        return new MonAn(
                rs.getString("maMon"),
                rs.getString("tenMon"),
                rs.getBigDecimal("giaBan"),
                rs.getString("moTa"),
                TrangThaiMon.valueOf(rs.getString("trangThai")),
                rs.getString("maDM")
        );
    }
}
