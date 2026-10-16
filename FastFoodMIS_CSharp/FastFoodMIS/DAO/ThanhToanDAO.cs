using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Utils;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.DAO;

public class ThanhToanDAO
{
    public List<ThanhToan> FindAll() => SqlDao.Query<ThanhToan>(
        "SELECT maTT, maDH, soTien, phuongThuc, trangThai, ngayThanhToan FROM thanh_toan ORDER BY maTT");

    public ThanhToan? FindById(string id) => SqlDao.Query<ThanhToan>(
        "SELECT maTT, maDH, soTien, phuongThuc, trangThai, ngayThanhToan FROM thanh_toan WHERE maTT = @id LIMIT 1",
        ("@id", id)).FirstOrDefault();

    public bool Insert(ThanhToan item) => SqlDao.Execute(
        "INSERT INTO thanh_toan (maTT, maDH, soTien, phuongThuc, trangThai, ngayThanhToan) VALUES (@maTT, @maDH, @soTien, @phuongThuc, @trangThai, @ngayThanhToan)",
        ("@maTT", item.MaTT),
        ("@maDH", item.MaDH),
        ("@soTien", item.SoTien),
        ("@phuongThuc", item.PhuongThuc),
        ("@trangThai", item.TrangThai),
        ("@ngayThanhToan", item.NgayThanhToan));

    public bool Delete(string id) => SqlDao.Execute(
        "DELETE FROM thanh_toan WHERE maTT = @id",
        ("@id", id));

    public List<ThanhToan> FindByMaDH(long id) => FindAll().Where(x => x.MaDH == id).ToList();

    public bool UpdateTrangThai(string id, TrangThaiThanhToan state) => SqlDao.Execute(
        "UPDATE thanh_toan SET trangThai=@state WHERE maTT=@id",
        ("@state", state), ("@id", id));

}
