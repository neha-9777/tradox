import { Link, useLocation } from 'react-router-dom';
import {
  TrendingUp,
  Bell,
  Search,
  Sparkles,
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

import GlobalSearch from '../ui/GlobalSearch';

export default function Navbar() {
  const { user } = useAuth();
  const location = useLocation();

  // Format page title from pathname
  const getPageTitle = (pathname) => {
    const path = pathname.split('/')[1];
    if (!path) return 'Dashboard';
    return path.charAt(0).toUpperCase() + path.slice(1);
  };

  return (
    <header className="sticky top-0 z-30 h-16 bg-slate-900/80 backdrop-blur-xl border-b border-slate-800 px-6 flex items-center justify-between transition-all duration-200">
      {/* Left: Brand Logo & Title + Current Page */}
      <div className="flex items-center gap-6">
        <Link to="/dashboard" className="flex items-center gap-3 group">
          <div className="w-8.5 h-8.5 rounded-xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-purple-500 flex items-center justify-center text-white shadow-md shadow-indigo-500/30 group-hover:scale-105 transition-transform duration-200 p-1.5">
            <TrendingUp className="w-4 h-4 stroke-[2.5]" />
          </div>
          <span className="text-xl font-black text-white tracking-tight">
            TradoX
          </span>
        </Link>

        <div className="h-4 w-px bg-slate-800 hidden sm:block" />

        <div className="hidden sm:flex items-center gap-2 text-sm">
          <span className="text-slate-500 font-medium">App</span>
          <span className="text-slate-600">/</span>
          <span className="text-slate-200 font-semibold">{getPageTitle(location.pathname)}</span>
        </div>
      </div>

      {/* Right: Search, Notifications & User badge */}
      <div className="flex items-center gap-3 sm:gap-4">
        {/* Quick Search */}
        <GlobalSearch />

        {/* Live Badge */}
        <div className="hidden xl:flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-semibold">
          <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
          <span>Market Live</span>
        </div>

        {/* Notifications */}
        <button
          className="relative p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800/60 transition-colors"
          title="Notifications"
        >
          <Bell className="w-4 h-4" />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-indigo-500 ring-2 ring-slate-900" />
        </button>

        {/* User Pill */}
        {user && (
          <div className="flex items-center gap-2.5 pl-2 border-l border-slate-800">
            <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-600 flex items-center justify-center text-white text-xs font-bold shadow-sm">
              {user.firstName ? user.firstName[0].toUpperCase() : 'T'}
            </div>
            <div className="hidden lg:block text-left">
              <p className="text-xs font-semibold text-white leading-tight">
                {user.firstName} {user.lastName}
              </p>
              <p className="text-[10px] text-indigo-400 font-medium flex items-center gap-1">
                <Sparkles className="w-2.5 h-2.5" /> Pro Plan
              </p>
            </div>
          </div>
        )}
      </div>
    </header>
  );
}
