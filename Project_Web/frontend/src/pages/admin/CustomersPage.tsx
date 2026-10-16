import React, { useEffect, useState, useMemo } from 'react';
import { Plus, Pencil, Trash2, Search, Users, X } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { Customer } from '../../types';

const CustomersPage: React.FC = () => {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [modal, setModal] = useState(false);
  const [editItem, setEditItem] = useState<Customer | null>(null);
  const [form, setForm] = useState<Partial<Customer>>({});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    try { setCustomers(await ApiClient.getCustomers()); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  const filtered = useMemo(() =>
    customers.filter(c =>
      c.hoTen.toLowerCase().includes(search.toLowerCase()) ||
      c.soDienThoai.includes(search) ||
      (c.email ?? '').toLowerCase().includes(search.toLowerCase())
    ), [customers, search]
  );

  const openAdd = () => { setEditItem(null); setForm({ ngayDangKy: new Date().toISOString() }); setModal(true); setError(null); };
  const openEdit = (c: Customer) => { setEditItem(c); setForm({ ...c }); setModal(true); setError(null); };

  const save = async () => {
    if (!form.maKH || !form.hoTen || !form.soDienThoai) { setError('ID, name, and phone are required'); return; }
    setSaving(true); setError(null);
    try {
      if (editItem) await ApiClient.updateCustomer(editItem.maKH, form as Customer);
      else await ApiClient.createCustomer(form as Customer);
      setModal(false); load();
    } catch (e: any) { setError(e.response?.data?.message ?? e.message); }
    finally { setSaving(false); }
  };

  const del = async (id: string) => {
    if (!window.confirm(`Delete customer ${id}?`)) return;
    try { await ApiClient.deleteCustomer(id); load(); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 style={{ fontSize: 22, fontWeight: 800 }}>Customer Management</h1>
        <button className="btn btn-primary" onClick={openAdd}><Plus size={16} /> Add Customer</button>
      </div>

      <div style={{ display: 'flex', gap: 12, marginBottom: 16, alignItems: 'center' }}>
        <div style={{ position: 'relative', flex: 1, maxWidth: 360 }}>
          <Search size={15} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
          <input className="input" style={{ paddingLeft: 32 }} placeholder="Search by name, phone, or email..." value={search} onChange={e => setSearch(e.target.value)} />
        </div>
        <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>Showing {filtered.length} of {customers.length} customers</span>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div>
        ) : filtered.length === 0 ? (
          <div className="empty-state"><Users size={40} style={{ opacity: 0.3, margin: '0 auto 8px' }} /><p>No customers found</p></div>
        ) : (
          <table className="data-table">
            <thead>
              <tr><th>ID</th><th>Full Name</th><th>Phone</th><th>Email</th><th>Address</th><th>Registered</th><th>Actions</th></tr>
            </thead>
            <tbody>
              {filtered.map(c => (
                <tr key={c.maKH}>
                  <td><code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{c.maKH}</code></td>
                  <td><strong>{c.hoTen}</strong></td>
                  <td>{c.soDienThoai}</td>
                  <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>{c.email ?? '—'}</td>
                  <td style={{ fontSize: 12, color: 'var(--text-muted)', maxWidth: 180 }}>{c.diaChi ?? '—'}</td>
                  <td style={{ fontSize: 12 }}>{new Date(c.ngayDangKy).toLocaleDateString('en-US')}</td>
                  <td>
                    <div style={{ display: 'flex', gap: 6 }}>
                      <button className="btn btn-secondary btn-sm" onClick={() => openEdit(c)}><Pencil size={13} /></button>
                      <button className="btn btn-danger btn-sm" onClick={() => del(c.maKH)}><Trash2 size={13} /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {modal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setModal(false)}>
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>{editItem ? 'Edit Customer' : 'Add Customer'}</h2>
              <button onClick={() => setModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {error && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{error}</div>}
              <div className="form-group">
                <label className="form-label">Customer ID *</label>
                <input className="input" disabled={!!editItem} value={form.maKH ?? ''} onChange={e => setForm(p => ({ ...p, maKH: e.target.value }))} placeholder="e.g. KH011" />
              </div>
              <div className="form-group">
                <label className="form-label">Full Name *</label>
                <input className="input" value={form.hoTen ?? ''} onChange={e => setForm(p => ({ ...p, hoTen: e.target.value }))} placeholder="e.g. Nguyen Van A" />
              </div>
              <div className="form-group">
                <label className="form-label">Phone Number *</label>
                <input className="input" value={form.soDienThoai ?? ''} onChange={e => setForm(p => ({ ...p, soDienThoai: e.target.value }))} placeholder="e.g. 0901234567" />
              </div>
              <div className="form-group">
                <label className="form-label">Email (optional)</label>
                <input type="email" className="input" value={form.email ?? ''} onChange={e => setForm(p => ({ ...p, email: e.target.value }))} />
              </div>
              <div className="form-group">
                <label className="form-label">Address (optional)</label>
                <textarea className="textarea" rows={2} value={form.diaChi ?? ''} onChange={e => setForm(p => ({ ...p, diaChi: e.target.value }))} />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={save} disabled={saving}>
                {saving ? 'Saving...' : 'Save Customer'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CustomersPage;
