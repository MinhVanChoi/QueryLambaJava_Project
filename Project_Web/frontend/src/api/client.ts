import axios from 'axios';
import {
  ApiResponse,
  Branch,
  Category,
  FoodItem,
  Employee,
  Account,
  Customer,
  Inventory,
  Promotion,
  Order,
  OrderDetailResponse,
  DashboardStats,
  QueryMeta,
  QueryResult,
  QueryComparison,
  TrangThaiDonHang,
} from '../types';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Response unwrap helper
const get = async <T>(url: string, params?: Record<string, any>): Promise<T> => {
  const res = await api.get<ApiResponse<T>>(url, { params });
  return res.data.data;
};

const post = async <T>(url: string, body?: any, params?: Record<string, any>): Promise<T> => {
  const res = await api.post<ApiResponse<T>>(url, body, { params });
  return res.data.data;
};

const put = async <T>(url: string, body?: any): Promise<T> => {
  const res = await api.put<ApiResponse<T>>(url, body);
  return res.data.data;
};

const patch = async <T>(url: string, body?: any): Promise<T> => {
  const res = await api.patch<ApiResponse<T>>(url, body);
  return res.data.data;
};

const del = async <T>(url: string): Promise<T> => {
  const res = await api.delete<ApiResponse<T>>(url);
  return res.data.data;
};

export const ApiClient = {
  // Dashboard & Database
  getDashboardStats: () => get<DashboardStats>('/dashboard/stats'),
  getDatabaseStatus: () => get<Record<string, number>>('/database/status'),
  reinitializeDatabase: () => post<Record<string, number>>('/database/reinitialize'),

  // Branches
  getBranches: () => get<Branch[]>('/branches'),
  getBranch: (id: string) => get<Branch>(`/branches/${id}`),
  createBranch: (data: Branch) => post<Branch>('/branches', data),
  updateBranch: (id: string, data: Branch) => put<Branch>(`/branches/${id}`, data),
  deleteBranch: (id: string) => del<void>(`/branches/${id}`),

  // Categories
  getCategories: () => get<Category[]>('/categories'),
  createCategory: (data: Category) => post<Category>('/categories', data),
  updateCategory: (id: string, data: Category) => put<Category>(`/categories/${id}`, data),
  deleteCategory: (id: string) => del<void>(`/categories/${id}`),

  // Food Items
  getFoodItems: (params?: { categoryId?: string; status?: string; sortBy?: string }) =>
    get<FoodItem[]>('/food-items', params),
  getFoodItem: (id: string) => get<FoodItem>(`/food-items/${id}`),
  createFoodItem: (data: Partial<FoodItem>) => post<FoodItem>('/food-items', data),
  updateFoodItem: (id: string, data: Partial<FoodItem>) => put<FoodItem>(`/food-items/${id}`, data),
  patchFoodItemField: (id: string, field: string, value: string) =>
    patch<boolean>(`/food-items/${id}`, { field, value }),
  deleteFoodItem: (id: string) => del<void>(`/food-items/${id}`),

  // Employees
  getEmployees: (params?: { keyword?: string; branchId?: string }) =>
    get<Employee[]>('/employees', params),
  getEmployee: (id: string) => get<Employee>(`/employees/${id}`),
  createEmployee: (data: Employee) => post<Employee>('/employees', data),
  updateEmployee: (id: string, data: Employee) => put<Employee>(`/employees/${id}`, data),
  deleteEmployee: (id: string) => del<void>(`/employees/${id}`),

  // Accounts & Auth
  getAccounts: () => get<Account[]>('/accounts'),
  getAccount: (id: string) => get<Account>(`/accounts/${id}`),
  getAccountByEmployee: (maNV: string) => get<Account>(`/accounts/by-employee/${maNV}`),
  createAccount: (data: Partial<Account>) => post<Account>('/accounts', data),
  lockAccount: (id: string) => put<Account>(`/accounts/${id}/lock`),
  unlockAccount: (id: string) => put<Account>(`/accounts/${id}/unlock`),
  deleteAccount: (id: string) => del<void>(`/accounts/${id}`),
  loginStaff: (username: string, password: string) =>
    post<any>('/auth/login', { username, password }),


  // Customers
  getCustomers: () => get<Customer[]>('/customers'),
  getCustomer: (id: string) => get<Customer>(`/customers/${id}`),
  createCustomer: (data: Customer) => post<Customer>('/customers', data),
  updateCustomer: (id: string, data: Customer) => put<Customer>(`/customers/${id}`, data),
  deleteCustomer: (id: string) => del<void>(`/customers/${id}`),

  // Inventory
  getInventory: (params?: { branchId?: string; lowStock?: boolean; threshold?: number }) =>
    get<Inventory[]>('/inventory', params),
  updateStock: (id: string, soLuong: number) =>
    put<Inventory>(`/inventory/${id}/stock`, { soLuong }),

  // Promotions
  getPromotions: (activeOnly?: boolean) =>
    get<Promotion[]>('/promotions', { activeOnly }),
  calculateDiscount: (id: string, subtotal: number) =>
    post<number>(`/promotions/${id}/calc-discount`, { subtotal }),
  createPromotion: (data: Promotion) => post<Promotion>('/promotions', data),
  updatePromotion: (id: string, data: Promotion) => put<Promotion>(`/promotions/${id}`, data),
  deletePromotion: (id: string) => del<void>(`/promotions/${id}`),

  // Orders
  getOrders: () => get<Order[]>('/orders'),
  getOrder: (id: number) => get<Order>(`/orders/${id}`),
  getOrderDetail: (id: number) => get<OrderDetailResponse>(`/orders/${id}/detail`),
  createOrder: (data: {
    maCN: string;
    maNV: string;
    maKH?: string;
    maKM?: string;
    items: { maMon: string; soLuong: number }[];
    phuongThuc?: string;
    payImmediately?: boolean;
  }) => post<OrderDetailResponse>('/orders', data),
  updateOrderStatus: (id: number, status: TrangThaiDonHang) =>
    put<Order>(`/orders/${id}/status`, { status }),
  deleteOrder: (id: number) => del<void>(`/orders/${id}`),

  // Queries
  getQueries: () => get<QueryMeta[]>('/queries'),
  getQueryMeta: (id: number) => get<QueryMeta>(`/queries/${id}`),
  executeQuery: (id: number, mode: 'sql' | 'lambda' = 'sql') =>
    get<QueryResult>(`/queries/${id}/execute`, { mode }),
  compareQuery: (id: number) => get<QueryComparison>(`/queries/${id}/compare`),
};
