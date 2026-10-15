
namespace FastFoodMIS_CSharp.Enums;

public static class TrangThaiDonHangExtensions
{
    public static bool CoTheChuyenSang(
        this TrangThaiDonHang hienTai,
        TrangThaiDonHang tiepTheo)
    {
        return hienTai switch
        {
            TrangThaiDonHang.CHO_XAC_NHAN =>
                tiepTheo == TrangThaiDonHang.DA_XAC_NHAN ||
                tiepTheo == TrangThaiDonHang.DA_HUY,

            TrangThaiDonHang.DA_XAC_NHAN =>
                tiepTheo == TrangThaiDonHang.DANG_CHUAN_BI ||
                tiepTheo == TrangThaiDonHang.DA_HUY,

            TrangThaiDonHang.DANG_CHUAN_BI =>
                tiepTheo == TrangThaiDonHang.SAN_SANG ||
                tiepTheo == TrangThaiDonHang.DA_HUY,

            TrangThaiDonHang.SAN_SANG =>
                tiepTheo == TrangThaiDonHang.DA_HOAN_THANH,

            _ => false
        };
    }
}
