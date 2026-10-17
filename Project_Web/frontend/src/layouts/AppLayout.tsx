import React, { useState } from 'react';
import { NavLink, Outlet, useLocation, Link, Navigate } from 'react-router-dom';
import {
  LayoutDashboard, UtensilsCrossed, ShoppingBag, Users, UserCheck,
  PackageOpen, Tag, DatabaseZap, RotateCcw, CheckCircle2,
  Menu, X, Building2, BarChart3, Globe, LogOut,
} from 'lucide-react';
import { ApiClient } from '../api/client';
import { useAuth } from '../context/AuthContext';

export const AppLayout: React.FC = () => {
  const { user, logout } = useAuth();
  const [resetting, setResetting] = useState(false);
  const [resetMsg, setResetMsg] = useState<string | null>(null);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  if (user.role === 'CUSTOMER') {
    return <Navigate to="/" replace />;
  }

  const handleResetDatabase = async () => {
    if (!window.confirm('Reset database to original sample data? This will reinitialize all 11 tables.')) return;
    try {
      setResetting(true);
      setResetMsg(null);
      await ApiClient.reinitializeDatabase();
      setResetMsg('Database reinitialized successfully! Refreshing...');
      setTimeout(() => window.location.reload(), 1200);
    } catch (err: any) {
      alert('Failed to reset: ' + (err.response?.data?.message || err.message));
    } finally {
      setResetting(false);
    }
  };

  const pageTitles: Record<string, string> = {
    '/admin/dashboard': 'System Dashboard & Metrics',
    '/admin/food': 'Food Items & Menu Categories',
    '/admin/orders': 'Orders & Point of Sale',
    '/admin/customers': 'Customer Management',
    '/admin/staff': 'Employees & User Accounts',
    '/admin/inventory': 'Inventory & Branch Stock',
    '/admin/promotions': 'Promotions & Discounts',
    '/admin/queries': '30 Business Queries Explorer',
    '/admin/branches': 'Branch Management',
    '/admin/reports': 'Reports & Analytics',
  };
  const pageTitle = pageTitles[location.pathname] ?? 'FastFood MIS Management';

  return (
    <div className="app-container">
      {/* Sidebar overlay for mobile */}
      {sidebarOpen && (
        <div
          className="sidebar-overlay"
          onClick={() => setSidebarOpen(false)}
          style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', zIndex: 35 }}
        />
      )}

      {/* Sidebar */}
      <aside className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <div className="sidebar-logo">FF</div>
          <div>
            <div className="sidebar-brand-name">FastFood MIS</div>
            <div className="sidebar-brand-desc">Enterprise Management</div>
          </div>
        </div>

        <nav className="sidebar-nav">
          <div className="nav-section-title">Overview</div>
          <NavLink to="/admin/dashboard" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={18} /><span>Dashboard</span>
          </NavLink>
          <NavLink to="/admin/reports" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <BarChart3 size={18} /><span>Reports & Analytics</span>
          </NavLink>

          <div className="nav-section-title">Operations</div>
          <NavLink to="/admin/orders" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <ShoppingBag size={18} /><span>Orders & POS</span>
          </NavLink>
          <NavLink to="/admin/food" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <UtensilsCrossed size={18} /><span>Food & Menu</span>
          </NavLink>
          <NavLink to="/admin/inventory" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <PackageOpen size={18} /><span>Inventory Stock</span>
          </NavLink>
          <NavLink to="/admin/promotions" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Tag size={18} /><span>Promotions</span>
          </NavLink>

          <div className="nav-section-title">Management</div>
          <NavLink to="/admin/branches" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Building2 size={18} /><span>Branches</span>
          </NavLink>
          <NavLink to="/admin/customers" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <Users size={18} /><span>Customers</span>
          </NavLink>
          <NavLink to="/admin/staff" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <UserCheck size={18} /><span>Employees & Accounts</span>
          </NavLink>

          <div className="nav-section-title">Analytics</div>
          <NavLink to="/admin/queries" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <DatabaseZap size={18} /><span>30 Queries Explorer</span>
          </NavLink>

          <div style={{ marginTop: 'auto', paddingTop: 16 }}>
            <div className="nav-section-title">Navigation</div>
            <Link to="/" className="nav-link">
              <Globe size={18} /><span>Customer Website</span>
            </Link>
          </div>
        </nav>
      </aside>

      {/* Main content */}
      <div className="main-wrapper">
        <header className="topbar">
          <div className="topbar-title">
            <button
              className="btn btn-secondary btn-sm"
              style={{ display: 'flex' }}
              onClick={() => setSidebarOpen((o: boolean) => !o)}
            >
              {sidebarOpen ? <X size={16} /> : <Menu size={16} />}
            </button>
            <span>{pageTitle}</span>
          </div>

          <div className="topbar-actions">
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, padding: '4px 10px', background: 'var(--primary-50)', borderRadius: 6, fontSize: 13 }}>
              <span style={{ fontWeight: 700, color: 'var(--primary-700)' }}>{(user as any).hoTen || (user as any).tenDangNhap}</span>
              <span className="badge badge-info" style={{ fontSize: 10, padding: '2px 6px' }}>{user.role}</span>
            </div>
            <button
              className="btn btn-secondary btn-sm"
              onClick={() => logout()}
              title="Sign Out"
            >
              <LogOut size={14} />
              <span>Sign Out</span>
            </button>
            <span className="badge badge-success" style={{ padding: '6px 12px' }}>
              <CheckCircle2 size={13} /> DB Online
            </span>
            <button
              className="btn btn-secondary btn-sm"
              onClick={handleResetDatabase}
              disabled={resetting}
              title="Reset to original sample data"
            >
              {resetting ? (
                <><span className="spinner spinner-primary" style={{ width: 14, height: 14 }} /><span>Resetting...</span></>
              ) : (
                <><RotateCcw size={14} /><span>Reset DB</span></>
              )}
            </button>
          </div>
        </header>

        {resetMsg && (
          <div style={{
            background: 'var(--success-bg)', color: 'var(--success-text)',
            padding: '10px 24px', borderBottom: '1px solid var(--success-border)',
            fontSize: 13, display: 'flex', alignItems: 'center', gap: 8
          }}>
            <CheckCircle2 size={16} /> {resetMsg}
          </div>
        )}

        <main className="content-area">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
