
namespace FastFoodMIS_CSharp.Models;

public class DanhMuc
{
    public string MaDM { get; set; } = string.Empty;
    public string TenDM { get; set; } = string.Empty;
    public string? MoTa { get; set; }

    public DanhMuc()
    {
    }

    public DanhMuc(string maDM, string tenDM)
    {
        MaDM = maDM;
        TenDM = tenDM;
    }

    public DanhMuc(string maDM, string tenDM, string? moTa)
    {
        MaDM = maDM;
        TenDM = tenDM;
        MoTa = moTa;
    }

    public override string ToString()
    {
        return $"{MaDM,-6} | {TenDM,-18} | " +
               $"{MoTa ?? "N/A",-30}";
    }

    public override bool Equals(object? obj)
    {
        return obj is DanhMuc dm && MaDM == dm.MaDM;
    }

    public override int GetHashCode()
    {
        return StringComparer.Ordinal.GetHashCode(MaDM);
    }
}
