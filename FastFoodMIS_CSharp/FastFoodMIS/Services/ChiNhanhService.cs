using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class ChiNhanhService
{
    private readonly ChiNhanhDAO _dao;
    public ChiNhanhService(ChiNhanhDAO? dao = null) => _dao = dao ?? new ChiNhanhDAO();

    public List<ChiNhanh> GetAll() => _dao.FindAll();
    public ChiNhanh? GetById(string id) => _dao.FindById(id);
    public bool AddChiNhanh(ChiNhanh item) => _dao.Insert(item);
    public bool DeleteChiNhanh(string id) => _dao.Delete(id);
    public bool UpdateChiNhanh(ChiNhanh item) => _dao.Update(item);
}
