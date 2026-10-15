using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class KhuyenMaiDAO
{
    public List<KhuyenMai> FindAll() => SqlDao.Query<KhuyenMai>(
        "SELECT maKM, tenKM, phanTramGiam, donToiThieu, giamToiDa, ngayBatDau, ngayKetThuc, trangThai FROM khuyen_mai ORDER BY maKM");

    public KhuyenMai? FindById(string id) => SqlDao.Query<KhuyenMai>(
        "SELECT maKM, tenKM, phanTramGiam, donToiThieu, giamToiDa, ngayBatDau, ngayKetThuc, trangThai FROM khuyen_mai WHERE maKM = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(KhuyenMai item) => SqlDao.Execute(
        "INSERT INTO khuyen_mai (maKM, tenKM, phanTramGiam, donToiThieu, giamToiDa, ngayBatDau, ngayKetThuc, trangThai) VALUES (@maKM, @tenKM, @phanTramGiam, @donToiThieu, @giamToiDa, @ngayBatDau, @ngayKetThuc, @trangThai)",
        ("@maKM", item.MaKM),
        ("@tenKM", item.TenKM),
        ("@phanTramGiam", item.PhanTramGiam),
        ("@donToiThieu", item.DonToiThieu),
        ("@giamToiDa", item.GiamToiDa),
        ("@ngayBatDau", item.NgayBatDau),
        ("@ngayKetThuc", item.NgayKetThuc),
        ("@trangThai", item.TrangThai));

    public bool Update(KhuyenMai item) => SqlDao.Execute(
        "UPDATE khuyen_mai SET tenKM = @tenKM, phanTramGiam = @phanTramGiam, donToiThieu = @donToiThieu, giamToiDa = @giamToiDa, ngayBatDau = @ngayBatDau, ngayKetThuc = @ngayKetThuc, trangThai = @trangThai WHERE maKM = @id",
        ("@tenKM", item.TenKM),
        ("@phanTramGiam", item.PhanTramGiam),
        ("@donToiThieu", item.DonToiThieu),
        ("@giamToiDa", item.GiamToiDa),
        ("@ngayBatDau", item.NgayBatDau),
        ("@ngayKetThuc", item.NgayKetThuc),
        ("@trangThai", item.TrangThai),
        ("@id", item.MaKM));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM khuyen_mai WHERE maKM = @id",
        ("@id", id));

}
