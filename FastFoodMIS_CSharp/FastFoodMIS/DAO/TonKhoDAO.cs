using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class TonKhoDAO
{
    public List<TonKho> FindAll() => SqlDao.Query<TonKho>(
        "SELECT maTon, maMon, maCN, soLuongTon, ngayCapNhat FROM ton_kho ORDER BY maCN, maMon");

    public TonKho? FindById(string id) => SqlDao.Query<TonKho>(
        "SELECT maTon, maMon, maCN, soLuongTon, ngayCapNhat FROM ton_kho WHERE maTon = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(TonKho item) => SqlDao.Execute(
        "INSERT INTO ton_kho (maTon, maMon, maCN, soLuongTon, ngayCapNhat) VALUES (@maTon, @maMon, @maCN, @soLuongTon, @ngayCapNhat)",
        ("@maTon", item.MaTon),
        ("@maMon", item.MaMon),
        ("@maCN", item.MaCN),
        ("@soLuongTon", item.SoLuongTon),
        ("@ngayCapNhat", item.NgayCapNhat));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM ton_kho WHERE maTon = @id",
        ("@id", id));

    public TonKho? FindByMonAndChiNhanh(string maMon, string maCN) => SqlDao.Query<TonKho>(
        "SELECT maTon, maMon, maCN, soLuongTon, ngayCapNhat FROM ton_kho WHERE maMon=@m AND maCN=@c LIMIT 1",
        ("@m", maMon), ("@c", maCN)).FirstOrDefault();

    public bool UpdateSoLuong(string maTon, int soLuongTon) => SqlDao.Execute(
        "UPDATE ton_kho SET soLuongTon=@qty, ngayCapNhat=CURRENT_TIMESTAMP WHERE maTon=@id",
        ("@qty", soLuongTon), ("@id", maTon));

}
