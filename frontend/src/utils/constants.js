export const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export const NAV_ITEMS = [
  { name: 'Dashboard', path: '/dashboard', icon: 'LayoutDashboard' },
  { name: 'Portfolio', path: '/portfolio', icon: 'Briefcase' },
  { name: 'Transactions', path: '/transactions', icon: 'ArrowLeftRight' },
  { name: 'Analytics', path: '/analytics', icon: 'BarChart3' },
  { name: 'Insights', path: '/insights', icon: 'Lightbulb' },
  { name: 'Brokers', path: '/brokers', icon: 'Link' },
  { name: 'Settings', path: '/settings', icon: 'Settings' },
];

export const TOKEN_KEY = 'tradox_token';
export const USER_KEY = 'tradox_user';
