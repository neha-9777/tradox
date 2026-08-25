import { useState, useEffect } from 'react';
import {
  Wallet,
  IndianRupee,
  TrendingUp,
  Percent,
  Plus,
  ArrowUpRight,
  ArrowDownRight,
  Briefcase,
  X,
  RefreshCw,
  Trash2,
  Zap
} from 'lucide-react';
import StatCard from '../components/ui/StatCard';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import { portfolioApi } from '../api/portfolioApi';
import { pnlApi } from '../api/pnlApi';
import { marketApi } from '../api/marketApi';
import { brokerApi } from '../api/brokerApi';
import { showToast } from '../components/ui/Toast';

export default function PortfolioPage() {
  const [portfolios, setPortfolios] = useState([]);
  const [selectedPortfolio, setSelectedPortfolio] = useState(null);
  const [pnlSummary, setPnlSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [refreshingPrices, setRefreshingPrices] = useState(false);
  const [isTxModalOpen, setIsTxModalOpen] = useState(false);
  const [isCreatePortfolioModalOpen, setIsCreatePortfolioModalOpen] = useState(false);

  // New Transaction Form State
  const [txForm, setTxForm] = useState({
    symbol: '',
    assetName: '',
    exchange: 'NSE',
    type: 'BUY',
    quantity: '',
    price: '',
    notes: ''
  });
  const [submittingTx, setSubmittingTx] = useState(false);

  // New Portfolio Form State
  const [newPortfolioName, setNewPortfolioName] = useState('');
  const [newPortfolioDesc, setNewPortfolioDesc] = useState('');
  const [submittingPortfolio, setSubmittingPortfolio] = useState(false);
  const [syncingMockData, setSyncingMockData] = useState(false);

  const handleSyncMockBroker = async () => {
    try {
      setSyncingMockData(true);
      const res = await brokerApi.triggerDevMockSync();
      if (res.status === 'SUCCESS') {
        showToast(`Synced ${res.holdingsSynced} holdings & ${res.transactionsSynced} trades!`, 'success');
      } else {
        showToast(`Sync failed: ${res.errorMessage}`, 'error');
      }
      await fetchPortfolios();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to sync mock broker data', 'error');
    } finally {
      setSyncingMockData(false);
    }
  };

  useEffect(() => {
    fetchPortfolios();
  }, []);

  const fetchPortfolios = async () => {
    try {
      setLoading(true);
      const [ports, pnl] = await Promise.all([
        portfolioApi.getUserPortfolios(),
        pnlApi.getPnLSummary()
      ]);
      setPortfolios(ports);
      setPnlSummary(pnl);
      if (ports.length > 0) {
        // Keep selected if available, or default
        const defaultPort = ports.find(p => p.isDefault) || ports[0];
        setSelectedPortfolio(prev => {
          if (prev) {
            const updated = ports.find(p => p.id === prev.id);
            return updated || defaultPort;
          }
          return defaultPort;
        });
      }
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to fetch portfolios', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleRefreshMarketPrices = async () => {
    if (!selectedPortfolio) return;
    try {
      setRefreshingPrices(true);
      await marketApi.refreshPortfolioHoldings(selectedPortfolio.id);
      showToast('Market prices refreshed successfully!', 'success');
      await fetchPortfolios();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to refresh market prices', 'error');
    } finally {
      setRefreshingPrices(false);
    }
  };


  const handleCreatePortfolio = async (e) => {
    e.preventDefault();
    if (!newPortfolioName.trim()) return;
    try {
      setSubmittingPortfolio(true);
      const created = await portfolioApi.createPortfolio({
        name: newPortfolioName.trim(),
        description: newPortfolioDesc.trim(),
        currency: 'INR'
      });
      showToast('Portfolio created successfully!');
      setNewPortfolioName('');
      setNewPortfolioDesc('');
      setIsCreatePortfolioModalOpen(false);
      await fetchPortfolios();
      setSelectedPortfolio(created);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to create portfolio', 'error');
    } finally {
      setSubmittingPortfolio(false);
    }
  };

  const handleAddTransaction = async (e) => {
    e.preventDefault();
    if (!selectedPortfolio) return;
    if (!txForm.symbol.trim() || !txForm.quantity || !txForm.price) {
      showToast('Please fill all required transaction fields', 'error');
      return;
    }

    try {
      setSubmittingTx(true);
      await portfolioApi.addTransaction(selectedPortfolio.id, {
        symbol: txForm.symbol.trim().toUpperCase(),
        assetName: txForm.assetName.trim() || txForm.symbol.trim().toUpperCase(),
        exchange: txForm.exchange,
        type: txForm.type,
        quantity: parseFloat(txForm.quantity),
        price: parseFloat(txForm.price),
        notes: txForm.notes
      });
      showToast(`${txForm.type} transaction added!`);
      setTxForm({
        symbol: '',
        assetName: '',
        exchange: 'NSE',
        type: 'BUY',
        quantity: '',
        price: '',
        notes: ''
      });
      setIsTxModalOpen(false);
      await fetchPortfolios();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to add transaction', 'error');
    } finally {
      setSubmittingTx(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] text-slate-400 gap-3">
        <RefreshCw className="w-8 h-8 animate-spin text-indigo-400" />
        <p className="text-sm font-medium">Loading portfolio data...</p>
      </div>
    );
  }

  const holdings = selectedPortfolio?.holdings || [];
  const totalInvested = selectedPortfolio?.totalInvested || 0;
  const currentValue = selectedPortfolio?.currentValue || 0;
  const totalPnL = selectedPortfolio?.totalPnL || 0;
  const returnPercentage = selectedPortfolio?.returnPercentage || 0;
  const isGain = totalPnL >= 0;

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Top Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-3">
            <Briefcase className="w-7 h-7 text-indigo-400" />
            Portfolio Management
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            Track positions, average buy price, valuation, and return performance
          </p>
        </div>

        {/* Portfolio Dropdown & Action Buttons */}
        <div className="flex flex-wrap items-center gap-3">
          <select
            value={selectedPortfolio?.id || ''}

            onChange={(e) => {
              const port = portfolios.find(p => p.id === Number(e.target.value));
              if (port) setSelectedPortfolio(port);
            }}
            className="bg-slate-900 border border-slate-700 text-white text-sm rounded-xl px-3.5 py-2 focus:outline-none focus:border-indigo-500"
          >
            {portfolios.map(p => (
              <option key={p.id} value={p.id}>
                {p.name} {p.isDefault ? '(Default)' : ''}
              </option>
            ))}
          </select>

          <Button
            variant="secondary"
            size="sm"
            onClick={handleSyncMockBroker}
            disabled={syncingMockData}
            className="border-purple-500/30 text-purple-300 hover:bg-purple-500/10 shadow-lg shadow-purple-900/20"
          >
            <Zap className={`w-4 h-4 mr-1.5 ${syncingMockData ? 'animate-spin text-purple-400' : 'text-purple-400'}`} />
            {syncingMockData ? 'Syncing...' : 'Sync Mock Broker'}
          </Button>

          <Button
            variant="secondary"
            size="sm"
            onClick={() => setIsCreatePortfolioModalOpen(true)}
          >
            + New Portfolio
          </Button>

          <Button
            variant="primary"
            size="sm"
            onClick={() => setIsTxModalOpen(true)}
            className="shadow-lg shadow-indigo-600/30"
          >
            <Plus className="w-4 h-4 mr-1.5" />
            Add Transaction
          </Button>
        </div>
      </div>

      {/* Stat Cards Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
        <StatCard
          title="Total Invested"
          value={totalInvested.toLocaleString('en-IN', { maximumFractionDigits: 2 })}
          prefix="₹"
          icon={Wallet}
          change="Cost basis"
          changeType="neutral"
        />
        <StatCard
          title="Current Valuation"
          value={currentValue.toLocaleString('en-IN', { maximumFractionDigits: 2 })}
          prefix="₹"
          icon={IndianRupee}
          change={isGain ? 'In profit' : 'In loss'}
          changeType={isGain ? 'gain' : 'loss'}
        />
        <StatCard
          title="Unrealized P&L"
          value={Math.abs(totalPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
          prefix={isGain ? '+₹' : '-₹'}
          icon={TrendingUp}
          change={isGain ? 'Profit' : 'Loss'}
          changeType={isGain ? 'gain' : 'loss'}
        />
        <StatCard
          title="Return Percentage"
          value={returnPercentage.toFixed(2)}
          suffix="%"
          icon={Percent}
          change={isGain ? '+All time' : '-All time'}
          changeType={isGain ? 'gain' : 'loss'}
        />
      </div>

      {/* P&L Engine Realized vs. Unrealized Breakdown */}
      {pnlSummary && (
        <GlassCard className="p-6">
          <div className="flex items-center justify-between mb-4 border-b border-slate-800 pb-3">
            <div>
              <h3 className="text-base font-bold text-white flex items-center gap-2">
                <TrendingUp className="w-5 h-5 text-emerald-400" />
                P&L Engine Analytics
              </h3>
              <p className="text-xs text-slate-400 mt-0.5">
                Realized gains on closed sales vs. unrealized gains on open positions
              </p>
            </div>
            <span className="text-xs font-semibold px-3 py-1 rounded-full bg-indigo-500/10 border border-indigo-500/20 text-indigo-400">
              Avg Cost Basis Method
            </span>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
            <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5">
              <p className="text-[11px] font-medium text-slate-400 uppercase tracking-wider">Realized P&L</p>
              <p className={`text-lg font-extrabold mt-1 ${pnlSummary.totalRealizedPnL >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                {pnlSummary.totalRealizedPnL >= 0 ? '+' : ''}₹{Number(pnlSummary.totalRealizedPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
              </p>
            </div>

            <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5">
              <p className="text-[11px] font-medium text-slate-400 uppercase tracking-wider">Unrealized P&L</p>
              <p className={`text-lg font-extrabold mt-1 ${pnlSummary.totalUnrealizedPnL >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                {pnlSummary.totalUnrealizedPnL >= 0 ? '+' : ''}₹{Number(pnlSummary.totalUnrealizedPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
              </p>
            </div>

            <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5">
              <p className="text-[11px] font-medium text-slate-400 uppercase tracking-wider">P&L Today</p>
              <p className={`text-lg font-extrabold mt-1 ${pnlSummary.dailyPnL >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                {pnlSummary.dailyPnL >= 0 ? '+' : ''}₹{Number(pnlSummary.dailyPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
              </p>
            </div>

            <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5">
              <p className="text-[11px] font-medium text-slate-400 uppercase tracking-wider">P&L (Past 30 Days)</p>
              <p className={`text-lg font-extrabold mt-1 ${pnlSummary.monthlyPnL >= 0 ? 'text-emerald-400' : 'text-red-400'}`}>
                {pnlSummary.monthlyPnL >= 0 ? '+' : ''}₹{Number(pnlSummary.monthlyPnL).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
              </p>
            </div>
          </div>
        </GlassCard>
      )}

      {/* Holdings Table Section */}
      <GlassCard className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-lg font-bold text-white">Current Holdings</h2>
            <p className="text-xs text-slate-400 mt-0.5">
              {holdings.length} active position{holdings.length === 1 ? '' : 's'} in {selectedPortfolio?.name}
            </p>
          </div>
          <Button
            variant="ghost"
            size="sm"
            onClick={handleRefreshMarketPrices}
            disabled={refreshingPrices}
            className="text-xs text-slate-400 hover:text-white"
          >
            <RefreshCw className={`w-3.5 h-3.5 mr-1 ${refreshingPrices ? 'animate-spin text-indigo-400' : ''}`} />
            {refreshingPrices ? 'Syncing Prices...' : 'Refresh Prices'}
          </Button>
        </div>

        {holdings.length === 0 ? (
          <div className="text-center py-16 px-4 border border-dashed border-slate-800 rounded-2xl">
            <Briefcase className="w-12 h-12 text-slate-600 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-300">No holdings found</h3>
            <p className="text-slate-500 text-xs mt-1 mb-5 max-w-sm mx-auto">
              Import mock broker data from JSON or add manual transactions to build your portfolio positions.
            </p>
            <div className="flex flex-wrap items-center justify-center gap-3">
              <Button size="sm" onClick={handleSyncMockBroker} disabled={syncingMockData} className="bg-purple-600 hover:bg-purple-500 shadow-lg shadow-purple-600/30">
                <Zap className={`w-4 h-4 mr-1.5 ${syncingMockData ? 'animate-spin' : ''}`} />
                {syncingMockData ? 'Syncing...' : 'Import Mock Broker Data'}
              </Button>
              <Button size="sm" variant="secondary" onClick={() => setIsTxModalOpen(true)}>
                <Plus className="w-4 h-4 mr-1.5" />
                Add Transaction
              </Button>
            </div>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm border-collapse">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 text-xs font-semibold uppercase tracking-wider">
                  <th className="pb-3 px-3">Asset / Symbol</th>
                  <th className="pb-3 px-3">Exchange</th>
                  <th className="pb-3 px-3 text-right">Quantity</th>
                  <th className="pb-3 px-3 text-right">Avg Price</th>
                  <th className="pb-3 px-3 text-right">Current Price</th>
                  <th className="pb-3 px-3 text-right">Total Invested</th>
                  <th className="pb-3 px-3 text-right">Current Value</th>
                  <th className="pb-3 px-3 text-right">Unrealized P&L</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {holdings.map((holding) => {
                  const pnl = holding.unrealizedPnL || 0;
                  const pnlPct = holding.unrealizedPnLPercentage || 0;
                  const isPositive = pnl >= 0;

                  return (
                    <tr key={holding.id} className="hover:bg-slate-800/30 transition-colors">
                      <td className="py-4 px-3">
                        <div className="flex items-center gap-3">
                          <div className="w-8 h-8 rounded-lg bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 flex items-center justify-center font-bold text-xs">
                            {holding.asset.symbol.substring(0, 2)}
                          </div>
                          <div>
                            <p className="font-bold text-white">{holding.asset.symbol}</p>
                            <p className="text-xs text-slate-400">{holding.asset.name}</p>
                          </div>
                        </div>
                      </td>
                      <td className="py-4 px-3">
                        <span className="px-2 py-0.5 rounded text-[11px] font-semibold bg-slate-800 text-slate-300">
                          {holding.asset.exchange}
                        </span>
                      </td>
                      <td className="py-4 px-3 text-right font-medium text-slate-200">
                        {holding.quantity}
                      </td>
                      <td className="py-4 px-3 text-right text-slate-300">
                        ₹{Number(holding.averageBuyPrice).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </td>
                      <td className="py-4 px-3 text-right text-slate-300 font-medium">
                        ₹{Number(holding.currentPrice || holding.averageBuyPrice).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </td>
                      <td className="py-4 px-3 text-right text-slate-300">
                        ₹{Number(holding.totalInvested).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </td>
                      <td className="py-4 px-3 text-right font-bold text-white">
                        ₹{Number(holding.currentValue).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </td>
                      <td className="py-4 px-3 text-right">
                        <div className={`inline-flex items-center gap-1 font-bold ${isPositive ? 'text-emerald-400' : 'text-red-400'}`}>
                          {isPositive ? <ArrowUpRight className="w-3.5 h-3.5" /> : <ArrowDownRight className="w-3.5 h-3.5" />}
                          <span>₹{Math.abs(pnl).toLocaleString('en-IN', { maximumFractionDigits: 2 })}</span>
                          <span className="text-xs font-normal">({isPositive ? '+' : ''}{pnlPct.toFixed(2)}%)</span>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </GlassCard>

      {/* Add Transaction Modal */}
      {isTxModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#0c0d1b] border border-slate-800 rounded-2xl w-full max-w-lg p-6 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <Plus className="w-5 h-5 text-indigo-400" />
                Add Transaction to {selectedPortfolio?.name}
              </h3>
              <button
                onClick={() => setIsTxModalOpen(false)}
                className="text-slate-400 hover:text-white p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddTransaction} className="space-y-4">
              {/* Type Switcher (BUY/SELL) */}
              <div className="grid grid-cols-2 gap-2 bg-slate-900 p-1 rounded-xl border border-slate-800">
                <button
                  type="button"
                  onClick={() => setTxForm({ ...txForm, type: 'BUY' })}
                  className={`py-2 rounded-lg text-xs font-bold transition-all ${
                    txForm.type === 'BUY'
                      ? 'bg-emerald-500 text-white shadow-md'
                      : 'text-slate-400 hover:text-white'
                  }`}
                >
                  BUY
                </button>
                <button
                  type="button"
                  onClick={() => setTxForm({ ...txForm, type: 'SELL' })}
                  className={`py-2 rounded-lg text-xs font-bold transition-all ${
                    txForm.type === 'SELL'
                      ? 'bg-red-500 text-white shadow-md'
                      : 'text-slate-400 hover:text-white'
                  }`}
                >
                  SELL
                </button>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Symbol (Required)"
                  placeholder="e.g. RELIANCE, TCS"
                  value={txForm.symbol}
                  onChange={(e) => setTxForm({ ...txForm, symbol: e.target.value })}
                  required
                />
                <Input
                  label="Exchange"
                  placeholder="NSE / BSE"
                  value={txForm.exchange}
                  onChange={(e) => setTxForm({ ...txForm, exchange: e.target.value })}
                />
              </div>

              <Input
                label="Company / Asset Name"
                placeholder="e.g. Reliance Industries Ltd"
                value={txForm.assetName}
                onChange={(e) => setTxForm({ ...txForm, assetName: e.target.value })}
              />

              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Quantity"
                  type="number"
                  step="any"
                  placeholder="e.g. 10"
                  value={txForm.quantity}
                  onChange={(e) => setTxForm({ ...txForm, quantity: e.target.value })}
                  required
                />
                <Input
                  label="Price per Share (₹)"
                  type="number"
                  step="any"
                  placeholder="e.g. 2850.50"
                  value={txForm.price}
                  onChange={(e) => setTxForm({ ...txForm, price: e.target.value })}
                  required
                />
              </div>

              <Input
                label="Notes (Optional)"
                placeholder="e.g. Long term accumulation"
                value={txForm.notes}
                onChange={(e) => setTxForm({ ...txForm, notes: e.target.value })}
              />

              <div className="flex justify-end gap-3 pt-3 border-t border-slate-800">
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => setIsTxModalOpen(false)}
                >
                  Cancel
                </Button>
                <Button type="submit" loading={submittingTx}>
                  Submit Transaction
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Create Portfolio Modal */}
      {isCreatePortfolioModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#0c0d1b] border border-slate-800 rounded-2xl w-full max-w-md p-6 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <Briefcase className="w-5 h-5 text-indigo-400" />
                Create Portfolio
              </h3>
              <button
                onClick={() => setIsCreatePortfolioModalOpen(false)}
                className="text-slate-400 hover:text-white p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleCreatePortfolio} className="space-y-4">
              <Input
                label="Portfolio Name"
                placeholder="e.g. High Growth Equity"
                value={newPortfolioName}
                onChange={(e) => setNewPortfolioName(e.target.value)}
                required
              />
              <Input
                label="Description"
                placeholder="e.g. Tech stocks & growth portfolio"
                value={newPortfolioDesc}
                onChange={(e) => setNewPortfolioDesc(e.target.value)}
              />

              <div className="flex justify-end gap-3 pt-3 border-t border-slate-800">
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => setIsCreatePortfolioModalOpen(false)}
                >
                  Cancel
                </Button>
                <Button type="submit" loading={submittingPortfolio}>
                  Create Portfolio
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}


