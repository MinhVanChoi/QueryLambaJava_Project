import React, { Suspense, lazy } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AppLayout } from './layouts/AppLayout';
import { PublicLayout } from './layouts/PublicLayout';
import { CartProvider } from './context/CartContext';
import { AuthProvider } from './context/AuthContext';

// Admin pages (lazy loaded)
const DashboardPage = lazy(() => import('./pages/admin/DashboardPage'));
const FoodPage = lazy(() => import('./pages/admin/FoodPage'));
const OrdersPage = lazy(() => import('./pages/admin/OrdersPage'));
const CustomersPage = lazy(() => import('./pages/admin/CustomersPage'));
const StaffPage = lazy(() => import('./pages/admin/StaffPage'));
const InventoryPage = lazy(() => import('./pages/admin/InventoryPage'));
const PromotionsPage = lazy(() => import('./pages/admin/PromotionsPage'));
const QueriesPage = lazy(() => import('./pages/admin/QueriesPage'));
const BranchesPage = lazy(() => import('./pages/admin/BranchesPage'));
const ReportsPage = lazy(() => import('./pages/admin/ReportsPage'));

// Public pages (lazy loaded)
const HomePage = lazy(() => import('./pages/public/HomePage'));
const MenuPage = lazy(() => import('./pages/public/MenuPage'));
const CartPage = lazy(() => import('./pages/public/CartPage'));
const CheckoutPage = lazy(() => import('./pages/public/CheckoutPage'));
const OrderConfirmPage = lazy(() => import('./pages/public/OrderConfirmPage'));
const LoginPage = lazy(() => import('./pages/public/LoginPage'));
const RegisterPage = lazy(() => import('./pages/public/RegisterPage'));
const MyOrdersPage = lazy(() => import('./pages/public/MyOrdersPage'));
const ProfilePage = lazy(() => import('./pages/public/ProfilePage'));

const LoadingFallback = () => (
  <div style={{
    display: 'flex', alignItems: 'center', justifyContent: 'center',
    height: '60vh', flexDirection: 'column', gap: 16
  }}>
    <div className="spinner spinner-primary" style={{ width: 36, height: 36 }} />
    <span style={{ color: 'var(--text-muted)', fontSize: 14 }}>Loading...</span>
  </div>
);

const App: React.FC = () => {
  return (
    <AuthProvider>
      <CartProvider>
        <Suspense fallback={<LoadingFallback />}>
          <Routes>
            {/* Public customer-facing website */}
            <Route element={<PublicLayout />}>
              <Route path="/" element={<HomePage />} />
              <Route path="/menu" element={<MenuPage />} />
              <Route path="/cart" element={<CartPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/order-confirm/:id" element={<OrderConfirmPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/my-orders" element={<MyOrdersPage />} />
              <Route path="/profile" element={<ProfilePage />} />
            </Route>

            {/* Admin / Staff management area */}
            <Route path="/admin" element={<AppLayout />}>
              <Route index element={<Navigate to="/admin/dashboard" replace />} />
              <Route path="dashboard" element={<DashboardPage />} />
              <Route path="food" element={<FoodPage />} />
              <Route path="orders" element={<OrdersPage />} />
              <Route path="customers" element={<CustomersPage />} />
              <Route path="staff" element={<StaffPage />} />
              <Route path="inventory" element={<InventoryPage />} />
              <Route path="promotions" element={<PromotionsPage />} />
              <Route path="queries" element={<QueriesPage />} />
              <Route path="branches" element={<BranchesPage />} />
              <Route path="reports" element={<ReportsPage />} />
            </Route>

            {/* Fallback */}
            <Route path="*" element={
              <div style={{ textAlign: 'center', padding: '80px 20px' }}>
                <h1 style={{ fontSize: 64, fontWeight: 800, color: 'var(--primary-500)' }}>404</h1>
                <p style={{ fontSize: 18, color: 'var(--text-muted)', marginBottom: 24 }}>Page not found</p>
                <a href="/" className="btn btn-primary">Go Home</a>
              </div>
            } />
          </Routes>
        </Suspense>
      </CartProvider>
    </AuthProvider>
  );
};

export default App;
