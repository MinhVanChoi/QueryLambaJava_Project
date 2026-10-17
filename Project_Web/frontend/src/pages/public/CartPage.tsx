import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShoppingBag, Trash2, Plus, Minus, ArrowRight, ArrowLeft } from 'lucide-react';
import { useCart } from '../../context/CartContext';
import { getFoodImage } from '../../utils/foodImages';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const CartPage: React.FC = () => {
  const { items, updateQty, removeItem, subtotal, totalItems } = useCart();
  const navigate = useNavigate();

  if (items.length === 0) {
    return (
      <div className="section-container" style={{ textAlign: 'center', padding: '80px 20px' }}>
        <div style={{ fontSize: 80, marginBottom: 16 }}>🛒</div>
        <h2 style={{ fontSize: 24, fontWeight: 800, marginBottom: 8 }}>Your cart is empty</h2>
        <p style={{ color: 'var(--text-muted)', marginBottom: 24 }}>Add some delicious items from our menu to get started!</p>
        <Link to="/menu" className="btn btn-primary" style={{ fontSize: 16, padding: '12px 28px' }}>
          <ShoppingBag size={18} /> Browse Menu
        </Link>
      </div>
    );
  }

  return (
    <div className="section-container" style={{ maxWidth: 900, margin: '0 auto' }}>
      <div style={{ marginBottom: 28 }}>
        <button onClick={() => navigate(-1)} className="btn btn-secondary btn-sm" style={{ marginBottom: 12 }}>
          <ArrowLeft size={14} /> Back
        </button>
        <h1 style={{ fontSize: 28, fontWeight: 800 }}>🛒 Your Cart</h1>
        <p style={{ color: 'var(--text-muted)', marginTop: 4 }}>{totalItems} item{totalItems !== 1 ? 's' : ''} in your cart</p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: 24 }}>
        {/* Items list */}
        <div>
          {items.map(item => (
            <div key={item.maMon} style={{
              background: 'white', border: '1px solid var(--border-subtle)', borderRadius: 'var(--radius-md)',
              padding: '16px 20px', marginBottom: 12, display: 'flex', alignItems: 'center', gap: 16,
              boxShadow: 'var(--shadow-sm)'
            }}>
              <div style={{
                width: 64, height: 64, borderRadius: 10, overflow: 'hidden', flexShrink: 0,
                background: 'var(--primary-50)'
              }}>
                <img
                  src={getFoodImage(item.maMon, item.tenMon)}
                  alt={item.tenMon}
                  style={{ width: '100%', height: '100%', objectFit: 'cover' }}
                />
              </div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontWeight: 700, fontSize: 15, marginBottom: 2 }}>{item.tenMon}</div>
                <div style={{ color: 'var(--primary-600)', fontWeight: 700 }}>{formatVND(item.giaBan)}</div>
              </div>

              {/* Qty controls */}
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <button
                  className="btn btn-secondary btn-sm"
                  style={{ width: 32, height: 32, padding: 0 }}
                  onClick={() => updateQty(item.maMon, item.soLuong - 1)}
                >
                  <Minus size={14} />
                </button>
                <span style={{ fontWeight: 800, fontSize: 16, minWidth: 24, textAlign: 'center' }}>{item.soLuong}</span>
                <button
                  className="btn btn-secondary btn-sm"
                  style={{ width: 32, height: 32, padding: 0 }}
                  onClick={() => updateQty(item.maMon, item.soLuong + 1)}
                >
                  <Plus size={14} />
                </button>
              </div>

              <div style={{ fontWeight: 800, color: 'var(--text-main)', minWidth: 100, textAlign: 'right' }}>
                {formatVND(item.giaBan * item.soLuong)}
              </div>

              <button className="btn btn-danger btn-sm" style={{ padding: '6px 8px' }} onClick={() => removeItem(item.maMon)}>
                <Trash2 size={14} />
              </button>
            </div>
          ))}
        </div>

        {/* Order summary */}
        <div>
          <div className="card" style={{ position: 'sticky', top: 80 }}>
            <div style={{ fontWeight: 800, fontSize: 18, marginBottom: 16 }}>Order Summary</div>

            {items.map(item => (
              <div key={item.maMon} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 6 }}>
                <span style={{ color: 'var(--text-muted)' }}>{item.tenMon} × {item.soLuong}</span>
                <span>{formatVND(item.giaBan * item.soLuong)}</span>
              </div>
            ))}

            <div style={{ borderTop: '1px solid var(--border-subtle)', margin: '14px 0', paddingTop: 14 }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 800, fontSize: 18 }}>
                <span>Subtotal</span>
                <span style={{ color: 'var(--primary-600)' }}>{formatVND(subtotal)}</span>
              </div>
              <div style={{ fontSize: 12, color: 'var(--text-muted)', marginTop: 4 }}>
                Promotions and final total calculated at checkout
              </div>
            </div>

            <Link to="/checkout" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center', fontSize: 16, padding: '14px' }}>
              Proceed to Checkout <ArrowRight size={18} />
            </Link>

            <Link to="/menu" className="btn btn-secondary" style={{ width: '100%', justifyContent: 'center', marginTop: 8 }}>
              <ShoppingBag size={16} /> Continue Shopping
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CartPage;
