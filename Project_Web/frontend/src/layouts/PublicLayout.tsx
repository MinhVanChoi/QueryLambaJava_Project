import React, { useState } from 'react';
import { NavLink, Outlet, Link, useNavigate } from 'react-router-dom';
import { ShoppingCart, Menu, X, Home, UtensilsCrossed, ChevronDown, User, LogOut } from 'lucide-react';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';

export const PublicLayout: React.FC = () => {
  const { totalItems } = useCart();
  const { user, logout } = useAuth();
  const [mobileOpen, setMobileOpen] = useState(false);
  const [userMenuOpen, setUserMenuOpen] = useState(false);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    setUserMenuOpen(false);
    navigate('/');
  };

  return (
    <div className="public-layout">
      {/* ===== Navigation Bar ===== */}
      <nav className="pub-nav">
        <div className="pub-nav-inner">
          {/* Brand */}
          <Link to="/" className="pub-brand">
            <div className="pub-brand-logo">🍔</div>
            <div>
              <div className="pub-brand-name">FastFood Chain</div>
              <div className="pub-brand-tagline">Order. Enjoy. Repeat.</div>
            </div>
          </Link>

          {/* Desktop nav links */}
          <div className="pub-nav-links">
            <NavLink to="/" className={({ isActive }) => `pub-nav-link ${isActive ? 'active' : ''}`} end>
              Home
            </NavLink>
            <NavLink to="/menu" className={({ isActive }) => `pub-nav-link ${isActive ? 'active' : ''}`}>
              Menu
            </NavLink>
          </div>

          {/* Right side actions */}
          <div className="pub-nav-actions">
            {/* Cart */}
            <Link to="/cart" className="pub-cart-btn">
              <ShoppingCart size={20} />
              {totalItems > 0 && (
                <span className="pub-cart-badge">{totalItems}</span>
              )}
            </Link>

            {/* User menu */}
            {user ? (
              <div className="pub-user-menu" style={{ position: 'relative' }}>
                <button
                  className="pub-user-btn"
                  onClick={() => setUserMenuOpen(o => !o)}
                >
                  <User size={16} />
                  <span>{user.role === 'CUSTOMER' ? (user as any).hoTen?.split(' ').pop() : (user as any).tenDangNhap}</span>
                  <ChevronDown size={14} />
                </button>
                {userMenuOpen && (
                  <div className="pub-user-dropdown">
                    {user.role !== 'CUSTOMER' && (
                      <Link
                        to="/admin/dashboard"
                        className="pub-dropdown-item"
                        onClick={() => setUserMenuOpen(false)}
                        style={{ color: 'var(--primary-600)', fontWeight: 700, borderBottom: '1px solid var(--border-subtle)' }}
                      >
                        🛡️ Admin Panel
                      </Link>
                    )}
                    <Link to="/my-orders" className="pub-dropdown-item" onClick={() => setUserMenuOpen(false)}>
                      My Orders
                    </Link>
                    <Link to="/profile" className="pub-dropdown-item" onClick={() => setUserMenuOpen(false)}>
                      Profile
                    </Link>
                    <button className="pub-dropdown-item danger" onClick={handleLogout}>
                      <LogOut size={14} /> Sign Out
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <div style={{ display: 'flex', gap: 8 }}>
                <Link to="/login" className="btn btn-secondary btn-sm">Sign In</Link>
                <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
              </div>
            )}

            {/* Mobile hamburger */}
            <button className="pub-hamburger" onClick={() => setMobileOpen(o => !o)}>
              {mobileOpen ? <X size={22} /> : <Menu size={22} />}
            </button>
          </div>
        </div>

        {/* Mobile menu */}
        {mobileOpen && (
          <div className="pub-mobile-menu">
            <NavLink to="/" className="pub-mobile-link" onClick={() => setMobileOpen(false)} end>
              <Home size={16} /> Home
            </NavLink>
            <NavLink to="/menu" className="pub-mobile-link" onClick={() => setMobileOpen(false)}>
              <UtensilsCrossed size={16} /> Menu
            </NavLink>
            <NavLink to="/cart" className="pub-mobile-link" onClick={() => setMobileOpen(false)}>
              <ShoppingCart size={16} /> Cart ({totalItems})
            </NavLink>
            {user ? (
              <>
                {user.role !== 'CUSTOMER' && (
                  <NavLink to="/admin/dashboard" className="pub-mobile-link" onClick={() => setMobileOpen(false)} style={{ color: 'var(--primary-600)', fontWeight: 700 }}>
                    🛡️ Admin Panel
                  </NavLink>
                )}
                <NavLink to="/my-orders" className="pub-mobile-link" onClick={() => setMobileOpen(false)}>
                  My Orders
                </NavLink>
                <button className="pub-mobile-link" onClick={handleLogout} style={{ width: '100%', textAlign: 'left', background: 'none', border: 'none', cursor: 'pointer' }}>
                  <LogOut size={16} /> Sign Out
                </button>
              </>
            ) : (
              <>
                <NavLink to="/login" className="pub-mobile-link" onClick={() => setMobileOpen(false)}>Sign In</NavLink>
                <NavLink to="/register" className="pub-mobile-link" onClick={() => setMobileOpen(false)}>Register</NavLink>
              </>
            )}
          </div>
        )}
      </nav>

      {/* ===== Page content ===== */}
      <main className="pub-main">
        <Outlet />
      </main>

      {/* ===== Footer ===== */}
      <footer className="pub-footer">
        <div className="pub-footer-inner">
          <div className="pub-footer-brand">
            <div style={{ fontSize: 28, marginBottom: 8 }}>🍔</div>
            <div style={{ fontWeight: 700, fontSize: 18, color: 'white', marginBottom: 4 }}>FastFood Chain</div>
            <div style={{ fontSize: 13, color: '#94a3b8', maxWidth: 260 }}>
              Bringing quality fast food to every corner of the city since 2020.
            </div>
          </div>

          <div className="pub-footer-col">
            <div className="pub-footer-col-title">Quick Links</div>
            <Link to="/" className="pub-footer-link">Home</Link>
            <Link to="/menu" className="pub-footer-link">View Menu</Link>
            <Link to="/cart" className="pub-footer-link">My Cart</Link>
            <Link to="/login" className="pub-footer-link">Sign In</Link>
          </div>

          <div className="pub-footer-col">
            <div className="pub-footer-col-title">Account</div>
            <Link to="/register" className="pub-footer-link">Register</Link>
            <Link to="/my-orders" className="pub-footer-link">Order History</Link>
            <Link to="/profile" className="pub-footer-link">My Profile</Link>
          </div>

          <div className="pub-footer-col">
            <div className="pub-footer-col-title">Contact</div>
            <div className="pub-footer-link">📞 1800-FASTFOOD</div>
            <div className="pub-footer-link">✉️ support@fastfood.vn</div>
            <div className="pub-footer-link">🕐 Open 7AM – 11PM Daily</div>
            <div className="pub-footer-link">🏪 4 Branches Citywide</div>
          </div>
        </div>
        <div className="pub-footer-bottom">
          © 2026 FastFood Chain Management System · All rights reserved
        </div>
      </footer>
    </div>
  );
};
