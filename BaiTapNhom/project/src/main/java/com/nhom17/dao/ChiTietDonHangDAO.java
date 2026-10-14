package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.model.ChiTietDonHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChiTietDonHangDAO {

    public List<ChiTietDonHang> findAll() {
        List<ChiTietDonHang> list = new ArrayList<>();
        String sql = "SELECT maCTDH, maDH, maMon, soLuong, donGia, thanhTien FROM chi_tiet_don_hang ORDER BY maCTDH";

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

    public List<ChiTietDonHang> findByMaDH(Long maDH) {
        return findAll().stream()
                .filter(ct -> ct.getMaDH().equals(maDH))
                .toList();
    }

    public Optional<ChiTietDonHang> findById(Long maCTDH) {
        return findAll().stream()
                .filter(ct -> ct.getMaCTDH().equals(maCTDH))
                .findFirst();
    }

    public boolean insert(ChiTietDonHang ctdh) {
        String sql = "INSERT INTO chi_tiet_don_hang (maCTDH, maDH, maMon, soLuong, donGia, thanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ctdh.getMaCTDH());
            ps.setLong(2, ctdh.getMaDH());
            ps.setString(3, ctdh.getMaMon());
            ps.setInt(4, ctdh.getSoLuong());
            ps.setBigDecimal(5, ctdh.getDonGia());
            ps.setBigDecimal(6, ctdh.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long maCTDH) {
        String sql = "DELETE FROM chi_tiet_don_hang WHERE maCTDH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maCTDH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ChiTietDonHang mapRow(ResultSet rs) throws SQLException {
        return new ChiTietDonHang(
                rs.getLong("maCTDH"),
                rs.getLong("maDH"),
                rs.getString("maMon"),
                rs.getInt("soLuong"),
                rs.getBigDecimal("donGia"),
                rs.getBigDecimal("thanhTien")
        );
    }
}
