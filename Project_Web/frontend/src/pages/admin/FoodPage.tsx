import React, { useEffect, useState, useMemo } from 'react';
import { Plus, Pencil, Trash2, Search, UtensilsCrossed, List, X } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FoodItem, Category } from '../../types';
import { getFoodImage } from '../../utils/foodImages';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const FoodPage: React.FC = () => {
  const [tab, setTab] = useState<'food' | 'categories'>('food');

  // --- Food state ---
  const [foods, setFoods] = useState<FoodItem[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [filterCat, setFilterCat] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [sortBy, setSortBy] = useState('');

  // --- Food modal ---
  const [foodModal, setFoodModal] = useState(false);
  const [editFood, setEditFood] = useState<FoodItem | null>(null);
  const [foodForm, setFoodForm] = useState<Partial<FoodItem>>({});
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // --- Category modal ---
  const [catModal, setCatModal] = useState(false);
  const [editCat, setEditCat] = useState<Category | null>(null);
  const [catForm, setCatForm] = useState<Partial<Category>>({});

  const loadAll = async () => {
    setLoading(true);
    try {
      const [f, c] = await Promise.all([
        ApiClient.getFoodItems({ categoryId: filterCat || undefined, status: filterStatus || undefined, sortBy: sortBy || undefined }),
        ApiClient.getCategories(),
      ]);
      setFoods(f);
      setCategories(c);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadAll(); }, [filterCat, filterStatus, sortBy]);

  const filteredFoods = useMemo(() =>
    foods.filter(f => f.tenMon.toLowerCase().includes(search.toLowerCase())),
    [foods, search]
  );

  // Food CRUD
  const openAddFood = () => {
    setEditFood(null);
    setFoodForm({ trangThai: 'DANG_BAN', giaBan: 0 });
    setFoodModal(true);
  };
  const openEditFood = (f: FoodItem) => {
    setEditFood(f);
    setFoodForm({ ...f });
    setFoodModal(true);
  };
  const saveFood = async () => {
    if (!foodForm.maMon || !foodForm.tenMon || !foodForm.maDM) {
      setError('Food code, name, and category are required'); return;
    }
    setSaving(true); setError(null);
    try {
      if (editFood) await ApiClient.updateFoodItem(editFood.maMon, foodForm);
      else await ApiClient.createFoodItem(foodForm);
      setFoodModal(false);
      loadAll();
    } catch (e: any) {
      setError(e.response?.data?.message ?? e.message);
    } finally {
      setSaving(false);
    }
  };
  const deleteFood = async (id: string) => {
    if (!window.confirm(`Delete food item ${id}? (Soft delete — keeps historical orders)`)) return;
    try {
      await ApiClient.deleteFoodItem(id);
      loadAll();
    } catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };

  // Category CRUD
  const openAddCat = () => { setEditCat(null); setCatForm({}); setCatModal(true); };
  const openEditCat = (c: Category) => { setEditCat(c); setCatForm({ ...c }); setCatModal(true); };
  const saveCat = async () => {
    if (!catForm.maDM || !catForm.tenDM) { setError('Category code and name required'); return; }
    setSaving(true); setError(null);
    try {
      if (editCat) await ApiClient.updateCategory(editCat.maDM, catForm as Category);
      else await ApiClient.createCategory(catForm as Category);
      setCatModal(false);
      loadAll();
    } catch (e: any) { setError(e.response?.data?.message ?? e.message); }
    finally { setSaving(false); }
  };
  const deleteCat = async (id: string) => {
    if (!window.confirm(`Delete category ${id}?`)) return;
    try { await ApiClient.deleteCategory(id); loadAll(); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
  };

  return (
    <div>
      <h1 style={{ fontSize: 22, fontWeight: 800, marginBottom: 20 }}>Food & Menu Management</h1>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: 4, marginBottom: 20, borderBottom: '1px solid var(--border-subtle)' }}>
        {(['food', 'categories'] as const).map(t => (
          <button key={t} onClick={() => setTab(t)} style={{
            padding: '10px 20px', border: 'none', background: 'none', cursor: 'pointer',
            fontWeight: tab === t ? 700 : 500,
            color: tab === t ? 'var(--primary-600)' : 'var(--text-muted)',
            borderBottom: tab === t ? '2px solid var(--primary-600)' : '2px solid transparent',
            fontSize: 14, display: 'flex', alignItems: 'center', gap: 8
          }}>
            {t === 'food' ? <><UtensilsCrossed size={16} /> Food Items ({foods.length})</> : <><List size={16} /> Categories ({categories.length})</>}
          </button>
        ))}
      </div>

      {/* FOOD ITEMS TAB */}
      {tab === 'food' && (
        <>
          {/* Toolbar */}
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap', marginBottom: 16, alignItems: 'center' }}>
            <div style={{ position: 'relative', flex: 1, minWidth: 200 }}>
              <Search size={15} style={{ position: 'absolute', left: 10, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input className="input" style={{ paddingLeft: 32 }} placeholder="Search food name..." value={search} onChange={e => setSearch(e.target.value)} />
            </div>
            <select className="select" style={{ width: 160 }} value={filterCat} onChange={e => setFilterCat(e.target.value)}>
              <option value="">All Categories</option>
              {categories.map(c => <option key={c.maDM} value={c.maDM}>{c.tenDM}</option>)}
            </select>
            <select className="select" style={{ width: 140 }} value={filterStatus} onChange={e => setFilterStatus(e.target.value)}>
              <option value="">All Status</option>
              <option value="DANG_BAN">Active</option>
              <option value="NGUNG_BAN">Inactive</option>
            </select>
            <select className="select" style={{ width: 150 }} value={sortBy} onChange={e => setSortBy(e.target.value)}>
              <option value="">Default Sort</option>
              <option value="price-asc">Price ↑</option>
              <option value="price-desc">Price ↓</option>
            </select>
            <button className="btn btn-primary" onClick={openAddFood}><Plus size={16} /> Add Food Item</button>
          </div>

          <div className="table-container">
            {loading ? (
              <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div>
            ) : filteredFoods.length === 0 ? (
              <div className="empty-state"><UtensilsCrossed size={40} style={{ opacity: 0.3, margin: '0 auto 8px' }} /><p>No food items found</p></div>
            ) : (
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Code</th><th>Name</th><th>Category</th>
                    <th>Price</th><th>Status</th><th>Description</th><th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {filteredFoods.map(f => (
                    <tr key={f.maMon}>
                      <td><code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{f.maMon}</code></td>
                      <td>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                          <img
                            src={getFoodImage(f.maMon, f.tenMon)}
                            alt={f.tenMon}
                            style={{ width: 36, height: 36, borderRadius: 6, objectFit: 'cover' }}
                          />
                          <strong>{f.tenMon}</strong>
                        </div>
                      </td>
                      <td>{categories.find(c => c.maDM === f.maDM)?.tenDM ?? f.maDM}</td>
                      <td style={{ fontWeight: 700, color: 'var(--primary-600)' }}>{formatVND(f.giaBan)}</td>
                      <td>
                        <span className={`badge ${f.trangThai === 'DANG_BAN' ? 'badge-success' : 'badge-danger'}`}>
                          {f.trangThai === 'DANG_BAN' ? 'Active' : 'Inactive'}
                        </span>
                      </td>
                      <td style={{ fontSize: 12, color: 'var(--text-muted)', maxWidth: 180 }}>{f.moTa ?? '—'}</td>
                      <td>
                        <div style={{ display: 'flex', gap: 6 }}>
                          <button className="btn btn-secondary btn-sm" onClick={() => openEditFood(f)}><Pencil size={13} /></button>
                          <button className="btn btn-danger btn-sm" onClick={() => deleteFood(f.maMon)}><Trash2 size={13} /></button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </>
      )}

      {/* CATEGORIES TAB */}
      {tab === 'categories' && (
        <>
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 16 }}>
            <button className="btn btn-primary" onClick={openAddCat}><Plus size={16} /> Add Category</button>
          </div>
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr><th>Code</th><th>Name</th><th>Description</th><th>Items</th><th>Actions</th></tr>
              </thead>
              <tbody>
                {categories.map(c => (
                  <tr key={c.maDM}>
                    <td><code style={{ fontSize: 12, background: '#f1f5f9', padding: '2px 6px', borderRadius: 4 }}>{c.maDM}</code></td>
                    <td><strong>{c.tenDM}</strong></td>
                    <td style={{ fontSize: 12, color: 'var(--text-muted)' }}>{c.moTa ?? '—'}</td>
                    <td>{foods.filter(f => f.maDM === c.maDM).length}</td>
                    <td>
                      <div style={{ display: 'flex', gap: 6 }}>
                        <button className="btn btn-secondary btn-sm" onClick={() => openEditCat(c)}><Pencil size={13} /></button>
                        <button className="btn btn-danger btn-sm" onClick={() => deleteCat(c.maDM)}><Trash2 size={13} /></button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </>
      )}

      {/* FOOD MODAL */}
      {foodModal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setFoodModal(false)}>
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>{editFood ? 'Edit Food Item' : 'Add Food Item'}</h2>
              <button onClick={() => setFoodModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {error && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{error}</div>}
              <div className="form-group">
                <label className="form-label">Food Code *</label>
                <input className="input" disabled={!!editFood} value={foodForm.maMon ?? ''} onChange={e => setFoodForm(p => ({ ...p, maMon: e.target.value }))} placeholder="e.g. M019" />
              </div>
              <div className="form-group">
                <label className="form-label">Food Name *</label>
                <input className="input" value={foodForm.tenMon ?? ''} onChange={e => setFoodForm(p => ({ ...p, tenMon: e.target.value }))} placeholder="e.g. Crispy Chicken" />
              </div>
              <div className="form-group">
                <label className="form-label">Category *</label>
                <select className="select" value={foodForm.maDM ?? ''} onChange={e => setFoodForm(p => ({ ...p, maDM: e.target.value }))}>
                  <option value="">Select category...</option>
                  {categories.map(c => <option key={c.maDM} value={c.maDM}>{c.tenDM}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Price (₫) *</label>
                <input type="number" className="input" value={foodForm.giaBan ?? 0} onChange={e => setFoodForm(p => ({ ...p, giaBan: Number(e.target.value) }))} />
              </div>
              <div className="form-group">
                <label className="form-label">Status</label>
                <select className="select" value={foodForm.trangThai ?? 'DANG_BAN'} onChange={e => setFoodForm(p => ({ ...p, trangThai: e.target.value as any }))}>
                  <option value="DANG_BAN">Active (For Sale)</option>
                  <option value="NGUNG_BAN">Inactive (Discontinued)</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="textarea" rows={3} value={foodForm.moTa ?? ''} onChange={e => setFoodForm(p => ({ ...p, moTa: e.target.value }))} placeholder="Short description..." />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setFoodModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={saveFood} disabled={saving}>
                {saving ? <><span className="spinner" style={{ width: 14, height: 14 }} /> Saving...</> : 'Save Food Item'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* CATEGORY MODAL */}
      {catModal && (
        <div className="modal-overlay" onClick={e => e.target === e.currentTarget && setCatModal(false)}>
          <div className="modal-content">
            <div className="modal-header">
              <h2 style={{ fontSize: 16, fontWeight: 700 }}>{editCat ? 'Edit Category' : 'Add Category'}</h2>
              <button onClick={() => setCatModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}><X size={20} /></button>
            </div>
            <div className="modal-body">
              {error && <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '8px 12px', borderRadius: 6, marginBottom: 12, fontSize: 13 }}>{error}</div>}
              <div className="form-group">
                <label className="form-label">Category Code *</label>
                <input className="input" disabled={!!editCat} value={catForm.maDM ?? ''} onChange={e => setCatForm(p => ({ ...p, maDM: e.target.value }))} placeholder="e.g. DM05" />
              </div>
              <div className="form-group">
                <label className="form-label">Category Name *</label>
                <input className="input" value={catForm.tenDM ?? ''} onChange={e => setCatForm(p => ({ ...p, tenDM: e.target.value }))} placeholder="e.g. Desserts" />
              </div>
              <div className="form-group">
                <label className="form-label">Description</label>
                <textarea className="textarea" rows={2} value={catForm.moTa ?? ''} onChange={e => setCatForm(p => ({ ...p, moTa: e.target.value }))} />
              </div>
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={() => setCatModal(false)}>Cancel</button>
              <button className="btn btn-primary" onClick={saveCat} disabled={saving}>Save Category</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default FoodPage;
