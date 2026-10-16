
using FastFoodMIS_CSharp.Enums;

namespace FastFoodMIS_CSharp.Models;

public class KhuyenMai
{
    public string MaKM { get; set; } = string.Empty;
    public string TenKM { get; set; } = string.Empty;
    public int PhanTramGiam { get; set; }
    public decimal DonToiThieu { get; set; }
    public decimal? GiamToiDa { get; set; }
    public DateOnly? NgayBatDau { get; set; }
    public DateOnly? NgayKetThuc { get; set; }
    public TrangThaiKhuyenMai TrangThai { get; set; }

    public KhuyenMai() { }

    public KhuyenMai(
        string maKM,
        string tenKM,
        int phanTramGiam,
        decimal donToiThieu,
        decimal? giamToiDa,
        DateOnly? ngayBatDau,
        DateOnly? ngayKetThuc,
        TrangThaiKhuyenMai trangThai)
    {
        MaKM = maKM;
        TenKM = tenKM;
        PhanTramGiam = phanTramGiam;
        DonToiThieu = donToiThieu;
        GiamToiDa = giamToiDa;
        NgayBatDau = ngayBatDau;
        NgayKetThuc = ngayKetThuc;
        TrangThai = trangThai;
    }

    public bool ConHieuLuc(DateOnly ngay)
    {
        return NgayBatDau.HasValue &&
               NgayKetThuc.HasValue &&
               ngay >= NgayBatDau.Value &&
               ngay <= NgayKetThuc.Value;
    }

    public decimal TinhTienGiam(decimal tienHang)
    {
        if (tienHang < DonToiThieu)
            return 0m;

        decimal giam = tienHang * PhanTramGiam / 100m;

        return GiamToiDa.HasValue
            ? Math.Min(giam, GiamToiDa.Value)
            : giam;
    }

    public override string ToString()
    {
        string toiDa = GiamToiDa.HasValue
            ? $"{GiamToiDa.Value:N0} VND"
            : "Không giới hạn";

        return $"{MaKM,-6} | {TenKM,-25} | " +
               $"{PhanTramGiam,3}% | " +
               $"{DonToiThieu,12:N0} VND | " +
               $"{toiDa,-18} | " +
               $"{NgayBatDau?.ToString("yyyy-MM-dd") ?? "N/A",-10} | " +
               $"{NgayKetThuc?.ToString("yyyy-MM-dd") ?? "N/A",-10} | " +
               $"{TrangThai,-12}";
    }

    public override bool Equals(object? obj)
    {
        return obj is KhuyenMai other &&
               MaKM == other.MaKM;
    }

    public override int GetHashCode()
    {
        return MaKM?.GetHashCode() ?? 0;
    }
}
