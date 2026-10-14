package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.enums.TrangThaiKhuyenMai;
import com.nhom17.model.KhuyenMai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KhuyenMaiDAO {

    public List<KhuyenMai> findAll() {
        List<KhuyenMai> list = new ArrayList<>();
        String sql = "SELECT maKM, tenKM, phanTramGiam, donToiThieu, giamToiDa, ngayBatDau, ngayKetThuc, trangThai FROM khuyen_mai ORDER BY maKM";

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

    public Optional<KhuyenMai> findById(String maKM) {
        return findAll().stream()
                .filter(km -> km.getMaKM().equalsIgnoreCase(maKM))
                .findFirst();
    }

    public boolean insert(KhuyenMai km) {
        String sql = "INSERT INTO khuyen_mai (maKM, tenKM, phanTramGiam, donToiThieu, giamToiDa, ngayBatDau, ngayKetThuc, trangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getMaKM());
            ps.setString(2, km.getTenKM());
            ps.setInt(3, km.getPhanTramGiam());
            ps.setBigDecimal(4, km.getDonToiThieu());
            ps.setBigDecimal(5, km.getGiamToiDa());
            ps.setDate(6, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(7, Date.valueOf(km.getNgayKetThuc()));
            ps.setString(8, km.getTrangThai().name());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhuyenMai km) {
        String sql = "UPDATE khuyen_mai SET tenKM = ?, phanTramGiam = ?, donToiThieu = ?, giamToiDa = ?, ngayBatDau = ?, ngayKetThuc = ?, trangThai = ? WHERE maKM = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, km.getTenKM());
            ps.setInt(2, km.getPhanTramGiam());
            ps.setBigDecimal(3, km.getDonToiThieu());
            ps.setBigDecimal(4, km.getGiamToiDa());
            ps.setDate(5, Date.valueOf(km.getNgayBatDau()));
            ps.setDate(6, Date.valueOf(km.getNgayKetThuc()));
            ps.setString(7, km.getTrangThai().name());
            ps.setString(8, km.getMaKM());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maKM) {
        String sql = "DELETE FROM khuyen_mai WHERE maKM = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKM);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private KhuyenMai mapRow(ResultSet rs) throws SQLException {
        return new KhuyenMai(
                rs.getString("maKM"),
                rs.getString("tenKM"),
                rs.getInt("phanTramGiam"),
                rs.getBigDecimal("donToiThieu"),
                rs.getBigDecimal("giamToiDa"),
                rs.getDate("ngayBatDau").toLocalDate(),
                rs.getDate("ngayKetThuc").toLocalDate(),
                TrangThaiKhuyenMai.valueOf(rs.getString("trangThai"))
        );
    }
}
