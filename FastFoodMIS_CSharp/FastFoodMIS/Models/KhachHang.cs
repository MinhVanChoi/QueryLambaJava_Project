
namespace FastFoodMIS_CSharp.Models;

public class KhachHang
{
    public string MaKH { get; set; } = string.Empty;
    public string HoTen { get; set; } = string.Empty;
    public string SoDienThoai { get; set; } = string.Empty;
    public string? Email { get; set; }
    public string? DiaChi { get; set; }
    public DateOnly? NgayDangKy { get; set; }

    public KhachHang()
    {
    }

    public KhachHang(
        string maKH, string hoTen,
        string soDienThoai, DateOnly ngayDangKy)
    {
        MaKH = maKH;
        HoTen = hoTen;
        SoDienThoai = soDienThoai;
        NgayDangKy = ngayDangKy;
    }

    public KhachHang(
        string maKH, string hoTen,
        string soDienThoai, string? email,
        string? diaChi, DateOnly ngayDangKy)
    {
        MaKH = maKH;
        HoTen = hoTen;
        SoDienThoai = soDienThoai;
        Email = email;
        DiaChi = diaChi;
        NgayDangKy = ngayDangKy;
    }

    public override string ToString()
    {
        return $"{MaKH,-6} | {HoTen,-20} | " +
               $"{SoDienThoai,-12} | " +
               $"{Email ?? "N/A",-20} | " +
               $"{DiaChi ?? "N/A",-25} | " +
               $"{NgayDangKy?.ToString("yyyy-MM-dd") ?? "N/A",-10}";
    }

    public override bool Equals(object? obj)
    {
        return obj is KhachHang kh &&
               MaKH == kh.MaKH;
    }

    public override int GetHashCode()
    {
        return StringComparer.Ordinal.GetHashCode(MaKH);
    }
}
