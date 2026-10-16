import React, { useEffect, useState, useMemo } from 'react';
import { Plus, Pencil, Trash2, Tag, Calculator, X, CheckCircle } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { Promotion, TrangThaiKhuyenMai } from '../../types';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const STATUS_COLORS: Record<TrangThaiKhuyenMai, string> = {
  DANG_AP_DUNG: 'badge-success', TAM_DUNG: 'badge-warning', HET_HAN: 'badge-danger'
};
const STATUS_LABELS: Record<TrangThaiKhuyenMai, string> = {
  DANG_AP_DUNG: 'Active', TAM_DUNG: 'Paused', HET_HAN: 'Expired'
};

const PromotionsPage: React.FC = () => {
  const [promos, setPromos] = useState<Promotion[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState('');
  const [modal, setModal] = useState(false);
  const [editItem, setEditItem] = useState<Promotion | null>(null);
  const [form, setForm] = useState<Partial<Promotion>>({});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  // Test discount modal
  const [testModal, setTestModal] = useState(false);
  const [testPromo, setTestPromo] = useState<Promotion | null>(null);
  const [testSubtotal, setTestSubtotal] = useState(100000);
  const [testResult, setTestResult] = useState<{ discount: number; final: number } | null>(null);
  const [testing, setTesting] = useState(false);

  const load = async () => {
    setLoading(true);
    try { setPromos(await ApiClient.getPromotions()); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const filtered = useMemo(() =>
    filterStatus ? promos.filter(p => p.trangThai === filterStatus) : promos,
    [promos, filterStatus]
  );

  const counts = useMemo(() => ({
    active: promos.filter(p => p.trangThai === 'DANG_AP_DUNG').length,
    paused: promos.filter(p => p.trangThai === 'TAM_DUNG').length,
    expired: promos.filter(p => p.trangThai === 'HET_HAN').length,
  }), [promos]);

  const openAdd = () => {
    setEditItem(null);
    setForm({ trangThai: 'DANG_AP_DUNG', phanTramGiam: 10, donToiThieu: 50000 });
    setModal(true); setError(null);
  };
  const openEdit = (p: Promotion) => { setEditItem(p); setForm({ ...p }); setModal(true); setError(null); };
  const save = async () => {
    if (!form.maKM || !form.tenKM || !form.ngayBatDau || !form.ngayKetThuc) {
      setError('Code, name, and dates are required'); return;
    }
    setSaving(true); setError(null);
    try {
      if (editItem) await ApiClient.updatePromotion(editItem.maKM, form as Promotion);
      else await ApiClient.createPromotion(form as Promotion);
      setModal(false); load();
    } catch (e: any) { setError(e.response?.data?.message ?? e.message); }
    finally { setSaving(false); }
  };
  const del = async (id: string) => {
    if (!window.confirm(`Delete promotion ${id}?`)) return;
    try { await ApiClient.deletePromotion(id); load(); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };
  const openTest = (p: Promotion) => { setTestPromo(p); setTestResult(null); setTestSubtotal(100000); setTestModal(true); };
  const runTest = async () => {
    if (!testPromo) return;
    setTesting(true);
    try {
      const discount = await ApiClient.calculateDiscount(testPromo.maKM, testSubtotal);
      setTestResult({ discount, final: testSubtotal - discount });
    } catch (e: any) { alert(e.response?.data?.message ?? e.message); }
    finally { setTesting(false); }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 style={{ fontSize: 22, fontWeight: 800 }}>Promotions & Discounts</h1>
        <button className="btn btn-primary" onClick={openAdd}><Plus size={16} /> Add Promotion</button>
      </div>

      {/* Summary cards */}
      <div className="stat-grid" style={{ marginBottom: 20 }}>
        {[
          { label: 'Active', count: counts.active, color: '#10b981', cls: 'badge-success' },
          { label: 'Paused', count: counts.paused, color: '#f59e0b', cls: 'badge-warning' },
          { label: 'Expired', count: counts.expired, color: '#ef4444', cls: 'badge-danger' },
        ].map(({ label, count, color }) => (
          <div key={label} className="stat-card">
            <div className="stat-icon-wrapper" style={{ backgroundColor: color }}>
              <Tag size={22} color="white" />
            </div>
            <div>
              <div className="stat-value">{count}</div>
              <div className="stat-label">{label} Promotions</div>
            </div>
          </div>
        ))}
      </div>

      {/* Filter */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 16 }}>
        <select className="select" style={{ width: 180 }} value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
          <option value="">All Statuses ({promos.length})</option>
          <option value="DANG_AP_DUNG">Active ({counts.active})</option>
          <option value="TAM_DUNG">Paused ({counts.paused})</option>
          <option value="HET_HAN">Expired ({counts.expired})</option>
        </select>
      </div>

      <div className="table-container">
        {loading ? <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div> : (
          <table className="data-table">
            <thead>
              <tr><th>Code</th><th>Name</th><th>Discount</th><th>Min Order</th><th>Max Discount</th><th>Duration</th><th>Status</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {filtered.map(p => (
                <tr key={p.maKM}>
                  <td><code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{p.maKM}</code></td>
                  <td><strong>{p.tenKM}</strong></td>
                  <td><strong style={{ color: 'var(--primary-600)' }}>{p.phanTramGiam}%</strong></td>
                  <td style={{ fontSize: 12 }}>{formatVND(p.donToiThieu)}</td>
                  <td style={{ fontSize: 12 }}>{p.giamToiDa ? formatVND(p.giamToiDa) : '—'}</td>
                  <td style={{ fontSize: 12 }}>
                    {new Date(p.ngayBatDau).toLocaleDateString('en-US')} –<br />
                    {new Date(p.ngayKetThuc).toLocaleDateString('en-US')}
                  </td>
                  <td><span className={`badge ${STATUS_COLORS[p.trangThai]}`}>{STATUS_LABELS[p.trangThai]}</span></td>
                  <td>
                    <div style={{ display: 'flex', gap: 6 }}>
                      <button className="btn btn-secondary btn-sm" onClick={() => openEdit(p)}><Pencil size={13} /></button>
                      <button className="btn btn-secondary btn-sm" onClick={() => openTest(p)} title="Test discount"><Calculator size={13} /></button>
                      <button className="btn btn-danger btn-sm" onClick={() => del(p.maKM)}><Trash2 size={13} /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* ADD/EDIT MODAL */}
      {modal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setModal(false)}>
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>{editItem ? 'Edit Promotion' : 'Add Promotion'}</h2>
              <button onClick={() => setModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {error && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{error}</div>}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label className="form-label">Promotion Code *</label>
                  <input className="input" disabled={!!editItem} value={form.maKM ?? ''} onChange={e => setForm(p => ({ ...p, maKM: e.target.value }))} placeholder="e.g. KM04" />
                </div>
                <div className="form-group">
                  <label className="form-label">Status</label>
                  <select className="select" value={form.trangThai ?? 'DANG_AP_DUNG'} onChange={e => setForm(p => ({ ...p, trangThai: e.target.value as TrangThaiKhuyenMai }))}>
                    <option value="DANG_AP_DUNG">Active</option>
                    <option value="TAM_DUNG">Paused</option>
                    <option value="HET_HAN">Expired</option>
                  </select>
                </div>
              </div>
              <div className="form-group">
                <label className="form-label">Promotion Name *</label>
                <input className="input" value={form.tenKM ?? ''} onChange={e => setForm(p => ({ ...p, tenKM: e.target.value }))} placeholder="e.g. Summer Sale 20%" />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label className="form-label">Discount % *</label>
                  <input type="number" className="input" min={0} max={100} value={form.phanTramGiam ?? 0} onChange={e => setForm(p => ({ ...p, phanTramGiam: Number(e.target.value) }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">Min Order (₫) *</label>
                  <input type="number" className="input" min={0} value={form.donToiThieu ?? 0} onChange={e => setForm(p => ({ ...p, donToiThieu: Number(e.target.value) }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">Max Discount (₫)</label>
                  <input type="number" className="input" min={0} value={form.giamToiDa ?? ''} onChange={e => setForm(p => ({ ...p, giamToiDa: e.target.value ? Number(e.target.value) : undefined }))} placeholder="No limit" />
                </div>
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label className="form-label">Start Date *</label>
                  <input type="date" className="input" value={form.ngayBatDau?.substring(0, 10) ?? ''} onChange={e => setForm(p => ({ ...p, ngayBatDau: e.target.value }))} />
                </div>
                <div className="form-group">
                  <label className="form-label">End Date *</label>
                  <input type="date" className="input" value={form.ngayKetThuc?.substring(0, 10) ?? ''} onChange={e => setForm(p => ({ ...p, ngayKetThuc: e.target.value }))} />
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={save} disabled={saving}>Save Promotion</button>
            </div>
          </div>
        </div>
      )}

      {/* TEST DISCOUNT MODAL */}
      {testModal && testPromo && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setTestModal(false)}>
          <div className="modal-content" style={{ maxWidth: 420 }}>
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>Test Discount Calculator</h2>
              <button onClick={() => setTestModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              <div style={{ background: 'var(--bg-main)', padding: 12, borderRadius: 8, marginBottom: 16, fontSize: 13 }}>
                <div><strong>Promotion:</strong> {testPromo.tenKM}</div>
                <div><strong>Discount:</strong> {testPromo.phanTramGiam}%</div>
                <div><strong>Min Order:</strong> {formatVND(testPromo.donToiThieu)}</div>
                {testPromo.giamToiDa && <div><strong>Max Discount:</strong> {formatVND(testPromo.giamToiDa)}</div>}
              </div>
              <div className="form-group">
                <label className="form-label">Order Subtotal (₫)</label>
                <input type="number" className="input" value={testSubtotal} onChange={e => setTestSubtotal(Number(e.target.value))} min={0} />
              </div>
              <button className="btn btn-primary" style={{ width: '100%' }} onClick={runTest} disabled={testing}>
                {testing ? 'Calculating...' : <><Calculator size={14} /> Calculate Discount</>}
              </button>
              {testResult && (
                <div style={{ marginTop: 16, background: 'var(--success-bg)', border: '1px solid var(--success-border)', borderRadius: 8, padding: 16 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontWeight: 700, color: 'var(--success-text)', marginBottom: 8 }}>
                    <CheckCircle size={18} /> Result
                  </div>
                  <div style={{ fontSize: 14 }}>Subtotal: <strong>{formatVND(testSubtotal)}</strong></div>
                  <div style={{ fontSize: 14, color: 'var(--danger-text)' }}>Discount: <strong>-{formatVND(testResult.discount)}</strong></div>
                  <div style={{ fontSize: 16, fontWeight: 800, color: 'var(--primary-600)', marginTop: 4 }}>You Pay: {formatVND(testResult.final)}</div>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default PromotionsPage;
