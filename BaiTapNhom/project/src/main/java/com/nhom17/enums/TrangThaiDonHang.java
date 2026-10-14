package com.nhom17.enums;

public enum TrangThaiDonHang {
    CHO_XAC_NHAN,
    DA_XAC_NHAN,
    DANG_CHUAN_BI,
    SAN_SANG,
    DA_HOAN_THANH,
    DA_HUY;

    /** Chỉ cho chuyển tiến; DA_HUY chỉ trước SAN_SANG; DA_HOAN_THANH và DA_HUY là trạng thái cuối. */
    public boolean coTheChuyenSang(TrangThaiDonHang tiep) {
        return switch (this) {
            case CHO_XAC_NHAN  -> tiep == DA_XAC_NHAN || tiep == DA_HUY;
            case DA_XAC_NHAN   -> tiep == DANG_CHUAN_BI || tiep == DA_HUY;
            case DANG_CHUAN_BI -> tiep == SAN_SANG || tiep == DA_HUY;
            case SAN_SANG      -> tiep == DA_HOAN_THANH;
            case DA_HOAN_THANH, DA_HUY -> false;
        };
    }
}
