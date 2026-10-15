using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class DonHangDAO
{
    public List<DonHang> FindAll() => SqlDao.Query<DonHang>(
        "SELECT maDH, ngayDat, trangThai, tienHang, tienGiam, tongTien, maKH, maKM, maNV, maCN FROM don_hang ORDER BY maDH");

    public DonHang? FindById(long id) => SqlDao.Query<DonHang>(
        "SELECT maDH, ngayDat, trangThai, tienHang, tienGiam, tongTien, maKH, maKM, maNV, maCN FROM don_hang WHERE maDH = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(DonHang item) => SqlDao.Execute(
        "INSERT INTO don_hang (maDH, ngayDat, trangThai, tienHang, tienGiam, tongTien, maKH, maKM, maNV, maCN) VALUES (@maDH, @ngayDat, @trangThai, @tienHang, @tienGiam, @tongTien, @maKH, @maKM, @maNV, @maCN)",
        ("@maDH", item.MaDH),
        ("@ngayDat", item.NgayDat),
        ("@trangThai", item.TrangThai),
        ("@tienHang", item.TienHang),
        ("@tienGiam", item.TienGiam),
        ("@tongTien", item.TongTien),
        ("@maKH", item.MaKH),
        ("@maKM", item.MaKM),
        ("@maNV", item.MaNV),
        ("@maCN", item.MaCN));

    public bool Delete(long id) => SqlDao.Execute(
        "DELETE FROM don_hang WHERE maDH = @id",
        ("@id", id));

    public bool UpdateTrangThai(long id, TrangThaiDonHang state) => SqlDao.Execute(
        "UPDATE don_hang SET trangThai=@state WHERE maDH=@id",
        ("@state", state), ("@id", id));

    public bool UpdateTotals(long id, decimal tienHang, decimal tienGiam, decimal tongTien) => SqlDao.Execute(
        "UPDATE don_hang SET tienHang=@a, tienGiam=@b, tongTien=@c WHERE maDH=@id",
        ("@a", tienHang), ("@b", tienGiam), ("@c", tongTien), ("@id", id));

}
