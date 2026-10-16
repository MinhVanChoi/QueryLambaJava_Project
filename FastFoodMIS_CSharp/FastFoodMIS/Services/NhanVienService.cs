using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class NhanVienService
{
    private readonly NhanVienDAO _dao;
    public NhanVienService(NhanVienDAO? dao = null) => _dao = dao ?? new NhanVienDAO();

    public List<NhanVien> GetAll() => _dao.FindAll();
    public NhanVien? GetById(string id) => _dao.FindById(id);
    public bool AddNhanVien(NhanVien item) => _dao.Insert(item);
    public bool DeleteNhanVien(string id) => _dao.Delete(id);
    public bool UpdateNhanVien(NhanVien item) => _dao.Update(item);
    public List<NhanVien> SearchByName(string keyword) => _dao.FindAll()
        .Where(n => n.HoTen.Contains(keyword, StringComparison.OrdinalIgnoreCase)).ToList();
    public List<NhanVien> Filter(Func<NhanVien, bool> test) => _dao.FindAll().Where(test).ToList();
    public Dictionary<string, List<NhanVien>> GroupByChiNhanh() => _dao.FindAll()
        .GroupBy(n => n.MaCN).ToDictionary(g => g.Key, g => g.ToList());
}
