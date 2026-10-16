using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class DanhMucDAO
{
    public List<DanhMuc> FindAll() => SqlDao.Query<DanhMuc>(
        "SELECT maDM, tenDM, moTa FROM danh_muc ORDER BY maDM");

    public DanhMuc? FindById(string id) => SqlDao.Query<DanhMuc>(
        "SELECT maDM, tenDM, moTa FROM danh_muc WHERE maDM = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(DanhMuc item) => SqlDao.Execute(
        "INSERT INTO danh_muc (maDM, tenDM, moTa) VALUES (@maDM, @tenDM, @moTa)",
        ("@maDM", item.MaDM),
        ("@tenDM", item.TenDM),
        ("@moTa", item.MoTa));

    public bool Update(DanhMuc item) => SqlDao.Execute(
        "UPDATE danh_muc SET tenDM = @tenDM, moTa = @moTa WHERE maDM = @id",
        ("@tenDM", item.TenDM),
        ("@moTa", item.MoTa),
        ("@id", item.MaDM));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM danh_muc WHERE maDM = @id",
        ("@id", id));

}
