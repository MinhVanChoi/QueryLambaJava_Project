package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.enums.ChucVu;
import com.nhom17.model.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NhanVienDAO {

    public List<NhanVien> findAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT maNV, hoTen, soDienThoai, chucVu, maCN FROM nhan_vien ORDER BY maNV";

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

    public Optional<NhanVien> findById(String maNV) {
        return findAll().stream()
                .filter(nv -> nv.getMaNV().equalsIgnoreCase(maNV))
                .findFirst();
    }

    public boolean insert(NhanVien nv) {
        String sql = "INSERT INTO nhan_vien (maNV, hoTen, soDienThoai, chucVu, maCN) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            ps.setString(3, nv.getSoDienThoai());
            ps.setString(4, nv.getChucVu().name());
            ps.setString(5, nv.getMaCN());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(NhanVien nv) {
        String sql = "UPDATE nhan_vien SET hoTen = ?, soDienThoai = ?, chucVu = ?, maCN = ? WHERE maNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nv.getHoTen());
            ps.setString(2, nv.getSoDienThoai());
            ps.setString(3, nv.getChucVu().name());
            ps.setString(4, nv.getMaCN());
            ps.setString(5, nv.getMaNV());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maNV) {
        String sql = "DELETE FROM nhan_vien WHERE maNV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private NhanVien mapRow(ResultSet rs) throws SQLException {
        return new NhanVien(
                rs.getString("maNV"),
                rs.getString("hoTen"),
                rs.getString("soDienThoai"),
                ChucVu.valueOf(rs.getString("chucVu")),
                rs.getString("maCN")
        );
    }
}
