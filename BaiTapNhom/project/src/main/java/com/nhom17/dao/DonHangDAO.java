package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.enums.TrangThaiDonHang;
import com.nhom17.model.DonHang;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DonHangDAO {

    public List<DonHang> findAll() {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT maDH, ngayDat, trangThai, tienHang, tienGiam, tongTien, maKH, maKM, maNV, maCN FROM don_hang ORDER BY maDH";

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

    public Optional<DonHang> findById(Long maDH) {
        return findAll().stream()
                .filter(dh -> dh.getMaDH().equals(maDH))
                .findFirst();
    }

    public boolean insert(DonHang dh) {
        String sql = "INSERT INTO don_hang (maDH, ngayDat, trangThai, tienHang, tienGiam, tongTien, maKH, maKM, maNV, maCN) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dh.getMaDH());
            ps.setTimestamp(2, Timestamp.valueOf(dh.getNgayDat()));
            ps.setString(3, dh.getTrangThai().name());
            ps.setBigDecimal(4, dh.getTienHang());
            ps.setBigDecimal(5, dh.getTienGiam());
            ps.setBigDecimal(6, dh.getTongTien());
            ps.setString(7, dh.getMaKH());
            ps.setString(8, dh.getMaKM());
            ps.setString(9, dh.getMaNV());
            ps.setString(10, dh.getMaCN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(Long maDH, TrangThaiDonHang trangThai) {
        String sql = "UPDATE don_hang SET trangThai = ? WHERE maDH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai.name());
            ps.setLong(2, maDH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTotals(Long maDH, BigDecimal tienHang, BigDecimal tienGiam, BigDecimal tongTien) {
        String sql = "UPDATE don_hang SET tienHang = ?, tienGiam = ?, tongTien = ? WHERE maDH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBigDecimal(1, tienHang);
            ps.setBigDecimal(2, tienGiam);
            ps.setBigDecimal(3, tongTien);
            ps.setLong(4, maDH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(Long maDH) {
        String sql = "DELETE FROM don_hang WHERE maDH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, maDH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private DonHang mapRow(ResultSet rs) throws SQLException {
        return new DonHang(
                rs.getLong("maDH"),
                rs.getTimestamp("ngayDat").toLocalDateTime(),
                TrangThaiDonHang.valueOf(rs.getString("trangThai")),
                rs.getBigDecimal("tienHang"),
                rs.getBigDecimal("tienGiam"),
                rs.getBigDecimal("tongTien"),
                rs.getString("maKH"),
                rs.getString("maKM"),
                rs.getString("maNV"),
                rs.getString("maCN")
        );
    }
}
