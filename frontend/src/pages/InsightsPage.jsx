import { useState, useEffect } from 'react';
import {
  Sparkles,
  ShieldAlert,
  TrendingUp,
  LineChart,
  Lightbulb,
  CheckCircle2,
  AlertTriangle,
  Info,
  RefreshCw,
  Zap,
  ArrowRight
} from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import { insightsApi } from '../api/insightsApi';
import { showToast } from '../components/ui/Toast';

export default function InsightsPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeCategory, setActiveCategory] = useState('ALL');

  useEffect(() => {
    fetchInsights();
  }, []);

  const fetchInsights = async () => {
    try {
      setLoading(true);
      const res = await insightsApi.getInsightsDashboard();
      setData(res);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to load insights', 'error');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] text-slate-400 gap-3">
        <RefreshCw className="w-8 h-8 animate-spin text-indigo-400" />
        <p className="text-sm font-medium">Synthesizing intelligent AI insights...</p>
      </div>
    );
  }

  const score = data?.overallHealthScore ?? 85;
  const riskRating = data?.riskRating || 'MODERATE';
  const executiveSummary = data?.executiveSummary || '';
  const insightsList = (data?.insights || []).filter(item => item.category !== 'AI_COMMENTARY');

  const filteredInsights = activeCategory === 'ALL'
    ? insightsList
    : insightsList.filter(item => item.category === activeCategory);

  const getSeverityIcon = (sev) => {
    switch (sev) {
      case 'DANGER': return <ShieldAlert className="w-5 h-5 text-red-400 shrink-0" />;
      case 'WARNING': return <AlertTriangle className="w-5 h-5 text-amber-400 shrink-0" />;
      case 'SUCCESS': return <CheckCircle2 className="w-5 h-5 text-emerald-400 shrink-0" />;
      default: return <Info className="w-5 h-5 text-indigo-400 shrink-0" />;
    }
  };

  const getSeverityStyle = (sev) => {
    switch (sev) {
      case 'DANGER': return 'border-red-500/30 bg-red-500/5';
      case 'WARNING': return 'border-amber-500/30 bg-amber-500/5';
      case 'SUCCESS': return 'border-emerald-500/30 bg-emerald-500/5';
      default: return 'border-indigo-500/30 bg-indigo-500/5';
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-3">
            <Lightbulb className="w-7 h-7 text-indigo-400" />
            Intelligent Portfolio Insights
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            AI-driven diagnostic checks, concentration alerts, and actionable financial advice
          </p>
        </div>

        <button
          onClick={fetchInsights}
          className="self-start sm:self-center px-4 py-2 rounded-xl bg-slate-900 border border-slate-800 hover:border-slate-700 text-slate-300 hover:text-white text-xs font-semibold flex items-center gap-2 transition-all"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Re-Analyze
        </button>
      </div>

      {/* Portfolio Health Gauge Card */}
      <GlassCard className="p-6">
        <div className="grid grid-cols-1 md:grid-cols-12 gap-6 items-center">
          {/* Health Score Gauge Circle */}
          <div className="md:col-span-4 flex flex-col items-center justify-center p-4 border-r-0 md:border-r border-slate-800">
            <div className="relative w-36 h-36 flex items-center justify-center">
              <svg className="w-full h-full transform -rotate-90" viewBox="0 0 100 100">
                <circle
                  cx="50"
                  cy="50"
                  r="40"
                  stroke="currentColor"
                  strokeWidth="8"
                  className="text-slate-800"
                  fill="transparent"
                />
                <circle
                  cx="50"
                  cy="50"
                  r="40"
                  stroke="currentColor"
                  strokeWidth="8"
                  strokeDasharray={`${(score / 100) * 251} 251`}
                  strokeLinecap="round"
                  className={score >= 75 ? 'text-emerald-400' : score >= 50 ? 'text-amber-400' : 'text-red-400'}
                  fill="transparent"
                />
              </svg>
              <div className="absolute flex flex-col items-center">
                <span className="text-3xl font-extrabold text-white">{score}</span>
                <span className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Health Score</span>
              </div>
            </div>
          </div>

          {/* Synthesis Details */}
          <div className="md:col-span-8 space-y-3">
            <div className="flex items-center gap-2">
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">Assessed Risk Profile:</span>
              <span
                className={`text-xs font-extrabold px-3 py-0.5 rounded-full border ${riskRating === 'AGGRESSIVE'
                    ? 'bg-red-500/10 text-red-400 border-red-500/30'
                    : riskRating === 'CONSERVATIVE'
                      ? 'bg-blue-500/10 text-blue-400 border-blue-500/30'
                      : 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30'
                  }`}
              >
                {riskRating}
              </span>
            </div>

            <h2 className="text-lg font-bold text-white">Executive Portfolio Diagnostics</h2>
            <p className="text-sm text-slate-300 leading-relaxed">{executiveSummary}</p>

            <div className="pt-2 flex items-center gap-4 text-xs text-slate-400">
              <div className="flex items-center gap-1.5">
                <Zap className="w-4 h-4 text-indigo-400" />
                <span>{insightsList.length} Insights Identified</span>
              </div>
              <div className="flex items-center gap-1.5">
                <Lightbulb className="w-4 h-4 text-amber-400" />
                <span>Real-Time Advisory Active</span>
              </div>
            </div>
          </div>
        </div>
      </GlassCard>

      {/* Category Filter Tabs */}
      <div className="flex items-center gap-2 overflow-x-auto pb-1">
        {[
          { id: 'ALL', label: 'All Insights' },
          { id: 'CONCENTRATION', label: 'Risk Warnings' },
          { id: 'PERFORMANCE', label: 'Performance' },
          { id: 'TREND', label: 'Sector Trends' }
        ].map((tab) => (
          <button
            key={tab.id}
            onClick={() => setActiveCategory(tab.id)}
            className={`px-4 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition-all ${activeCategory === tab.id
                ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-600/30'
                : 'bg-slate-900 border border-slate-800 text-slate-400 hover:text-white'
              }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Insight Cards Grid */}
      <div className="space-y-4">
        {filteredInsights.length === 0 ? (
          <GlassCard className="p-12 text-center border-dashed border-slate-800">
            <Lightbulb className="w-12 h-12 text-slate-600 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-300">No insights in this category</h3>
            <p className="text-slate-500 text-xs mt-1">Select another filter tab to inspect portfolio observations.</p>
          </GlassCard>
        ) : (
          filteredInsights.map((item) => (
            <div
              key={item.id}
              className={`border rounded-2xl p-5 transition-all space-y-3 ${getSeverityStyle(item.severity)}`}
            >
              <div className="flex items-start justify-between gap-3">
                <div className="flex items-center gap-3">
                  {getSeverityIcon(item.severity)}
                  <div>
                    <h3 className="text-base font-bold text-white flex items-center gap-2">
                      {item.title}
                      {item.symbol && (
                        <span className="text-xs font-mono font-bold text-indigo-400 bg-indigo-500/10 px-2 py-0.5 rounded">
                          {item.symbol}
                        </span>
                      )}
                    </h3>
                    <span className="text-[10px] font-bold uppercase text-slate-400 tracking-wider">
                      {item.category}
                    </span>
                  </div>
                </div>
              </div>

              <p className="text-sm text-slate-300 leading-relaxed pl-8">{item.description}</p>

              {item.actionableAdvice && (
                <div className="ml-8 mt-2 bg-slate-950/60 border border-slate-800 rounded-xl p-3 flex items-start gap-2 text-xs text-indigo-300 font-medium">
                  <ArrowRight className="w-4 h-4 text-indigo-400 shrink-0 mt-0.5" />
                  <span><strong>Actionable Step:</strong> {item.actionableAdvice}</span>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
}


