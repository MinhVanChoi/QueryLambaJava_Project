using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class MonAnDAO
{
    public List<MonAn> FindAll() => SqlDao.Query<MonAn>(
        "SELECT maMon, tenMon, giaBan, moTa, trangThai, maDM FROM mon_an ORDER BY maMon");

    public MonAn? FindById(string id) => SqlDao.Query<MonAn>(
        "SELECT maMon, tenMon, giaBan, moTa, trangThai, maDM FROM mon_an WHERE maMon = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(MonAn item) => SqlDao.Execute(
        "INSERT INTO mon_an (maMon, tenMon, giaBan, moTa, trangThai, maDM) VALUES (@maMon, @tenMon, @giaBan, @moTa, @trangThai, @maDM)",
        ("@maMon", item.MaMon),
        ("@tenMon", item.TenMon),
        ("@giaBan", item.GiaBan),
        ("@moTa", item.MoTa),
        ("@trangThai", item.TrangThai),
        ("@maDM", item.MaDM));

    public bool Update(MonAn item) => SqlDao.Execute(
        "UPDATE mon_an SET tenMon = @tenMon, giaBan = @giaBan, moTa = @moTa, trangThai = @trangThai, maDM = @maDM WHERE maMon = @id",
        ("@tenMon", item.TenMon),
        ("@giaBan", item.GiaBan),
        ("@moTa", item.MoTa),
        ("@trangThai", item.TrangThai),
        ("@maDM", item.MaDM),
        ("@id", item.MaMon));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM mon_an WHERE maMon = @id",
        ("@id", id));

}
