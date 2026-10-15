using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class KhuyenMaiService
{
    private readonly KhuyenMaiDAO _dao;
    public KhuyenMaiService(KhuyenMaiDAO? dao = null) => _dao = dao ?? new KhuyenMaiDAO();

    public List<KhuyenMai> GetAll() => _dao.FindAll();
    public KhuyenMai? GetById(string id) => _dao.FindById(id);
    public bool AddKhuyenMai(KhuyenMai item) => _dao.Insert(item);
    public bool DeleteKhuyenMai(string id) => _dao.Delete(id);
    public bool UpdateKhuyenMai(KhuyenMai item) => _dao.Update(item);
    public List<KhuyenMai> FilterActive(DateOnly date) => _dao.FindAll()
        .Where(k => k.TrangThai == TrangThaiKhuyenMai.DANG_AP_DUNG
                 && k.ConHieuLuc(date)).ToList();
}
