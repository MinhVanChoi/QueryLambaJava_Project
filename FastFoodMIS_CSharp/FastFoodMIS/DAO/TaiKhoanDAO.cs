using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class TaiKhoanDAO
{
    public List<TaiKhoan> FindAll() => SqlDao.Query<TaiKhoan>(
        "SELECT maTK, maNV, tenDangNhap, matKhauHash, trangThai, ngayTao, lanDangNhapCuoi FROM tai_khoan ORDER BY maTK");

    public TaiKhoan? FindById(string id) => SqlDao.Query<TaiKhoan>(
        "SELECT maTK, maNV, tenDangNhap, matKhauHash, trangThai, ngayTao, lanDangNhapCuoi FROM tai_khoan WHERE maTK = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(TaiKhoan item) => SqlDao.Execute(
        "INSERT INTO tai_khoan (maTK, maNV, tenDangNhap, matKhauHash, trangThai, ngayTao, lanDangNhapCuoi) VALUES (@maTK, @maNV, @tenDangNhap, @matKhauHash, @trangThai, @ngayTao, @lanDangNhapCuoi)",
        ("@maTK", item.MaTK),
        ("@maNV", item.MaNV),
        ("@tenDangNhap", item.TenDangNhap),
        ("@matKhauHash", item.MatKhauHash),
        ("@trangThai", item.TrangThai),
        ("@ngayTao", item.NgayTao),
        ("@lanDangNhapCuoi", item.LanDangNhapCuoi));

    public bool Update(TaiKhoan item) => SqlDao.Execute(
        "UPDATE tai_khoan SET matKhauHash = @matKhauHash, trangThai = @trangThai, lanDangNhapCuoi = @lanDangNhapCuoi WHERE maTK = @id",
        ("@matKhauHash", item.MatKhauHash),
        ("@trangThai", item.TrangThai),
        ("@lanDangNhapCuoi", item.LanDangNhapCuoi),
        ("@id", item.MaTK));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM tai_khoan WHERE maTK = @id",
        ("@id", id));

    public TaiKhoan? FindByUsername(string username) => SqlDao.Query<TaiKhoan>(
        "SELECT maTK, maNV, tenDangNhap, matKhauHash, trangThai, ngayTao, lanDangNhapCuoi FROM tai_khoan WHERE tenDangNhap=@name LIMIT 1",
        ("@name", username)).FirstOrDefault();

    public bool LockAccount(string maTK) => SqlDao.Execute(
        "UPDATE tai_khoan SET trangThai='LOCKED' WHERE maTK=@id",
        ("@id", maTK));

}
