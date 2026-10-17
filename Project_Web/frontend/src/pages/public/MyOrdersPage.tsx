import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingBag, Eye, Calendar, MapPin, Tag } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { Order, OrderDetailResponse, Branch } from '../../types';
import { useAuth } from '../../context/AuthContext';
import { getFoodImage } from '../../utils/foodImages';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';
const formatDate = (d: string) => new Date(d).toLocaleString('en-US');

const STATUS_COLORS: Record<string, string> = {
  CHO_XAC_NHAN: 'badge-info',
  DA_XAC_NHAN: 'badge-info',
  DANG_CHUAN_BI: 'badge-warning',
  SAN_SANG: 'badge-warning',
  DA_HOAN_THANH: 'badge-success',
  DA_HUY: 'badge-danger',
};

const STATUS_LABELS: Record<string, string> = {
  CHO_XAC_NHAN: 'Pending',
  DA_XAC_NHAN: 'Confirmed',
  DANG_CHUAN_BI: 'Preparing',
  SAN_SANG: 'Ready',
  DA_HOAN_THANH: 'Completed',
  DA_HUY: 'Cancelled',
};

const MyOrdersPage: React.FC = () => {
  const { user } = useAuth();
  const [orders, setOrders] = useState<Order[]>([]);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedOrder, setSelectedOrder] = useState<OrderDetailResponse | null>(null);
  const [detailLoading, setDetailLoading] = useState(false);

  useEffect(() => {
    Promise.all([
      ApiClient.getOrders(),
      ApiClient.getBranches()
    ]).then(([allOrders, allBranches]) => {
      setBranches(allBranches);
      if (user && user.role === 'CUSTOMER') {
        const customerOrders = allOrders.filter(o => o.maKH === (user as any).maKH);
        setOrders(customerOrders);
      } else {
        // If not logged in or staff viewing, display recent orders
        setOrders(allOrders);
      }
    }).finally(() => setLoading(false));
  }, [user]);

  const viewDetails = async (id: number) => {
    setDetailLoading(true);
    try {
      const detail = await ApiClient.getOrderDetail(id);
      setSelectedOrder(detail);
    } catch (err: any) {
      alert('Failed to load order details: ' + (err.response?.data?.message || err.message));
    } finally {
      setDetailLoading(false);
    }
  };

  const getBranchName = (maCN: string) => {
    return branches.find(b => b.maCN === maCN)?.tenCN || maCN;
  };

  return (
    <div className="section-container" style={{ maxWidth: 1000, margin: '0 auto', padding: '32px 20px' }}>
      <div style={{ marginBottom: 24, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h1 style={{ fontSize: 26, fontWeight: 800 }}>📜 Order History</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 14, marginTop: 4 }}>
            {user && user.role === 'CUSTOMER'
              ? `Orders placed by ${(user as any).hoTen}`
              : 'Recent store orders and active tickets'}
          </p>
        </div>
        <Link to="/menu" className="btn btn-primary btn-sm">
          <ShoppingBag size={15} /> Order More
        </Link>
      </div>

      {loading ? (
        <div className="empty-state">
          <div className="spinner spinner-primary" style={{ margin: '0 auto 12px' }} />
          Loading orders...
        </div>
      ) : orders.length === 0 ? (
        <div className="empty-state card">
          <ShoppingBag size={48} style={{ opacity: 0.3, margin: '0 auto 12px' }} />
          <h3>No Orders Yet</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: 4, marginBottom: 16 }}>
            You haven't placed any orders yet. Discover our fresh burgers, pizzas, and combos!
          </p>
          <Link to="/menu" className="btn btn-primary">Browse Menu</Link>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: selectedOrder ? '1fr 1fr' : '1fr', gap: 20 }}>
          {/* Orders Table */}
          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Order #</th>
                  <th>Date</th>
                  <th>Branch</th>
                  <th>Status</th>
                  <th>Total</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(o => (
                  <tr key={o.maDH}>
                    <td><strong>#{o.maDH}</strong></td>
                    <td style={{ fontSize: 12 }}>{formatDate(o.ngayDat)}</td>
                    <td style={{ fontSize: 12 }}>{getBranchName(o.maCN)}</td>
                    <td>
                      <span className={`badge ${STATUS_COLORS[o.trangThai] || 'badge-info'}`}>
                        {STATUS_LABELS[o.trangThai] || o.trangThai}
                      </span>
                    </td>
                    <td style={{ fontWeight: 700, color: 'var(--primary-600)' }}>
                      {formatVND(o.tongTien)}
                    </td>
                    <td>
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => viewDetails(o.maDH)}
                        disabled={detailLoading}
                      >
                        <Eye size={13} /> View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Details Sidebar if selected */}
          {selectedOrder && (
            <div className="card" style={{ height: 'fit-content' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                <h3 style={{ fontSize: 18, fontWeight: 700 }}>Order #{selectedOrder.order.maDH}</h3>
                <button
                  className="btn btn-secondary btn-sm"
                  onClick={() => setSelectedOrder(null)}
                >
                  Close
                </button>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: 10, fontSize: 13, marginBottom: 16 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <Calendar size={14} color="var(--text-muted)" />
                  <span>{formatDate(selectedOrder.order.ngayDat)}</span>
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <MapPin size={14} color="var(--text-muted)" />
                  <span>{selectedOrder.tenCN}</span>
                </div>
                {selectedOrder.tenKM && (
                  <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: 'var(--success-text)' }}>
                    <Tag size={14} />
                    <span>Promo Applied: {selectedOrder.tenKM}</span>
                  </div>
                )}
              </div>

              <div style={{ borderTop: '1px solid var(--border-subtle)', paddingTop: 12, marginBottom: 16 }}>
                <h4 style={{ fontSize: 14, fontWeight: 700, marginBottom: 8 }}>Items</h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                  {selectedOrder.items.map(item => (
                    <div key={item.maCTDH} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', fontSize: 13 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <img
                          src={getFoodImage(item.maMon, item.tenMon)}
                          alt={item.tenMon}
                          style={{ width: 32, height: 32, borderRadius: 6, objectFit: 'cover' }}
                        />
                        <span>{item.tenMon || item.maMon} × {item.soLuong}</span>
                      </div>
                      <strong>{formatVND(item.thanhTien)}</strong>
                    </div>
                  ))}
                </div>
              </div>

              <div style={{ background: 'var(--bg-main)', padding: 12, borderRadius: 8, fontSize: 13 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                  <span>Subtotal</span>
                  <span>{formatVND(selectedOrder.order.tienHang)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--danger-text)', marginBottom: 4 }}>
                  <span>Discount</span>
                  <span>-{formatVND(selectedOrder.order.tienGiam)}</span>
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 800, fontSize: 15, borderTop: '1px solid var(--border-subtle)', paddingTop: 6, marginTop: 4 }}>
                  <span>Total</span>
                  <span style={{ color: 'var(--primary-600)' }}>{formatVND(selectedOrder.order.tongTien)}</span>
                </div>
              </div>

              <div style={{ marginTop: 16 }}>
                <Link to={`/order-confirm/${selectedOrder.order.maDH}`} className="btn btn-secondary btn-sm" style={{ width: '100%', justifyContent: 'center' }}>
                  Open Full Receipt
                </Link>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default MyOrdersPage;
