using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class ChiTietDonHangDAO
{
    public List<ChiTietDonHang> FindAll() => SqlDao.Query<ChiTietDonHang>(
        "SELECT maCTDH, maDH, maMon, soLuong, donGia, thanhTien FROM chi_tiet_don_hang ORDER BY maCTDH");

    public ChiTietDonHang? FindById(long id) => SqlDao.Query<ChiTietDonHang>(
        "SELECT maCTDH, maDH, maMon, soLuong, donGia, thanhTien FROM chi_tiet_don_hang WHERE maCTDH = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(ChiTietDonHang item) => SqlDao.Execute(
        "INSERT INTO chi_tiet_don_hang (maCTDH, maDH, maMon, soLuong, donGia, thanhTien) VALUES (@maCTDH, @maDH, @maMon, @soLuong, @donGia, @thanhTien)",
        ("@maCTDH", item.MaCTDH),
        ("@maDH", item.MaDH),
        ("@maMon", item.MaMon),
        ("@soLuong", item.SoLuong),
        ("@donGia", item.DonGia),
        ("@thanhTien", item.ThanhTien));

    public bool Delete(long id) => SqlDao.Execute(
        "DELETE FROM chi_tiet_don_hang WHERE maCTDH = @id",
        ("@id", id));

    public List<ChiTietDonHang> FindByMaDH(long id) => FindAll().Where(x => x.MaDH == id).ToList();

}
