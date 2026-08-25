import { useState, useEffect } from 'react';
import {
  Settings,
  Shield,
  Key,
  Clock,
  User as UserIcon,
  Lock,
  Zap,
  RefreshCw,
  FileCheck,
  CheckCircle2
} from 'lucide-react';
import GlassCard from '../components/ui/GlassCard';
import Button from '../components/ui/Button';
import { useAuth } from '../context/AuthContext';
import { auditApi } from '../api/auditApi';
import { showToast } from '../components/ui/Toast';

export default function SettingsPage() {
  const { user } = useAuth();
  const [auditLogs, setAuditLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAuditLogs();
  }, []);

  const fetchAuditLogs = async () => {
    try {
      setLoading(true);
      const data = await auditApi.getUserAuditLogs();
      setAuditLogs(data);
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to load audit logs', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white flex items-center gap-3">
            <Settings className="w-7 h-7 text-indigo-400" />
            Security & System Settings
          </h1>
          <p className="text-slate-400 text-sm mt-1">
            Account security, AES-256 database encryption status, rate limiting health, and audit logs
          </p>
        </div>

        <button
          onClick={fetchAuditLogs}
          className="self-start sm:self-center px-4 py-2 rounded-xl bg-slate-900 border border-slate-800 hover:border-slate-700 text-slate-300 hover:text-white text-xs font-semibold flex items-center gap-2 transition-all"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Refresh Audit Trail
        </button>
      </div>

      {/* User Profile & Security Overview */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* User Card */}
        <GlassCard className="p-5 space-y-3">
          <div className="w-10 h-10 rounded-xl bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 flex items-center justify-center font-bold">
            <UserIcon className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">{user?.firstName} {user?.lastName}</h3>
            <p className="text-xs text-slate-400">{user?.email}</p>
          </div>
          <div className="pt-2 border-t border-slate-800 flex items-center justify-between text-xs text-slate-400">
            <span>Account Role:</span>
            <span className="font-bold text-indigo-400">USER</span>
          </div>
        </GlassCard>

        {/* AES-256 Encryption Card */}
        <GlassCard className="p-5 space-y-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 flex items-center justify-center font-bold">
            <Lock className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">AES-256 Secret Protection</h3>
            <p className="text-xs text-slate-400">Database secret key encryption active</p>
          </div>
          <div className="pt-2 border-t border-slate-800 flex items-center justify-between text-xs">
            <span className="text-slate-400">Cipher Protocol:</span>
            <span className="font-bold text-emerald-400 flex items-center gap-1">
              <CheckCircle2 className="w-3.5 h-3.5" /> AES-GCM 256-bit
            </span>
          </div>
        </GlassCard>

        {/* Rate Limiter Health Card */}
        <GlassCard className="p-5 space-y-3">
          <div className="w-10 h-10 rounded-xl bg-amber-500/10 border border-amber-500/20 text-amber-400 flex items-center justify-center font-bold">
            <Zap className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-base font-bold text-white">API Rate Limiter</h3>
            <p className="text-xs text-slate-400">DDoS & brute force protection active</p>
          </div>
          <div className="pt-2 border-t border-slate-800 flex items-center justify-between text-xs">
            <span className="text-slate-400">Threshold Limit:</span>
            <span className="font-bold text-amber-400">60 req / min</span>
          </div>
        </GlassCard>
      </div>

      {/* Security Audit Log Table */}
      <GlassCard className="p-6">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h2 className="text-lg font-bold text-white flex items-center gap-2">
              <Shield className="w-5 h-5 text-indigo-400" />
              Security Audit Trail
            </h2>
            <p className="text-xs text-slate-400 mt-0.5">Chronological record of authentication, transactions, and broker sync events</p>
          </div>
          <span className="text-xs text-slate-400">{auditLogs.length} Events Recorded</span>
        </div>

        {loading ? (
          <div className="text-center py-12 text-slate-400 text-xs">Loading audit events...</div>
        ) : auditLogs.length === 0 ? (
          <div className="text-center py-12 text-slate-500 text-xs border border-dashed border-slate-800 rounded-xl">
            <FileCheck className="w-10 h-10 text-slate-600 mx-auto mb-2" />
            <p>No audit events recorded yet</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-300 border-collapse">
              <thead>
                <tr className="border-b border-slate-800 text-slate-400 font-semibold uppercase tracking-wider">
                  <th className="py-3 px-4">Timestamp</th>
                  <th className="py-3 px-4">Event Action</th>
                  <th className="py-3 px-4">Resource</th>
                  <th className="py-3 px-4">IP Address</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Details</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60">
                {auditLogs.map((log) => (
                  <tr key={log.id} className="hover:bg-slate-900/40 transition-colors">
                    <td className="py-3.5 px-4 font-mono text-slate-400">
                      {new Date(log.timestamp).toLocaleString('en-IN')}
                    </td>
                    <td className="py-3.5 px-4 font-bold text-white">{log.actionType}</td>
                    <td className="py-3.5 px-4 text-indigo-400 font-medium">{log.resourceName || '—'}</td>
                    <td className="py-3.5 px-4 font-mono text-slate-400">{log.ipAddress || '127.0.0.1'}</td>
                    <td className="py-3.5 px-4">
                      <span
                        className={`text-[10px] font-extrabold px-2 py-0.5 rounded ${
                          log.status === 'SUCCESS'
                            ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                            : 'bg-red-500/10 text-red-400 border border-red-500/20'
                        }`}
                      >
                        {log.status}
                      </span>
                    </td>
                    <td className="py-3.5 px-4 text-right text-slate-400 max-w-xs truncate">{log.details || '—'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </GlassCard>
    </div>
  );
}
