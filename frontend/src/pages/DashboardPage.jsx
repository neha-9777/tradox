import { useState, useEffect } from 'react';
import {
  IndianRupee,
  TrendingUp,
  Wallet,
  Percent,
  RefreshCw
} from 'lucide-react';
import {
  AreaChart,
  Area,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';
import StatCard from '../components/ui/StatCard';
import GlassCard from '../components/ui/GlassCard';
import { useAuth } from '../hooks/useAuth';
import { portfolioApi } from '../api/portfolioApi';
import { analyticsApi } from '../api/analyticsApi';

const CustomTooltip = ({ active, payload }) => {
  if (active && payload && payload.length) {
    const d = payload[0].payload;
    const isPnLPositive = (d.pnl || 0) >= 0;
    return (
      <div className="glass-card px-4 py-3 border border-slate-700/60 shadow-xl">
        <p className="text-xs font-semibold text-slate-400 mb-1">{d.label || d.date}</p>
        <p className="text-sm font-extrabold text-white">
          Valuation: ₹{Number(d.value || 0).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
        </p>
        <p className="text-xs text-slate-300 mt-0.5">
          Invested: ₹{Number(d.invested || 0).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
        </p>
        <p className={`text-xs font-bold mt-1 ${isPnLPositive ? 'text-emerald-400' : 'text-red-400'}`}>
          P&L: {isPnLPositive ? '+' : ''}₹{Number(d.pnl || 0).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
        </p>
      </div>
    );
  }
  return null;
};

export default function DashboardPage() {
  const { user } = useAuth();
  const [summary, setSummary] = useState(null);
  const [period, setPeriod] = useState('1Y');
  const [trendData, setTrendData] = useState([]);
  const [loadingTrend, setLoadingTrend] = useState(false);

  useEffect(() => {
    fetchSummary();
  }, []);

  useEffect(() => {
    fetchPerformanceTrend(period);
  }, [period]);

  const fetchSummary = async () => {
    try {
      const data = await portfolioApi.getOverallSummary();
      setSummary(data);
    } catch {
      // Fallback if network issue
    }
  };

  const fetchPerformanceTrend = async (selectedPeriod) => {
    try {
      setLoadingTrend(true);
      const data = await analyticsApi.getPerformanceTrend(selectedPeriod);
      setTrendData(data);
    } catch {
      // Fallback if network issue
    } finally {
      setLoadingTrend(false);
    }
  };

  const totalInvestment = summary?.totalInvestment || 0;
  const currentValue = summary?.currentValue || 0;
  const totalPnL = summary?.totalPnL || 0;
  const returnPct = summary?.returnPercentage || 0;
  const isGain = totalPnL >= 0;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Greeting Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white">
            Welcome back, <span className="gradient-text">{user?.firstName || 'Trader'}</span>
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            Here's your central portfolio overview for today
          </p>
        </div>
      </div>

      {/* Stat Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        <StatCard
          title="Total Investment"
          value={totalInvestment.toLocaleString('en-IN', { maximumFractionDigits: 2 })}
          prefix="₹"
          icon={Wallet}
          change={`${summary?.activeHoldingsCount || 0} active holdings`}
          changeType="neutral"
        />
        <StatCard
          title="Current Value"
          value={currentValue.toLocaleString('en-IN', { maximumFractionDigits: 2 })}
          prefix="₹"
          icon={IndianRupee}
          change={isGain ? 'Valuation up' : 'Valuation down'}
          changeType={isGain ? 'gain' : 'loss'}
        />
        <StatCard
          title="Total P&L"
          value={Math.abs(totalPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
          prefix={isGain ? '+₹' : '-₹'}
          icon={TrendingUp}
          change={isGain ? 'Unrealized gain' : 'Unrealized loss'}
          changeType={isGain ? 'gain' : 'loss'}
        />
        <StatCard
          title="Return %"
          value={returnPct.toFixed(2)}
          suffix="%"
          icon={Percent}
          change="Overall performance"
          changeType={isGain ? 'gain' : 'loss'}
        />
      </div>

      {/* Performance Chart */}
      <GlassCard className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-semibold text-white flex items-center gap-2">
              <span>Portfolio Performance Trend</span>
              {loadingTrend && <RefreshCw className="w-4 h-4 animate-spin text-indigo-400" />}
            </h2>
            <p className="text-sm text-slate-400 mt-0.5">Historical growth overview</p>
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

        <div className="h-72">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={trendData}>
              <defs>
                <linearGradient id="colorValue" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#6366f1" stopOpacity={0.35} />
                  <stop offset="95%" stopColor="#6366f1" stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
              <XAxis
                dataKey="label"
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#94a3b8', fontSize: 12 }}
              />
              <YAxis
                axisLine={false}
                tickLine={false}
                tick={{ fill: '#94a3b8', fontSize: 12 }}
                tickFormatter={(v) => `₹${(v / 1000).toFixed(0)}K`}
              />
              <Tooltip content={<CustomTooltip />} />
              <Area
                type="monotone"
                dataKey="value"
                stroke="#6366f1"
                strokeWidth={2.5}
                fill="url(#colorValue)"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </GlassCard>
    </div>
  );
}
