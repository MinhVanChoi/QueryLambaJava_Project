import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react';

export interface CustomerUser {
  maKH: string;
  hoTen: string;
  email?: string;
  soDienThoai: string;
  token: string;
  role: 'CUSTOMER';
}

export interface StaffUser {
  maTK: string;
  maNV: string;
  tenDangNhap: string;
  chucVu: string;
  maCN: string;
  hoTen: string;
  token: string;
  role: 'ADMIN' | 'QUAN_LY' | 'THU_NGAN' | 'NHAN_VIEN';
}

export type AuthUser = CustomerUser | StaffUser;

interface AuthContextValue {
  user: AuthUser | null;
  login: (user: AuthUser) => void;
  logout: () => void;
  isCustomer: () => boolean;
  isAdmin: () => boolean;
  isManager: () => boolean;
}

const AuthContext = createContext<AuthContextValue | null>(null);

const STORAGE_KEY = 'ff_auth_user';

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthUser | null>(() => {
    try {
      const stored = localStorage.getItem(STORAGE_KEY);
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  const login = useCallback((u: AuthUser) => {
    setUser(u);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(u));
  }, []);

  const logout = useCallback(() => {
    setUser(null);
    localStorage.removeItem(STORAGE_KEY);
  }, []);

  const isCustomer = useCallback(() => user?.role === 'CUSTOMER', [user]);
  const isAdmin = useCallback(() => user?.role === 'ADMIN', [user]);
  const isManager = useCallback(() => user?.role === 'ADMIN' || user?.role === 'QUAN_LY', [user]);

  return (
    <AuthContext.Provider value={{ user, login, logout, isCustomer, isAdmin, isManager }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextValue => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};
