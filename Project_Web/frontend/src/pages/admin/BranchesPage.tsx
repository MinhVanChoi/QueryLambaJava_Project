import React, { useEffect, useState } from 'react';
import { Building2, Plus, Pencil, Trash2, X, Phone, MapPin } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { Branch } from '../../types';

const BranchesPage: React.FC = () => {
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(false);
  const [modal, setModal] = useState(false);
  const [editItem, setEditItem] = useState<Branch | null>(null);
  const [form, setForm] = useState<Partial<Branch>>({});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    try { setBranches(await ApiClient.getBranches()); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const openAdd = () => { setEditItem(null); setForm({}); setModal(true); setError(null); };
  const openEdit = (b: Branch) => { setEditItem(b); setForm({ ...b }); setModal(true); setError(null); };
  const save = async () => {
    if (!form.maCN || !form.tenCN) { setError('Branch ID and name are required'); return; }
    setSaving(true); setError(null);
    try {
      if (editItem) await ApiClient.updateBranch(editItem.maCN, form as Branch);
      else await ApiClient.createBranch(form as Branch);
      setModal(false); load();
    } catch (e: any) { setError(e.response?.data?.message ?? e.message); }
    finally { setSaving(false); }
  };
  const del = async (id: string) => {
    if (!window.confirm(`Delete branch ${id}? This will fail if employees or orders are associated.`)) return;
    try { await ApiClient.deleteBranch(id); load(); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <h1 style={{ fontSize: 22, fontWeight: 800 }}>Branch Management</h1>
        <button className="btn btn-primary" onClick={openAdd}><Plus size={16} /> Add Branch</button>
      </div>

      {loading ? (
        <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
          {branches.map(b => (
            <div key={b.maCN} className="card" style={{ position: 'relative' }}>
              <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14, marginBottom: 14 }}>
                <div style={{
                  width: 52, height: 52, borderRadius: 12, background: 'linear-gradient(135deg, #f97316, #dc2626)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0
                }}>
                  <Building2 size={24} color="white" />
                </div>
                <div>
                  <div style={{ fontWeight: 800, fontSize: 16 }}>{b.tenCN}</div>
                  <code style={{ fontSize: 11, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4, color: 'var(--text-muted)' }}>{b.maCN}</code>
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: 6, marginBottom: 16 }}>
                <div style={{ display: 'flex', gap: 8, alignItems: 'flex-start', fontSize: 13 }}>
                  <MapPin size={14} style={{ color: 'var(--text-muted)', flexShrink: 0, marginTop: 2 }} />
                  <span style={{ color: 'var(--text-muted)' }}>{b.diaChi || 'No address'}</span>
                </div>
                <div style={{ display: 'flex', gap: 8, alignItems: 'center', fontSize: 13 }}>
                  <Phone size={14} style={{ color: 'var(--text-muted)' }} />
                  <span style={{ color: 'var(--text-muted)' }}>{b.soDienThoai || 'No phone'}</span>
                </div>
              </div>

              <div style={{ display: 'flex', gap: 8 }}>
                <button className="btn btn-secondary btn-sm" onClick={() => openEdit(b)} style={{ flex: 1 }}>
                  <Pencil size={13} /> Edit
                </button>
                <button className="btn btn-danger btn-sm" onClick={() => del(b.maCN)}>
                  <Trash2 size={13} />
                </button>
              </div>
            </div>
          ))}

          {branches.length === 0 && (
            <div className="empty-state" style={{ gridColumn: '1/-1' }}>
              <Building2 size={40} style={{ opacity: 0.3, margin: '0 auto 8px' }} />
              <p>No branches found</p>
            </div>
          )}
        </div>
      )}

      {modal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setModal(false)}>
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>{editItem ? 'Edit Branch' : 'Add Branch'}</h2>
              <button onClick={() => setModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {error && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{error}</div>}
              <div className="form-group">
                <label className="form-label">Branch ID *</label>
                <input className="input" disabled={!!editItem} value={form.maCN ?? ''} onChange={e => setForm(p => ({ ...p, maCN: e.target.value }))} placeholder="e.g. CN005" />
              </div>
              <div className="form-group">
                <label className="form-label">Branch Name *</label>
                <input className="input" value={form.tenCN ?? ''} onChange={e => setForm(p => ({ ...p, tenCN: e.target.value }))} placeholder="e.g. District 5 Branch" />
              </div>
              <div className="form-group">
                <label className="form-label">Address</label>
                <textarea className="textarea" rows={2} value={form.diaChi ?? ''} onChange={e => setForm(p => ({ ...p, diaChi: e.target.value }))} placeholder="Full address..." />
              </div>
              <div className="form-group">
                <label className="form-label">Phone Number</label>
                <input className="input" value={form.soDienThoai ?? ''} onChange={e => setForm(p => ({ ...p, soDienThoai: e.target.value }))} placeholder="e.g. 028-XXXX-XXXX" />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={save} disabled={saving}>Save Branch</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default BranchesPage;
