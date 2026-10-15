using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class TonKhoService
{
    private readonly TonKhoDAO _dao;
    public TonKhoService(TonKhoDAO? dao = null) => _dao = dao ?? new TonKhoDAO();

    public List<TonKho> GetAll() => _dao.FindAll();
    public TonKho? GetById(string id) => _dao.FindById(id);
    public bool AddTonKho(TonKho item) => _dao.Insert(item);
    public bool DeleteTonKho(string id) => _dao.Delete(id);
    public TonKho? GetByMonAndChiNhanh(string maMon, string maCN) =>
        _dao.FindByMonAndChiNhanh(maMon, maCN);
    public bool UpdateSoLuong(string id, int amount) => amount >= 0 && _dao.UpdateSoLuong(id, amount);
    public List<TonKho> FilterLowStock(int threshold) => _dao.FindAll()
        .Where(t => t.SoLuongTon < threshold).ToList();
}
