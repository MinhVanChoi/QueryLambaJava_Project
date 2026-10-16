import React, { useEffect, useState } from 'react';
import { DatabaseZap, Play, GitCompare, ChevronDown, ChevronUp, CheckCircle, XCircle, AlertCircle } from 'lucide-react';
import { ApiClient } from '../../api/client';
import { QueryMeta, QueryResult, QueryComparison } from '../../types';

const QueriesPage: React.FC = () => {
  const [queries, setQueries] = useState<QueryMeta[]>([]);
  const [loading, setLoading] = useState(false);
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const [mode, setMode] = useState<'sql' | 'lambda'>('sql');
  const [result, setResult] = useState<QueryResult | null>(null);
  const [comparison, setComparison] = useState<QueryComparison | null>(null);
  const [executing, setExecuting] = useState(false);
  const [comparing, setComparing] = useState(false);
  const [expandedSql, setExpandedSql] = useState(false);
  const [bulkResults, setBulkResults] = useState<{ id: number; sqlCount: number; lambdaCount: number; matched: boolean }[]>([]);

  const [bulkRunning, setBulkRunning] = useState(false);

  useEffect(() => {
    setLoading(true);
    ApiClient.getQueries().then(setQueries).finally(() => setLoading(false));
  }, []);

  const selected = queries.find(q => q.id === selectedId);

  const execute = async () => {
    if (!selectedId) return;
    setExecuting(true); setResult(null); setComparison(null);
    try { setResult(await ApiClient.executeQuery(selectedId, mode)); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
    finally { setExecuting(false); }
  };

  const compare = async () => {
    if (!selectedId) return;
    setComparing(true); setResult(null); setComparison(null);
    try { setComparison(await ApiClient.compareQuery(selectedId)); }
    catch (e: any) { alert(e.response?.data?.message ?? e.message); }
    finally { setComparing(false); }
  };

  const runAllBulk = async () => {
    setBulkRunning(true); setBulkResults([]);
    const res: typeof bulkResults = [];
    for (const q of queries) {
      try {
        const cmp = await ApiClient.compareQuery(q.id);
        res.push({ id: q.id, sqlCount: cmp.sqlResult.totalRows, lambdaCount: cmp.lambdaResult.totalRows, matched: cmp.matched });
      } catch {
        res.push({ id: q.id, sqlCount: -1, lambdaCount: -1, matched: false });
      }
    }
    setBulkResults(res); setBulkRunning(false);
  };

  const CATEGORY_COLORS: Record<string, string> = {
    'Basic Query': '#3b82f6',
    'Aggregate': '#f59e0b',
    'JOIN': '#8b5cf6',
    'GROUP BY': '#10b981',
    'Subquery': '#ef4444',
    'ORDER BY': '#06b6d4',
    'HAVING': '#f97316',
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 800 }}>30 Business Queries Explorer</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 13, marginTop: 4 }}>
            Execute all 30 original queries in SQL mode or Lambda/Stream API mode, then compare results.
          </p>
        </div>
        <button className="btn btn-secondary" onClick={runAllBulk} disabled={bulkRunning}>
          {bulkRunning ? <><span className="spinner spinner-primary" style={{ width: 14, height: 14 }} /> Running All...</> : <><GitCompare size={14} /> Run All & Compare</>}
        </button>
      </div>

      {/* Bulk results summary */}
      {bulkResults.length > 0 && (
        <div className="card" style={{ marginBottom: 20 }}>
          <div className="card-title">
            Bulk Comparison Results
            <span className={`badge ${bulkResults.every(r => r.matched) ? 'badge-success' : 'badge-warning'}`}>
              {bulkResults.filter(r => r.matched).length}/{bulkResults.length} Matched
            </span>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(80px, 1fr))', gap: 6 }}>
            {bulkResults.map(r => (
              <div key={r.id} style={{
                padding: '8px 6px', borderRadius: 6, textAlign: 'center', fontSize: 11, fontWeight: 700,
                background: r.matched ? 'var(--success-bg)' : 'var(--danger-bg)',
                color: r.matched ? 'var(--success-text)' : 'var(--danger-text)',
                border: `1px solid ${r.matched ? 'var(--success-border)' : 'var(--danger-border)'}`
              }}>
                <div>Q{r.id}</div>
                <div>{r.matched ? '✓' : '✗'}</div>
                <div>{r.sqlCount === -1 ? 'ERR' : r.sqlCount}</div>
              </div>
            ))}
          </div>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '340px 1fr', gap: 20 }}>
        {/* Query list */}
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <div style={{ padding: '14px 16px', borderBottom: '1px solid var(--border-subtle)', fontWeight: 700 }}>
            <DatabaseZap size={16} style={{ display: 'inline', marginRight: 8 }} />
            {loading ? 'Loading...' : `${queries.length} Queries`}
          </div>
          <div style={{ overflowY: 'auto', maxHeight: 600 }}>
            {queries.map(q => (
              <div key={q.id}>
                <button
                  onClick={() => { setSelectedId(q.id); setResult(null); setComparison(null); }}
                  style={{
                    width: '100%', textAlign: 'left', padding: '10px 16px', border: 'none', cursor: 'pointer',
                    background: selectedId === q.id ? 'var(--primary-50)' : 'white',
                    borderLeft: selectedId === q.id ? '3px solid var(--primary-600)' : '3px solid transparent',
                    borderBottom: '1px solid var(--border-subtle)',
                    display: 'flex', alignItems: 'flex-start', gap: 10
                  }}
                >
                  <span style={{
                    minWidth: 28, height: 28, borderRadius: '50%', background: selectedId === q.id ? 'var(--primary-600)' : '#f1f5f9',
                    color: selectedId === q.id ? 'white' : 'var(--text-muted)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 11, fontWeight: 700, flexShrink: 0
                  }}>{q.id}</span>
                  <div style={{ minWidth: 0 }}>
                    <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--text-main)', lineHeight: 1.3 }}>{q.title}</div>
                    <div style={{ marginTop: 4 }}>
                      <span style={{
                        fontSize: 10, padding: '1px 6px', borderRadius: 9999,
                        background: CATEGORY_COLORS[q.category] ?? '#6b7280',
                        color: 'white', fontWeight: 700
                      }}>{q.category}</span>
                    </div>
                  </div>
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Query detail + execution */}
        <div>
          {!selected ? (
            <div className="card">
              <div className="empty-state">
                <DatabaseZap size={48} style={{ opacity: 0.2, margin: '0 auto 12px' }} />
                <p style={{ fontWeight: 600 }}>Select a query to execute</p>
                <p style={{ fontSize: 13, color: 'var(--text-muted)', marginTop: 4 }}>
                  Choose any of the 30 business queries from the list on the left.
                </p>
              </div>
            </div>
          ) : (
            <>
              {/* Query info */}
              <div className="card" style={{ marginBottom: 16 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                  <div>
                    <h2 style={{ fontSize: 16, fontWeight: 800 }}>Q{selected.id}: {selected.title}</h2>
                    <p style={{ color: 'var(--text-muted)', fontSize: 13, marginTop: 4 }}>{selected.description}</p>
                  </div>
                  <span style={{
                    fontSize: 11, padding: '4px 10px', borderRadius: 9999,
                    background: CATEGORY_COLORS[selected.category] ?? '#6b7280', color: 'white', fontWeight: 700, flexShrink: 0
                  }}>{selected.category}</span>
                </div>

                {/* SQL preview */}
                <div style={{ marginBottom: 16 }}>
                  <button
                    onClick={() => setExpandedSql(e => !e)}
                    style={{ fontSize: 13, fontWeight: 700, background: 'none', border: 'none', cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 6, color: 'var(--text-main)', marginBottom: 8 }}
                  >
                    {expandedSql ? <ChevronUp size={14} /> : <ChevronDown size={14} />} View SQL
                  </button>
                  {expandedSql && (
                    <pre style={{ background: '#0f172a', color: '#e2e8f0', padding: 16, borderRadius: 8, fontSize: 12, overflowX: 'auto', lineHeight: 1.6 }}>
                      {selected.sql}
                    </pre>
                  )}
                </div>

                {/* Execution controls */}
                <div style={{ display: 'flex', gap: 10, alignItems: 'center', flexWrap: 'wrap' }}>
                  <select className="select" style={{ width: 160 }} value={mode} onChange={e => setMode(e.target.value as any)}>
                    <option value="sql">SQL Mode</option>
                    <option value="lambda">Lambda/Stream Mode</option>
                  </select>
                  <button className="btn btn-primary" onClick={execute} disabled={executing}>
                    <Play size={14} /> {executing ? 'Executing...' : 'Execute Query'}
                  </button>
                  <button className="btn btn-secondary" onClick={compare} disabled={comparing}>
                    <GitCompare size={14} /> {comparing ? 'Comparing...' : 'SQL vs Lambda'}
                  </button>
                </div>
              </div>

              {/* Single result */}
              {result && (
                <div className="card" style={{ marginBottom: 16 }}>
                  <div className="card-title">
                    Result ({result.executionMode} mode)
                    <div style={{ display: 'flex', gap: 8 }}>
                      <span className="badge badge-info">{result.totalRows} rows</span>
                      <span className="badge badge-success">{result.executionTimeMs}ms</span>
                    </div>
                  </div>
                  {result.totalRows === 0 ? (
                    <div className="empty-state"><AlertCircle size={32} style={{ opacity: 0.3, margin: '0 auto 8px' }} /><p>No results returned</p></div>
                  ) : (
                    <div className="table-container" style={{ maxHeight: 400, overflowY: 'auto' }}>
                      <table className="data-table">
                        <thead>
                          <tr>{result.headers.map(h => <th key={h}>{h}</th>)}</tr>
                        </thead>
                        <tbody>
                          {result.rows.map((row, ri) => (
                            <tr key={ri}>
                              {row.map((cell, ci) => (
                                <td key={ci} style={{ fontSize: 12 }}>
                                  {cell === null ? <span style={{ color: 'var(--text-muted)' }}>NULL</span> : String(cell)}
                                </td>
                              ))}
                            </tr>
                          ))}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>
              )}

              {/* Comparison result */}
              {comparison && (
                <div className="card">
                  <div className="card-title">
                    SQL vs Lambda Comparison
                    {comparison.matched
                      ? <span className="badge badge-success"><CheckCircle size={12} /> Results Match</span>
                      : <span className="badge badge-danger"><XCircle size={12} /> Mismatch!</span>
                    }
                  </div>
                  <p style={{ fontSize: 13, color: 'var(--text-muted)', marginBottom: 16 }}>{comparison.matchSummary}</p>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
                    {[
                      { label: 'SQL Mode', r: comparison.sqlResult },
                      { label: 'Lambda/Stream Mode', r: comparison.lambdaResult },
                    ].map(({ label, r }) => (
                      <div key={label}>
                        <div style={{ fontWeight: 700, marginBottom: 8, fontSize: 13 }}>
                          {label} — <span className="badge badge-info">{r.totalRows} rows</span>
                          <span className="badge badge-success" style={{ marginLeft: 4 }}>{r.executionTimeMs}ms</span>
                        </div>
                        {r.totalRows === 0 ? (
                          <div style={{ fontSize: 12, color: 'var(--text-muted)' }}>No results</div>
                        ) : (
                          <div className="table-container" style={{ maxHeight: 280, overflowY: 'auto' }}>
                            <table className="data-table">
                              <thead><tr>{r.headers.map(h => <th key={h} style={{ fontSize: 11 }}>{h}</th>)}</tr></thead>
                              <tbody>
                                {r.rows.map((row, ri) => (
                                  <tr key={ri}>{row.map((cell, ci) => (
                                    <td key={ci} style={{ fontSize: 11 }}>
                                      {cell === null ? <span style={{ color: 'var(--text-muted)' }}>NULL</span> : String(cell)}
                                    </td>
                                  ))}</tr>
                                ))}
                              </tbody>
                            </table>
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default QueriesPage;
