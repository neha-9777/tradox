import { useState, useEffect } from 'react';
import {
  Link2,
  Plus,
  RefreshCw,
  Trash2,
  CheckCircle2,
  AlertCircle,
  Clock,
  ShieldCheck,
  X,
  FileText,
  Key,
  Zap
} from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import Input from '../components/ui/Input';
import { brokerApi } from '../api/brokerApi';
import { showToast } from '../components/ui/Toast';

const BROKER_CATALOG = [
  {
    id: 'ZERODHA',
    name: 'Zerodha Kite',
    logo: 'ZK',
    color: 'from-orange-500 to-amber-600',
    description: 'Official Kite Connect read-only API connector'
  },
  {
    id: 'GROWW',
    name: 'Groww',
    logo: 'GW',
    color: 'from-emerald-500 to-teal-600',
    description: 'Direct read-only portfolio integration API'
  },
  {
    id: 'ANGEL_ONE',
    name: 'Angel One SmartAPI',
    logo: 'AO',
    color: 'from-blue-500 to-indigo-600',
    description: 'SmartAPI connector for holdings and trades'
  },
  {
    id: 'MOCK_BROKER',
    name: 'TradoX Mock Connector',
    logo: 'TX',
    color: 'from-purple-500 to-indigo-600',
    description: 'Simulated broker connector for testing holdings sync'
  }
];

