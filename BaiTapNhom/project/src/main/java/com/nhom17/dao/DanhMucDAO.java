package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.model.DanhMuc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DanhMucDAO {

    public List<DanhMuc> findAll() {
        List<DanhMuc> list = new ArrayList<>();
        String sql = "SELECT maDM, tenDM, moTa FROM danh_muc ORDER BY maDM";

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

    public Optional<DanhMuc> findById(String maDM) {
        return findAll().stream()
                .filter(dm -> dm.getMaDM().equalsIgnoreCase(maDM))
                .findFirst();
    }

    public boolean insert(DanhMuc dm) {
        String sql = "INSERT INTO danh_muc (maDM, tenDM, moTa) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dm.getMaDM());
            ps.setString(2, dm.getTenDM());
            ps.setString(3, dm.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(DanhMuc dm) {
        String sql = "UPDATE danh_muc SET tenDM = ?, moTa = ? WHERE maDM = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dm.getTenDM());
            ps.setString(2, dm.getMoTa());
            ps.setString(3, dm.getMaDM());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maDM) {
        String sql = "DELETE FROM danh_muc WHERE maDM = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maDM);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private DanhMuc mapRow(ResultSet rs) throws SQLException {
        return new DanhMuc(
                rs.getString("maDM"),
                rs.getString("tenDM"),
                rs.getString("moTa")
        );
    }
}
