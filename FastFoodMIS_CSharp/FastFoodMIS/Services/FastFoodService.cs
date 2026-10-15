using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Models;

namespace FastFoodMIS_CSharp.Services;

/// <summary>Lớp phối hợp giữa các DAO/Service, tương tự FastFoodService.java.</summary>
public class FastFoodService
{
    public List<ChiNhanh> GetAllChiNhanh() => new ChiNhanhService().GetAll();
    public List<NhanVien> GetAllNhanVien() => new NhanVienService().GetAll();
    public List<TaiKhoan> GetAllTaiKhoan() => new TaiKhoanService().GetAll();
    public List<KhachHang> GetAllKhachHang() => new KhachHangService().GetAll();
    public List<DanhMuc> GetAllDanhMuc() => new DanhMucService().GetAll();
    public List<MonAn> GetAllMonAn() => new MonAnService().GetAll();
    public List<TonKho> GetAllTonKho() => new TonKhoService().GetAll();
    public List<KhuyenMai> GetAllKhuyenMai() => new KhuyenMaiService().GetAll();
    public List<DonHang> GetAllDonHang() => new DonHangService().GetAll();
    public List<ChiTietDonHang> GetAllChiTietDonHang() => new ChiTietDonHangService().GetAll();
    public List<ThanhToan> GetAllThanhToan() => new ThanhToanService().GetAll();
    public QueryDAO GetQueryDAO() => new QueryDAO();
}
