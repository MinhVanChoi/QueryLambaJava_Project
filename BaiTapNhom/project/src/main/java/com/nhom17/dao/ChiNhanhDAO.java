package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.model.ChiNhanh;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChiNhanhDAO {

    public List<ChiNhanh> findAll() {
        List<ChiNhanh> list = new ArrayList<>();
        String sql = "SELECT maCN, tenCN, diaChi, soDienThoai FROM chi_nhanh ORDER BY maCN";

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

    public Optional<ChiNhanh> findById(String maCN) {
        return findAll().stream()
                .filter(cn -> cn.getMaCN().equalsIgnoreCase(maCN))
                .findFirst();
    }

    public boolean insert(ChiNhanh cn) {
        String sql = "INSERT INTO chi_nhanh (maCN, tenCN, diaChi, soDienThoai) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cn.getMaCN());
            ps.setString(2, cn.getTenCN());
            ps.setString(3, cn.getDiaChi());
            ps.setString(4, cn.getSoDienThoai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(ChiNhanh cn) {
        String sql = "UPDATE chi_nhanh SET tenCN = ?, diaChi = ?, soDienThoai = ? WHERE maCN = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cn.getTenCN());
            ps.setString(2, cn.getDiaChi());
            ps.setString(3, cn.getSoDienThoai());
            ps.setString(4, cn.getMaCN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maCN) {
        String sql = "DELETE FROM chi_nhanh WHERE maCN = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCN);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ChiNhanh mapRow(ResultSet rs) throws SQLException {
        return new ChiNhanh(
                rs.getString("maCN"),
                rs.getString("tenCN"),
                rs.getString("diaChi"),
                rs.getString("soDienThoai")
        );
    }
}
