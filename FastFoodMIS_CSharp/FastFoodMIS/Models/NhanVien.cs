
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Models;

public class NhanVien
{
    public string MaNV { get; set; } = string.Empty;
    public string HoTen { get; set; } = string.Empty;
    public string SoDienThoai { get; set; } = string.Empty;
    public ChucVu ChucVu { get; set; }
    public string MaCN { get; set; } = string.Empty;
    public string? TenCN { get; set; }

    public NhanVien() { }

    public NhanVien(
        string maNV,
        string hoTen,
        string soDienThoai,
        ChucVu chucVu,
        string maCN)
    {
        MaNV = maNV;
        HoTen = hoTen;
        SoDienThoai = soDienThoai;
        ChucVu = chucVu;
        MaCN = maCN;
    }

    public NhanVien(
        string maNV,
        string hoTen,
        string soDienThoai,
        ChucVu chucVu,
        string maCN,
        string? tenCN)
        : this(maNV, hoTen, soDienThoai, chucVu, maCN)
    {
        TenCN = tenCN;
    }

    public override string ToString()
    {
        return $"{MaNV,-6} | {HoTen,-20} | " +
               $"{SoDienThoai,-12} | {ChucVu,-12} | " +
               $"{MaCN,-6} | {TenCN ?? "N/A",-20}";
    }

    public override bool Equals(object? obj)
    {
        return obj is NhanVien other &&
               MaNV == other.MaNV;
    }

    public override int GetHashCode()
    {
        return MaNV?.GetHashCode() ?? 0;
    }
}
