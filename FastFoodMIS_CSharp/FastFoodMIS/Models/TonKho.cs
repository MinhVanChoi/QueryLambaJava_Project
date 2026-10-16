
namespace FastFoodMIS_CSharp.Models;

public class TonKho
{
    public string MaTon { get; set; } = string.Empty;
    public string MaMon { get; set; } = string.Empty;
    public string? TenMon { get; set; }
    public string MaCN { get; set; } = string.Empty;
    public string? TenCN { get; set; }
    public int SoLuongTon { get; set; }
    public DateTime? NgayCapNhat { get; set; }

    public TonKho() { }

    public TonKho(
        string maTon,
        string maCN,
        string maMon,
        int soLuongTon,
        DateTime? ngayCapNhat)
    {
        MaTon = maTon;
        MaCN = maCN;
        MaMon = maMon;
        SoLuongTon = soLuongTon;
        NgayCapNhat = ngayCapNhat;
    }

    public TonKho(
        string maTon,
        string maCN,
        string? tenCN,
        string maMon,
        string? tenMon,
        int soLuongTon,
        DateTime? ngayCapNhat)
        : this(maTon, maCN, maMon,
               soLuongTon, ngayCapNhat)
    {
        TenCN = tenCN;
        TenMon = tenMon;
    }

    public override string ToString()
    {
        string ngay = NgayCapNhat?.ToString(
            "yyyy-MM-dd HH:mm:ss") ?? "N/A";

        return $"{MaTon,-6} | " +
               $"{MaCN,-6} | " +
               $"{TenCN ?? "N/A",-18} | " +
               $"{MaMon,-6} | " +
               $"{TenMon ?? "N/A",-18} | " +
               $"{SoLuongTon,-4} | " +
               $"{ngay,-20}";
    }

    public override bool Equals(object? obj)
    {
        return obj is TonKho other &&
               MaTon == other.MaTon;
    }

    public override int GetHashCode()
    {
        return MaTon?.GetHashCode() ?? 0;
    }
}
