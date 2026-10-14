package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.model.KhachHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KhachHangDAO {

    public List<KhachHang> findAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy FROM khach_hang ORDER BY maKH";

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

    public Optional<KhachHang> findById(String maKH) {
        return findAll().stream()
                .filter(kh -> kh.getMaKH().equalsIgnoreCase(maKH))
                .findFirst();
    }

    public boolean insert(KhachHang kh) {
        String sql = "INSERT INTO khach_hang (maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            ps.setString(3, kh.getSoDienThoai());
            ps.setString(4, kh.getEmail());
            ps.setString(5, kh.getDiaChi());
            ps.setDate(6, Date.valueOf(kh.getNgayDangKy()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(KhachHang kh) {
        String sql = "UPDATE khach_hang SET hoTen = ?, soDienThoai = ?, email = ?, diaChi = ?, ngayDangKy = ? WHERE maKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getHoTen());
            ps.setString(2, kh.getSoDienThoai());
            ps.setString(3, kh.getEmail());
            ps.setString(4, kh.getDiaChi());
            ps.setDate(5, Date.valueOf(kh.getNgayDangKy()));
            ps.setString(6, kh.getMaKH());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maKH) {
        String sql = "DELETE FROM khach_hang WHERE maKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private KhachHang mapRow(ResultSet rs) throws SQLException {
        Date date = rs.getDate("ngayDangKy");
        return new KhachHang(
                rs.getString("maKH"),
                rs.getString("hoTen"),
                rs.getString("soDienThoai"),
                rs.getString("email"),
                rs.getString("diaChi"),
                date != null ? date.toLocalDate() : null
        );
    }
}
