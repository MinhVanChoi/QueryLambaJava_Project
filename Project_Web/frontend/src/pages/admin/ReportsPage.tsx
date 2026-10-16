import React, { useEffect, useState } from 'react';
import { BarChart3, TrendingUp, Download, RefreshCw, DollarSign } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { DashboardStats } from '../../types';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

// Simple bar chart using divs
const BarChart: React.FC<{ data: Record<string, number>; color?: string; formatValue?: (v: number) => string }> = ({
  data, color = '#f97316', formatValue = String
}) => {
  const max = Math.max(...Object.values(data), 1);
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
      {Object.entries(data).map(([label, value]) => (
        <div key={label}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 13, marginBottom: 4 }}>
            <span style={{ fontWeight: 600 }}>{label}</span>
            <span style={{ color: 'var(--text-muted)' }}>{formatValue(value)}</span>
          </div>
          <div style={{ height: 10, background: '#f1f5f9', borderRadius: 9999 }}>
            <div style={{ height: '100%', borderRadius: 9999, width: `${(value / max) * 100}%`, background: color, transition: 'width 0.5s' }} />
          </div>
        </div>
      ))}
    </div>
  );
};

const ReportsPage: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);

  const load = async () => {
    setLoading(true);
    try { setStats(await ApiClient.getDashboardStats()); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, []);

  // CSV export helper
  const exportCSV = (filename: string, data: Record<string, number>) => {
    const rows = ['Category,Value', ...Object.entries(data).map(([k, v]) => `"${k}",${v}`)];
    const blob = new Blob([rows.join('\n')], { type: 'text/csv' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url; a.download = filename; a.click();
    URL.revokeObjectURL(url);
  };

  if (loading) return <div className="empty-state"><div className="spinner spinner-primary" style={{ margin: '0 auto' }} /> Loading reports...</div>;
  if (!stats) return null;

  const totalRevenue = stats.totalCompletedRevenue;
  const completedOrders = stats.ordersByStatus['DA_HOAN_THANH'] ?? 0;
  const cancelledOrders = stats.ordersByStatus['DA_HUY'] ?? 0;
  const completedPct = stats.totalOrders > 0 ? ((completedOrders / stats.totalOrders) * 100).toFixed(1) : '0';
  const cancelledPct = stats.totalOrders > 0 ? ((cancelledOrders / stats.totalOrders) * 100).toFixed(1) : '0';

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 800 }}>Reports & Analytics</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 13, marginTop: 4 }}>
            Business intelligence based on actual database data · Revenue counts completed orders only
          </p>
        </div>
        <button className="btn btn-secondary btn-sm" onClick={load}><RefreshCw size={14} /> Refresh</button>
      </div>

      {/* KPI summary row */}
      <div className="stat-grid" style={{ marginBottom: 24 }}>
        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ background: '#10b981' }}><DollarSign size={22} color="white" /></div>
          <div>
            <div className="stat-value">{formatVND(totalRevenue)}</div>
            <div className="stat-label">Total Completed Revenue</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ background: '#3b82f6' }}><BarChart3 size={22} color="white" /></div>
          <div>
            <div className="stat-value">{stats.totalCompletedOrders}</div>
            <div className="stat-label">Completed Orders</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon-wrapper" style={{ background: '#f97316' }}><TrendingUp size={22} color="white" /></div>
          <div>
            <div className="stat-value">{formatVND(stats.averageOrderValue)}</div>
            <div className="stat-label">Avg Order Value</div>
          </div>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 20 }}>
        {/* Revenue by Branch */}
        <div className="card">
          <div className="card-title">
            Revenue by Branch
            <button className="btn btn-secondary btn-sm" onClick={() => exportCSV('revenue-by-branch.csv', stats.revenueByBranch)}>
              <Download size={12} /> CSV
            </button>
          </div>
          <BarChart data={stats.revenueByBranch} formatValue={formatVND} color="#f97316" />
        </div>

        {/* Revenue by Payment Method */}
        <div className="card">
          <div className="card-title">
            Revenue by Payment Method
            <button className="btn btn-secondary btn-sm" onClick={() => exportCSV('revenue-by-payment.csv', stats.revenueByPaymentMethod)}>
              <Download size={12} /> CSV
            </button>
          </div>
          <BarChart
            data={{
              'Cash (TIEN_MAT)': stats.revenueByPaymentMethod['TIEN_MAT'] ?? 0,
              'Bank Transfer': stats.revenueByPaymentMethod['CHUYEN_KHOAN'] ?? 0,
              'E-Wallet': stats.revenueByPaymentMethod['VI_DIEN_TU'] ?? 0,
              'Card': stats.revenueByPaymentMethod['THE'] ?? 0,
            }}
            formatValue={formatVND}
            color="#3b82f6"
          />
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 20 }}>
        {/* Order Status Distribution */}
        <div className="card">
          <div className="card-title">
            Order Status Distribution
            <button className="btn btn-secondary btn-sm" onClick={() => exportCSV('order-status.csv', stats.ordersByStatus)}>
              <Download size={12} /> CSV
            </button>
          </div>
          <BarChart
            data={Object.fromEntries(Object.entries(stats.ordersByStatus).map(([k, v]) => [
              { CHO_XAC_NHAN: 'Pending', DA_XAC_NHAN: 'Confirmed', DANG_CHUAN_BI: 'Preparing', SAN_SANG: 'Ready', DA_HOAN_THANH: 'Completed', DA_HUY: 'Cancelled' }[k] ?? k,
              v
            ]))}
            color="#8b5cf6"
          />
        </div>

        {/* Business metrics */}
        <div className="card">
          <div className="card-title">Completion vs Cancellation Rate</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 16, paddingTop: 8 }}>
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                <span style={{ fontWeight: 700, color: 'var(--success-text)' }}>✅ Completed</span>
                <span><strong>{completedOrders}</strong> orders ({completedPct}%)</span>
              </div>
              <div style={{ height: 16, background: '#f1f5f9', borderRadius: 9999 }}>
                <div style={{ height: '100%', background: '#10b981', borderRadius: 9999, width: `${completedPct}%` }} />
              </div>
            </div>
            <div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                <span style={{ fontWeight: 700, color: 'var(--danger-text)' }}>❌ Cancelled</span>
                <span><strong>{cancelledOrders}</strong> orders ({cancelledPct}%)</span>
              </div>
              <div style={{ height: 16, background: '#f1f5f9', borderRadius: 9999 }}>
                <div style={{ height: '100%', background: '#ef4444', borderRadius: 9999, width: `${cancelledPct}%` }} />
              </div>
            </div>
            <div style={{ background: 'var(--bg-main)', padding: 14, borderRadius: 8, fontSize: 13 }}>
              <div style={{ fontWeight: 700, marginBottom: 8 }}>Business Rules Applied</div>
              <ul style={{ paddingLeft: 16, display: 'flex', flexDirection: 'column', gap: 4, color: 'var(--text-muted)' }}>
                <li>Revenue = Completed orders (DA_HOAN_THANH) only</li>
                <li>Cancelled orders (DA_HUY) excluded from revenue</li>
                <li>Discount = promotion % of subtotal, capped at giamToiDa</li>
                <li>Promo requires order ≥ donToiThieu minimum</li>
                <li>Historical prices preserved in chi_tiet_don_hang.don_gia</li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      {/* Business entity overview table */}
      <div className="card">
        <div className="card-title">Business Entity Overview</div>
        <table className="data-table">
          <thead>
            <tr><th>Metric</th><th>Value</th><th>Details</th></tr>
          </thead>
          <tbody>
            <tr><td>Branches</td><td><strong>{stats.totalBranches}</strong></td><td>Active restaurant locations</td></tr>
            <tr><td>Employees</td><td><strong>{stats.totalEmployees}</strong></td><td>Total headcount across all branches</td></tr>
            <tr><td>Active Accounts</td><td><strong>{stats.totalActiveAccounts}</strong></td><td>Login-enabled staff accounts</td></tr>
            <tr><td>Registered Customers</td><td><strong>{stats.totalCustomers}</strong></td><td>Total customer records</td></tr>
            <tr><td>Food Categories</td><td><strong>{stats.totalCategories}</strong></td><td>Menu categories</td></tr>
            <tr><td>Food Items</td><td><strong>{stats.totalFoodItems}</strong></td><td>Active + discontinued items</td></tr>
            <tr><td>Total Orders</td><td><strong>{stats.totalOrders}</strong></td><td>All-time order count</td></tr>
            <tr><td>Completed Orders</td><td><strong>{stats.totalCompletedOrders}</strong></td><td>Revenue-generating orders</td></tr>
            <tr><td>Total Revenue</td><td><strong style={{ color: 'var(--primary-600)' }}>{formatVND(totalRevenue)}</strong></td><td>Sum of completed order totals (after discount)</td></tr>
            <tr><td>Average Order Value</td><td><strong>{formatVND(stats.averageOrderValue)}</strong></td><td>Based on completed orders</td></tr>
            <tr><td>Low Stock Items</td><td><strong style={{ color: 'var(--danger-text)' }}>{stats.lowStockCount}</strong></td><td>Stock quantity below threshold (10 units)</td></tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ReportsPage;
