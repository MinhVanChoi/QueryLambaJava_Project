using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class DanhMucService
{
    private readonly DanhMucDAO _dao;
    public DanhMucService(DanhMucDAO? dao = null) => _dao = dao ?? new DanhMucDAO();

    public List<DanhMuc> GetAll() => _dao.FindAll();
    public DanhMuc? GetById(string id) => _dao.FindById(id);
    public bool AddDanhMuc(DanhMuc item) => _dao.Insert(item);
    public bool DeleteDanhMuc(string id) => _dao.Delete(id);
    public bool UpdateDanhMuc(DanhMuc item) => _dao.Update(item);
}
