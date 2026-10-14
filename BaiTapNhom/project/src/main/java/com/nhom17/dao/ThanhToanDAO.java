package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.enums.PhuongThucThanhToan;
import com.nhom17.enums.TrangThaiThanhToan;
import com.nhom17.model.ThanhToan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ThanhToanDAO {

    public List<ThanhToan> findAll() {
        List<ThanhToan> list = new ArrayList<>();
        String sql = "SELECT maTT, maDH, soTien, phuongThuc, trangThai, ngayThanhToan FROM thanh_toan ORDER BY maTT";

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

    public List<ThanhToan> findByMaDH(Long maDH) {
        return findAll().stream()
                .filter(tt -> tt.getMaDH().equals(maDH))
                .toList();
    }

    public Optional<ThanhToan> findById(String maTT) {
        return findAll().stream()
                .filter(tt -> tt.getMaTT().equalsIgnoreCase(maTT))
                .findFirst();
    }

    public boolean insert(ThanhToan tt) {
        String sql = "INSERT INTO thanh_toan (maTT, maDH, soTien, phuongThuc, trangThai, ngayThanhToan) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tt.getMaTT());
            ps.setLong(2, tt.getMaDH());
            ps.setBigDecimal(3, tt.getSoTien());
            ps.setString(4, tt.getPhuongThuc().name());
            ps.setString(5, tt.getTrangThai().name());
            ps.setTimestamp(6, tt.getNgayThanhToan() != null ? Timestamp.valueOf(tt.getNgayThanhToan()) : null);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTrangThai(String maTT, TrangThaiThanhToan trangThai) {
        String sql = "UPDATE thanh_toan SET trangThai = ? WHERE maTT = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, trangThai.name());
            ps.setString(2, maTT);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maTT) {
        String sql = "DELETE FROM thanh_toan WHERE maTT = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maTT);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ThanhToan mapRow(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("ngayThanhToan");
        return new ThanhToan(
                rs.getString("maTT"),
                rs.getLong("maDH"),
                rs.getBigDecimal("soTien"),
                PhuongThucThanhToan.valueOf(rs.getString("phuongThuc")),
                TrangThaiThanhToan.valueOf(rs.getString("trangThai")),
                ts != null ? ts.toLocalDateTime() : null
        );
    }
}
