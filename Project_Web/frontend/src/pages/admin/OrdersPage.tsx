import React, { useEffect, useState, useMemo } from 'react';
import { Plus, Eye, X, Trash2, ShoppingBag } from 'lucide-react';
import { ApiClient } from '../../api/client';
import {
  Order, OrderDetailResponse, Branch, Employee,
  Customer, FoodItem, Promotion, TrangThaiDonHang
} from '../../types';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';
const formatDate = (d: string) => new Date(d).toLocaleString('en-US');

const STATUS_COLORS: Record<string, string> = {
  CHO_XAC_NHAN: 'badge-info', DA_XAC_NHAN: 'badge-info',
  DANG_CHUAN_BI: 'badge-warning', SAN_SANG: 'badge-warning',
  DA_HOAN_THANH: 'badge-success', DA_HUY: 'badge-danger',
};
const STATUS_LABELS: Record<string, string> = {
  CHO_XAC_NHAN: 'Pending', DA_XAC_NHAN: 'Confirmed',
  DANG_CHUAN_BI: 'Preparing', SAN_SANG: 'Ready',
  DA_HOAN_THANH: 'Completed', DA_HUY: 'Cancelled',
};
const ALL_STATUSES: TrangThaiDonHang[] = [
  'CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DANG_CHUAN_BI',
  'SAN_SANG', 'DA_HOAN_THANH', 'DA_HUY'
];

const OrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState('');

  // Detail modal
  const [detail, setDetail] = useState<OrderDetailResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);
  const [newStatus, setNewStatus] = useState<TrangThaiDonHang>('CHO_XAC_NHAN');
  const [updatingStatus, setUpdatingStatus] = useState(false);

  // Create order modal
  const [createModal, setCreateModal] = useState(false);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [foodItems, setFoodItems] = useState<FoodItem[]>([]);
  const [promos, setPromos] = useState<Promotion[]>([]);
  const [creating, setCreating] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);

  // Order form state
  const [form, setForm] = useState({
    maCN: '', maNV: '', maKH: '', maKM: '',
    phuongThuc: 'TIEN_MAT', payImmediately: true,
    items: [{ maMon: '', soLuong: 1 }] as { maMon: string; soLuong: number }[]
  });

  const loadOrders = async () => {
    setLoading(true);
    try { setOrders(await ApiClient.getOrders()); }
    finally { setLoading(false); }
  };

  useEffect(() => { loadOrders(); }, []);

  const filteredOrders = useMemo(() =>
    filterStatus ? orders.filter(o => o.trangThai === filterStatus) : orders,
    [orders, filterStatus]
  );

  const openDetail = async (id: number) => {
    setDetailLoading(true);
    try {
      const d = await ApiClient.getOrderDetail(id);
      setDetail(d);
      setNewStatus(d.order.trangThai);
    } finally { setDetailLoading(false); }
  };

  const updateStatus = async () => {
    if (!detail) return;
    setUpdatingStatus(true);
    try {
      await ApiClient.updateOrderStatus(detail.order.maDH, newStatus);
      setDetail(null);
      loadOrders();
    } catch (e: any) { alert(e.response?.data?.message ?? e.message); }
    finally { setUpdatingStatus(false); }
  };

  const openCreate = async () => {
    setCreateModal(true);
    setCreateError(null);
    setForm({ maCN: '', maNV: '', maKH: '', maKM: '', phuongThuc: 'TIEN_MAT', payImmediately: true, items: [{ maMon: '', soLuong: 1 }] });
    const [b, e, c, f, p] = await Promise.all([
      ApiClient.getBranches(), ApiClient.getEmployees(),
      ApiClient.getCustomers(), ApiClient.getFoodItems({ status: 'DANG_BAN' }),
      ApiClient.getPromotions(true)
    ]);
    setBranches(b); setEmployees(e); setCustomers(c); setFoodItems(f); setPromos(p);
  };

  const submitCreate = async () => {
    if (!form.maCN || !form.maNV) { setCreateError('Branch and employee are required'); return; }
    const validItems = form.items.filter(i => i.maMon && i.soLuong > 0);
    if (!validItems.length) { setCreateError('Add at least one item'); return; }
    setCreating(true); setCreateError(null);
    try {
      await ApiClient.createOrder({
        maCN: form.maCN, maNV: form.maNV,
        maKH: form.maKH || undefined, maKM: form.maKM || undefined,
        items: validItems, phuongThuc: form.phuongThuc,
        payImmediately: form.payImmediately
      });
      setCreateModal(false);
      loadOrders();
    } catch (e: any) { setCreateError(e.response?.data?.message ?? e.message); }
    finally { setCreating(false); }
  };

  const branchEmployees = useMemo(() =>
    form.maCN ? employees.filter(e => e.maCN === form.maCN) : employees,
    [employees, form.maCN]
  );

  const orderSubtotal = form.items.reduce((s, i) => {
    const f = foodItems.find(fi => fi.maMon === i.maMon);
    return s + (f ? f.giaBan * i.soLuong : 0);
  }, 0);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 style={{ fontSize: 22, fontWeight: 800 }}>Orders & POS</h1>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16} /> New Order</button>
      </div>

      {/* Filter toolbar */}
      <div style={{ display: 'flex', gap: 10, marginBottom: 16, alignItems: 'center' }}>
        <select className="select" style={{ width: 180 }} value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
          <option value="">All Statuses ({orders.length})</option>
          {ALL_STATUSES.map(s => (
            <option key={s} value={s}>{STATUS_LABELS[s]} ({orders.filter(o => o.trangThai === s).length})</option>
          ))}
        </select>
        <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>
          Showing {filteredOrders.length} order{filteredOrders.length !== 1 ? 's' : ''}
        </span>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div>
        ) : filteredOrders.length === 0 ? (
          <div className="empty-state"><ShoppingBag size={40} style={{ opacity: 0.3, margin: '0 auto 8px' }} /><p>No orders found</p></div>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th><th>Date</th><th>Branch</th><th>Employee</th>
                <th>Customer</th><th>Status</th>
                <th>Subtotal</th><th>Discount</th><th>Total</th><th>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredOrders.map(o => (
                <tr key={o.maDH}>
                  <td><strong>#{o.maDH}</strong></td>
                  <td style={{ fontSize: 12, whiteSpace: 'nowrap' }}>{formatDate(o.ngayDat)}</td>
                  <td style={{ fontSize: 12 }}>{o.maCN}</td>
                  <td style={{ fontSize: 12 }}>{o.maNV}</td>
                  <td style={{ fontSize: 12 }}>{o.maKH ?? <span style={{ color: 'var(--text-muted)' }}>Guest</span>}</td>
                  <td><span className={`badge ${STATUS_COLORS[o.trangThai]}`}>{STATUS_LABELS[o.trangThai]}</span></td>
                  <td style={{ fontSize: 12 }}>{formatVND(o.tienHang)}</td>
                  <td style={{ fontSize: 12, color: 'var(--danger-text)' }}>-{formatVND(o.tienGiam)}</td>
                  <td style={{ fontWeight: 700, color: 'var(--primary-600)' }}>{formatVND(o.tongTien)}</td>
                  <td>
                    <button className="btn btn-secondary btn-sm" onClick={() => openDetail(o.maDH)} disabled={detailLoading}>
                      <Eye size={13} /> View
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* ORDER DETAIL MODAL */}
      {detail && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setDetail(null)}>
          <div className="modal-content" style={{ maxWidth: 780 }}>
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>Order #{detail.order.maDH} Details</h2>
              <button onClick={() => setDetail(null)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
              {/* Info */}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                {[
                  ['Branch', detail.tenCN], ['Employee', detail.tenNV],
                  ['Customer', detail.tenKH || 'Guest'], ['Promotion', detail.tenKM ?? 'None'],
                  ['Date', formatDate(detail.order.ngayDat)],
                  ['Status', STATUS_LABELS[detail.order.trangThai]],
                ].map(([k, v]) => (
                  <div key={k} style={{ background: 'var(--bg-main)', padding: '8px 12px', borderRadius: 6 }}>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)', fontWeight: 600 }}>{k}</div>
                    <div style={{ fontWeight: 600, marginTop: 2 }}>{v}</div>
                  </div>
                ))}
              </div>

              {/* Items */}
              <div>
                <div style={{ fontWeight: 700, marginBottom: 8 }}>Order Items</div>
                <table className="data-table" style={{ border: '1px solid var(--border-subtle)', borderRadius: 8 }}>
                  <thead>
                    <tr><th>Food Item</th><th>Qty</th><th>Unit Price</th><th>Subtotal</th></tr>
                  </thead>
                  <tbody>
                    {detail.items.map(i => (
                      <tr key={i.maCTDH}>
                        <td>{i.tenMon ?? i.maMon}</td>
                        <td>{i.soLuong}</td>
                        <td>{formatVND(i.donGia)}</td>
                        <td><strong>{formatVND(i.thanhTien)}</strong></td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>

              {/* Totals */}
              <div style={{ background: 'var(--bg-main)', padding: 16, borderRadius: 8 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6 }}>
                  <span>Subtotal</span><span>{formatVND(detail.order.tienHang)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6, color: 'var(--danger-text)' }}>
                  <span>Discount</span><span>-{formatVND(detail.order.tienGiam)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 800, fontSize: 16, borderTop: '1px solid var(--border-subtle)', paddingTop: 8 }}>
                  <span>Total</span><span style={{ color: 'var(--primary-600)' }}>{formatVND(detail.order.tongTien)}</span>
                </div>
              </div>

              {/* Payments */}
              {detail.payments.length > 0 && (
                <div>
                  <div style={{ fontWeight: 700, marginBottom: 8 }}>Payment Records</div>
                  <table className="data-table" style={{ border: '1px solid var(--border-subtle)', borderRadius: 8 }}>
                    <thead><tr><th>ID</th><th>Method</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead>
                    <tbody>
                      {detail.payments.map(p => (
                        <tr key={p.maTT}>
                          <td style={{ fontSize: 12 }}>{p.maTT}</td>
                          <td style={{ fontSize: 12 }}>{p.phuongThuc}</td>
                          <td>{formatVND(p.soTien)}</td>
                          <td><span className={`badge ${p.trangThai === 'DA_THANH_TOAN' ? 'badge-success' : 'badge-warning'}`}>{p.trangThai}</span></td>
                          <td style={{ fontSize: 12 }}>{p.ngayThanhToan ? formatDate(p.ngayThanhToan) : '—'}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}

              {/* Status update */}
              <div style={{ background: 'var(--info-bg)', border: '1px solid var(--info-border)', padding: 16, borderRadius: 8 }}>
                <div style={{ fontWeight: 700, marginBottom: 10, color: 'var(--info-text)' }}>Update Order Status</div>
                <div style={{ display: 'flex', gap: 10, alignItems: 'center' }}>
                  <select className="select" style={{ flex: 1 }} value={newStatus} onChange={e => setNewStatus(e.target.value as TrangThaiDonHang)}>
                    {ALL_STATUSES.map(s => <option key={s} value={s}>{STATUS_LABELS[s]}</option>)}
                  </select>
                  <button className="btn btn-primary" onClick={updateStatus} disabled={updatingStatus}>
                    {updatingStatus ? 'Updating...' : 'Update Status'}
                  </button>
                </div>
                <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 8 }}>
                  ⚠️ Backend validates allowed transitions. Invalid transitions will be rejected.
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* CREATE ORDER MODAL */}
      {createModal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setCreateModal(false)}>
          <div className="modal-content" style={{ maxWidth: 720 }}>
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>Create New Order (POS)</h2>
              <button onClick={() => setCreateModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {createError && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{createError}</div>}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label className="form-label">Branch *</label>
                  <select className="select" value={form.maCN} onChange={e => setForm(p => ({ ...p, maCN: e.target.value, maNV: '' }))}>
                    <option value="">Select branch...</option>
                    {branches.map(b => <option key={b.maCN} value={b.maCN}>{b.tenCN}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Employee *</label>
                  <select className="select" value={form.maNV} onChange={e => setForm(p => ({ ...p, maNV: e.target.value }))}>
                    <option value="">Select employee...</option>
                    {branchEmployees.map(e => <option key={e.maNV} value={e.maNV}>{e.hoTen} ({e.maNV})</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Customer (optional)</label>
                  <select className="select" value={form.maKH} onChange={e => setForm(p => ({ ...p, maKH: e.target.value }))}>
                    <option value="">Guest (no account)</option>
                    {customers.map(c => <option key={c.maKH} value={c.maKH}>{c.hoTen} ({c.maKH})</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Promotion (optional)</label>
                  <select className="select" value={form.maKM} onChange={e => setForm(p => ({ ...p, maKM: e.target.value }))}>
                    <option value="">No promotion</option>
                    {promos.map(p => <option key={p.maKM} value={p.maKM}>{p.tenKM} ({p.phanTramGiam}% off)</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Payment Method</label>
                  <select className="select" value={form.phuongThuc} onChange={e => setForm(p => ({ ...p, phuongThuc: e.target.value }))}>
                    <option value="TIEN_MAT">Cash</option>
                    <option value="CHUYEN_KHOAN">Bank Transfer</option>
                    <option value="VI_DIEN_TU">E-Wallet</option>
                    <option value="THE">Card</option>
                  </select>
                </div>
                <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: 8, paddingTop: 24 }}>
                  <input type="checkbox" id="payNow" checked={form.payImmediately} onChange={e => setForm(p => ({ ...p, payImmediately: e.target.checked }))} />
                  <label htmlFor="payNow" style={{ fontWeight: 600, fontSize: 13 }}>Pay Immediately</label>
                </div>
              </div>

              {/* Items */}
              <div style={{ marginTop: 8 }}>
                <div style={{ fontWeight: 700, marginBottom: 10 }}>Order Items</div>
                {form.items.map((item, idx) => (
                  <div key={idx} style={{ display: 'flex', gap: 8, marginBottom: 8, alignItems: 'center' }}>
                    <select className="select" style={{ flex: 1 }} value={item.maMon} onChange={e => setForm(p => ({
                      ...p, items: p.items.map((it, i) => i === idx ? { ...it, maMon: e.target.value } : it)
                    }))}>
                      <option value="">Select food item...</option>
                      {foodItems.map(f => <option key={f.maMon} value={f.maMon}>{f.tenMon} — {formatVND(f.giaBan)}</option>)}
                    </select>
                    <input type="number" className="input" style={{ width: 80 }} min={1} value={item.soLuong}
                      onChange={e => setForm(p => ({
                        ...p, items: p.items.map((it, i) => i === idx ? { ...it, soLuong: Number(e.target.value) } : it)
                      }))} />
                    <button className="btn btn-danger btn-sm" onClick={() => setForm(p => ({ ...p, items: p.items.filter((_, i) => i !== idx) }))}>
                      <Trash2 size={13} />
                    </button>
                  </div>
                ))}
                <button className="btn btn-secondary btn-sm" onClick={() => setForm(p => ({ ...p, items: [...p.items, { maMon: '', soLuong: 1 }] }))}>
                  <Plus size={13} /> Add Item
                </button>
              </div>

              {/* Subtotal preview */}
              {orderSubtotal > 0 && (
                <div style={{ marginTop: 12, background: 'var(--bg-main)', padding: 12, borderRadius: 8, fontWeight: 700 }}>
                  Estimated Subtotal: <span style={{ color: 'var(--primary-600)' }}>{formatVND(orderSubtotal)}</span>
                  <span style={{ fontSize: 12, color: 'var(--text-muted)', fontWeight: 400, marginLeft: 8 }}>(backend recalculates final total)</span>
                </div>
              )}
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setCreateModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={submitCreate} disabled={creating}>
                {creating ? 'Creating...' : 'Create Order'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OrdersPage;
