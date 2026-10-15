
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Models;

public class ThanhToan
{
    public string MaTT { get; set; } = string.Empty;
    public long? MaDH { get; set; }
    public decimal SoTien { get; set; }
    public PhuongThucThanhToan PhuongThuc { get; set; }
    public TrangThaiThanhToan TrangThai { get; set; }
    public DateTime? NgayThanhToan { get; set; }

    public ThanhToan() { }

    public ThanhToan(
        string maTT,
        long? maDH,
        decimal soTien,
        PhuongThucThanhToan phuongThuc,
        TrangThaiThanhToan trangThai,
        DateTime? ngayThanhToan)
    {
        MaTT = maTT;
        MaDH = maDH;
        SoTien = soTien;
        PhuongThuc = phuongThuc;
        TrangThai = trangThai;
        NgayThanhToan = ngayThanhToan;
    }

    public override string ToString()
    {
        string ngay = NgayThanhToan?.ToString(
            "yyyy-MM-dd HH:mm:ss") ?? "(Chờ TT)";

        return $"{MaTT,-6} | {MaDH,-6} | " +
               $"{SoTien,12:N0} VND | " +
               $"{PhuongThuc,-14} | " +
               $"{TrangThai,-14} | " +
               $"{ngay,-20}";
    }

    public override bool Equals(object? obj)
    {
        return obj is ThanhToan other &&
               MaTT == other.MaTT;
    }

    public override int GetHashCode()
    {
        return MaTT?.GetHashCode() ?? 0;
    }
}
