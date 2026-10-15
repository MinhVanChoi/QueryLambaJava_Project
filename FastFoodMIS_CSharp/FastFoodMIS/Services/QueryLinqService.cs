using FastFoodMIS_CSharp.DAO;
using FastFoodMIS_CSharp.Enums;
using FastFoodMIS_CSharp.Models;

namespace FastFoodMIS_CSharp.Services;

/// <summary>
/// 30 câu LINQ tương ứng QueryLambdaService.java. LINQ xử lý trên các Model đã đọc từ DAO.
/// Dữ liệu được cache trong một lượt chạy để tránh truy vấn DB 30x11 lần.
/// Gọi Refresh() trước lượt chạy mới nếu dữ liệu MySQL thay đổi.
/// </summary>
public class QueryLinqService
{
    private Snapshot? _snapshot;
    private Snapshot Data => _snapshot ??= Snapshot.Load();
    public void Refresh() => _snapshot = null;

    private sealed class Snapshot
    {
        public List<ChiNhanh> ChiNhanh { get; init; } = new();
        public List<NhanVien> NhanVien { get; init; } = new();
        public List<TaiKhoan> TaiKhoan { get; init; } = new();
        public List<KhachHang> KhachHang { get; init; } = new();
        public List<DanhMuc> DanhMuc { get; init; } = new();
        public List<MonAn> MonAn { get; init; } = new();
        public List<TonKho> TonKho { get; init; } = new();
        public List<KhuyenMai> KhuyenMai { get; init; } = new();
        public List<DonHang> DonHang { get; init; } = new();
        public List<ChiTietDonHang> ChiTietDonHang { get; init; } = new();
        public List<ThanhToan> ThanhToan { get; init; } = new();

        public static Snapshot Load()
        {
            var data = new Snapshot
            {
                ChiNhanh = new ChiNhanhDAO().FindAll(),
                NhanVien = new NhanVienDAO().FindAll(),
                TaiKhoan = new TaiKhoanDAO().FindAll(),
                KhachHang = new KhachHangDAO().FindAll(),
                DanhMuc = new DanhMucDAO().FindAll(),
                MonAn = new MonAnDAO().FindAll(),
                TonKho = new TonKhoDAO().FindAll(),
                KhuyenMai = new KhuyenMaiDAO().FindAll(),
                DonHang = new DonHangDAO().FindAll(),
                ChiTietDonHang = new ChiTietDonHangDAO().FindAll(),
                ThanhToan = new ThanhToanDAO().FindAll()
            };

            // Ghép tên liên quan từ các Model có sẵn, chỉ để hiển thị.
            // Không truy vấn lại MySQL và không sửa dữ liệu trong database.
            var cn = data.ChiNhanh.ToDictionary(x => x.MaCN,
                x => x.TenCN, StringComparer.OrdinalIgnoreCase);
            var dm = data.DanhMuc.ToDictionary(x => x.MaDM,
                x => x.TenDM, StringComparer.OrdinalIgnoreCase);
            var mon = data.MonAn.ToDictionary(x => x.MaMon,
                x => x.TenMon, StringComparer.OrdinalIgnoreCase);
            var nv = data.NhanVien.ToDictionary(x => x.MaNV,
                x => x.HoTen, StringComparer.OrdinalIgnoreCase);
            var kh = data.KhachHang.ToDictionary(x => x.MaKH,
                x => x.HoTen, StringComparer.OrdinalIgnoreCase);
            var km = data.KhuyenMai.ToDictionary(x => x.MaKM,
                x => x.TenKM, StringComparer.OrdinalIgnoreCase);

            foreach (var item in data.MonAn)
                if (dm.TryGetValue(item.MaDM, out var tenDM)) item.TenDM = tenDM;

            foreach (var item in data.NhanVien)
                if (cn.TryGetValue(item.MaCN, out var tenCN)) item.TenCN = tenCN;

            foreach (var item in data.TonKho)
            {
                if (cn.TryGetValue(item.MaCN, out var tenCN)) item.TenCN = tenCN;
                if (mon.TryGetValue(item.MaMon, out var tenMon)) item.TenMon = tenMon;
            }

            foreach (var item in data.ChiTietDonHang)
                if (mon.TryGetValue(item.MaMon, out var tenMon)) item.TenMon = tenMon;

            foreach (var item in data.TaiKhoan)
                if (nv.TryGetValue(item.MaNV, out var tenNV)) item.HoTenNV = tenNV;

            foreach (var item in data.DonHang)
            {
                if (item.MaKH is not null && kh.TryGetValue(item.MaKH, out var tenKH))
                    item.TenKH = tenKH;
                if (item.MaNV is not null && nv.TryGetValue(item.MaNV, out var tenNV))
                    item.TenNV = tenNV;
                if (item.MaCN is not null && cn.TryGetValue(item.MaCN, out var tenCN))
                    item.TenCN = tenCN;
                if (item.MaKM is not null && km.TryGetValue(item.MaKM, out var tenKM))
                    item.TenKM = tenKM;
            }

            return data;
        }
    }

