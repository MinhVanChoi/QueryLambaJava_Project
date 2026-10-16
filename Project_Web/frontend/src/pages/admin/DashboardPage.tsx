import React, { useEffect, useState } from 'react';
import {
  Building2, Users, UserCheck, Heart, UtensilsCrossed,
  ShoppingBag, DollarSign, TrendingUp, AlertTriangle, RefreshCw
} from 'lucide-react';
import { ApiClient } from '../../api/client';
import { DashboardStats } from '../../types';

const formatVND = (v: number) => v.toLocaleString('vi-VN') + ' ₫';

const statusColors: Record<string, string> = {
  CHO_XAC_NHAN: 'badge-info',
  DA_XAC_NHAN: 'badge-info',
  DANG_CHUAN_BI: 'badge-warning',
  SAN_SANG: 'badge-warning',
  DA_HOAN_THANH: 'badge-success',
  DA_HUY: 'badge-danger',
};
const statusLabels: Record<string, string> = {
  CHO_XAC_NHAN: 'Pending',
  DA_XAC_NHAN: 'Confirmed',
  DANG_CHUAN_BI: 'Preparing',
  SAN_SANG: 'Ready',
  DA_HOAN_THANH: 'Completed',
  DA_HUY: 'Cancelled',
};
const paymentLabels: Record<string, string> = {
  TIEN_MAT: '💵 Cash',
  CHUYEN_KHOAN: '🏦 Bank Transfer',
  VI_DIEN_TU: '📱 E-Wallet',
  THE: '💳 Card',
};

interface StatCardProps {
  icon: React.ReactNode;
  value: string;
  label: string;
  color: string;
}

