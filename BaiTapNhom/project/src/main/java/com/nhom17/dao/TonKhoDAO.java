package com.nhom17.dao;

import com.nhom17.database.DBConnection;
import com.nhom17.model.TonKho;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TonKhoDAO {

    public List<TonKho> findAll() {
        List<TonKho> list = new ArrayList<>();
        String sql = "SELECT maTon, maMon, maCN, soLuongTon, ngayCapNhat FROM ton_kho ORDER BY maCN, maMon";

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

    public Optional<TonKho> findById(String maTon) {
        return findAll().stream()
                .filter(tk -> tk.getMaTon().equalsIgnoreCase(maTon))
                .findFirst();
    }

    public Optional<TonKho> findByMonAndChiNhanh(String maMon, String maCN) {
        return findAll().stream()
                .filter(tk -> tk.getMaMon().equalsIgnoreCase(maMon) && tk.getMaCN().equalsIgnoreCase(maCN))
                .findFirst();
    }

    public boolean insert(TonKho tk) {
        String sql = "INSERT INTO ton_kho (maTon, maMon, maCN, soLuongTon, ngayCapNhat) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getMaTon());
            ps.setString(2, tk.getMaMon());
            ps.setString(3, tk.getMaCN());
            ps.setInt(4, tk.getSoLuongTon());
            ps.setTimestamp(5, Timestamp.valueOf(tk.getNgayCapNhat()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSoLuong(String maTon, int soLuongTon) {
        String sql = "UPDATE ton_kho SET soLuongTon = ?, ngayCapNhat = CURRENT_TIMESTAMP WHERE maTon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, soLuongTon);
            ps.setString(2, maTon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(String maTon) {
        String sql = "DELETE FROM ton_kho WHERE maTon = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maTon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private TonKho mapRow(ResultSet rs) throws SQLException {
        return new TonKho(
                rs.getString("maTon"),
                rs.getString("maCN"),
                rs.getString("maMon"),
                rs.getInt("soLuongTon"),
                rs.getTimestamp("ngayCapNhat").toLocalDateTime()
        );
    }
}
