
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Models;

public class TaiKhoan
{
    public string MaTK { get; set; } = string.Empty;
    public string MaNV { get; set; } = string.Empty;
    public string? HoTenNV { get; set; }
    public string TenDangNhap { get; set; } = string.Empty;
    public string MatKhauHash { get; set; } = string.Empty;
    public TrangThaiTaiKhoan TrangThai { get; set; }
    public DateTime? NgayTao { get; set; }
    public DateTime? LanDangNhapCuoi { get; set; }

    public TaiKhoan() { }

    public TaiKhoan(
        string maTK,
        string maNV,
        string tenDangNhap,
        string matKhauHash,
        TrangThaiTaiKhoan trangThai,
        DateTime? ngayTao,
        DateTime? lanDangNhapCuoi)
    {
        MaTK = maTK;
        MaNV = maNV;
        TenDangNhap = tenDangNhap;
        MatKhauHash = matKhauHash;
        TrangThai = trangThai;
        NgayTao = ngayTao;
        LanDangNhapCuoi = lanDangNhapCuoi;
    }

    public TaiKhoan(
        string maTK,
        string maNV,
        string? hoTenNV,
        string tenDangNhap,
        string matKhauHash,
        TrangThaiTaiKhoan trangThai,
        DateTime? ngayTao,
        DateTime? lanDangNhapCuoi)
        : this(maTK, maNV, tenDangNhap, matKhauHash,
               trangThai, ngayTao, lanDangNhapCuoi)
    {
        HoTenNV = hoTenNV;
    }

    public override string ToString()
    {
        string ngayTao = NgayTao?.ToString(
            "yyyy-MM-dd HH:mm:ss") ?? "N/A";

        string lanCuoi = LanDangNhapCuoi?.ToString(
            "yyyy-MM-dd HH:mm:ss") ?? "(Chưa đăng nhập)";

        return $"{MaTK,-6} | {MaNV,-6} | " +
               $"{TenDangNhap,-15} | {TrangThai,-8} | " +
               $"{ngayTao,-20} | {lanCuoi,-20}";
    }

    public override bool Equals(object? obj)
    {
        return obj is TaiKhoan other &&
               MaTK == other.MaTK;
    }

    public override int GetHashCode()
    {
        return MaTK?.GetHashCode() ?? 0;
    }
}
