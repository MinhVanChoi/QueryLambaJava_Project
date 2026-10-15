using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class MonAnService
{
    private readonly MonAnDAO _dao;
    public MonAnService(MonAnDAO? dao = null) => _dao = dao ?? new MonAnDAO();

    public List<MonAn> GetAll() => _dao.FindAll();
    public MonAn? GetById(string id) => _dao.FindById(id);
    public bool AddMonAn(MonAn item) => item.GiaBan > 0 && _dao.Insert(item);
    public bool DeleteMonAn(string id) => _dao.Delete(id);
    public bool UpdateMonAn(MonAn item) => _dao.Update(item);
    public List<MonAn> Filter(Func<MonAn, bool> test) => _dao.FindAll().Where(test).ToList();
    public List<MonAn> SortByPriceAsc() => _dao.FindAll().OrderBy(m => m.GiaBan).ToList();
    public List<MonAn> SortByPriceDescThenName() => _dao.FindAll()
        .OrderByDescending(m => m.GiaBan).ThenBy(m => m.TenMon).ToList();
    public MonAn? FindMostExpensive() => _dao.FindAll().MaxBy(m => m.GiaBan);
    public Dictionary<string, List<MonAn>> GroupByDanhMuc() => _dao.FindAll()
        .GroupBy(m => m.MaDM).ToDictionary(g => g.Key, g => g.ToList());

    public bool UpdateSingleField(string maMon, string field, string value)
    {
        var mon = GetById(maMon);
        if (mon is null) return false;
        switch (field.Trim().ToLowerInvariant())
        {
            case "ten": mon.TenMon = value; break;
            case "gia":
                if (!decimal.TryParse(value, out var price) || price <= 0) return false;
                mon.GiaBan = price; break;
            case "mota": mon.MoTa = value; break;
            case "trangthai":
                if (!Enum.TryParse<TrangThaiMon>(value, true, out var state)) return false;
                mon.TrangThai = state; break;
            case "danhmuc": mon.MaDM = value; break;
            default: return false;
        }
        return _dao.Update(mon);
    }
    public bool AddMonAnChecked(MonAn m) => m.GiaBan > 0 && _dao.Insert(m);
}
