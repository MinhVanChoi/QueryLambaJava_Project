import React, { useEffect, useState, useMemo } from 'react';
import { Plus, Pencil, Trash2, Search, UserCheck, Users, Lock, Unlock, X } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { Employee, Account, Branch, ChucVu } from '../../types';

const ROLE_LABELS: Record<ChucVu, string> = {
  ADMIN: 'Admin', QUAN_LY: 'Manager', THU_NGAN: 'Cashier', NHAN_VIEN: 'Staff'
};
const ROLE_COLORS: Record<ChucVu, string> = {
  ADMIN: 'badge-danger', QUAN_LY: 'badge-warning', THU_NGAN: 'badge-info', NHAN_VIEN: 'badge-success'
};

const StaffPage: React.FC = () => {
  const [tab, setTab] = useState<'employees' | 'accounts'>('employees');
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [filterBranch, setFilterBranch] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  // Employee modal
  const [empModal, setEmpModal] = useState(false);
  const [editEmp, setEditEmp] = useState<Employee | null>(null);
  const [empForm, setEmpForm] = useState<Partial<Employee>>({});

  // Account modal
  const [accModal, setAccModal] = useState(false);
  const [accForm, setAccForm] = useState<{ maTK: string; maNV: string; tenDangNhap: string; matKhauHash: string; trangThai: string }>({
    maTK: '', maNV: '', tenDangNhap: '', matKhauHash: '', trangThai: 'ACTIVE'
  });

  const loadAll = async () => {
    setLoading(true);
    try {
      const [e, a, b] = await Promise.all([
        ApiClient.getEmployees({ keyword: undefined, branchId: filterBranch || undefined }),
        ApiClient.getAccounts(),
        ApiClient.getBranches()
      ]);
      setEmployees(e); setAccounts(a); setBranches(b);
    } finally { setLoading(false); }
  };

  useEffect(() => { loadAll(); }, [filterBranch]);

  const filteredEmps = useMemo(() =>
    employees.filter(e => e.hoTen.toLowerCase().includes(search.toLowerCase()) || e.soDienThoai.includes(search)),
    [employees, search]
  );

  // Employee CRUD
  const openAddEmp = () => { setEditEmp(null); setEmpForm({ chucVu: 'NHAN_VIEN' }); setEmpModal(true); setError(null); };
  const openEditEmp = (e: Employee) => { setEditEmp(e); setEmpForm({ ...e }); setEmpModal(true); setError(null); };
  const saveEmp = async () => {
    if (!empForm.maNV || !empForm.hoTen || !empForm.maCN) { setError('ID, name, and branch required'); return; }
    setSaving(true); setError(null);
    try {
      if (editEmp) await ApiClient.updateEmployee(editEmp.maNV, empForm as Employee);
      else await ApiClient.createEmployee(empForm as Employee);
      setEmpModal(false); loadAll();
    } catch (e: any) { setError(e.response?.data?.message ?? e.message); }
    finally { setSaving(false); }
  };
  const delEmp = async (id: string) => {
    if (!window.confirm(`Delete employee ${id}?`)) return;
    try { await ApiClient.deleteEmployee(id); loadAll(); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };

  // Account actions
  const openAddAcc = () => { setAccForm({ maTK: '', maNV: '', tenDangNhap: '', matKhauHash: '', trangThai: 'ACTIVE' }); setAccModal(true); setError(null); };
  const saveAcc = async () => {
    if (!accForm.maTK || !accForm.maNV || !accForm.tenDangNhap || !accForm.matKhauHash) { setError('All fields required'); return; }
    setSaving(true); setError(null);
    try {
      await ApiClient.createAccount({ ...accForm, trangThai: accForm.trangThai as any });
      setAccModal(false); loadAll();
    } catch (e: any) { setError(e.response?.data?.message ?? e.message); }
    finally { setSaving(false); }
  };
  const toggleLock = async (acc: Account) => {
    try {
      if (acc.trangThai === 'ACTIVE') await ApiClient.lockAccount(acc.maTK);
      else await ApiClient.unlockAccount(acc.maTK);
      loadAll();
    } catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };
  const delAcc = async (id: string) => {
    if (!window.confirm(`Delete account ${id}?`)) return;
    try { await ApiClient.deleteAccount(id); loadAll(); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };

  const getEmpAccount = (maNV: string) => accounts.find(a => a.maNV === maNV);

  return (
    <div>
      <h1 style={{ fontSize: 22, fontWeight: 800, marginBottom: 20 }}>Employees & User Accounts</h1>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: 4, marginBottom: 20, borderBottom: '1px solid var(--border-subtle)' }}>
        {(['employees', 'accounts'] as const).map(t => (
          <button key={t} onClick={() => setTab(t)} style={{
            padding: '10px 20px', border: 'none', background: 'none', cursor: 'pointer',
            fontWeight: tab === t ? 700 : 500,
            color: tab === t ? 'var(--primary-600)' : 'var(--text-muted)',
            borderBottom: tab === t ? '2px solid var(--primary-600)' : '2px solid transparent',
            fontSize: 14, display: 'flex', alignItems: 'center', gap: 8
          }}>
            {t === 'employees'
              ? <><Users size={16} /> Employees ({employees.length})</>
              : <><UserCheck size={16} /> Accounts ({accounts.length})</>}
          </button>
        ))}
      </div>

      {/* EMPLOYEES TAB */}
      {tab === 'employees' && (
        <>
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap', marginBottom: 16, alignItems: 'center' }}>
            <div style={{ position: 'relative', flex: 1, minWidth: 200 }}>
              <Search size={15} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input className="input" style={{ paddingLeft: 32 }} placeholder="Search name or phone..." value={search} onChange={e => setSearch(e.target.value)} />
            </div>
            <select className="select" style={{ width: 180 }} value={filterBranch} onChange={e => setFilterBranch(e.target.value)}>
              <option value="">All Branches</option>
              {branches.map(b => <option key={b.maCN} value={b.maCN}>{b.tenCN}</option>)}
            </select>
            <button className="btn btn-primary" onClick={openAddEmp}><Plus size={16} /> Add Employee</button>
          </div>
          <div className="table-container">
            {loading ? <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div> : (
              <table className="data-table">
                <thead>
                  <tr><th>ID</th><th>Name</th><th>Phone</th><th>Role</th><th>Branch</th><th>Has Account</th><th>Actions</th></tr>
                </thead>
                <tbody>
                  {filteredEmps.map(e => {
                    const acc = getEmpAccount(e.maNV);
                    return (
                      <tr key={e.maNV}>
                        <td><code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{e.maNV}</code></td>
                        <td><strong>{e.hoTen}</strong></td>
                        <td>{e.soDienThoai}</td>
                        <td><span className={`badge ${ROLE_COLORS[e.chucVu]}`}>{ROLE_LABELS[e.chucVu]}</span></td>
                        <td style={{ fontSize: 12 }}>{branches.find(b => b.maCN === e.maCN)?.tenCN ?? e.maCN}</td>
                        <td>
                          {acc ? (
                            <span className={`badge ${acc.trangThai === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                              {acc.tenDangNhap} ({acc.trangThai === 'ACTIVE' ? 'Active' : 'Locked'})
                            </span>
                          ) : (
                            <span className="badge badge-warning">No Account</span>
                          )}
                        </td>
                        <td>
                          <div style={{ display: 'flex', gap: 6 }}>
                            <button className="btn btn-secondary btn-sm" onClick={() => openEditEmp(e)}><Pencil size={13} /></button>
                            <button className="btn btn-danger btn-sm" onClick={() => delEmp(e.maNV)}><Trash2 size={13} /></button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}

      {/* ACCOUNTS TAB */}
      {tab === 'accounts' && (
        <>
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 16 }}>
            <button className="btn btn-primary" onClick={openAddAcc}><Plus size={16} /> Add Account</button>
          </div>
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr><th>Account ID</th><th>Employee</th><th>Username</th><th>Status</th><th>Created</th><th>Last Login</th><th>Actions</th></tr>
              </thead>
              <tbody>
                {accounts.map(a => {
                  const emp = employees.find(e => e.maNV === a.maNV);
                  return (
                    <tr key={a.maTK}>
                      <td><code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{a.maTK}</code></td>
                      <td style={{ fontSize: 13 }}>{emp ? `${emp.hoTen} (${a.maNV})` : a.maNV}</td>
                      <td><strong>{a.tenDangNhap}</strong></td>
                      <td><span className={`badge ${a.trangThai === 'ACTIVE' ? 'badge-success' : 'badge-danger'}`}>
                        {a.trangThai === 'ACTIVE' ? 'Active' : 'Locked'}
                      </span></td>
                      <td style={{ fontSize: 12 }}>{new Date(a.ngayTao).toLocaleDateString('en-US')}</td>
                      <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                        {a.lanDangNhapCuoi ? new Date(a.lanDangNhapCuoi).toLocaleDateString('en-US') : 'Never'}
                      </td>
                      <td>
                        <div style={{ display: 'flex', gap: 6 }}>
                          <button className="btn btn-secondary btn-sm" title={a.trangThai === 'ACTIVE' ? 'Lock account' : 'Unlock account'} onClick={() => toggleLock(a)}>
                            {a.trangThai === 'ACTIVE' ? <Lock size={13} /> : <Unlock size={13} />}
                          </button>
                          <button className="btn btn-danger btn-sm" onClick={() => delAcc(a.maTK)}><Trash2 size={13} /></button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* EMPLOYEE MODAL */}
      {empModal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setEmpModal(false)}>
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>{editEmp ? 'Edit Employee' : 'Add Employee'}</h2>
              <button onClick={() => setEmpModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {error && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{error}</div>}
              <div className="form-group">
                <label className="form-label">Employee ID *</label>
                <input className="input" disabled={!!editEmp} value={empForm.maNV ?? ''} onChange={e => setEmpForm(p => ({ ...p, maNV: e.target.value }))} placeholder="e.g. NV09" />
              </div>
              <div className="form-group">
                <label className="form-label">Full Name *</label>
                <input className="input" value={empForm.hoTen ?? ''} onChange={e => setEmpForm(p => ({ ...p, hoTen: e.target.value }))} />
              </div>
              <div className="form-group">
                <label className="form-label">Phone</label>
                <input className="input" value={empForm.soDienThoai ?? ''} onChange={e => setEmpForm(p => ({ ...p, soDienThoai: e.target.value }))} />
              </div>
              <div className="form-group">
                <label className="form-label">Role</label>
                <select className="select" value={empForm.chucVu ?? 'NHAN_VIEN'} onChange={e => setEmpForm(p => ({ ...p, chucVu: e.target.value as ChucVu }))}>
                  {(Object.keys(ROLE_LABELS) as ChucVu[]).map(r => <option key={r} value={r}>{ROLE_LABELS[r]}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Branch *</label>
                <select className="select" value={empForm.maCN ?? ''} onChange={e => setEmpForm(p => ({ ...p, maCN: e.target.value }))}>
                  <option value="">Select branch...</option>
                  {branches.map(b => <option key={b.maCN} value={b.maCN}>{b.tenCN}</option>)}
                </select>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setEmpModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={saveEmp} disabled={saving}>Save Employee</button>
            </div>
          </div>
        </div>
      )}

      {/* ACCOUNT MODAL */}
      {accModal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setAccModal(false)}>
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>Create Employee Account</h2>
              <button onClick={() => setAccModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {error && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{error}</div>}
              <div className="form-group">
                <label className="form-label">Account ID *</label>
                <input className="input" value={accForm.maTK} onChange={e => setAccForm(p => ({ ...p, maTK: e.target.value }))} placeholder="e.g. TK08" />
              </div>
              <div className="form-group">
                <label className="form-label">Employee *</label>
                <select className="select" value={accForm.maNV} onChange={e => setAccForm(p => ({ ...p, maNV: e.target.value }))}>
                  <option value="">Select employee...</option>
                  {employees.filter(e => !getEmpAccount(e.maNV)).map(e => (
                    <option key={e.maNV} value={e.maNV}>{e.hoTen} ({e.maNV})</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Username *</label>
                <input className="input" value={accForm.tenDangNhap} onChange={e => setAccForm(p => ({ ...p, tenDangNhap: e.target.value }))} placeholder="e.g. staff08" />
              </div>
              <div className="form-group">
                <label className="form-label">Password *</label>
                <input type="password" className="input" value={accForm.matKhauHash} onChange={e => setAccForm(p => ({ ...p, matKhauHash: e.target.value }))} />
              </div>
              <div className="form-group">
                <label className="form-label">Initial Status</label>
                <select className="select" value={accForm.trangThai} onChange={e => setAccForm(p => ({ ...p, trangThai: e.target.value }))}>
                  <option value="ACTIVE">Active</option>
                  <option value="LOCKED">Locked</option>
                </select>
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setAccModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={saveAcc} disabled={saving}>Create Account</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StaffPage;
