import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Search,
  X,
  Briefcase,
  ArrowLeftRight,
  BarChart3,
  Lightbulb,
  Sparkles,
  Link as LinkIcon,
  Settings,
  TrendingUp,
  LayoutDashboard
} from 'lucide-react';
import { portfolioApi } from '../../api/portfolioApi';

const NAVIGATION_ITEMS = [
  { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard, category: 'Page' },
  { name: 'Portfolio', path: '/portfolio', icon: Briefcase, category: 'Page' },
  { name: 'Transactions', path: '/transactions', icon: ArrowLeftRight, category: 'Page' },
  { name: 'Analytics', path: '/analytics', icon: BarChart3, category: 'Page' },
  { name: 'Insights', path: '/insights', icon: Lightbulb, category: 'Page' },
  { name: 'TradoX AI', path: '/trado-ai', icon: Sparkles, category: 'Page' },
  { name: 'Brokers', path: '/brokers', icon: LinkIcon, category: 'Page' },
  { name: 'Settings', path: '/settings', icon: Settings, category: 'Page' },
];

export default function GlobalSearch() {
  const [query, setQuery] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const [holdings, setHoldings] = useState([]);
  const [transactions, setTransactions] = useState([]);
  const containerRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchSearchData();
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const fetchSearchData = async () => {
    try {
      const ports = await portfolioApi.getUserPortfolios();
      if (ports && ports.length > 0) {
        let allHoldings = [];
        let allTx = [];
        ports.forEach((p) => {
          if (p.holdings) allHoldings.push(...p.holdings);
          if (p.transactions) allTx.push(...p.transactions);
        });
        setHoldings(allHoldings);
        setTransactions(allTx);
      }
    } catch {
      // Ignore background error if unauthenticated
    }
  };

  const cleanQuery = query.trim().toLowerCase();

  // Filter navigation
  const matchingNav = cleanQuery
    ? NAVIGATION_ITEMS.filter((item) =>
        item.name.toLowerCase().includes(cleanQuery)
      )
    : NAVIGATION_ITEMS.slice(0, 4);

  // Filter holdings
  const matchingHoldings = cleanQuery
    ? holdings.filter(
        (h) =>
          h.asset?.symbol?.toLowerCase().includes(cleanQuery) ||
          h.asset?.name?.toLowerCase().includes(cleanQuery)
      )
    : [];

  // Filter transactions
  const matchingTransactions = cleanQuery
    ? transactions.filter(
        (t) =>
          t.asset?.symbol?.toLowerCase().includes(cleanQuery) ||
          t.type?.toLowerCase().includes(cleanQuery) ||
          t.notes?.toLowerCase().includes(cleanQuery)
      )
    : [];

  const handleSelectResult = (path) => {
    setQuery('');
    setIsOpen(false);
    navigate(path);
  };

  const hasResults =
    matchingNav.length > 0 ||
    matchingHoldings.length > 0 ||
    matchingTransactions.length > 0;

  return (
    <div ref={containerRef} className="relative hidden md:block z-50">
      {/* Input Box */}
      <div className="relative">
        <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
        <input
          type="text"
          placeholder="Search stocks, transactions, pages..."
          value={query}
          onFocus={() => setIsOpen(true)}
          onChange={(e) => {
            setQuery(e.target.value);
            setIsOpen(true);
          }}
          className="w-60 lg:w-72 bg-slate-950/70 border border-slate-800 focus:border-indigo-500 rounded-xl pl-9 pr-8 py-1.5 text-xs text-white placeholder-slate-500 focus:outline-none focus:ring-1 focus:ring-indigo-500/50 transition-all shadow-inner"
        />
        {query && (
          <button
            onClick={() => {
              setQuery('');
              setIsOpen(false);
            }}
            className="absolute right-2.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-white p-0.5"
          >
            <X className="w-3.5 h-3.5" />
          </button>
        )}
      </div>

      {/* Floating Dropdown Results */}
      {isOpen && (
        <div className="absolute top-11 left-0 w-80 lg:w-96 bg-[#0b0d1e] border border-indigo-500/30 rounded-2xl shadow-2xl p-3 space-y-4 max-h-96 overflow-y-auto animate-fade-in backdrop-blur-2xl">
          {!hasResults ? (
            <div className="py-6 text-center text-slate-400 text-xs">
              No matching stocks, transactions, or pages found for &quot;{query}&quot;
            </div>
          ) : (
            <>
              {/* Pages Section */}
              {matchingNav.length > 0 && (
                <div>
                  <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest px-2 mb-1.5">
                    Navigation & Pages
                  </p>
                  <div className="space-y-1">
                    {matchingNav.map((item) => {
                      const IconComp = item.icon;
                      return (
                        <button
                          key={item.path}
                          onClick={() => handleSelectResult(item.path)}
                          className="w-full flex items-center justify-between p-2 rounded-xl hover:bg-indigo-500/10 text-left transition-colors group"
                        >
                          <div className="flex items-center gap-2.5">
                            <div className="w-7 h-7 rounded-lg bg-slate-900 border border-slate-800 text-indigo-400 flex items-center justify-center group-hover:border-indigo-500/40">
                              <IconComp className="w-3.5 h-3.5" />
                            </div>
                            <span className="text-xs font-semibold text-slate-200 group-hover:text-white">
                              {item.name}
                            </span>
                          </div>
                          <span className="text-[10px] text-slate-500 uppercase font-mono">
                            Shortcut
                          </span>
                        </button>
                      );
                    })}
                  </div>
                </div>
              )}

              {/* Holdings Section */}
              {matchingHoldings.length > 0 && (
                <div className="border-t border-slate-800/80 pt-2">
                  <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest px-2 mb-1.5 flex items-center justify-between">
                    <span>Portfolio Holdings ({matchingHoldings.length})</span>
                  </p>
                  <div className="space-y-1">
                    {matchingHoldings.slice(0, 5).map((h) => (
                      <button
                        key={h.id}
                        onClick={() => handleSelectResult('/portfolio')}
                        className="w-full flex items-center justify-between p-2 rounded-xl hover:bg-slate-800/60 text-left transition-colors"
                      >
                        <div className="flex items-center gap-2.5">
                          <div className="w-7 h-7 rounded-lg bg-indigo-500/10 border border-indigo-500/20 text-indigo-400 flex items-center justify-center font-bold text-xs">
                            {h.asset?.symbol?.substring(0, 2)}
                          </div>
                          <div>
                            <p className="text-xs font-bold text-white">
                              {h.asset?.symbol}
                            </p>
                            <p className="text-[10px] text-slate-400">
                              {h.quantity} shares @ ₹{Number(h.averageBuyPrice).toFixed(2)}
                            </p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="text-xs font-bold text-white">
                            ₹{Number(h.currentValue || h.totalInvested).toLocaleString('en-IN')}
                          </p>
                          <span className="text-[10px] text-emerald-400 font-semibold">
                            Holding
                          </span>
                        </div>
                      </button>
                    ))}
                  </div>
                </div>
              )}

              {/* Transactions Section */}
              {matchingTransactions.length > 0 && (
                <div className="border-t border-slate-800/80 pt-2">
                  <p className="text-[10px] font-bold text-slate-500 uppercase tracking-widest px-2 mb-1.5">
                    Transactions ({matchingTransactions.length})
                  </p>
                  <div className="space-y-1">
                    {matchingTransactions.slice(0, 4).map((t) => (
                      <button
                        key={t.id}
                        onClick={() => handleSelectResult('/transactions')}
                        className="w-full flex items-center justify-between p-2 rounded-xl hover:bg-slate-800/60 text-left transition-colors"
                      >
                        <div className="flex items-center gap-2.5">
                          <span
                            className={`px-2 py-0.5 rounded text-[10px] font-extrabold ${
                              t.type === 'BUY'
                                ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
                                : 'bg-red-500/10 text-red-400 border border-red-500/20'
                            }`}
                          >
                            {t.type}
                          </span>
                          <div>
                            <p className="text-xs font-bold text-white">
                              {t.asset?.symbol}
                            </p>
                            <p className="text-[10px] text-slate-400">
                              {t.quantity} shares
                            </p>
                          </div>
                        </div>
                        <div className="text-right">
                          <p className="text-xs font-bold text-slate-300">
                            ₹{Number(t.price).toLocaleString('en-IN')}
                          </p>
                          <span className="text-[10px] text-slate-500">
                            {t.transactionDate ? t.transactionDate.substring(0, 10) : ''}
                          </span>
                        </div>
                      </button>
                    ))}
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      )}
    </div>
  );
}
