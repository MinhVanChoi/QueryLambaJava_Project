
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Models;

public class DonHang
{
    public long? MaDH { get; set; }
    public DateTime NgayDat { get; set; }
    public TrangThaiDonHang TrangThai { get; set; }

    public decimal TienHang { get; set; }
    public decimal TienGiam { get; set; }
    public decimal TongTien { get; set; }

    public string? MaKH { get; set; }
    public string? TenKH { get; set; }

    public string? MaKM { get; set; }
    public string? TenKM { get; set; }

    public string? MaNV { get; set; }
    public string? TenNV { get; set; }

    public string? MaCN { get; set; }
    public string? TenCN { get; set; }

    public DonHang()
    {
    }

    public DonHang(
        long? maDH,
        DateTime ngayDat,
        TrangThaiDonHang trangThai,
        decimal tienHang,
        decimal tienGiam,
        decimal tongTien,
        string? maKH,
        string? maKM,
        string? maNV,
        string? maCN)
    {
        MaDH = maDH;
        NgayDat = ngayDat;
        TrangThai = trangThai;
        TienHang = tienHang;
        TienGiam = tienGiam;
        TongTien = tongTien;
        MaKH = maKH;
        MaKM = maKM;
        MaNV = maNV;
        MaCN = maCN;
    }

    public DonHang(
        long? maDH,
        DateTime ngayDat,
        TrangThaiDonHang trangThai,
        decimal tienHang,
        decimal tienGiam,
        decimal tongTien,
        string? maKH,
        string? tenKH,
        string? maKM,
        string? tenKM,
        string? maNV,
        string? tenNV,
        string? maCN,
        string? tenCN)
        : this(
            maDH, ngayDat, trangThai,
            tienHang, tienGiam, tongTien,
            maKH, maKM, maNV, maCN)
    {
        TenKH = tenKH;
        TenKM = tenKM;
        TenNV = tenNV;
        TenCN = tenCN;
    }

    public override string ToString()
    {
        string khachHang = TenKH ?? MaKH ?? "(Vãng lai)";
        string nhanVien = TenNV ?? MaNV ?? "N/A";

        return $"{MaDH,-6} | " +
               $"{NgayDat:yyyy-MM-dd HH:mm} | " +
               $"{TrangThai,-16} | " +
               $"{TienHang,12:N0} | " +
               $"{TienGiam,10:N0} | " +
               $"{TongTien,12:N0} | " +
               $"{khachHang,-20} | " +
               $"{nhanVien,-20} | " +
               $"{MaCN ?? "N/A",-6}";
    }

    public override bool Equals(object? obj)
    {
        return obj is DonHang other &&
               MaDH == other.MaDH;
    }

    public override int GetHashCode()
    {
        return MaDH.GetHashCode();
    }
}
