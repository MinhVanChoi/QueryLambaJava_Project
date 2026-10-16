using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class KhachHangService
{
    private readonly KhachHangDAO _dao;
    public KhachHangService(KhachHangDAO? dao = null) => _dao = dao ?? new KhachHangDAO();

    public List<KhachHang> GetAll() => _dao.FindAll();
    public KhachHang? GetById(string id) => _dao.FindById(id);
    public bool AddKhachHang(KhachHang item) => !PhoneExists(item.SoDienThoai) && _dao.Insert(item);
    public bool DeleteKhachHang(string id) => _dao.Delete(id);
    public bool UpdateKhachHang(KhachHang item) => _dao.Update(item);
    public bool PhoneExists(string phone) => _dao.FindAll()
        .Any(k => string.Equals(k.SoDienThoai, phone, StringComparison.OrdinalIgnoreCase));

    public List<KhachHang> Filter(Func<KhachHang, bool> test) => _dao.FindAll().Where(test).ToList();

    public Dictionary<string, int> CountByEmailDomain() => _dao.FindAll()
        .GroupBy(k => k.Email is null ? "no_email" :
             k.Email.Contains('@') ? k.Email.Split('@').Last().ToLowerInvariant() : "unknown")
        .OrderBy(g => g.Key).ToDictionary(g => g.Key, g => g.Count());
    // Ghi đè kiểm tra trùng số điện thoại trước khi thêm.
    public bool AddKhachHangChecked(KhachHang k) => !PhoneExists(k.SoDienThoai) && _dao.Insert(k);
}
