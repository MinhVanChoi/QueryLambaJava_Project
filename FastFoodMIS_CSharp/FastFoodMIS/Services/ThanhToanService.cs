using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class ThanhToanService
{
    private readonly ThanhToanDAO _dao;
    public ThanhToanService(ThanhToanDAO? dao = null) => _dao = dao ?? new ThanhToanDAO();

    public List<ThanhToan> GetAll() => _dao.FindAll();
    public ThanhToan? GetById(string id) => _dao.FindById(id);
    public bool AddThanhToan(ThanhToan item) => _dao.Insert(item);
    public bool DeleteThanhToan(string id) => _dao.Delete(id);
    public List<ThanhToan> GetByMaDH(long id) => _dao.FindByMaDH(id);
    public bool UpdateTrangThai(string id, TrangThaiThanhToan state) => _dao.UpdateTrangThai(id, state);
    public Dictionary<PhuongThucThanhToan, decimal> RevenueByPaymentMethod() => _dao.FindAll()
        .Where(t => t.TrangThai == TrangThaiThanhToan.DA_THANH_TOAN)
        .GroupBy(t => t.PhuongThuc)
        .ToDictionary(g => g.Key, g => g.Sum(t => t.SoTien));
}
