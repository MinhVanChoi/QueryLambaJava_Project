
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Models;

public class MonAn
{
    public string MaMon { get; set; } = string.Empty;
    public string TenMon { get; set; } = string.Empty;
    public decimal GiaBan { get; set; }
    public string? MoTa { get; set; }
    public TrangThaiMon TrangThai { get; set; }
    public string MaDM { get; set; } = string.Empty;
    public string? TenDM { get; set; }

    public MonAn()
    {
    }

    public MonAn(
        string maMon,
        string tenMon,
        decimal giaBan,
        TrangThaiMon trangThai,
        string maDM)
    {
        MaMon = maMon;
        TenMon = tenMon;
        GiaBan = giaBan;
        TrangThai = trangThai;
        MaDM = maDM;
    }

    public MonAn(
        string maMon,
        string tenMon,
        decimal giaBan,
        string? moTa,
        TrangThaiMon trangThai,
        string maDM)
        : this(maMon, tenMon, giaBan, trangThai, maDM)
    {
        MoTa = moTa;
    }

    public MonAn(
        string maMon,
        string tenMon,
        decimal giaBan,
        string? moTa,
        TrangThaiMon trangThai,
        string maDM,
        string? tenDM)
        : this(maMon, tenMon, giaBan, moTa, trangThai, maDM)
    {
        TenDM = tenDM;
    }

    public override string ToString()
    {
        return $"{MaMon,-6} | " +
               $"{TenMon,-20} | " +
               $"{GiaBan,12:N0} VND | " +
               $"{TrangThai,-12} | " +
               $"{MaDM,-6} | " +
               $"{TenDM ?? "N/A",-15}";
    }

    public override bool Equals(object? obj)
    {
        return obj is MonAn other &&
               string.Equals(
                   MaMon,
                   other.MaMon,
                   StringComparison.Ordinal);
    }

    public override int GetHashCode()
    {
        return StringComparer.Ordinal.GetHashCode(MaMon);
    }
}
