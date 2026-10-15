using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class KhachHangDAO
{
    public List<KhachHang> FindAll() => SqlDao.Query<KhachHang>(
        "SELECT maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy FROM khach_hang ORDER BY maKH");

    public KhachHang? FindById(string id) => SqlDao.Query<KhachHang>(
        "SELECT maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy FROM khach_hang WHERE maKH = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(KhachHang item) => SqlDao.Execute(
        "INSERT INTO khach_hang (maKH, hoTen, soDienThoai, email, diaChi, ngayDangKy) VALUES (@maKH, @hoTen, @soDienThoai, @email, @diaChi, @ngayDangKy)",
        ("@maKH", item.MaKH),
        ("@hoTen", item.HoTen),
        ("@soDienThoai", item.SoDienThoai),
        ("@email", item.Email),
        ("@diaChi", item.DiaChi),
        ("@ngayDangKy", item.NgayDangKy));

    public bool Update(KhachHang item) => SqlDao.Execute(
        "UPDATE khach_hang SET hoTen = @hoTen, soDienThoai = @soDienThoai, email = @email, diaChi = @diaChi, ngayDangKy = @ngayDangKy WHERE maKH = @id",
        ("@hoTen", item.HoTen),
        ("@soDienThoai", item.SoDienThoai),
        ("@email", item.Email),
        ("@diaChi", item.DiaChi),
        ("@ngayDangKy", item.NgayDangKy),
        ("@id", item.MaKH));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM khach_hang WHERE maKH = @id",
        ("@id", id));

}
