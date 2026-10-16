using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class NhanVienDAO
{
    public List<NhanVien> FindAll() => SqlDao.Query<NhanVien>(
        "SELECT maNV, hoTen, soDienThoai, chucVu, maCN FROM nhan_vien ORDER BY maNV");

    public NhanVien? FindById(string id) => SqlDao.Query<NhanVien>(
        "SELECT maNV, hoTen, soDienThoai, chucVu, maCN FROM nhan_vien WHERE maNV = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(NhanVien item) => SqlDao.Execute(
        "INSERT INTO nhan_vien (maNV, hoTen, soDienThoai, chucVu, maCN) VALUES (@maNV, @hoTen, @soDienThoai, @chucVu, @maCN)",
        ("@maNV", item.MaNV),
        ("@hoTen", item.HoTen),
        ("@soDienThoai", item.SoDienThoai),
        ("@chucVu", item.ChucVu),
        ("@maCN", item.MaCN));

    public bool Update(NhanVien item) => SqlDao.Execute(
        "UPDATE nhan_vien SET hoTen = @hoTen, soDienThoai = @soDienThoai, chucVu = @chucVu, maCN = @maCN WHERE maNV = @id",
        ("@hoTen", item.HoTen),
        ("@soDienThoai", item.SoDienThoai),
        ("@chucVu", item.ChucVu),
        ("@maCN", item.MaCN),
        ("@id", item.MaNV));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM nhan_vien WHERE maNV = @id",
        ("@id", id));

}
