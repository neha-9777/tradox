import { useState, useEffect } from 'react';
import {
  ArrowLeftRight,
  Search,
  Plus,
  Trash2,
  RefreshCw,
  X,
  Calendar,
  Tag
} from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import { portfolioApi } from '../api/portfolioApi';
import { showToast } from '../components/ui/Toast';

export default function TransactionsPage() {
  const [portfolios, setPortfolios] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterType, setFilterType] = useState('ALL'); // ALL, BUY, SELL
  const [isTxModalOpen, setIsTxModalOpen] = useState(false);
  const [selectedPortfolioId, setSelectedPortfolioId] = useState('');

  // Transaction form state
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

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    try {
      setLoading(true);
      const [ports, txs] = await Promise.all([
        portfolioApi.getUserPortfolios(),
        portfolioApi.getAllTransactions()
      ]);
      setPortfolios(ports);
      setTransactions(txs);
      if (ports.length > 0) {
        const defaultPort = ports.find(p => p.isDefault) || ports[0];
        setSelectedPortfolioId(defaultPort.id.toString());
      }
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to fetch transaction data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteTransaction = async (portfolioId, txId) => {
    if (!window.confirm('Are you sure you want to delete this transaction? Holding positions will be recalculated.')) {
      return;
    }
    try {
      await portfolioApi.deleteTransaction(portfolioId, txId);
      showToast('Transaction deleted');
      fetchInitialData();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to delete transaction', 'error');
    }
  };

  const handleAddTransaction = async (e) => {
    e.preventDefault();
    if (!selectedPortfolioId) {
      showToast('Please select a portfolio first', 'error');
      return;
    }
    if (!txForm.symbol.trim() || !txForm.quantity || !txForm.price) {
      showToast('Please fill required fields', 'error');
      return;
    }

    try {
      setSubmittingTx(true);
      await portfolioApi.addTransaction(parseInt(selectedPortfolioId, 10), {
        symbol: txForm.symbol.trim().toUpperCase(),
        assetName: txForm.assetName.trim() || txForm.symbol.trim().toUpperCase(),
        exchange: txForm.exchange,
        type: txForm.type,
        quantity: parseFloat(txForm.quantity),
        price: parseFloat(txForm.price),
        notes: txForm.notes
      });
      showToast(`${txForm.type} transaction recorded!`);
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
      fetchInitialData();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to add transaction', 'error');
    } finally {
      setSubmittingTx(false);
    }
  };

  const filteredTransactions = transactions.filter((tx) => {
    const matchesSearch =
      tx.asset.symbol.toLowerCase().includes(searchQuery.toLowerCase()) ||
      tx.asset.name.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesType = filterType === 'ALL' || tx.type === filterType;
    return matchesSearch && matchesType;
  });

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] text-slate-400 gap-3">
        <RefreshCw className="w-8 h-8 animate-spin text-indigo-400" />
        <p className="text-sm font-medium">Loading transaction log...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-3">
            <ArrowLeftRight className="w-7 h-7 text-indigo-400" />
            Transaction History
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            Review, filter, and track all your trade executions across portfolios
          </p>
        </div>

        <Button
          variant="primary"
          size="sm"
          onClick={() => setIsTxModalOpen(true)}
          className="shadow-lg shadow-indigo-600/30"
        >
          <Plus className="w-4 h-4 mr-1.5" />
          Log New Trade
        </Button>
      </div>

      {/* Filter & Search Toolbar */}
      <GlassCard className="p-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          {/* Search bar */}
          <div className="relative flex-1 max-w-md">
            <Search className="w-4 h-4 text-slate-500 absolute left-3.5 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              placeholder="Search by symbol or company name..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-slate-900 border border-slate-700 rounded-xl pl-10 pr-4 py-2 text-sm text-slate-200 placeholder-slate-500 focus:outline-none focus:border-indigo-500"
            />
          </div>

          {/* Type Filter Buttons */}
          <div className="flex items-center gap-2">
            {['ALL', 'BUY', 'SELL'].map((type) => (
              <button
                key={type}
                onClick={() => setFilterType(type)}
                className={`px-4 py-1.5 rounded-xl text-xs font-bold transition-all ${
                  filterType === type
                    ? type === 'BUY'
                      ? 'bg-emerald-500 text-white'
                      : type === 'SELL'
                      ? 'bg-red-500 text-white'
                      : 'bg-indigo-600 text-white'
                    : 'bg-slate-900 text-slate-400 hover:text-white border border-slate-800'
                }`}
              >
                {type}
              </button>
            ))}
          </div>
        </div>
      </GlassCard>

      {/* Transactions Table */}
      <GlassCard className="p-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-bold text-white">Execution Log</h2>
          <span className="text-xs text-slate-400 font-medium">
            Showing {filteredTransactions.length} of {transactions.length} trade records
          </span>
        </div>

        {filteredTransactions.length === 0 ? (
          <div className="text-center py-16 px-4 border border-dashed border-slate-800 rounded-2xl">
            <ArrowLeftRight className="w-12 h-12 text-slate-600 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-300">No transactions recorded</h3>
            <p className="text-slate-500 text-xs mt-1 mb-5 max-w-sm mx-auto">
              No transactions match your current search/filter. Log a trade to keep your portfolio up to date.
            </p>
            <Button size="sm" onClick={() => setIsTxModalOpen(true)}>
              <Plus className="w-4 h-4 mr-1.5" />
              Log Trade
            </Button>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-sm border-collapse">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 text-xs font-semibold uppercase tracking-wider">
                  <th className="pb-3 px-3">Date</th>
                  <th className="pb-3 px-3">Type</th>
                  <th className="pb-3 px-3">Asset</th>
                  <th className="pb-3 px-3 text-right">Quantity</th>
                  <th className="pb-3 px-3 text-right">Price</th>
                  <th className="pb-3 px-3 text-right">Total Amount</th>
                  <th className="pb-3 px-3">Notes</th>
                  <th className="pb-3 px-3 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {filteredTransactions.map((tx) => {
                  const isBuy = tx.type === 'BUY';
                  const dateStr = new Date(tx.transactionDate).toLocaleDateString('en-IN', {
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric'
                  });

                  return (
                    <tr key={tx.id} className="hover:bg-slate-800/30 transition-colors">
                      <td className="py-4 px-3 text-xs text-slate-400 font-medium">
                        <div className="flex items-center gap-1.5">
                          <Calendar className="w-3.5 h-3.5 text-slate-500" />
                          {dateStr}
                        </div>
                      </td>
                      <td className="py-4 px-3">
                        <span
                          className={`px-2.5 py-1 rounded-md text-xs font-bold ${
                            isBuy
                              ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                              : 'bg-red-500/10 text-red-400 border border-red-500/20'
                          }`}
                        >
                          {tx.type}
                        </span>
                      </td>
                      <td className="py-4 px-3">
                        <div>
                          <p className="font-bold text-white">{tx.asset.symbol}</p>
                          <p className="text-xs text-slate-400">{tx.asset.name}</p>
                        </div>
                      </td>
                      <td className="py-4 px-3 text-right font-medium text-slate-200">
                        {tx.quantity}
                      </td>
                      <td className="py-4 px-3 text-right text-slate-300">
                        ₹{Number(tx.price).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </td>
                      <td className="py-4 px-3 text-right font-bold text-white">
                        ₹{Number(tx.totalAmount).toLocaleString('en-IN', { maximumFractionDigits: 2 })}
                      </td>
                      <td className="py-4 px-3 text-xs text-slate-400 max-w-xs truncate">
                        {tx.notes || '—'}
                      </td>
                      <td className="py-4 px-3 text-right">
                        <button
                          onClick={() => handleDeleteTransaction(tx.portfolioId, tx.id)}
                          className="p-1.5 rounded-lg text-slate-500 hover:text-red-400 hover:bg-red-500/10 transition-colors"
                          title="Delete Transaction"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </GlassCard>

      {/* Log Trade Modal */}
      {isTxModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#0c0d1b] border border-slate-800 rounded-2xl w-full max-w-lg p-6 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <Plus className="w-5 h-5 text-indigo-400" />
                Log Trade Execution
              </h3>
              <button
                onClick={() => setIsTxModalOpen(false)}
                className="text-slate-400 hover:text-white p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleAddTransaction} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                  Select Target Portfolio
                </label>
                <select
                  value={selectedPortfolioId}
                  onChange={(e) => setSelectedPortfolioId(e.target.value)}
                  className="w-full bg-slate-900 border border-slate-700 text-white text-sm rounded-xl px-3.5 py-2 focus:outline-none focus:border-indigo-500"
                >
                  {portfolios.map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.name} {p.isDefault ? '(Default)' : ''}
                    </option>
                  ))}
                </select>
              </div>

              {/* Type Switcher */}
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
                  label="Symbol"
                  placeholder="e.g. INFYS, INFY"
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
                label="Asset / Company Name"
                placeholder="e.g. Infosys Ltd"
                value={txForm.assetName}
                onChange={(e) => setTxForm({ ...txForm, assetName: e.target.value })}
              />

              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Quantity"
                  type="number"
                  step="any"
                  placeholder="e.g. 15"
                  value={txForm.quantity}
                  onChange={(e) => setTxForm({ ...txForm, quantity: e.target.value })}
                  required
                />
                <Input
                  label="Price (₹)"
                  type="number"
                  step="any"
                  placeholder="e.g. 1650.00"
                  value={txForm.price}
                  onChange={(e) => setTxForm({ ...txForm, price: e.target.value })}
                  required
                />
              </div>

              <Input
                label="Notes"
                placeholder="e.g. Earnings breakout entry"
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
                  Log Execution
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
