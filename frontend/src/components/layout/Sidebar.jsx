import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  Briefcase,
  ArrowLeftRight,
  BarChart3,
  Lightbulb,
  Sparkles,
  Link,
  Settings,
  LogOut,
  ChevronLeft,
  ChevronRight,
  TrendingUp,
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

const iconMap = {
  LayoutDashboard,
  Briefcase,
  ArrowLeftRight,
  BarChart3,
  Lightbulb,
  Sparkles,
  Link,
  Settings,
};

const NAV_ITEMS = [
  { name: 'Dashboard', path: '/dashboard', icon: 'LayoutDashboard' },
  { name: 'Portfolio', path: '/portfolio', icon: 'Briefcase' },
  { name: 'Transactions', path: '/transactions', icon: 'ArrowLeftRight' },
  { name: 'Analytics', path: '/analytics', icon: 'BarChart3' },
  { name: 'Insights', path: '/insights', icon: 'Lightbulb' },
  { name: 'Brokers', path: '/brokers', icon: 'Link' },
  { name: 'Settings', path: '/settings', icon: 'Settings' },
];

export default function Sidebar() {
  const [collapsed, setCollapsed] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/', { replace: true });
  };

  return (
    <aside
      className={`fixed left-0 top-0 h-screen bg-slate-900/80 backdrop-blur-xl border-r border-slate-700/50 flex flex-col z-40 transition-all duration-300 ${collapsed ? 'w-[72px]' : 'w-64'
        }`}
    >
      {/* Logo Header */}
      <div className="flex flex-col items-center justify-center px-3 py-4 border-b border-slate-700/50">
        <div className="flex items-center gap-3">
          <div className="flex-shrink-0 w-9 h-9 rounded-xl bg-gradient-to-br from-indigo-500 to-violet-500 flex items-center justify-center shadow-lg shadow-indigo-500/30">
            <TrendingUp className="h-5 w-5 text-white" />
          </div>
          {!collapsed && (
            <span className="text-xl font-extrabold bg-gradient-to-r from-white via-indigo-200 to-violet-400 bg-clip-text text-transparent tracking-tight">
              TradoX
            </span>
          )}
        </div>
        {collapsed && (
          <span className="text-[10px] font-extrabold tracking-wider bg-gradient-to-r from-indigo-400 to-purple-400 bg-clip-text text-transparent mt-1">
            TradoX
          </span>
        )}
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {NAV_ITEMS.map((item) => {
          const IconComp = iconMap[item.icon] || LayoutDashboard;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-200 group ${isActive
                  ? 'bg-indigo-500/15 text-indigo-400 border border-indigo-500/20'
                  : 'text-slate-400 hover:text-white hover:bg-slate-800/50'
                }`
              }
            >
              <IconComp className="h-5 w-5 flex-shrink-0" />
              {!collapsed && <span>{item.name}</span>}
            </NavLink>
          );
        })}
      </nav>

      {/* User section */}
      <div className="px-3 py-4 border-t border-slate-700/50 space-y-2">
        {!collapsed && user && (
          <div className="px-3 py-2">
            <p className="text-sm font-medium text-white truncate">
              {user.firstName} {user.lastName}
            </p>
            <p className="text-xs text-slate-500 truncate">{user.email}</p>
          </div>
        )}
        <button
          onClick={handleLogout}
          className="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-slate-400 hover:text-red-400 hover:bg-red-500/10 transition-all duration-200 w-full"
        >
          <LogOut className="h-5 w-5 flex-shrink-0" />
          {!collapsed && <span>Logout</span>}
        </button>
      </div>

      {/* Collapse toggle */}
      <button
        onClick={() => setCollapsed(!collapsed)}
        className="absolute -right-3 top-20 w-6 h-6 rounded-full bg-slate-800 border border-slate-700/50 flex items-center justify-center text-slate-400 hover:text-white hover:bg-slate-700 transition-all duration-200 shadow-lg"
      >
        {collapsed ? <ChevronRight className="h-3.5 w-3.5" /> : <ChevronLeft className="h-3.5 w-3.5" />}
      </button>
    </aside>
  );
}
