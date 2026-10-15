
using System.Globalization;

namespace FastFoodMIS_CSharp.Models;

public class ChiTietDonHang
{
    private int _soLuong;
    private decimal? _donGia;
    private decimal? _thanhTien;

    public long? MaCTDH { get; set; }
    public long? MaDH { get; set; }
    public string MaMon { get; set; } = string.Empty;
    public string? TenMon { get; set; }

    public int SoLuong
    {
        get => _soLuong;
        set
        {
            _soLuong = value;
            if (_donGia.HasValue)
                _thanhTien = _donGia.Value * value;
        }
    }

    public decimal? DonGia
    {
        get => _donGia;
        set
        {
            _donGia = value;
            if (value.HasValue)
                _thanhTien = value.Value * SoLuong;
        }
    }

    public decimal ThanhTien
    {
        get => _thanhTien ?? (DonGia ?? 0m) * SoLuong;
        set => _thanhTien = value;
    }

    public ChiTietDonHang()
    {
    }

    public ChiTietDonHang(
        long? maCTDH, long? maDH, string maMon,
        int soLuong, decimal donGia)
    {
        MaCTDH = maCTDH;
        MaDH = maDH;
        MaMon = maMon;
        SoLuong = soLuong;
        DonGia = donGia;
    }

    public ChiTietDonHang(
        long? maCTDH, long? maDH, string maMon,
        int soLuong, decimal donGia, decimal thanhTien)
        : this(maCTDH, maDH, maMon, soLuong, donGia)
    {
        ThanhTien = thanhTien;
    }

    public ChiTietDonHang(
        long? maCTDH, long? maDH, string maMon,
        string? tenMon, int soLuong,
        decimal donGia, decimal thanhTien)
        : this(maCTDH, maDH, maMon,
               soLuong, donGia, thanhTien)
    {
        TenMon = tenMon;
    }

    private static string FormatMoney(decimal? amount)
    {
        return (amount ?? 0m).ToString(
            "N0", CultureInfo.InvariantCulture) + " VND";
    }

    public override string ToString()
    {
        return $"{MaCTDH,-6} | {MaDH,-6} | " +
               $"{MaMon,-6} | {TenMon ?? "N/A",-18} | " +
               $"{SoLuong,-3} | " +
               $"{FormatMoney(DonGia),-12} | " +
               $"{FormatMoney(ThanhTien),-12}";
    }

    public override bool Equals(object? obj)
    {
        return obj is ChiTietDonHang other &&
               MaCTDH == other.MaCTDH;
    }

    public override int GetHashCode()
    {
        return MaCTDH.GetHashCode();
    }
}
