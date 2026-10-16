import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { UserPlus } from 'lucide-react';
import { ApiClient } from '../../api/client';

const RegisterPage: React.FC = () => {
  const [form, setForm] = useState({ maKH: '', hoTen: '', soDienThoai: '', email: '', diaChi: '' });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.hoTen || !form.soDienThoai) { setError('Name and phone are required'); return; }
    if (!form.maKH) { setError('Customer ID is required (e.g. KH011)'); return; }
    setLoading(true); setError(null);
    try {
      await ApiClient.createCustomer({
        ...form,
        ngayDangKy: new Date().toISOString()
      });
      setSuccess(true);
      setTimeout(() => navigate('/login'), 2000);
    } catch (e: any) {
      setError(e.response?.data?.message ?? e.message);
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div style={{ textAlign: 'center', padding: '80px 20px' }}>
        <div style={{ fontSize: 64 }}>✅</div>
        <h2 style={{ fontSize: 24, fontWeight: 800, marginTop: 16 }}>Registration successful!</h2>
        <p style={{ color: 'var(--text-muted)', marginTop: 8 }}>Redirecting to login...</p>
      </div>
    );
  }

  return (
    <div style={{ minHeight: '80vh', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 20 }}>
      <div style={{ width: '100%', maxWidth: 460 }}>
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{ fontSize: 48, marginBottom: 8 }}>🍔</div>
          <h1 style={{ fontSize: 24, fontWeight: 800 }}>Create Account</h1>
          <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>Join FastFood Chain for exclusive deals</p>
        </div>

        <div className="card">
          <form onSubmit={handleSubmit}>
            {error && (
              <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '10px 14px', borderRadius: 8, marginBottom: 16, fontSize: 13 }}>
                ⚠️ {error}
              </div>
            )}

            <div className="form-group">
              <label className="form-label">Customer ID *</label>
              <input className="input" value={form.maKH} onChange={e => setForm(p => ({ ...p, maKH: e.target.value }))} placeholder="e.g. KH011" />
              <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 4 }}>Choose a unique ID (e.g. KH011, KH012, ...)</div>
            </div>

            <div className="form-group">
              <label className="form-label">Full Name *</label>
              <input className="input" value={form.hoTen} onChange={e => setForm(p => ({ ...p, hoTen: e.target.value }))} placeholder="e.g. Nguyen Van A" />
            </div>

            <div className="form-group">
              <label className="form-label">Phone Number *</label>
              <input className="input" type="tel" value={form.soDienThoai} onChange={e => setForm(p => ({ ...p, soDienThoai: e.target.value }))} placeholder="e.g. 0901234567" />
            </div>

            <div className="form-group">
              <label className="form-label">Email (optional)</label>
              <input className="input" type="email" value={form.email} onChange={e => setForm(p => ({ ...p, email: e.target.value }))} placeholder="your@email.com" />
            </div>

            <div className="form-group">
              <label className="form-label">Address (optional)</label>
              <textarea className="textarea" rows={2} value={form.diaChi} onChange={e => setForm(p => ({ ...p, diaChi: e.target.value }))} placeholder="Your delivery address" />
            </div>

            <button type="submit" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', padding: '12px', fontSize: 15, marginTop: 4 }} disabled={loading}>
              {loading ? 'Creating account...' : <><UserPlus size={18} /> Create Account</>}
            </button>
          </form>

          <div style={{ textAlign: 'center', marginTop: 16, fontSize: 13, color: 'var(--text-muted)' }}>
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'var(--primary-600)', fontWeight: 600 }}>Sign In</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
