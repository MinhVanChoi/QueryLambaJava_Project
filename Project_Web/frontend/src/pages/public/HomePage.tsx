import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingBag, Star, ArrowRight, UtensilsCrossed, Tag } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { FoodItem, Promotion, Branch } from '../../types';
import { useCart } from '../../context/CartContext';
import { getFoodImage } from '../../utils/foodImages';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const HomePage: React.FC = () => {
  const [featuredItems, setFeaturedItems] = useState<FoodItem[]>([]);
  const [promos, setPromos] = useState<Promotion[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(true);
  const { addItem } = useCart();
  const [addedId, setAddedId] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([
      ApiClient.getFoodItems({ status: 'DANG_BAN' }),
      ApiClient.getPromotions(true),
      ApiClient.getBranches()
    ]).then(([foods, p, b]) => {
      // Show first 6 available items as featured
      setFeaturedItems(foods.slice(0, 6));
      setPromos(p);
      setBranches(b);
    }).finally(() => setLoading(false));
  }, []);

  const handleAdd = (item: FoodItem) => {
    addItem({ maMon: item.maMon, tenMon: item.tenMon, giaBan: item.giaBan });
    setAddedId(item.maMon);
    setTimeout(() => setAddedId(null), 1500);
  };

  return (
    <div className="home-page">
      {/* ===== HERO SECTION ===== */}
      <section className="hero-section">
        <div className="hero-bg" />
        <div className="hero-content">
          <div className="hero-badge">🔥 Order Online & Pick Up Fresh</div>
          <h1 className="hero-title">
            Great Food,<br />
            <span className="hero-title-accent">Great Taste</span>
          </h1>
          <p className="hero-subtitle">
            Vietnam's favorite fast food chain. Fresh ingredients, bold flavors, served fast — across {branches.length} locations citywide.
          </p>
          <div className="hero-actions">
            <Link to="/menu" className="btn btn-primary" style={{ fontSize: 16, padding: '14px 32px' }}>
              <UtensilsCrossed size={20} /> Order Now
            </Link>
            <Link to="/menu" className="btn btn-secondary" style={{ fontSize: 16, padding: '14px 32px' }}>
              View Full Menu <ArrowRight size={18} />
            </Link>
          </div>
          <div className="hero-stats">
            <div className="hero-stat"><strong>4+</strong><span>Branches</span></div>
            <div className="hero-stat-divider" />
            <div className="hero-stat"><strong>18+</strong><span>Menu Items</span></div>
            <div className="hero-stat-divider" />
            <div className="hero-stat"><strong>⭐ 4.8</strong><span>Rating</span></div>
          </div>
        </div>
        <div className="hero-visual">
          <div className="hero-food-circle">
            <span style={{ fontSize: 100 }}>🍔</span>
          </div>
        </div>
      </section>

      {/* ===== PROMOTIONS BANNER ===== */}
      {promos.length > 0 && (
        <section className="section-container">
          <div className="section-header">
            <div>
              <h2 className="section-title">🎁 Current Promotions</h2>
              <p className="section-subtitle">Limited time offers — order now to save</p>
            </div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 16 }}>
            {promos.map(p => (
              <div key={p.maKM} className="promo-card">
                <div className="promo-badge"><Tag size={14} /> {p.phanTramGiam}% OFF</div>
                <div className="promo-name">{p.tenKM}</div>
                <div className="promo-detail">Min. order: <strong>{formatVND(p.donToiThieu)}</strong></div>
                {p.giamToiDa && <div className="promo-detail">Max discount: <strong>{formatVND(p.giamToiDa)}</strong></div>}
                <div className="promo-dates">
                  Valid: {new Date(p.ngayBatDau).toLocaleDateString('en-US')} – {new Date(p.ngayKetThuc).toLocaleDateString('en-US')}
                </div>
                <Link to="/menu" className="btn btn-primary btn-sm" style={{ marginTop: 12 }}>
                  Order Now <ArrowRight size={14} />
                </Link>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* ===== FEATURED MENU ===== */}
      <section className="section-container section-alt">
        <div className="section-header">
          <div>
            <h2 className="section-title">⭐ Featured Items</h2>
            <p className="section-subtitle">Our most popular dishes, loved by thousands</p>
          </div>
          <Link to="/menu" className="btn btn-secondary">
            View All <ArrowRight size={16} />
          </Link>
        </div>
        {loading ? (
          <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /></div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: 20 }}>
            {featuredItems.map(item => (
              <div key={item.maMon} className="food-card">
                <div className="food-card-image" style={{ overflow: 'hidden', height: 160 }}>
                  <img
                    src={getFoodImage(item.maMon, item.tenMon)}
                    alt={item.tenMon}
                    loading="lazy"
                    style={{ width: '100%', height: '100%', objectFit: 'cover', transition: 'transform 0.3s ease' }}
                  />
                  <div className="food-card-overlay" />
                </div>
                <div className="food-card-body">
                  <div className="food-card-name">{item.tenMon}</div>
                  {item.moTa && <div className="food-card-desc">{item.moTa}</div>}
                  <div className="food-card-footer">
                    <div className="food-card-price">{formatVND(item.giaBan)}</div>
                    <button
                      className={`btn btn-sm ${addedId === item.maMon ? 'btn-secondary' : 'btn-primary'}`}
                      onClick={() => handleAdd(item)}
                    >
                      {addedId === item.maMon ? '✓ Added' : <><ShoppingBag size={13} /> Add</>}
                    </button>
                  </div>
                  <div className="food-card-stars">
                    {[1,2,3,4,5].map(s => <Star key={s} size={12} fill={s <= 4 ? '#f59e0b' : 'none'} color="#f59e0b" />)}
                    <span style={{ fontSize: 11, color: 'var(--text-muted)', marginLeft: 4 }}>4.8</span>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      {/* ===== BRANCHES ===== */}
      <section className="section-container">
        <div className="section-header">
          <div>
            <h2 className="section-title">📍 Our Locations</h2>
            <p className="section-subtitle">Find a branch near you</p>
          </div>
        </div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))', gap: 16 }}>
          {branches.map(b => (
            <div key={b.maCN} className="branch-card">
              <div style={{ fontSize: 32, marginBottom: 8 }}>🏪</div>
              <div style={{ fontWeight: 800, fontSize: 16 }}>{b.tenCN}</div>
              <div style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>{b.diaChi}</div>
              <div style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>📞 {b.soDienThoai}</div>
              <div style={{ marginTop: 12 }}>
                <span className="badge badge-success">🟢 Open Now</span>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ===== WHY CHOOSE US ===== */}
      <section className="section-container section-alt">
        <h2 className="section-title" style={{ textAlign: 'center', marginBottom: 32 }}>Why Choose FastFood Chain?</h2>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 24 }}>
          {[
            { emoji: '⚡', title: 'Lightning Fast', desc: 'Order ready in under 10 minutes' },
            { emoji: '🥩', title: 'Fresh Ingredients', desc: 'Daily sourced, never frozen' },
            { emoji: '💰', title: 'Best Value', desc: 'Quality meals at affordable prices' },
            { emoji: '📱', title: 'Easy Ordering', desc: 'Order online, skip the queue' },
          ].map(({ emoji, title, desc }) => (
            <div key={title} style={{ textAlign: 'center', padding: '24px 16px' }}>
              <div style={{ fontSize: 48, marginBottom: 12 }}>{emoji}</div>
              <div style={{ fontWeight: 800, fontSize: 16, marginBottom: 6 }}>{title}</div>
              <div style={{ fontSize: 13, color: 'var(--text-muted)' }}>{desc}</div>
            </div>
          ))}
        </div>
      </section>

      {/* ===== CTA ===== */}
      <section style={{
        background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)',
        padding: '60px 24px', textAlign: 'center'
      }}>
        <h2 style={{ fontSize: 32, fontWeight: 800, color: 'white', marginBottom: 12 }}>Hungry? Let's go!</h2>
        <p style={{ color: '#94a3b8', fontSize: 16, marginBottom: 28, maxWidth: 480, margin: '0 auto 28px' }}>
          Browse our full menu and add your favorites to the cart. Fast delivery or in-store pickup.
        </p>
        <div style={{ display: 'flex', gap: 12, justifyContent: 'center', flexWrap: 'wrap' }}>
          <Link to="/menu" className="btn btn-primary" style={{ fontSize: 16, padding: '14px 32px' }}>
            <UtensilsCrossed size={20} /> View Full Menu
          </Link>
          <Link to="/register" className="btn btn-secondary" style={{ fontSize: 16, padding: '14px 32px', color: 'white', borderColor: '#334155' }}>
            Create Account
          </Link>
        </div>
      </section>
    </div>
  );
};

export default HomePage;
