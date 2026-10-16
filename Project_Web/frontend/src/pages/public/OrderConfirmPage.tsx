import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { CheckCircle, ShoppingBag, Home } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { OrderDetailResponse } from '../../types';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const STATUS_LABELS: Record<string, string> = {
  CHO_XAC_NHAN: 'Pending Confirmation', DA_XAC_NHAN: 'Confirmed',
  DANG_CHUAN_BI: 'Being Prepared', SAN_SANG: 'Ready for Pickup',
  DA_HOAN_THANH: 'Completed', DA_HUY: 'Cancelled',
};

const OrderConfirmPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [detail, setDetail] = useState<OrderDetailResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    ApiClient.getOrderDetail(Number(id))
      .then(setDetail)
      .catch(e => setError(e.response?.data?.message ?? e.message))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return (
    <div className="empty-state" style={{ padding: '80px 20px' }}>
      <div className="spinner spinner-primary" style={{ margin: '0 auto 12px' }} />
      Loading order details...
    </div>
  );

  if (error || !detail) return (
    <div className="empty-state" style={{ padding: '80px 20px' }}>
      <p style={{ color: 'var(--danger-text)' }}>{error ?? 'Order not found'}</p>
      <Link to="/" className="btn btn-primary" style={{ marginTop: 16 }}>Go Home</Link>
    </div>
  );

  return (
    <div className="section-container" style={{ maxWidth: 680, margin: '0 auto', padding: '48px 20px' }}>
      {/* Success header */}
      <div style={{ textAlign: 'center', marginBottom: 32 }}>
        <div style={{
          width: 80, height: 80, borderRadius: '50%', background: 'var(--success-bg)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 16px',
          border: '3px solid var(--success-border)'
        }}>
          <CheckCircle size={40} color="var(--success-text)" />
        </div>
        <h1 style={{ fontSize: 28, fontWeight: 800, color: 'var(--success-text)', marginBottom: 8 }}>
          Order Placed Successfully! 🎉
        </h1>
        <p style={{ color: 'var(--text-muted)', fontSize: 16 }}>
          Your order <strong>#{detail.order.maDH}</strong> has been received and is being processed.
        </p>
      </div>

      {/* Order details card */}
      <div className="card" style={{ marginBottom: 16 }}>
        <div style={{ fontWeight: 800, fontSize: 16, marginBottom: 16 }}>Order Details</div>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
          {[
            ['Order #', `#${detail.order.maDH}`],
            ['Status', STATUS_LABELS[detail.order.trangThai] ?? detail.order.trangThai],
            ['Branch', detail.tenCN],
            ['Order Date', new Date(detail.order.ngayDat).toLocaleString('en-US')],
          ].map(([k, v]) => (
            <div key={k} style={{ background: 'var(--bg-main)', padding: '10px 14px', borderRadius: 8 }}>
              <div style={{ fontSize: 11, color: 'var(--text-muted)', fontWeight: 600 }}>{k}</div>
              <div style={{ fontWeight: 700, marginTop: 2 }}>{v}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Items */}
      <div className="card" style={{ marginBottom: 16 }}>
        <div style={{ fontWeight: 800, fontSize: 16, marginBottom: 12 }}>Items Ordered</div>
        {detail.items.map(item => (
          <div key={item.maCTDH} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid var(--border-subtle)' }}>
            <span>{item.tenMon ?? item.maMon} × {item.soLuong}</span>
            <span style={{ fontWeight: 700 }}>{formatVND(item.thanhTien)}</span>
          </div>
        ))}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 12, fontSize: 13, color: 'var(--text-muted)' }}>
          <span>Subtotal</span><span>{formatVND(detail.order.tienHang)}</span>
        </div>
        {detail.order.tienGiam > 0 && (
          <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--success-text)', fontSize: 13 }}>
            <span>Discount {detail.tenKM ? `(${detail.tenKM})` : ''}</span>
            <span>-{formatVND(detail.order.tienGiam)}</span>
          </div>
        )}
        <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 800, fontSize: 18, marginTop: 8, borderTop: '2px solid var(--border-subtle)', paddingTop: 10 }}>
          <span>Total Paid</span>
          <span style={{ color: 'var(--primary-600)' }}>{formatVND(detail.order.tongTien)}</span>
        </div>
      </div>

      {/* Payment */}
      {detail.payments.length > 0 && (
        <div className="card" style={{ marginBottom: 24 }}>
          <div style={{ fontWeight: 800, fontSize: 16, marginBottom: 12 }}>Payment</div>
          {detail.payments.map(p => (
            <div key={p.maTT} style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13 }}>
              <span>{p.phuongThuc}</span>
              <span className={`badge ${p.trangThai === 'DA_THANH_TOAN' ? 'badge-success' : 'badge-warning'}`}>{p.trangThai}</span>
            </div>
          ))}
        </div>
      )}

      {/* What's next */}
      <div style={{ background: 'var(--info-bg)', border: '1px solid var(--info-border)', borderRadius: 'var(--radius-md)', padding: 16, marginBottom: 24 }}>
        <div style={{ fontWeight: 700, color: 'var(--info-text)', marginBottom: 8 }}>📋 What happens next?</div>
        <ol style={{ paddingLeft: 20, fontSize: 13, color: 'var(--text-muted)', display: 'flex', flexDirection: 'column', gap: 4 }}>
          <li>Our staff will confirm your order shortly</li>
          <li>Your food will be freshly prepared</li>
          <li>Pick up at {detail.tenCN} when it's ready</li>
          <li>Show your order # <strong>#{detail.order.maDH}</strong> at the counter</li>
        </ol>
      </div>

      <div style={{ display: 'flex', gap: 12 }}>
        <Link to="/" className="btn btn-secondary" style={{ flex: 1, justifyContent: 'center' }}>
          <Home size={16} /> Home
        </Link>
        <Link to="/menu" className="btn btn-primary" style={{ flex: 1, justifyContent: 'center' }}>
          <ShoppingBag size={16} /> Order Again
        </Link>
      </div>
    </div>
  );
};

export default OrderConfirmPage;
