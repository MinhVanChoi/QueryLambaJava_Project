import React, { useEffect, useState, useMemo } from 'react';
import { Search, ShoppingBag, Filter, Star } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FoodItem, Category, Branch } from '../../types';
import { useCart } from '../../context/CartContext';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const getFoodEmoji = (name: string) => {
  const n = name.toLowerCase();
  if (n.includes('burger') || n.includes('ham')) return '🍔';
  if (n.includes('pizza')) return '🍕';
  if (n.includes('chicken') || n.includes('ga')) return '🍗';
  if (n.includes('noodle') || n.includes('pho') || n.includes('bun')) return '🍜';
  if (n.includes('rice') || n.includes('com')) return '🍚';
  if (n.includes('drink') || n.includes('juice') || n.includes('soda') || n.includes('nuoc')) return '🥤';
  if (n.includes('fry') || n.includes('khoai')) return '🍟';
  if (n.includes('cake') || n.includes('banh')) return '🍰';
  if (n.includes('salad') || n.includes('rau')) return '🥗';
  return '🍽️';
};

const MenuPage: React.FC = () => {
  const [foods, setFoods] = useState<FoodItem[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [filterCat, setFilterCat] = useState('');
  const [filterBranch, setFilterBranch] = useState('');
  const [sortBy, setSortBy] = useState('');
  const [addedId, setAddedId] = useState<string | null>(null);
  const { addItem, setBranch, maCN } = useCart();

  useEffect(() => {
    Promise.all([
      ApiClient.getFoodItems({ status: 'DANG_BAN' }),
      ApiClient.getCategories(),
      ApiClient.getBranches()
    ]).then(([f, c, b]) => {
      setFoods(f);
      setCategories(c);
      setBranches(b);
    }).finally(() => setLoading(false));
  }, []);

  const filtered = useMemo(() => {
    let result = foods.filter(f => {
      if (search && !f.tenMon.toLowerCase().includes(search.toLowerCase())) return false;
      if (filterCat && f.maDM !== filterCat) return false;
      return true;
    });
    if (sortBy === 'price-asc') result = [...result].sort((a, b) => a.giaBan - b.giaBan);
    if (sortBy === 'price-desc') result = [...result].sort((a, b) => b.giaBan - a.giaBan);
    return result;
  }, [foods, search, filterCat, sortBy]);

  const handleAdd = (item: FoodItem) => {
    if (filterBranch && maCN !== filterBranch) setBranch(filterBranch);
    addItem({ maMon: item.maMon, tenMon: item.tenMon, giaBan: item.giaBan });
    setAddedId(item.maMon);
    setTimeout(() => setAddedId(null), 1200);
  };

  const handleBranchChange = (id: string) => {
    setFilterBranch(id);
    if (id) setBranch(id);
  };

  return (
    <div className="menu-page">
      {/* Header */}
      <div style={{ background: 'linear-gradient(135deg, #0f172a, #1e293b)', padding: '48px 24px', textAlign: 'center', color: 'white' }}>
        <h1 style={{ fontSize: 36, fontWeight: 800, marginBottom: 8 }}>🍽️ Our Menu</h1>
        <p style={{ color: '#94a3b8', fontSize: 16 }}>Fresh ingredients, bold flavors — {foods.length} items to choose from</p>
      </div>

      <div className="section-container">
        {/* Filters */}
        <div style={{
          background: 'white', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)',
          padding: 20, marginBottom: 28, boxShadow: 'var(--shadow-sm)'
        }}>
          <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap', alignItems: 'center' }}>
            <div style={{ position: 'relative', flex: 1, minWidth: 200 }}>
              <Search size={15} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
              <input className="input" style={{ paddingLeft: 36 }} placeholder="Search food..." value={search} onChange={e => setSearch(e.target.value)} />
            </div>
            <select className="select" style={{ width: 180 }} value={filterCat} onChange={e => setFilterCat(e.target.value)}>
              <option value="">All Categories</option>
              {categories.map(c => <option key={c.maDM} value={c.maDM}>{c.tenDM}</option>)}
            </select>
            <select className="select" style={{ width: 180 }} value={filterBranch} onChange={e => handleBranchChange(e.target.value)}>
              <option value="">All Branches</option>
              {branches.map(b => <option key={b.maCN} value={b.maCN}>{b.tenCN}</option>)}
            </select>
            <select className="select" style={{ width: 160 }} value={sortBy} onChange={e => setSortBy(e.target.value)}>
              <option value="">Default Order</option>
              <option value="price-asc">Price: Low to High</option>
              <option value="price-desc">Price: High to Low</option>
            </select>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8, fontSize: 13, color: 'var(--text-muted)', flexShrink: 0 }}>
              <Filter size={14} />
              <strong style={{ color: 'var(--text-main)' }}>{filtered.length}</strong> items
            </div>
          </div>

          {/* Category chips */}
          <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap', marginTop: 14 }}>
            <button
              onClick={() => setFilterCat('')}
              style={{
                padding: '6px 14px', borderRadius: 9999, border: '1px solid', cursor: 'pointer', fontSize: 13, fontWeight: 600,
                background: filterCat === '' ? 'var(--primary-600)' : 'white',
                color: filterCat === '' ? 'white' : 'var(--text-muted)',
                borderColor: filterCat === '' ? 'var(--primary-600)' : 'var(--border-strong)',
              }}
            >
              All
            </button>
            {categories.map(c => (
              <button
                key={c.maDM}
                onClick={() => setFilterCat(c.maDM)}
                style={{
                  padding: '6px 14px', borderRadius: 9999, border: '1px solid', cursor: 'pointer', fontSize: 13, fontWeight: 600,
                  background: filterCat === c.maDM ? 'var(--primary-600)' : 'white',
                  color: filterCat === c.maDM ? 'white' : 'var(--text-muted)',
                  borderColor: filterCat === c.maDM ? 'var(--primary-600)' : 'var(--border-strong)',
                }}
              >
                {c.tenDM} ({foods.filter(f => f.maDM === c.maDM).length})
              </button>
            ))}
          </div>
        </div>

        {/* Food grid */}
        {loading ? (
          <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /><p style={{ marginTop: 12 }}>Loading menu...</p></div>
        ) : filtered.length === 0 ? (
          <div className="empty-state">
            <div style={{ fontSize: 64, marginBottom: 12 }}>🔍</div>
            <p style={{ fontWeight: 700, fontSize: 18 }}>No items found</p>
            <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>Try adjusting your search or filters</p>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: 20 }}>
            {filtered.map(item => (
              <div key={item.maMon} className="food-card">
                <div className="food-card-image">
                  <span style={{ fontSize: 72 }}>{getFoodEmoji(item.tenMon)}</span>
                </div>
                <div className="food-card-body">
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 6 }}>
                    <div className="food-card-name">{item.tenMon}</div>
                    <span style={{ fontSize: 10, background: '#f1f5f9', color: 'var(--text-muted)', padding: '2px 6px', borderRadius: 4, flexShrink: 0, marginLeft: 6 }}>
                      {categories.find(c => c.maDM === item.maDM)?.tenDM ?? item.maDM}
                    </span>
                  </div>
                  {item.moTa && <div className="food-card-desc">{item.moTa}</div>}
                  <div className="food-card-stars" style={{ marginBottom: 10 }}>
                    {[1,2,3,4,5].map(s => <Star key={s} size={12} fill={s <= 4 ? '#f59e0b' : 'none'} color="#f59e0b" />)}
                    <span style={{ fontSize: 11, color: 'var(--text-muted)', marginLeft: 4 }}>4.8</span>
                  </div>
                  <div className="food-card-footer">
                    <div className="food-card-price">{formatVND(item.giaBan)}</div>
                    <button
                      className={`btn btn-sm ${addedId === item.maMon ? 'btn-secondary' : 'btn-primary'}`}
                      onClick={() => handleAdd(item)}
                    >
                      {addedId === item.maMon ? '✓ Added!' : <><ShoppingBag size={13} /> Add</>}
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default MenuPage;
