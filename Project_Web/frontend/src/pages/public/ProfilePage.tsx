import React from 'react';
import { User, Phone, Mail, Shield, LogOut } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';

const ProfilePage: React.FC = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  if (!user) {
    return (
      <div className="section-container" style={{ maxWidth: 600, margin: '80px auto', textAlign: 'center' }}>
        <div style={{ fontSize: 48, marginBottom: 12 }}>🔒</div>
        <h2>Please Sign In</h2>
        <p style={{ color: 'var(--text-muted)', marginTop: 8, marginBottom: 20 }}>
          You need to be signed in to view and manage your account details.
        </p>
        <button onClick={() => navigate('/login')} className="btn btn-primary">Go to Login</button>
      </div>
    );
  }

  const isCust = user.role === 'CUSTOMER';
  const cust = isCust ? (user as any) : null;
  const staff = !isCust ? (user as any) : null;

  return (
    <div className="section-container" style={{ maxWidth: 650, margin: '40px auto', padding: '0 20px' }}>
      <div className="card">
        <div style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 24 }}>
          <div style={{
            width: 64, height: 64, borderRadius: '50%',
            background: 'linear-gradient(135deg, #f97316, #ea580c)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            color: 'white', fontSize: 28, fontWeight: 700
          }}>
            {(cust?.hoTen || staff?.hoTen || staff?.tenDangNhap || 'U')[0].toUpperCase()}
          </div>
          <div>
            <h2 style={{ fontSize: 20, fontWeight: 800 }}>
              {cust?.hoTen || staff?.hoTen || staff?.tenDangNhap}
            </h2>
            <div style={{ display: 'flex', gap: 8, alignItems: 'center', marginTop: 4 }}>
              <span className={`badge ${isCust ? 'badge-info' : 'badge-danger'}`}>
                {user.role}
              </span>
              <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>
                ID: {cust?.maKH || staff?.maNV || staff?.maTK}
              </span>
            </div>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 14, borderTop: '1px solid var(--border-subtle)', paddingTop: 20 }}>
          {cust && (
            <>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, fontSize: 14 }}>
                <Phone size={16} color="var(--text-muted)" />
                <span style={{ color: 'var(--text-muted)', width: 80 }}>Phone:</span>
                <strong>{cust.soDienThoai}</strong>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, fontSize: 14 }}>
                <Mail size={16} color="var(--text-muted)" />
                <span style={{ color: 'var(--text-muted)', width: 80 }}>Email:</span>
                <strong>{cust.email || '—'}</strong>
              </div>
            </>
          )}

          {staff && (
            <>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, fontSize: 14 }}>
                <Shield size={16} color="var(--text-muted)" />
                <span style={{ color: 'var(--text-muted)', width: 80 }}>Role:</span>
                <strong>{staff.chucVu}</strong>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, fontSize: 14 }}>
                <User size={16} color="var(--text-muted)" />
                <span style={{ color: 'var(--text-muted)', width: 80 }}>Branch:</span>
                <strong>{staff.maCN || 'Headquarters'}</strong>
              </div>
            </>
          )}
        </div>

        <div style={{ borderTop: '1px solid var(--border-subtle)', marginTop: 24, paddingTop: 20, display: 'flex', justifyContent: 'space-between' }}>
          {!isCust && (
            <button onClick={() => navigate('/admin/dashboard')} className="btn btn-secondary">
              Go to Admin Dashboard
            </button>
          )}
          <button onClick={handleLogout} className="btn btn-danger" style={{ marginLeft: 'auto' }}>
            <LogOut size={16} /> Sign Out
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
