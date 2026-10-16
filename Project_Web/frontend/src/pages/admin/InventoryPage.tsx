import React, { useEffect, useState, useMemo } from 'react';
import { PackageOpen, AlertTriangle, Edit, X } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { Inventory, Branch, FoodItem } from '../../types';

const InventoryPage: React.FC = () => {
  const [inventory, setInventory] = useState<Inventory[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [foodItems, setFoodItems] = useState<FoodItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterBranch, setFilterBranch] = useState('');
  const [lowStockOnly, setLowStockOnly] = useState(false);
  const [threshold, setThreshold] = useState(10);
  const [modal, setModal] = useState(false);
  const [editItem, setEditItem] = useState<Inventory | null>(null);
  const [newQty, setNewQty] = useState(0);
  const [saving, setSaving] = useState(false);

  const loadAll = async () => {
    setLoading(true);
    try {
      const [inv, b, f] = await Promise.all([
        ApiClient.getInventory({ branchId: filterBranch || undefined, lowStock: lowStockOnly || undefined, threshold: lowStockOnly ? threshold : undefined }),
        ApiClient.getBranches(),
        ApiClient.getFoodItems()
      ]);
      setInventory(inv); setBranches(b); setFoodItems(f);
    } finally { setLoading(false); }
  };

  useEffect(() => { loadAll(); }, [filterBranch, lowStockOnly, threshold]);

  const lowStockItems = useMemo(() => inventory.filter(i => i.soLuongTon < threshold), [inventory, threshold]);
  const outOfStock = useMemo(() => inventory.filter(i => i.soLuongTon === 0), [inventory]);

  const getBranchName = (id: string) => branches.find(b => b.maCN === id)?.tenCN ?? id;
  const getFoodName = (id: string) => foodItems.find(f => f.maMon === id)?.tenMon ?? id;

  const openEdit = (inv: Inventory) => { setEditItem(inv); setNewQty(inv.soLuongTon); setModal(true); };
  const saveStock = async () => {
    if (!editItem || newQty < 0) return;
    setSaving(true);
    try {
      await ApiClient.updateStock(editItem.maTon, newQty);
      setModal(false); loadAll();
    } catch (e: any) { alert(e.response?.data?.message ?? e.message); }
    finally { setSaving(false); }
  };

  const stockBadge = (qty: number) =>
    qty === 0 ? 'badge-danger' : qty < threshold ? 'badge-warning' : 'badge-success';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h1 style={{ fontSize: 22, fontWeight: 800 }}>Inventory Management</h1>
        <button className="btn btn-secondary btn-sm" onClick={loadAll}><PackageOpen size={14} /> Refresh</button>
      </div>

      {/* Summary stats */}
      <div className="stat-grid" style={{ marginBottom: 20 }}>
        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ backgroundColor: '#3b82f6' }}>
            <PackageOpen size={22} color="white" />
          </div>
          <div>
            <div className="stat-value">{inventory.length}</div>
            <div className="stat-label">Total Inventory Entries</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ backgroundColor: '#f59e0b' }}>
            <AlertTriangle size={22} color="white" />
          </div>
          <div>
            <div className="stat-value">{lowStockItems.length}</div>
            <div className="stat-label">Low Stock (under {threshold})</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ backgroundColor: '#ef4444' }}>
            <AlertTriangle size={22} color="white" />
          </div>
          <div>
            <div className="stat-value">{outOfStock.length}</div>
            <div className="stat-label">Out of Stock</div>
          </div>
        </div>
      </div>

      {/* Low stock alert */}
      {lowStockItems.length > 0 && (
        <div style={{
          background: 'var(--warning-bg)', border: '1px solid var(--warning-border)',
          borderRadius: 'var(--radius-md)', padding: 16, marginBottom: 20
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontWeight: 700, color: 'var(--warning-text)', marginBottom: 8 }}>
            <AlertTriangle size={18} /> Low Stock Alert ({lowStockItems.length} items)
          </div>
          <div style={{ fontSize: 13, color: 'var(--warning-text)' }}>
            {lowStockItems.map(i => (
              <span key={i.maTon} style={{ display: 'inline-block', marginRight: 12, marginBottom: 4 }}>
                <strong>{getFoodName(i.maMon)}</strong> @ {getBranchName(i.maCN)}: <strong>{i.soLuongTon}</strong> units
              </span>
            ))}
          </div>
        </div>
      )}

      {/* Toolbar */}
      <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap', marginBottom: 16, alignItems: 'center' }}>
        <select className="select" style={{ width: 200 }} value={filterBranch} onChange={e => setFilterBranch(e.target.value)}>
          <option value="">All Branches</option>
          {branches.map(b => <option key={b.maCN} value={b.maCN}>{b.tenCN}</option>)}
        </select>
        <label style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, fontWeight: 600, cursor: 'pointer' }}>
          <input type="checkbox" checked={lowStockOnly} onChange={e => setLowStockOnly(e.target.checked)} />
          Low Stock Only
        </label>
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 13 }}>
          <span style={{ color: 'var(--text-muted)' }}>Threshold:</span>
          <input type="number" className="input" style={{ width: 70 }} value={threshold}
            onChange={e => setThreshold(Number(e.target.value))} min={0} />
        </div>
        <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>Showing {inventory.length} entries</span>
      </div>

      <div className="table-container">
        {loading ? (
          <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div>
        ) : (
          <table className="data-table">
            <thead>
              <tr><th>ID</th><th>Branch</th><th>Food Item</th><th>Stock</th><th>Last Updated</th><th>Action</th></tr>
            </thead>
            <tbody>
              {inventory.map(inv => (
                <tr key={inv.maTon} style={inv.soLuongTon === 0 ? { background: '#fff5f5' } : undefined}>
                  <td><code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{inv.maTon}</code></td>
                  <td style={{ fontSize: 13 }}>{getBranchName(inv.maCN)}</td>
                  <td><strong>{getFoodName(inv.maMon)}</strong> <span style={{ fontSize: 11, color: 'var(--text-muted)' }}>({inv.maMon})</span></td>
                  <td>
                    <span className={`badge ${stockBadge(inv.soLuongTon)}`} style={{ fontSize: 13, padding: '5px 10px' }}>
                      {inv.soLuongTon} {inv.soLuongTon === 0 ? '(OUT)' : ''}
                    </span>
                  </td>
                  <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>
                    {new Date(inv.ngayCapNhat).toLocaleString('en-US')}
                  </td>
                  <td>
                    <button className="btn btn-secondary btn-sm" onClick={() => openEdit(inv)}>
                      <Edit size={13} /> Adjust
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* EDIT STOCK MODAL */}
      {modal && editItem && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setModal(false)}>
          <div className="modal-content" style={{ maxWidth: 420 }}>
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>Adjust Stock</h2>
              <button onClick={() => setModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              <div style={{ background: 'var(--bg-main)', padding: 12, borderRadius: 8, marginBottom: 16, fontSize: 13 }}>
                <div><strong>Inventory ID:</strong> {editItem.maTon}</div>
                <div><strong>Branch:</strong> {getBranchName(editItem.maCN)}</div>
                <div><strong>Food Item:</strong> {getFoodName(editItem.maMon)}</div>
                <div><strong>Current Stock:</strong> <span style={{ fontWeight: 800 }}>{editItem.soLuongTon} units</span></div>
              </div>
              <div className="form-group">
                <label className="form-label">New Quantity *</label>
                <input type="number" className="input" min={0} value={newQty} onChange={e => setNewQty(Number(e.target.value))} />
                {newQty < 0 && <span style={{ fontSize: 12, color: 'var(--danger-text)' }}>Cannot be negative</span>}
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={saveStock} disabled={saving || newQty < 0}>
                {saving ? 'Saving...' : 'Update Stock'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default InventoryPage;
