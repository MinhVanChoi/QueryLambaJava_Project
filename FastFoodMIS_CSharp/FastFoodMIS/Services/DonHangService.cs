using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;
using FastFoodMIS_CSharp.Database;
using MySqlConnector;

namespace FastFoodMIS_CSharp.Services;

public class DonHangService
{
    private readonly DonHangDAO _dao;
    public DonHangService(DonHangDAO? dao = null) => _dao = dao ?? new DonHangDAO();

    public List<DonHang> GetAll() => _dao.FindAll();
    public DonHang? GetById(long id) => _dao.FindById(id);
    public bool AddDonHang(DonHang item) => _dao.Insert(item);
    public bool DeleteDonHang(long id) => _dao.Delete(id);
    public bool UpdateTrangThai(long id, TrangThaiDonHang state)
    {
        var current = GetById(id);
        return current is not null && current.TrangThai.CoTheChuyenSang(state)
               && _dao.UpdateTrangThai(id, state);
    }
    public long CountCompletedOrders() => _dao.FindAll()
        .LongCount(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH);
    public double CalculateAverageOrderValue()
    {
        var orders = _dao.FindAll();
        return orders.Count == 0 ? 0 : (double)orders.Average(d => d.TongTien);
    }
    public decimal CalculateTotalCompletedRevenue() => _dao.FindAll()
        .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH).Sum(d => d.TongTien);
    public Dictionary<TrangThaiDonHang, long> CountOrdersByStatus() => _dao.FindAll()
        .GroupBy(d => d.TrangThai).ToDictionary(g => g.Key, g => g.LongCount());
    // Ghi đơn, chi tiết và thanh toán trong 1 giao dịch để tránh dữ liệu dang dở.
    public bool AddDonHang(DonHang don, List<ChiTietDonHang> chiTiets, List<ThanhToan> thanhToans)
    {
        if (don.MaDH is null || don.MaDH <= 0) return false;
        using var conn = DBConnection.GetConnection();
        using var tx = conn.BeginTransaction();
        try
        {
            void Exec(string sql, params (string, object?)[] args)
            {
                using var cmd = new MySqlCommand(sql, conn, tx);
                foreach (var (key, value) in args)
                    cmd.Parameters.AddWithValue(key, value ?? DBNull.Value);
                cmd.ExecuteNonQuery();
            }

            Exec("INSERT INTO don_hang (maDH, ngayDat, trangThai, tienHang, tienGiam, tongTien, maKH, maKM, maNV, maCN) VALUES (@id,@date,@state,@items,@discount,@total,@customer,@promo,@staff,@branch)",
                ("@id", don.MaDH), ("@date", don.NgayDat), ("@state", don.TrangThai.ToString()),
                ("@items", don.TienHang), ("@discount", don.TienGiam), ("@total", don.TongTien),
                ("@customer", don.MaKH), ("@promo", don.MaKM), ("@staff", don.MaNV), ("@branch", don.MaCN));

            foreach (var ct in chiTiets ?? new List<ChiTietDonHang>())
            {
                Exec("INSERT INTO chi_tiet_don_hang (maCTDH, maDH, maMon, soLuong, donGia, thanhTien) VALUES (@id,@order,@food,@qty,@price,@total)",
                    ("@id", ct.MaCTDH), ("@order", ct.MaDH ?? don.MaDH), ("@food", ct.MaMon),
                    ("@qty", ct.SoLuong), ("@price", ct.DonGia), ("@total", ct.ThanhTien));
            }

            foreach (var tt in thanhToans ?? new List<ThanhToan>())
            {
                Exec("INSERT INTO thanh_toan (maTT, maDH, soTien, phuongThuc, trangThai, ngayThanhToan) VALUES (@id,@order,@money,@method,@state,@date)",
                    ("@id", tt.MaTT), ("@order", tt.MaDH ?? don.MaDH), ("@money", tt.SoTien),
                    ("@method", tt.PhuongThuc.ToString()), ("@state", tt.TrangThai.ToString()), ("@date", tt.NgayThanhToan));
            }

            tx.Commit();
            return true;
        }
        catch
        {
            tx.Rollback();
            throw;
        }
    }

}
