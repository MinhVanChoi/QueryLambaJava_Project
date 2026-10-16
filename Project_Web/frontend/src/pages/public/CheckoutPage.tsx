import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ShoppingBag, ArrowLeft, Tag, CheckCircle } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { Branch, Promotion } from '../../types';
import { useCart } from '../../context/CartContext';
import { useAuth } from '../../context/AuthContext';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const CheckoutPage: React.FC = () => {
  const { items, subtotal, maCN, maKM, setBranch, setPromotion, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [branches, setBranches] = useState<Branch[]>([]);
  const [promos, setPromos] = useState<Promotion[]>([]);
  const [selectedBranch, setSelectedBranch] = useState(maCN);
  const [selectedPromo, setSelectedPromo] = useState(maKM);
  const [discount, setDiscount] = useState(0);
  const [payMethod, setPayMethod] = useState('TIEN_MAT');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Customer info for guest
  const [guestName, setGuestName] = useState('');
  const [guestPhone, setGuestPhone] = useState('');

  useEffect(() => {
    Promise.all([
      ApiClient.getBranches(),
      ApiClient.getPromotions(true)
    ]).then(([b, p]) => { setBranches(b); setPromos(p); });
  }, []);

  // Calculate discount when promo or subtotal changes
  useEffect(() => {
    if (selectedPromo && subtotal > 0) {
      ApiClient.calculateDiscount(selectedPromo, subtotal)
        .then(setDiscount)
        .catch(() => setDiscount(0));
    } else {
      setDiscount(0);
    }
  }, [selectedPromo, subtotal]);

  const total = subtotal - discount;

  const submit = async () => {
    if (!selectedBranch) { setError('Please select a branch'); return; }
    if (items.length === 0) { setError('Cart is empty'); return; }
    setSubmitting(true); setError(null);
    try {
      // For demo: use first employee of selected branch, or a default
      const employees = await ApiClient.getEmployees({ branchId: selectedBranch });
      const maNV = employees[0]?.maNV ?? 'NV01';

      const result = await ApiClient.createOrder({
        maCN: selectedBranch,
        maNV,
        maKH: user?.role === 'CUSTOMER' ? (user as any).maKH : undefined,
        maKM: selectedPromo || undefined,
        items: items.map(i => ({ maMon: i.maMon, soLuong: i.soLuong })),
        phuongThuc: payMethod,
        payImmediately: true,
      });

      clearCart();
      navigate(`/order-confirm/${result.order.maDH}`);
    } catch (e: any) {
      setError(e.response?.data?.message ?? e.message);
    } finally {
      setSubmitting(false);
    }
  };

  if (items.length === 0) {
    return (
      <div className="section-container" style={{ textAlign: 'center', padding: '80px 20px' }}>
        <div style={{ fontSize: 64, marginBottom: 16 }}>🛒</div>
        <h2 style={{ fontWeight: 800, marginBottom: 8 }}>Cart is empty</h2>
        <button onClick={() => navigate('/menu')} className="btn btn-primary">Browse Menu</button>
      </div>
    );
  }

  return (
    <div className="section-container" style={{ maxWidth: 900, margin: '0 auto' }}>
      <div style={{ marginBottom: 24 }}>
        <button onClick={() => navigate('/cart')} className="btn btn-secondary btn-sm" style={{ marginBottom: 12 }}>
          <ArrowLeft size={14} /> Back to Cart
        </button>
        <h1 style={{ fontSize: 28, fontWeight: 800 }}>Checkout</h1>
      </div>

      {error && (
        <div style={{ background: 'var(--danger-bg)', color: 'var(--danger-text)', padding: '12px 16px', borderRadius: 8, marginBottom: 16, fontSize: 14 }}>
          ⚠️ {error}
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 360px', gap: 24 }}>
        {/* Left: Form */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          {/* Customer info */}
          <div className="card">
            <div style={{ fontWeight: 700, fontSize: 16, marginBottom: 16 }}>
              {user ? `👤 Ordering as: ${(user as any).hoTen ?? (user as any).tenDangNhap}` : '👤 Guest Checkout'}
            </div>
            {!user && (
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Your Name</label>
                  <input className="input" value={guestName} onChange={e => setGuestName(e.target.value)} placeholder="e.g. Nguyen Van A" />
                </div>
                <div className="form-group" style={{ marginBottom: 0 }}>
                  <label className="form-label">Phone Number</label>
                  <input className="input" value={guestPhone} onChange={e => setGuestPhone(e.target.value)} placeholder="e.g. 0901234567" />
                </div>
              </div>
            )}
          </div>

          {/* Branch */}
          <div className="card">
            <div style={{ fontWeight: 700, fontSize: 16, marginBottom: 12 }}>🏪 Pick-Up Branch</div>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: 10 }}>
              {branches.map(b => (
                <button
                  key={b.maCN}
                  onClick={() => { setSelectedBranch(b.maCN); setBranch(b.maCN); }}
                  style={{
                    padding: '12px', borderRadius: 8, border: '2px solid',
                    borderColor: selectedBranch === b.maCN ? 'var(--primary-600)' : 'var(--border-subtle)',
                    background: selectedBranch === b.maCN ? 'var(--primary-50)' : 'white',
                    cursor: 'pointer', textAlign: 'left'
                  }}
                >
                  <div style={{ fontWeight: 700, fontSize: 13 }}>{b.tenCN}</div>
                  <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>{b.diaChi}</div>
                </button>
              ))}
            </div>
          </div>

          {/* Promotion */}
          <div className="card">
            <div style={{ fontWeight: 700, fontSize: 16, marginBottom: 12 }}><Tag size={16} style={{ display: 'inline', marginRight: 6 }} />Apply Promotion</div>
            <select className="select" value={selectedPromo} onChange={e => { setSelectedPromo(e.target.value); setPromotion(e.target.value); }}>
              <option value="">No promotion</option>
              {promos.filter(p => subtotal >= p.donToiThieu).map(p => (
                <option key={p.maKM} value={p.maKM}>
                  {p.tenKM} — {p.phanTramGiam}% off (min {formatVND(p.donToiThieu)})
                </option>
              ))}
            </select>
            {discount > 0 && (
              <div style={{ marginTop: 10, color: 'var(--success-text)', fontWeight: 700, fontSize: 14 }}>
                <CheckCircle size={14} style={{ display: 'inline', marginRight: 4 }} />
                You save {formatVND(discount)}!
              </div>
            )}
            {promos.filter(p => subtotal < p.donToiThieu).length > 0 && (
              <div style={{ marginTop: 8, fontSize: 12, color: 'var(--text-muted)' }}>
                {promos.filter(p => subtotal < p.donToiThieu).length} promotion(s) not eligible (minimum order not met)
              </div>
            )}
          </div>

          {/* Payment */}
          <div className="card">
            <div style={{ fontWeight: 700, fontSize: 16, marginBottom: 12 }}>💳 Payment Method</div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
              {[
                { value: 'TIEN_MAT', label: '💵 Cash', desc: 'Pay at counter' },
                { value: 'CHUYEN_KHOAN', label: '🏦 Bank Transfer', desc: 'Transfer to our account' },
                { value: 'VI_DIEN_TU', label: '📱 E-Wallet', desc: 'MoMo, ZaloPay, etc.' },
                { value: 'THE', label: '💳 Card', desc: 'Visa / Mastercard' },
              ].map(opt => (
                <button
                  key={opt.value}
                  onClick={() => setPayMethod(opt.value)}
                  style={{
                    padding: '12px', borderRadius: 8, border: '2px solid', cursor: 'pointer', textAlign: 'left',
                    borderColor: payMethod === opt.value ? 'var(--primary-600)' : 'var(--border-subtle)',
                    background: payMethod === opt.value ? 'var(--primary-50)' : 'white',
                  }}
                >
                  <div style={{ fontWeight: 700, fontSize: 13 }}>{opt.label}</div>
                  <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>{opt.desc}</div>
                </button>
              ))}
            </div>
            <div style={{ marginTop: 12, padding: 10, background: 'var(--warning-bg)', borderRadius: 6, fontSize: 12, color: 'var(--warning-text)' }}>
              ⚠️ Demo mode: Payment is simulated. No real transaction occurs.
            </div>
          </div>
        </div>

        {/* Right: Summary */}
        <div>
          <div className="card" style={{ position: 'sticky', top: 80 }}>
            <div style={{ fontWeight: 800, fontSize: 18, marginBottom: 16 }}>Order Summary</div>
            {items.map(item => (
              <div key={item.maMon} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 8 }}>
                <span>{item.tenMon} × {item.soLuong}</span>
                <span>{formatVND(item.giaBan * item.soLuong)}</span>
              </div>
            ))}
            <div style={{ borderTop: '1px solid var(--border-subtle)', paddingTop: 12, marginTop: 8 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6, fontSize: 14 }}>
                <span>Subtotal</span><span>{formatVND(subtotal)}</span>
              </div>
              {discount > 0 && (
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 6, fontSize: 14, color: 'var(--success-text)' }}>
                  <span>Discount</span><span>-{formatVND(discount)}</span>
                </div>
              )}
              <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 800, fontSize: 20, marginTop: 8 }}>
                <span>Total</span>
                <span style={{ color: 'var(--primary-600)' }}>{formatVND(total)}</span>
              </div>
            </div>
            <button
              className="btn btn-primary"
              style={{ width: '100%', justifyContent: 'center', marginTop: 16, fontSize: 16, padding: '14px' }}
              onClick={submit}
              disabled={submitting || !selectedBranch}
            >
              {submitting ? <><span className="spinner" style={{ width: 16, height: 16 }} /> Placing Order...</>
                : <><ShoppingBag size={18} /> Place Order ({formatVND(total)})</>}
            </button>
            {!selectedBranch && <div style={{ fontSize: 12, color: 'var(--danger-text)', textAlign: 'center', marginTop: 6 }}>Please select a branch first</div>}
          </div>
        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