    // 01: Nhân viên chưa có tài khoản (NOT IN).
    public List<NhanVien> Q01_NhanVienChuaCoTaiKhoan()
    {
        var accountEmployees = Data.TaiKhoan.Select(t => t.MaNV).ToHashSet();
        return Data.NhanVien.Where(n => !accountEmployees.Contains(n.MaNV)).ToList();
    }

    // 02: Burger có giá 40.000-60.000, sắp xếp giảm dần.
    public List<MonAn> Q02_MonBurgerGia40kDen60k()
    {
        // Ghép danh mục theo tên 'Burger' giống câu SQL thay vì cố định mã DM01.
        var maDanhMucBurger = Data.DanhMuc
            .Where(d => d.TenDM == "Burger")
            .Select(d => d.MaDM)
            .ToHashSet(StringComparer.OrdinalIgnoreCase);
        return Data.MonAn
            .Where(m => maDanhMucBurger.Contains(m.MaDM)
                     && m.GiaBan >= 40000m && m.GiaBan <= 60000m)
            .OrderByDescending(m => m.GiaBan).ToList();
    }

    // 03: Tồn kho thấp.
    public List<TonKho> Q03_TonKhoNhoHon10() => Data.TonKho
        .Where(t => t.SoLuongTon < 10)
        .OrderBy(t => t.MaCN).ThenBy(t => t.MaMon).ToList();

    // 04: Khách hàng ở TP.HCM.
    public List<KhachHang> Q04_KhachHangTPHCM() => Data.KhachHang
        .Where(k => (k.DiaChi ?? "").Contains("TP.HCM", StringComparison.OrdinalIgnoreCase)
                 || (k.DiaChi ?? "").Contains("TPHCM", StringComparison.OrdinalIgnoreCase))
        .ToList();

    // 05: Phương thức từng thanh toán thành công.
    public List<PhuongThucThanhToan> Q05_PhuongThucThanhCong() => Data.ThanhToan
        .Where(t => t.TrangThai == TrangThaiThanhToan.DA_THANH_TOAN)
        .Select(t => t.PhuongThuc).Distinct().ToList();

