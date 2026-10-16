
namespace FastFoodMIS_CSharp.Models;

public class ChiNhanh
{
    public string MaCN { get; set; } = string.Empty;
    public string TenCN { get; set; } = string.Empty;
    public string DiaChi { get; set; } = string.Empty;
    public string SoDienThoai { get; set; } = string.Empty;

    public ChiNhanh()
    {
    }

    public ChiNhanh(string maCN, string tenCN,
                    string diaChi, string soDienThoai)
    {
        MaCN = maCN;
        TenCN = tenCN;
        DiaChi = diaChi;
        SoDienThoai = soDienThoai;
    }

    public override string ToString()
    {
        return $"{MaCN,-6} | {TenCN,-22} | " +
               $"{DiaChi,-35} | {SoDienThoai,-12}";
    }

    public override bool Equals(object? obj)
    {
        return obj is ChiNhanh cn && MaCN == cn.MaCN;
    }

    public override int GetHashCode()
    {
        return StringComparer.Ordinal.GetHashCode(MaCN);
    }
}