export default function BrokersPage() {
  const [connections, setConnections] = useState([]);
  const [capabilities, setCapabilities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [syncingId, setSyncingId] = useState(null);

  // Modal States
  const [isConnectModalOpen, setIsConnectModalOpen] = useState(false);
  const [isLogsModalOpen, setIsLogsModalOpen] = useState(false);
  const [selectedLogs, setSelectedLogs] = useState([]);
  const [selectedLogBroker, setSelectedLogBroker] = useState('');

  // Connect Form State
  const [connectForm, setConnectForm] = useState({
    brokerId: 'MOCK_BROKER',
    accountName: '',
    apiKey: '',
    apiSecret: ''
  });
  const [submittingConnect, setSubmittingConnect] = useState(false);
  const [devSyncing, setDevSyncing] = useState(false);

  const handleDevMockSync = async () => {
    try {
      setDevSyncing(true);
      const result = await brokerApi.triggerDevMockSync();
      if (result.status === 'SUCCESS') {
        showToast(`Synced ${result.holdingsSynced} holdings & ${result.transactionsSynced} trades from JSON mock broker!`, 'success');
      } else {
        showToast(`Sync failed: ${result.errorMessage}`, 'error');
      }
      await fetchConnectionsAndCapabilities();
    } catch (err) {
      showToast(err.response?.data?.message || 'Dev mock synchronization failed', 'error');
    } finally {
      setDevSyncing(false);
    }
  };

  useEffect(() => {
    fetchConnectionsAndCapabilities();
  }, []);

  const fetchConnectionsAndCapabilities = async () => {
    try {
      setLoading(true);
      const [conns, caps] = await Promise.all([
        brokerApi.getUserConnections(),
        brokerApi.getBrokerCapabilities()
      ]);
      setConnections(conns);
      setCapabilities(caps);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to fetch broker data', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleConnectBroker = async (e) => {
    e.preventDefault();
    if (!connectForm.accountName.trim()) {
      showToast('Please enter an account name', 'error');
      return;
    }

    try {
      setSubmittingConnect(true);
      const conn = await brokerApi.connectBroker({
        brokerId: connectForm.brokerId,
        accountName: connectForm.accountName.trim(),
        apiKey: connectForm.apiKey.trim() || 'demo-api-key',
        apiSecret: connectForm.apiSecret.trim()
      });

      showToast(`Connected ${connectForm.accountName}! Auto-syncing data...`, 'success');
      setIsConnectModalOpen(false);
      setConnectForm({ brokerId: 'MOCK_BROKER', accountName: '', apiKey: '', apiSecret: '' });

      // Automatically trigger first sync
      await handleSync(conn.id);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to connect broker', 'error');
    } finally {
      setSubmittingConnect(false);
    }
  };

  const handleSync = async (connectionId) => {
    try {
      setSyncingId(connectionId);
      const result = await brokerApi.syncBrokerData(connectionId);
      if (result.status === 'SUCCESS') {
        showToast(`Synced ${result.holdingsSynced} holdings & ${result.transactionsSynced} trades!`, 'success');
      } else {
        showToast(`Sync failed: ${result.errorMessage}`, 'error');
      }
      await fetchConnections();
    } catch (err) {
      showToast(err.response?.data?.message || 'Synchronization failed', 'error');
    } finally {
      setSyncingId(null);
    }
  };

  const handleDisconnect = async (connectionId, accountName) => {
    if (!window.confirm(`Disconnect broker account "${accountName}"?`)) return;
    try {
      await brokerApi.disconnectBroker(connectionId);
      showToast(`Disconnected ${accountName}`, 'info');
      fetchConnections();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to disconnect broker', 'error');
    }
  };

  const handleViewLogs = async (connectionId, accountName) => {
    try {
      setSelectedLogBroker(accountName);
      const logs = await brokerApi.getConnectionLogs(connectionId);
      setSelectedLogs(logs);
      setIsLogsModalOpen(true);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to load logs', 'error');
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] text-slate-400 gap-3">
        <RefreshCw className="w-8 h-8 animate-spin text-indigo-400" />
        <p className="text-sm font-medium">Loading connected broker accounts...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Top Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-3">
            <Link2 className="w-7 h-7 text-indigo-400" />
            Broker Connector Hub
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            Connect supported brokers via official read-only APIs to automate holdings & trade sync
          </p>
        </div>

        <div className="flex items-center gap-3">
          <Button
            variant="secondary"
            size="sm"
            onClick={handleDevMockSync}
            disabled={devSyncing}
            className="border-purple-500/30 text-purple-300 hover:bg-purple-500/10 shadow-lg shadow-purple-900/20"
          >
            <Zap className={`w-4 h-4 mr-1.5 ${devSyncing ? 'animate-spin text-purple-400' : 'text-purple-400'}`} />
            {devSyncing ? 'Syncing Mock Data...' : 'Sync Mock Broker'}
          </Button>

          <Button
            variant="primary"
            size="sm"
            onClick={() => setIsConnectModalOpen(true)}
            className="shadow-lg shadow-indigo-600/30"
          >
            <Plus className="w-4 h-4 mr-1.5" />
            Connect Broker Account
          </Button>
        </div>
      </div>

      {/* Security Disclaimer Banner */}
      <div className="bg-indigo-950/40 border border-indigo-800/40 rounded-2xl p-4 flex items-center gap-3 text-indigo-300 text-xs">
        <ShieldCheck className="w-5 h-5 text-indigo-400 shrink-0" />
        <span>
          <strong>Bank-Grade Read-Only Connectors:</strong> TradoX utilizes official read-only broker API tokens. We never store broker passwords, take custody of funds, or place unauthorized trades.
        </span>
      </div>

      {/* Connected Accounts Section */}
      <div>
        <h2 className="text-lg font-bold text-white mb-4">Your Connected Accounts ({connections.length})</h2>

        {connections.length === 0 ? (
          <GlassCard className="p-12 text-center border-dashed border-slate-800">
            <Link2 className="w-12 h-12 text-slate-600 mx-auto mb-3" />
            <h3 className="text-base font-semibold text-slate-300">No broker accounts connected yet</h3>
            <p className="text-slate-500 text-xs mt-1 mb-5 max-w-md mx-auto">
              Connect your broker account to automatically synchronize holdings, positions, and trade history into TradoX.
            </p>
            <Button size="sm" onClick={() => setIsConnectModalOpen(true)}>
              <Plus className="w-4 h-4 mr-1.5" />
              Connect Broker
            </Button>
          </GlassCard>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
            {connections.map((conn) => {
              const isSyncing = syncingId === conn.id;
              const lastSyncedStr = conn.lastSyncedAt
                ? new Date(conn.lastSyncedAt).toLocaleString('en-IN', {
                    day: '2-digit',
                    month: 'short',
                    hour: '2-digit',
                    minute: '2-digit'
                  })
                : 'Never';

              const isConnected = conn.status === 'CONNECTED';

              return (
                <GlassCard key={conn.id} className="p-5 flex flex-col justify-between space-y-4">
                  <div>
                    <div className="flex items-center justify-between mb-3">
                      <span className="text-xs font-bold text-indigo-400 bg-indigo-500/10 px-2.5 py-1 rounded-lg border border-indigo-500/20">
                        {conn.brokerId}
                      </span>
                      <span
                        className={`inline-flex items-center gap-1 text-[11px] font-bold px-2.5 py-0.5 rounded-full ${
                          isConnected
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : 'bg-red-500/10 text-red-400 border border-red-500/20'
                        }`}
                      >
                        {isConnected ? <CheckCircle2 className="w-3 h-3" /> : <AlertCircle className="w-3 h-3" />}
                        {conn.status}
                      </span>
                    </div>

                    <h3 className="text-base font-bold text-white">{conn.accountName}</h3>
                    <p className="text-xs text-slate-400 mt-0.5">{conn.brokerName}</p>

                    <div className="flex items-center gap-1.5 text-xs text-slate-500 mt-4">
                      <Clock className="w-3.5 h-3.5" />
                      <span>Last Synced: <strong className="text-slate-300">{lastSyncedStr}</strong></span>
                    </div>
                  </div>

                  <div className="pt-3 border-t border-slate-800/80 flex items-center justify-between gap-2">
                    <Button
                      variant="secondary"
                      size="sm"
                      onClick={() => handleSync(conn.id)}
                      disabled={isSyncing}
                      className="text-xs flex-1"
                    >
                      <RefreshCw className={`w-3.5 h-3.5 mr-1.5 ${isSyncing ? 'animate-spin text-indigo-400' : ''}`} />
                      {isSyncing ? 'Syncing...' : 'Sync Now'}
                    </Button>

                    <button
                      onClick={() => handleViewLogs(conn.id, conn.accountName)}
                      className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
                      title="Sync Logs"
                    >
                      <FileText className="w-4 h-4" />
                    </button>

                    <button
                      onClick={() => handleDisconnect(conn.id, conn.accountName)}
                      className="p-2 rounded-xl text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-colors"
                      title="Disconnect Broker"
                    >
                      <Trash2 className="w-4 h-4" />
                    </button>
                  </div>
                </GlassCard>
              );
            })}
          </div>
        )}
      </div>

      {/* Supported Broker Catalog Section */}
      <div className="pt-4">
        <h2 className="text-lg font-bold text-white mb-4">Supported Broker Catalog</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
          {BROKER_CATALOG.map((b) => (
            <GlassCard key={b.id} className="p-5 flex flex-col justify-between space-y-4 hover:border-indigo-600/40 transition-colors">
              <div>
                <div className={`w-10 h-10 rounded-xl bg-gradient-to-tr ${b.color} flex items-center justify-center text-white font-extrabold text-sm shadow-md mb-3`}>
                  {b.logo}
                </div>
                <h3 className="text-sm font-bold text-white">{b.name}</h3>
                <p className="text-xs text-slate-400 mt-1 leading-relaxed">{b.description}</p>
              </div>

              <Button
                variant="ghost"
                size="sm"
                onClick={() => {
                  setConnectForm({ ...connectForm, brokerId: b.id });
                  setIsConnectModalOpen(true);
                }}
                className="w-full text-xs text-indigo-400 hover:bg-indigo-500/10 justify-center"
              >
                + Connect Account
              </Button>
            </GlassCard>
          ))}
        </div>
      </div>

      {/* Broker Capability Comparison Matrix */}
      {capabilities.length > 0 && (
        <GlassCard className="p-6">
          <h2 className="text-base font-bold text-white mb-4">Broker API Feature Capability Matrix</h2>
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-300 border-collapse">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 font-semibold uppercase tracking-wider">
                  <th className="py-3 px-4">Broker Platform</th>
                  <th className="py-3 px-4">Holdings Sync</th>
                  <th className="py-3 px-4">Positions</th>
                  <th className="py-3 px-4">Orders & Trades</th>
                  <th className="py-3 px-4">Historical Candles</th>
                  <th className="py-3 px-4">Auth Type</th>
                  <th className="py-3 px-4 text-right">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {capabilities.map((cap) => (
                  <tr key={cap.brokerId} className="hover:bg-slate-900/40 transition-colors">
                    <td className="py-3.5 px-4 font-bold text-white flex items-center gap-2">
                      <span className="w-2 h-2 rounded-full bg-indigo-500" />
                      {cap.brokerName} ({cap.brokerId})
                    </td>
                    <td className="py-3.5 px-4">
                      {cap.supportsHoldings ? (
                        <span className="text-emerald-400 font-bold flex items-center gap-1">✓ Supported</span>
                      ) : (
                        <span className="text-slate-500">—</span>
                      )}
                    </td>
                    <td className="py-3.5 px-4">
                      {cap.supportsPositions ? (
                        <span className="text-emerald-400 font-bold flex items-center gap-1">✓ Supported</span>
                      ) : (
                        <span className="text-slate-500">—</span>
                      )}
                    </td>
                    <td className="py-3.5 px-4">
                      {cap.supportsOrders ? (
                        <span className="text-emerald-400 font-bold flex items-center gap-1">✓ Supported</span>
                      ) : (
                        <span className="text-slate-500">—</span>
                      )}
                    </td>
                    <td className="py-3.5 px-4">
                      {cap.supportsHistoricalData ? (
                        <span className="text-emerald-400 font-bold flex items-center gap-1">✓ Supported</span>
                      ) : (
                        <span className="text-amber-400">Read-Only</span>
                      )}
                    </td>
                    <td className="py-3.5 px-4">
                      <span className="bg-slate-800 px-2 py-0.5 rounded text-[11px] font-mono text-slate-300">
                        {cap.authType}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-right">
                      <span className="text-[10px] font-extrabold bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 px-2 py-0.5 rounded-full">
                        {cap.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </GlassCard>
      )}

      {/* Connect Broker Modal */}
      {isConnectModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#0c0d1b] border border-slate-800 rounded-2xl w-full max-w-md p-6 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <Link2 className="w-5 h-5 text-indigo-400" />
                Connect Broker Account
              </h3>
              <button
                onClick={() => setIsConnectModalOpen(false)}
                className="text-slate-400 hover:text-white p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <form onSubmit={handleConnectBroker} className="space-y-4">
              <div>
                <label className="block text-xs font-semibold text-slate-300 mb-1.5">
                  Select Broker Platform
                </label>
                <select
                  value={connectForm.brokerId}
                  onChange={(e) => setConnectForm({ ...connectForm, brokerId: e.target.value })}
                  className="w-full bg-slate-900 border border-slate-700 text-white text-sm rounded-xl px-3.5 py-2 focus:outline-none focus:border-indigo-500"
                >
                  {BROKER_CATALOG.map((b) => (
                    <option key={b.id} value={b.id}>
                      {b.name} ({b.id})
                    </option>
                  ))}
                </select>
              </div>

              <Input
                label="Account Label Name"
                placeholder="e.g. Primary Trading Account"
                value={connectForm.accountName}
                onChange={(e) => setConnectForm({ ...connectForm, accountName: e.target.value })}
                required
              />

              <Input
                label="API Key / Client ID"
                placeholder="e.g. kite_api_key_8892"
                value={connectForm.apiKey}
                onChange={(e) => setConnectForm({ ...connectForm, apiKey: e.target.value })}
              />

              <Input
                label="API Secret Token (Read-Only)"
                type="password"
                placeholder="••••••••••••••••"
                value={connectForm.apiSecret}
                onChange={(e) => setConnectForm({ ...connectForm, apiSecret: e.target.value })}
              />

              <div className="flex justify-end gap-3 pt-3 border-t border-slate-800">
                <Button
                  type="button"
                  variant="ghost"
                  onClick={() => setIsConnectModalOpen(false)}
                >
                  Cancel
                </Button>
                <Button type="submit" loading={submittingConnect}>
                  Connect & Sync
                </Button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Sync Logs Modal */}
      {isLogsModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm animate-fade-in">
          <div className="bg-[#0c0d1b] border border-slate-800 rounded-2xl w-full max-w-xl p-6 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-slate-800 pb-4">
              <h3 className="text-lg font-bold text-white flex items-center gap-2">
                <FileText className="w-5 h-5 text-indigo-400" />
                Sync Audit History for {selectedLogBroker}
              </h3>
              <button
                onClick={() => setIsLogsModalOpen(false)}
                className="text-slate-400 hover:text-white p-1 rounded-lg"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            <div className="max-h-80 overflow-y-auto space-y-2 pr-1">
              {selectedLogs.length === 0 ? (
                <p className="text-slate-500 text-xs py-8 text-center">No synchronization logs found</p>
              ) : (
                selectedLogs.map((log) => (
                  <div key={log.id} className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 text-xs flex items-center justify-between">
                    <div>
                      <div className="flex items-center gap-2">
                        <span
                          className={`font-bold px-2 py-0.5 rounded ${
                            log.status === 'SUCCESS'
                              ? 'bg-emerald-500/10 text-emerald-400'
                              : log.status === 'FAILED'
                              ? 'bg-red-500/10 text-red-400'
                              : 'bg-amber-500/10 text-amber-400'
                          }`}
                        >
                          {log.status}
                        </span>
                        <span className="text-slate-400">
                          {new Date(log.syncStartedAt).toLocaleString('en-IN')}
                        </span>
                      </div>
                      {log.errorMessage && (
                        <p className="text-red-400 mt-1 text-[11px]">{log.errorMessage}</p>
                      )}
                    </div>

                    <div className="text-right text-slate-300 font-medium">
                      <p>Holdings: {log.holdingsSynced || 0}</p>
                      <p className="text-[11px] text-slate-400">Trades: {log.transactionsSynced || 0}</p>
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="flex justify-end pt-3 border-t border-slate-800">
              <Button onClick={() => setIsLogsModalOpen(false)}>Close Logs</Button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
