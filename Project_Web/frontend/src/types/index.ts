export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type ChucVu = 'ADMIN' | 'QUAN_LY' | 'THU_NGAN' | 'NHAN_VIEN';

export type PhuongThucThanhToan = 'TIEN_MAT' | 'CHUYEN_KHOAN' | 'VI_DIEN_TU' | 'THE';

export type TrangThaiDonHang =
  | 'CHO_XAC_NHAN'
  | 'DA_XAC_NHAN'
  | 'DANG_CHUAN_BI'
  | 'SAN_SANG'
  | 'DA_HOAN_THANH'
  | 'DA_HUY';

export type TrangThaiKhuyenMai = 'DANG_AP_DUNG' | 'TAM_DUNG' | 'HET_HAN';

export type TrangThaiMon = 'DANG_BAN' | 'NGUNG_BAN';

export type TrangThaiTaiKhoan = 'ACTIVE' | 'LOCKED';

export type TrangThaiThanhToan =
  | 'CHO_THANH_TOAN'
  | 'DA_THANH_TOAN'
  | 'THAT_BAI'
  | 'DA_HOAN_TIEN';

export interface Branch {
  maCN: string;
  tenCN: string;
  diaChi: string;
  soDienThoai: string;
}

export interface Category {
  maDM: string;
  tenDM: string;
  moTa?: string;
}

export interface FoodItem {
  maMon: string;
  tenMon: string;
  giaBan: number;
  moTa?: string;
  trangThai: TrangThaiMon;
  maDM: string;
  hinhAnh?: string;
}

export interface Employee {
  maNV: string;
  hoTen: string;
  soDienThoai: string;
  chucVu: ChucVu;
  maCN: string;
}

export interface Account {
  maTK: string;
  maNV: string;
  tenDangNhap: string;
  matKhauHash: string;
  trangThai: TrangThaiTaiKhoan;
  ngayTao: string;
  lanDangNhapCuoi?: string;
}

export interface Customer {
  maKH: string;
  hoTen: string;
  soDienThoai: string;
  email?: string;
  diaChi?: string;
  ngayDangKy: string;
}

export interface Inventory {
  maTon: string;
  maMon: string;
  maCN: string;
  soLuongTon: number;
  ngayCapNhat: string;
}

export interface Promotion {
  maKM: string;
  tenKM: string;
  phanTramGiam: number;
  donToiThieu: number;
  giamToiDa?: number;
  ngayBatDau: string;
  ngayKetThuc: string;
  trangThai: TrangThaiKhuyenMai;
}

export interface Order {
  maDH: number;
  ngayDat: string;
  trangThai: TrangThaiDonHang;
  tienHang: number;
  tienGiam: number;
  tongTien: number;
  maKH?: string;
  maKM?: string;
  maNV: string;
  maCN: string;
}

export interface OrderItem {
  maCTDH: number;
  maDH: number;
  maMon: string;
  tenMon?: string;
  soLuong: number;
  donGia: number;
  thanhTien: number;
}

export interface Payment {
  maTT: string;
  maDH: number;
  soTien: number;
  phuongThuc: PhuongThucThanhToan;
  trangThai: TrangThaiThanhToan;
  ngayThanhToan?: string;
}

export interface OrderDetailResponse {
  order: Order;
  tenCN: string;
  tenNV: string;
  tenKH: string;
  tenKM?: string;
  items: OrderItem[];
  payments: Payment[];
}

export interface DashboardStats {
  totalBranches: number;
  totalEmployees: number;
  totalActiveAccounts: number;
  totalCustomers: number;
  totalCategories: number;
  totalFoodItems: number;
  totalOrders: number;
  totalCompletedOrders: number;
  totalCompletedRevenue: number;
  averageOrderValue: number;
  lowStockCount: number;
  ordersByStatus: Record<string, number>;
  revenueByPaymentMethod: Record<string, number>;
  revenueByBranch: Record<string, number>;
}

export interface QueryMeta {
  id: number;
  title: string;
  description: string;
  category: string;
  sql: string;
}

export interface QueryResult {
  queryId: number;
  title: string;
  description: string;
  sql: string;
  executionMode: string;
  headers: string[];
  rows: (string | number | boolean | null)[][];
  totalRows: number;
  executionTimeMs: number;
}

export interface QueryComparison {
  queryId: number;
  title: string;
  sqlResult: QueryResult;
  lambdaResult: QueryResult;
  matched: boolean;
  matchSummary: string;
}
