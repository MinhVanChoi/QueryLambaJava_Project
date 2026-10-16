using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class ChiTietDonHangService
{
    private readonly ChiTietDonHangDAO _dao;
    public ChiTietDonHangService(ChiTietDonHangDAO? dao = null) => _dao = dao ?? new ChiTietDonHangDAO();

    public List<ChiTietDonHang> GetAll() => _dao.FindAll();
    public ChiTietDonHang? GetById(long id) => _dao.FindById(id);
    public bool AddChiTiet(ChiTietDonHang item) => _dao.Insert(item);
    public bool DeleteChiTiet(long id) => _dao.Delete(id);
    public List<ChiTietDonHang> GetByMaDH(long id) => _dao.FindByMaDH(id);
}