    // 06: Đơn hoàn thành trong tháng 2/2026.
    public List<DonHang> Q06_DonHangThang2() => Data.DonHang
        .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH
                 && d.NgayDat.Year == 2026 && d.NgayDat.Month == 2).ToList();

    // 07: Đơn hàng cùng tên khách và nhân viên; so khớp trong bộ nhớ.
    public List<string> Q07_DonHangKemThongTin()
    {
        var employees = Data.NhanVien.ToDictionary(n => n.MaNV, n => n.HoTen);
        var customers = Data.KhachHang.ToDictionary(k => k.MaKH, k => k.HoTen);
        return Data.DonHang.Select(d =>
        {
            var nv = d.MaNV is not null && employees.TryGetValue(d.MaNV, out var tenNV)
                ? tenNV : "";
            var kh = d.MaKH is not null && customers.TryGetValue(d.MaKH, out var tenKH)
                ? tenKH : "(Khách vãng lai)";
            return $"{d.MaDH} | {kh} | {nv} | {d.TongTien:N0}";
        }).ToList();
    }

    // 08: Chi tiết đơn 1001.
    public List<ChiTietDonHang> Q08_ChiTietDon1001() => Data.ChiTietDonHang
        .Where(ct => ct.MaDH == 1001L).ToList();

    // 09: So tổng tiền hàng từ chi tiết với bảng đơn hàng.
    public Dictionary<long, bool> Q09_DoiChieuTienHang()
    {
        var sums = Data.ChiTietDonHang.Where(ct => ct.MaDH.HasValue)
            .GroupBy(ct => ct.MaDH!.Value)
            .ToDictionary(g => g.Key, g => g.Sum(ct => ct.ThanhTien));
        return Data.DonHang.Where(d => d.MaDH.HasValue)
            .ToDictionary(d => d.MaDH!.Value,
                d => d.TienHang == sums.GetValueOrDefault(d.MaDH!.Value, 0m));
    }

    // 10: Tổng doanh thu đơn hoàn thành.
    public decimal Q10_TongDoanhThu() => Data.DonHang
        .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH)
        .Sum(d => d.TongTien);

    // 11: Doanh thu theo tháng năm 2026.
    public Dictionary<int, decimal> Q11_DoanhThuTheoThang() => Data.DonHang
        .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH && d.NgayDat.Year == 2026)
        .GroupBy(d => d.NgayDat.Month).OrderBy(g => g.Key)
        .ToDictionary(g => g.Key, g => g.Sum(d => d.TongTien));

    // 12: Top 3 món bán chạy từ các đơn hoàn thành.
    public List<KeyValuePair<string, int>> Q12_Top3MonBanChay()
    {
        var done = Data.DonHang
            .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH && d.MaDH.HasValue)
            .Select(d => d.MaDH!.Value).ToHashSet();
        return Data.ChiTietDonHang.Where(ct => ct.MaDH.HasValue && done.Contains(ct.MaDH.Value))
            .GroupBy(ct => ct.MaMon)
            .Select(g => new KeyValuePair<string, int>(g.Key, g.Sum(ct => ct.SoLuong)))
            .OrderByDescending(x => x.Value).Take(3).ToList();
    }

    // 13: Doanh thu mỗi khách hàng (key theo tên, cùng tên thì gộp).
    public Dictionary<string, decimal> Q13_DoanhThuTungKhachHang() => Data.KhachHang
        .GroupBy(k => k.HoTen)
        .ToDictionary(g => g.Key, g => Data.DonHang
            .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH
                 && g.Any(k => k.MaKH == d.MaKH))
            .Sum(d => d.TongTien));

    // 14: Khách chưa từng đặt hàng.
    public List<KhachHang> Q14_KhachHangChuaTungDatHang()
    {
        var purchased = Data.DonHang.Where(d => d.MaKH is not null)
            .Select(d => d.MaKH!).ToHashSet();
        return Data.KhachHang.Where(k => !purchased.Contains(k.MaKH)).ToList();
    }

    // 15: Món chưa được bán trong đơn hoàn thành.
    public List<MonAn> Q15_MonChuaBanThanhCong()
    {
        var done = Data.DonHang.Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH)
            .Select(d => d.MaDH).ToHashSet();
        var sold = Data.ChiTietDonHang.Where(ct => done.Contains(ct.MaDH))
            .Select(ct => ct.MaMon).ToHashSet();
        return Data.MonAn.Where(m => !sold.Contains(m.MaMon)).ToList();
    }

    // 16: Thống kê cả số đơn và doanh thu theo từng mã nhân viên.
    // Khác cách nhóm theo tên: hai nhân viên trùng tên vẫn tách riêng.
    public List<string> Q16_DoanhThuTheoNhanVien() => Data.NhanVien
        .Select(nv =>
        {
            var orders = Data.DonHang.Where(d => d.MaNV == nv.MaNV
                && d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH).ToList();
            return new
            {
                nv.MaNV,
                nv.HoTen,
                SoDon = orders.Count,
                DoanhThu = orders.Sum(d => d.TongTien)
            };
        })
        .OrderByDescending(x => x.DoanhThu)
        .ThenBy(x => x.MaNV)
        .Select(x => $"{x.MaNV} | {x.HoTen} | Số đơn: {x.SoDon} | Doanh thu: {x.DoanhThu:N0} VND")
        .ToList();

    // 17: Khách có ít nhất 2 đơn hoàn thành.
    public List<string> Q17_KhachHangCoItNhat2Don() => Data.DonHang
        .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH && d.MaKH is not null)
        .GroupBy(d => d.MaKH!).Where(g => g.Count() >= 2)
        .Select(g => g.Key).ToList();

    // 18: Giá bán lúc đặt khác giá hiện tại.
    public List<ChiTietDonHang> Q18_ChiTietGiaKhacHienTai()
    {
        var prices = Data.MonAn.ToDictionary(m => m.MaMon, m => m.GiaBan);
        return Data.ChiTietDonHang
            .Where(ct => ct.DonGia.HasValue && prices.TryGetValue(ct.MaMon, out var price)
                     && ct.DonGia.Value != price)
            .ToList();
    }

    // 19: Tài khoản bị khóa.
    public List<TaiKhoan> Q19_TaiKhoanBiKhoa() => Data.TaiKhoan
        .Where(t => t.TrangThai == TrangThaiTaiKhoan.LOCKED).ToList();

    // 20: Các đơn có tổng tiền cao hơn mức trung bình của tất cả đơn.
    public List<DonHang> Q20_DonHangCaoHonTrungBinh()
    {
        decimal avg = Data.DonHang.Count == 0 ? 0m : Data.DonHang.Average(d => d.TongTien);
        return Data.DonHang.Where(d => d.TongTien > avg)
            .OrderByDescending(d => d.TongTien).ToList();
    }

    // 21: Tài khoản chưa đăng nhập.
    public List<TaiKhoan> Q21_TaiKhoanChuaTungDangNhap() => Data.TaiKhoan
        .Where(t => t.LanDangNhapCuoi is null).ToList();

    // 22: Đơn dùng khuyến mãi.
    public List<DonHang> Q22_DonHangDungKhuyenMai() => Data.DonHang
        .Where(d => !string.IsNullOrWhiteSpace(d.MaKM)).ToList();

    // 23: Tổng giá trị giảm theo từng chương trình.
    public Dictionary<string, decimal> Q23_TongTienGiamTungKhuyenMai() => Data.KhuyenMai
        .GroupBy(k => k.TenKM)
        .ToDictionary(g => g.Key, g => Data.DonHang
            .Where(d => g.Any(k => k.MaKM == d.MaKM))
            .Sum(d => d.TienGiam));

    // 24: Khuyến mãi chưa từng được dùng.
    public List<KhuyenMai> Q24_KhuyenMaiChuaTungDung()
    {
        var used = Data.DonHang.Where(d => d.MaKM is not null)
            .Select(d => d.MaKM!).ToHashSet();
        return Data.KhuyenMai.Where(k => !used.Contains(k.MaKM)).ToList();
    }

    // 25: So sánh doanh thu đơn hoàn thành có và không có KM.
    public Dictionary<bool, decimal> Q25_SoSanhKhuyenMai()
    {
        var done = Data.DonHang.Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH);
        return new Dictionary<bool, decimal>
        {
            [true] = done.Where(d => !string.IsNullOrWhiteSpace(d.MaKM)).Sum(d => d.TongTien),
            [false] = done.Where(d => string.IsNullOrWhiteSpace(d.MaKM)).Sum(d => d.TongTien)
        };
    }

    // 26: Những đơn được giảm tới đúng mức trần khuyến mãi.
    public List<DonHang> Q26_DonBiChanGiamToiDa()
    {
        var promos = Data.KhuyenMai.ToDictionary(k => k.MaKM);
        return Data.DonHang.Where(d => d.MaKM is not null
            && promos.TryGetValue(d.MaKM, out var promo)
            && promo.GiamToiDa.HasValue
            && d.TienGiam == promo.GiamToiDa.Value
            && d.TienHang * promo.PhanTramGiam / 100m > promo.GiamToiDa.Value).ToList();
    }

    // 27: Tiền thu theo phương thức thanh toán thành công.
    public Dictionary<PhuongThucThanhToan, decimal> Q27_TienThuTheoPhuongThuc() => Data.ThanhToan
        .Where(t => t.TrangThai == TrangThaiThanhToan.DA_THANH_TOAN)
        .GroupBy(t => t.PhuongThuc)
        .ToDictionary(g => g.Key, g => g.Sum(t => t.SoTien));

    // 28: Doanh thu theo mã chi nhánh từ đơn hoàn thành.
    public Dictionary<string, decimal> Q28_DoanhThuTheoChiNhanh() => Data.DonHang
        .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH && d.MaCN is not null)
        .GroupBy(d => d.MaCN!)
        .ToDictionary(g => g.Key, g => g.Sum(d => d.TongTien));

    // 29: Khách có chi tiêu trên trung bình chi tiêu của những khách đã mua.
    public List<string> Q29_KhachHangChiTieuTrenTrungBinh()
    {
        var spends = Data.DonHang
            .Where(d => d.TrangThai == TrangThaiDonHang.DA_HOAN_THANH && d.MaKH is not null)
            .GroupBy(d => d.MaKH!)
            .ToDictionary(g => g.Key, g => g.Sum(d => d.TongTien));
        decimal avg = spends.Count == 0 ? 0m : spends.Values.Average();
        return spends.Where(x => x.Value > avg).Select(x => x.Key).ToList();
    }

    // 30: Đơn thanh toán từng thất bại nhưng sau đó thành công (cùng maDH).
    public List<long> Q30_DonThatBaiRoiThanhCong() => Data.ThanhToan
        .Where(t => t.MaDH.HasValue)
        .GroupBy(t => t.MaDH!.Value)
        .Where(g => g.Any(t => t.TrangThai == TrangThaiThanhToan.THAT_BAI)
                 && g.Any(t => t.TrangThai == TrangThaiThanhToan.DA_THANH_TOAN))
        .Select(g => g.Key).ToList();

    public object Run(int number) => number switch
    {
        1 => Q01_NhanVienChuaCoTaiKhoan(),
        2 => Q02_MonBurgerGia40kDen60k(),
        3 => Q03_TonKhoNhoHon10(),
        4 => Q04_KhachHangTPHCM(),
        5 => Q05_PhuongThucThanhCong(),
        6 => Q06_DonHangThang2(),
        7 => Q07_DonHangKemThongTin(),
        8 => Q08_ChiTietDon1001(),
        9 => Q09_DoiChieuTienHang(),
        10 => Q10_TongDoanhThu(),
        11 => Q11_DoanhThuTheoThang(),
        12 => Q12_Top3MonBanChay(),
        13 => Q13_DoanhThuTungKhachHang(),
        14 => Q14_KhachHangChuaTungDatHang(),
        15 => Q15_MonChuaBanThanhCong(),
        16 => Q16_DoanhThuTheoNhanVien(),
        17 => Q17_KhachHangCoItNhat2Don(),
        18 => Q18_ChiTietGiaKhacHienTai(),
        19 => Q19_TaiKhoanBiKhoa(),
        20 => Q20_DonHangCaoHonTrungBinh(),
        21 => Q21_TaiKhoanChuaTungDangNhap(),
        22 => Q22_DonHangDungKhuyenMai(),
        23 => Q23_TongTienGiamTungKhuyenMai(),
        24 => Q24_KhuyenMaiChuaTungDung(),
        25 => Q25_SoSanhKhuyenMai(),
        26 => Q26_DonBiChanGiamToiDa(),
        27 => Q27_TienThuTheoPhuongThuc(),
        28 => Q28_DoanhThuTheoChiNhanh(),
        29 => Q29_KhachHangChiTieuTrenTrungBinh(),
        30 => Q30_DonThatBaiRoiThanhCong(),
        _ => throw new ArgumentOutOfRangeException(nameof(number), "Chọn từ 1 đến 30")
    };
}
