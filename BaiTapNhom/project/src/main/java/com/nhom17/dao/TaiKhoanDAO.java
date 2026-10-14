package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.enums.TrangThaiTaiKhoan;
import com.nhom17.model.TaiKhoan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaiKhoanDAO {

    public List<TaiKhoan> findAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT maTK, maNV, tenDangNhap, matKhauHash, trangThai, ngayTao, lanDangNhapCuoi FROM tai_khoan ORDER BY maTK";

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

    public Optional<TaiKhoan> findById(String maTK) {
        return findAll().stream()
                .filter(tk -> tk.getMaTK().equalsIgnoreCase(maTK))
                .findFirst();
    }

    public Optional<TaiKhoan> findByUsername(String username) {
        return findAll().stream()
                .filter(tk -> tk.getTenDangNhap().equalsIgnoreCase(username))
                .findFirst();
    }

    public boolean insert(TaiKhoan tk) {
        String sql = "INSERT INTO tai_khoan (maTK, maNV, tenDangNhap, matKhauHash, trangThai, ngayTao, lanDangNhapCuoi) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getMaTK());
            ps.setString(2, tk.getMaNV());
            ps.setString(3, tk.getTenDangNhap());
            ps.setString(4, tk.getMatKhauHash());
            ps.setString(5, tk.getTrangThai().name());
            ps.setTimestamp(6, Timestamp.valueOf(tk.getNgayTao()));
            ps.setTimestamp(7, tk.getLanDangNhapCuoi() != null ? Timestamp.valueOf(tk.getLanDangNhapCuoi()) : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(TaiKhoan tk) {
        String sql = "UPDATE tai_khoan SET matKhauHash = ?, trangThai = ?, lanDangNhapCuoi = ? WHERE maTK = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getMatKhauHash());
            ps.setString(2, tk.getTrangThai().name());
            ps.setTimestamp(3, tk.getLanDangNhapCuoi() != null ? Timestamp.valueOf(tk.getLanDangNhapCuoi()) : null);
            ps.setString(4, tk.getMaTK());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean lockAccount(String maTK) {
        String sql = "UPDATE tai_khoan SET trangThai = 'LOCKED' WHERE maTK = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maTK) {
        String sql = "DELETE FROM tai_khoan WHERE maTK = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maTK);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private TaiKhoan mapRow(ResultSet rs) throws SQLException {
        Timestamp lastLogin = rs.getTimestamp("lanDangNhapCuoi");
        return new TaiKhoan(
                rs.getString("maTK"),
                rs.getString("maNV"),
                rs.getString("tenDangNhap"),
                rs.getString("matKhauHash"),
                TrangThaiTaiKhoan.valueOf(rs.getString("trangThai")),
                rs.getTimestamp("ngayTao").toLocalDateTime(),
                lastLogin != null ? lastLogin.toLocalDateTime() : null
        );
    }
}
