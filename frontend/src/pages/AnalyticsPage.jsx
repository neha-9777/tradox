import { useState, useEffect } from 'react';
import {
  BarChart3,
  PieChart as PieIcon,
  TrendingUp,
  TrendingDown,
  AlertTriangle,
  ShieldAlert,
  CheckCircle2,
  RefreshCw,
  Award
} from 'lucide-react';
import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid
} from 'recharts';
import StatCard from '../components/ui/StatCard';
import GlassCard from '../components/ui/GlassCard';
import { analyticsApi } from '../api/analyticsApi';
import { showToast } from '../components/ui/Toast';

const PIE_COLORS = ['#6366f1', '#a855f7', '#ec4899', '#10b981', '#f59e0b', '#06b6d4', '#3b82f6'];

const CustomPieTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    const data = payload[0];
    return (
      <div className="glass-card px-3.5 py-2.5 shadow-xl border border-slate-700/60">
        <p className="text-xs font-bold text-white mb-0.5">{data.name}</p>
        <p className="text-xs text-indigo-400 font-semibold">
          ₹{Number(data.value).toLocaleString('en-IN', { maximumFractionDigits: 2 })} ({data.payload.percentage.toFixed(1)}%)
        </p>
      </div>
    );
  }
  return null;
};

export default function AnalyticsPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [period, setPeriod] = useState('1Y');
  const [trendData, setTrendData] = useState([]);
  const [loadingTrend, setLoadingTrend] = useState(false);

  useEffect(() => {
    fetchAnalytics();
  }, []);

  useEffect(() => {
    fetchTrend(period);
  }, [period]);

  const fetchAnalytics = async () => {
    try {
      setLoading(true);
      const res = await analyticsApi.getDashboardAnalytics();
      setData(res);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to load analytics', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchTrend = async (selectedPeriod) => {
    try {
      setLoadingTrend(true);
      const res = await analyticsApi.getPerformanceTrend(selectedPeriod);
      setTrendData(res);
    } catch {
      // Fallback
    } finally {
      setLoadingTrend(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] text-slate-400 gap-3">
        <RefreshCw className="w-8 h-8 animate-spin text-indigo-400" />
        <p className="text-sm font-medium">Analyzing portfolio metrics...</p>
      </div>
    );
  }

  const summary = data?.portfolioSummary || {};
  const assetAlloc = data?.assetAllocation || [];
  const sectorAlloc = data?.sectorAllocation || [];
  const best = data?.bestPerformers || [];
  const worst = data?.worstPerformers || [];
  const alerts = data?.concentrationAlerts || [];
  const history = data?.historicalPerformance || [];

  const totalInvested = summary.totalInvestment || 0;
  const currentValue = summary.currentValue || 0;
  const totalPnL = summary.totalPnL || 0;
  const returnPct = summary.returnPercentage || 0;
  const isGain = totalPnL >= 0;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-3">
            <BarChart3 className="w-7 h-7 text-indigo-400" />
            Portfolio Analytics & Insights
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            Diversification, sector allocation, top stock performers, and concentration warnings
          </p>
        </div>

        <button
          onClick={fetchAnalytics}
          className="self-start sm:self-center px-4 py-2 rounded-xl bg-slate-900 border border-slate-800 hover:border-slate-700 text-slate-300 hover:text-white text-xs font-semibold flex items-center gap-2 transition-all"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Re-Analyze
        </button>
      </div>

      {/* Concentration Risk Alerts Banner */}
      {alerts.length > 0 ? (
        <div className="bg-amber-500/10 border border-amber-500/30 rounded-2xl p-5 space-y-3">
          <div className="flex items-center gap-2.5 text-amber-400 font-bold text-sm">
            <ShieldAlert className="w-5 h-5" />
            <span>Portfolio Concentration Risk Alerts ({alerts.length})</span>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
            {alerts.map((alert, idx) => (
              <div
                key={idx}
                className="bg-slate-950/70 border border-amber-500/20 rounded-xl p-3.5 flex items-start gap-3"
              >
                <AlertTriangle className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
                <div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-bold text-white">{alert.symbol}</span>
                    <span
                      className={`text-[10px] font-extrabold px-2 py-0.5 rounded-full uppercase ${
                        alert.riskLevel === 'HIGH'
                          ? 'bg-red-500/20 text-red-400 border border-red-500/30'
                          : 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                      }`}
                    >
                      {alert.riskLevel} Risk
                    </span>
                  </div>
                  <p className="text-xs text-slate-300 mt-1">{alert.message}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="bg-emerald-500/10 border border-emerald-500/20 rounded-2xl px-5 py-3.5 flex items-center gap-3 text-emerald-400 text-xs font-semibold">
          <CheckCircle2 className="w-4 h-4 shrink-0" />
          <span>Well-diversified portfolio! No single holding exceeds 25% concentration threshold.</span>
        </div>
      )}

      {/* Allocation Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Asset Type Allocation */}
        <GlassCard className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <PieIcon className="w-4 h-4 text-indigo-400" />
              Asset Allocation
            </h2>
            <span className="text-xs text-slate-400">{assetAlloc.length} Asset Types</span>
          </div>

          {assetAlloc.length === 0 ? (
            <div className="text-center py-12 text-slate-500 text-xs">No active asset data</div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-12 gap-4 items-center">
              <div className="sm:col-span-7 h-56">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={assetAlloc}
                      dataKey="value"
                      nameKey="label"
                      cx="50%"
                      cy="50%"
                      innerRadius={50}
                      outerRadius={80}
                      paddingAngle={4}
                    >
                      {assetAlloc.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomPieTooltip />} />
                  </PieChart>
                </ResponsiveContainer>
              </div>

              {/* Legend List */}
              <div className="sm:col-span-5 space-y-2 max-h-56 overflow-y-auto pr-1">
                {assetAlloc.map((item, idx) => (
                  <div key={item.label} className="flex items-center justify-between text-xs">
                    <div className="flex items-center gap-2 truncate">
                      <span
                        className="w-2.5 h-2.5 rounded-full shrink-0"
                        style={{ backgroundColor: PIE_COLORS[idx % PIE_COLORS.length] }}
                      />
                      <span className="text-slate-300 font-medium truncate">{item.label}</span>
                    </div>
                    <span className="text-white font-bold">{item.percentage.toFixed(1)}%</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </GlassCard>

        {/* Sector Allocation */}
        <GlassCard className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-base font-bold text-white flex items-center gap-2">
              <PieIcon className="w-4 h-4 text-purple-400" />
              Sector Allocation
            </h2>
            <span className="text-xs text-slate-400">{sectorAlloc.length} Sectors</span>
          </div>

          {sectorAlloc.length === 0 ? (
            <div className="text-center py-12 text-slate-500 text-xs">No active sector data</div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-12 gap-4 items-center">
              <div className="sm:col-span-7 h-56">
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={sectorAlloc}
                      dataKey="value"
                      nameKey="label"
                      cx="50%"
                      cy="50%"
                      innerRadius={50}
                      outerRadius={80}
                      paddingAngle={4}
                    >
                      {sectorAlloc.map((entry, index) => (
                        <Cell key={`cell-${index}`} fill={PIE_COLORS[(index + 3) % PIE_COLORS.length]} />
                      ))}
                    </Pie>
                    <Tooltip content={<CustomPieTooltip />} />
                  </PieChart>
                </ResponsiveContainer>
              </div>

              {/* Legend List */}
              <div className="sm:col-span-5 space-y-2 max-h-56 overflow-y-auto pr-1">
                {sectorAlloc.map((item, idx) => (
                  <div key={item.label} className="flex items-center justify-between text-xs">
                    <div className="flex items-center gap-2 truncate">
                      <span
                        className="w-2.5 h-2.5 rounded-full shrink-0"
                        style={{ backgroundColor: PIE_COLORS[(idx + 3) % PIE_COLORS.length] }}
                      />
                      <span className="text-slate-300 font-medium truncate">{item.label}</span>
                    </div>
                    <span className="text-white font-bold">{item.percentage.toFixed(1)}%</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </GlassCard>
      </div>

      {/* Best & Worst Performers Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Best Performers */}
        <GlassCard className="p-6">
          <div className="flex items-center justify-between mb-4 border-b border-slate-800 pb-3">
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <Award className="w-5 h-5 text-emerald-400" />
              Top Stock Performers
            </h3>
            <span className="text-xs text-emerald-400 font-semibold bg-emerald-500/10 px-2.5 py-0.5 rounded-full border border-emerald-500/20">
              Highest Returns
            </span>
          </div>

          {best.length === 0 ? (
            <p className="text-slate-500 text-xs py-6 text-center">No active stock positions</p>
          ) : (
            <div className="space-y-3">
              {best.map((stock) => (
                <div
                  key={stock.symbol}
                  className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex items-center justify-between"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-lg bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center font-bold text-xs">
                      {stock.symbol.substring(0, 2)}
                    </div>
                    <div>
                      <p className="text-sm font-bold text-white">{stock.symbol}</p>
                      <p className="text-xs text-slate-400">{stock.name}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm font-bold text-emerald-400 flex items-center justify-end gap-1">
                      <TrendingUp className="w-3.5 h-3.5" />
                      +{stock.returnPercentage.toFixed(2)}%
                    </p>
                    <p className="text-xs text-slate-400">
                      ₹{Number(stock.totalPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </GlassCard>

        {/* Worst Performers */}
        <GlassCard className="p-6">
          <div className="flex items-center justify-between mb-4 border-b border-slate-800 pb-3">
            <h3 className="text-base font-bold text-white flex items-center gap-2">
              <TrendingDown className="w-5 h-5 text-red-400" />
              Underperforming Stocks
            </h3>
            <span className="text-xs text-red-400 font-semibold bg-red-500/10 px-2.5 py-0.5 rounded-full border border-red-500/20">
              Lowest Returns
            </span>
          </div>

          {worst.length === 0 ? (
            <p className="text-slate-500 text-xs py-6 text-center">No active stock positions</p>
          ) : (
            <div className="space-y-3">
              {worst.map((stock) => (
                <div
                  key={stock.symbol}
                  className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex items-center justify-between"
                >
                  <div className="flex items-center gap-3">
                    <div className="w-8 h-8 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 flex items-center justify-center font-bold text-xs">
                      {stock.symbol.substring(0, 2)}
                    </div>
                    <div>
                      <p className="text-sm font-bold text-white">{stock.symbol}</p>
                      <p className="text-xs text-slate-400">{stock.name}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className={`text-sm font-bold flex items-center justify-end gap-1 ${stock.returnPercentage >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                      {stock.returnPercentage >= 0 ? <TrendingUp className="w-3.5 h-3.5" /> : <TrendingDown className="w-3.5 h-3.5" />}
                      {stock.returnPercentage >= 0 ? '+' : ''}{stock.returnPercentage.toFixed(2)}%
                    </p>
                    <p className="text-xs text-slate-400">
                      ₹{Number(stock.totalPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </GlassCard>
      </div>

      {/* Historical Performance Timeline */}
      <GlassCard className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-bold text-white flex items-center gap-2">
              <span>Portfolio Performance Trend</span>
              {loadingTrend && <RefreshCw className="w-4 h-4 animate-spin text-indigo-400" />}
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">Historical growth overview</p>
          </div>
          <div className="flex items-center gap-2">
            {['1M', '3M', '6M', '1Y', 'ALL'].map((p) => (
              <button
                key={p}
                onClick={() => setPeriod(p)}
                className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all duration-200 ${
                  period === p
                    ? 'bg-indigo-500/15 text-indigo-400 border border-indigo-500/20 font-bold'
                    : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                }`}
              >
                {p}
              </button>
            ))}
          </div>
        </div>

        <div className="h-64">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={trendData}>
              <defs>
                <linearGradient id="areaGradient" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#818cf8" stopOpacity={0.35} />
                  <stop offset="95%" stopColor="#818cf8" stopOpacity={0.0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
              <XAxis dataKey="label" tick={{ fill: '#94a3b8', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#94a3b8', fontSize: 11 }} axisLine={false} tickLine={false} tickFormatter={(v) => `₹${(v/1000).toFixed(0)}k`} />
              <Tooltip />
              <Area type="monotone" dataKey="value" stroke="#818cf8" strokeWidth={2.5} fill="url(#areaGradient)" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </GlassCard>
    </div>
  );
}
