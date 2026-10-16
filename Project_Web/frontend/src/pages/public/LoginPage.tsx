import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { LogIn, Eye, EyeOff } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { useAuth } from '../../context/AuthContext';

const LoginPage: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) { setError('Username and password are required'); return; }
    setLoading(true); setError(null);
    try {
      // Try staff login via existing /api/accounts endpoint
      // The backend AccountController has a /login endpoint
      const result = await ApiClient.loginStaff(username, password);
      login({
        maTK: result.maTK,
        maNV: result.maNV,
        tenDangNhap: result.tenDangNhap,
        chucVu: result.chucVu ?? 'NHAN_VIEN',
        maCN: result.maCN ?? '',
        hoTen: result.hoTen ?? result.tenDangNhap,
        token: result.token ?? result.maTK,
        role: (result.chucVu ?? 'NHAN_VIEN') as any,
      });
      navigate('/admin/dashboard');
    } catch (e: any) {
      setError(e.response?.data?.message ?? 'Invalid username or password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 20 }}>
      <div style={{ width: '100%', maxWidth: 420 }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{ fontSize: 48, marginBottom: 8 }}>🍔</div>
          <h1 style={{ fontSize: 24, fontWeight: 800 }}>Welcome Back</h1>
          <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>Sign in to your FastFood account</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit}>
            {error && (
              <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '10px 14px', borderRadius: 8, marginBottom: 16, fontSize: 13 }}>
                ⚠️ {error}
              </div>
            )}

            <div className="form-group">
              <label className="form-label">Username</label>
              <input className="input" type="text" value={username} onChange={e => setUsername(e.target.value)} placeholder="e.g. admin01" autoComplete="username" />
            </div>

            <div className="form-group">
              <label className="form-label">Password</label>
              <div style={{ position: 'relative' }}>
                <input
                  className="input"
                  type={showPw ? 'text' : 'password'}
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  placeholder="Your password"
                  style={{ paddingRight: 44 }}
                  autoComplete="current-password"
                />
                <button type="button" onClick={() => setShowPw(p => !p)} style={{
                  position: 'absolute', right: 12, top: '50%', transform: 'translateY(-50%)',
                  background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-muted)'
                }}>
                  {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', padding: '12px', fontSize: 15, marginTop: 4 }} disabled={loading}>
              {loading ? <><span className="spinner" style={{ width: 16, height: 16 }} /> Signing in...</> : <><LogIn size={18} /> Sign In</>}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: 16, fontSize: 13 }}>
            <div style={{ color: 'var(--text-muted)', marginBottom: 8 }}>
              Hint: Sample staff credentials from the original dataset are pre-seeded (see backend README).
            </div>
            <Link to="/register" style={{ color: 'var(--primary-600)', fontWeight: 600 }}>
              Don't have an account? Register
            </Link>
          </div>
        </div>

        {/* Demo credentials */}
        <div style={{ marginTop: 16, background: 'var(--info-bg)', border: '1px solid var(--info-border)', borderRadius: 'var(--radius-md)', padding: 14, fontSize: 12, color: 'var(--info-text)' }}>
          <div style={{ fontWeight: 700, marginBottom: 6 }}>ℹ️ Demo Login Info</div>
          <div>The original sample data includes 7 staff accounts (TK01–TK07).</div>
          <div style={{ marginTop: 4 }}>Passwords are stored as-entered from the seed data.</div>
          <div style={{ marginTop: 4 }}>Example: username <code>admin01</code>, password as seeded.</div>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;
