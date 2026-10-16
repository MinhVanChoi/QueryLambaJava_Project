using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Services;

public class TaiKhoanService
{
    private readonly TaiKhoanDAO _dao;
    public TaiKhoanService(TaiKhoanDAO? dao = null) => _dao = dao ?? new TaiKhoanDAO();

    public List<TaiKhoan> GetAll() => _dao.FindAll();
    public TaiKhoan? GetById(string id) => _dao.FindById(id);
    public bool AddTaiKhoan(TaiKhoan item) => !UsernameExists(item.TenDangNhap) && _dao.Insert(item);
    public bool DeleteTaiKhoan(string id) => _dao.Delete(id);
    public bool UpdateTaiKhoan(TaiKhoan item) => _dao.Update(item);
    public TaiKhoan? GetByUsername(string username) => _dao.FindByUsername(username);
    public bool UsernameExists(string username) => GetByUsername(username) is not null;
    public bool LockTaiKhoan(string id) => _dao.LockAccount(id);
}