const StatCard: React.FC<StatCardProps> = ({ icon, value, label, color }) => (
  <div className="stat-card">
    <div className="stat-icon-wrapper" style={{ backgroundColor: color }}>
      {icon}
    </div>
    <div>
      <div className="stat-value">{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  </div>
);

const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = async () => {
    try {
      setLoading(true);
      setError(null);
      setStats(await ApiClient.getDashboardStats());
    } catch (e: any) {
      setError(e.message ?? 'Failed to load dashboard stats');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, []);

  if (loading) return (
    <div className="empty-state">
      <div className="spinner spinner-primary" style={{ margin: '0 auto 12px' }} />
      Loading dashboard...
    </div>
  );

  if (error) return (
    <div className="empty-state">
      <AlertTriangle size={40} style={{ color: 'var(--danger-text)', margin: '0 auto 8px' }} />
      <p style={{ color: 'var(--danger-text)' }}>{error}</p>
      <button className="btn btn-primary" onClick={load} style={{ marginTop: 12 }}>Retry</button>
    </div>
  );

  if (!stats) return null;

  const maxBranchRevenue = Math.max(...Object.values(stats.revenueByBranch), 1);

  const totalOrders = stats.totalOrders || 1;

  return (
    <div>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 24 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 800, marginBottom: 4 }}>Business Overview</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 13 }}>
            Real-time metrics from the H2 database (MySQL mode) · Sample data from original project
          </p>
        </div>
        <button className="btn btn-secondary btn-sm" onClick={load}>
          <RefreshCw size={14} /> Refresh
        </button>
      </div>

      {/* Stat cards */}
      <div className="stat-grid">
        <StatCard
          icon={<Building2 size={22} color="white" />}
          value={String(stats.totalBranches)}
          label="Branches"
          color="#f97316"
        />
        <StatCard
          icon={<Users size={22} color="white" />}
          value={String(stats.totalEmployees)}
          label="Employees"
          color="#3b82f6"
        />
        <StatCard
          icon={<UserCheck size={22} color="white" />}
          value={String(stats.totalActiveAccounts)}
          label="Active Accounts"
          color="#10b981"
        />
        <StatCard
          icon={<Heart size={22} color="white" />}
          value={String(stats.totalCustomers)}
          label="Customers"
          color="#8b5cf6"
        />
        <StatCard
          icon={<UtensilsCrossed size={22} color="white" />}
          value={String(stats.totalFoodItems)}
          label="Food Items"
          color="#f59e0b"
        />
        <StatCard
          icon={<ShoppingBag size={22} color="white" />}
          value={String(stats.totalOrders)}
          label="Total Orders"
          color="#06b6d4"
        />
        <StatCard
          icon={<DollarSign size={22} color="white" />}
          value={formatVND(stats.totalCompletedRevenue)}
          label="Completed Revenue"
          color="#10b981"
        />
        <StatCard
          icon={<TrendingUp size={22} color="white" />}
          value={formatVND(stats.averageOrderValue)}
          label="Avg Order Value"
          color="#f97316"
        />
        <StatCard
          icon={<AlertTriangle size={22} color="white" />}
          value={String(stats.lowStockCount)}
          label="Low Stock Items"
          color="#ef4444"
        />
      </div>

      {/* Two columns: Orders by Status + Revenue by Branch */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 20 }}>
        {/* Orders by Status */}
        <div className="card">
          <div className="card-title">Orders by Status</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {Object.entries(stats.ordersByStatus).map(([status, count]) => {
              const pct = Math.round((count / totalOrders) * 100);
              return (
                <div key={status}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 }}>
                    <span style={{ fontSize: 13, fontWeight: 600 }}>{statusLabels[status] ?? status}</span>
                    <span className={`badge ${statusColors[status] ?? 'badge-info'}`}>{count}</span>
                  </div>
                  <div style={{ height: 8, background: '#f1f5f9', borderRadius: 9999 }}>
                    <div style={{
                      height: '100%', borderRadius: 9999, width: `${pct}%`,
                      background: status === 'DA_HOAN_THANH' ? '#10b981'
                        : status === 'DA_HUY' ? '#ef4444'
                          : status.includes('CHUAN_BI') || status === 'SAN_SANG' ? '#f59e0b'
                            : '#3b82f6'
                    }} />
                  </div>
                  <div style={{ fontSize: 11, color: 'var(--text-light)', marginTop: 2 }}>{pct}% of total</div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Revenue by Branch */}
        <div className="card">
          <div className="card-title">Revenue by Branch</div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
            {Object.entries(stats.revenueByBranch).map(([branch, revenue]) => {
              const pct = Math.round((revenue / maxBranchRevenue) * 100);
              return (
                <div key={branch}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span style={{ fontSize: 13, fontWeight: 600 }}>{branch}</span>
                    <span style={{ fontSize: 13, color: 'var(--text-muted)' }}>{formatVND(revenue)}</span>
                  </div>
                  <div style={{ height: 10, background: '#f1f5f9', borderRadius: 9999 }}>
                    <div style={{
                      height: '100%', borderRadius: 9999, width: `${pct}%`,
                      background: 'linear-gradient(90deg, #f97316, #ea580c)'
                    }} />
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Revenue by Payment Method */}
      <div className="card" style={{ marginBottom: 20 }}>
        <div className="card-title">Revenue by Payment Method</div>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: 12 }}>
          {Object.entries(stats.revenueByPaymentMethod).map(([method, amount]) => (
            <div key={method} style={{
              background: 'var(--bg-main)', border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)', padding: '14px 16px', textAlign: 'center'
            }}>
              <div style={{ fontSize: 22, marginBottom: 6 }}>
                {method === 'TIEN_MAT' ? '💵' : method === 'CHUYEN_KHOAN' ? '🏦' : method === 'VI_DIEN_TU' ? '📱' : '💳'}
              </div>
              <div style={{ fontSize: 13, fontWeight: 700, marginBottom: 2 }}>{formatVND(amount)}</div>
              <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{paymentLabels[method] ?? method}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Summary table */}
      <div className="card">
        <div className="card-title">Order Status Summary Table</div>
        <div className="table-container" style={{ border: 'none', boxShadow: 'none' }}>
          <table className="data-table">
            <thead>
              <tr>
                <th>Status</th>
                <th>Count</th>
                <th>% of Total Orders</th>
                <th>Interpretation</th>
              </tr>
            </thead>
            <tbody>
              {Object.entries(stats.ordersByStatus).map(([status, count]) => (
                <tr key={status}>
                  <td><span className={`badge ${statusColors[status] ?? 'badge-info'}`}>{statusLabels[status] ?? status}</span></td>
                  <td><strong>{count}</strong></td>
                  <td>{((count / totalOrders) * 100).toFixed(1)}%</td>
                  <td style={{ color: 'var(--text-muted)', fontSize: 12 }}>
                    {status === 'DA_HOAN_THANH' ? '✅ Counts toward revenue'
                      : status === 'DA_HUY' ? '❌ Excluded from revenue'
                        : '⏳ In-progress'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;
