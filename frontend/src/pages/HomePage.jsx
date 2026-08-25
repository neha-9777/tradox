import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  TrendingUp,
  Zap,
  BarChart3,
  Bot,
  Check,
  ChevronRight,
  ShieldCheck,
  Link2,
  PieChart,
  ArrowUpRight,
  ChevronDown,
  ArrowUp,
  Menu,
  X,
  Star,
  Layers,
  Calculator,
  Lock,
  Activity,
  Award
} from 'lucide-react';

export default function HomePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [openFaq, setOpenFaq] = useState(null);

  // Handle navbar background blur and shadow on scroll
  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 20) {
        setScrolled(true);
      } else {
        setScrolled(false);
      }
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const toggleFaq = (index) => {
    setOpenFaq(openFaq === index ? null : index);
  };

  const faqs = [
    {
      q: "How does TradoX connect to my broker accounts?",
      a: "TradoX utilizes official read-only broker API connectors and secure authorization tokens. It fetches permitted holdings, positions, and transaction history safely without ever asking for or storing your broker passwords."
    },
    {
      q: "Does TradoX take custody of funds or execute trades?",
      a: "No. TradoX is purely an analytics and portfolio management platform. We never hold user funds, place unauthorized trades, or store broker credentials. Your funds remain 100% safe in your broker accounts."
    },
    {
      q: "How does the P&L Engine calculate returns?",
      a: "Our automated P&L Engine processes your transaction history using standard average cost methodology. It computes Realized P&L, Unrealized P&L, Daily, Weekly, Monthly, and Overall return percentages automatically."
    },
    {
      q: "Can I manually add holdings if my broker is not yet integrated?",
      a: "Yes! TradoX features a complete manual transaction and portfolio entry system, as well as CSV/Excel import, allowing you to track assets from any broker or financial institution."
    },
    {
      q: "Is TradoX suitable for both active traders and long-term investors?",
      a: "Absolutely. Active stock traders benefit from daily/weekly P&L breakdowns and stock-level performance analytics, while long-term investors gain clarity through asset allocation, diversification metrics, and concentration risk warnings."
    }
  ];

  return (
    <div className="min-h-screen bg-[#07080e] text-slate-100 font-sans selection:bg-indigo-500 selection:text-white">
      
      {/* Sticky Fixed Navbar */}
      <header
        className={`fixed top-0 left-0 right-0 z-50 transition-all duration-500 ease-in-out ${
          scrolled
            ? 'py-2.5 px-3 sm:px-6'
            : 'py-5 px-4 sm:px-8 bg-gradient-to-b from-[#07080e]/95 via-[#07080e]/60 to-transparent backdrop-blur-sm'
        }`}
      >
        <div
          className={`max-w-7xl mx-auto flex items-center justify-between transition-all duration-500 ease-in-out ${
            scrolled
              ? 'bg-[#090a17]/90 backdrop-blur-2xl border border-indigo-500/30 shadow-2xl shadow-indigo-950/70 px-5 sm:px-7 py-2.5 rounded-full ring-1 ring-indigo-500/20'
              : 'bg-transparent border border-transparent px-2 py-1'
          }`}
        >
          {/* Brand Logo & Name */}
          <Link to="/" className="flex items-center gap-3 group">
            <div
              className={`rounded-xl bg-gradient-to-tr from-indigo-600 via-indigo-500 to-purple-500 flex items-center justify-center text-white shadow-lg shadow-indigo-500/40 group-hover:scale-105 transition-all duration-300 ${
                scrolled ? 'w-9 h-9' : 'w-10 h-10'
              }`}
            >
              <TrendingUp className="w-5 h-5 stroke-[2.5]" />
            </div>
            <div className="flex items-center gap-2">
              <span className="text-2xl font-black text-white tracking-tight">
                TradoX
              </span>
            </div>
          </Link>

          {/* Desktop Nav Links */}
          <nav className="hidden md:flex items-center gap-1 lg:gap-2">
            <a
              href="#hero"
              className="text-sm font-medium text-slate-300 hover:text-white hover:bg-indigo-500/10 px-3.5 py-1.5 rounded-full transition-all duration-200"
            >
              Home
            </a>
            <a
              href="#features"
              className="text-sm font-medium text-slate-300 hover:text-white hover:bg-indigo-500/10 px-3.5 py-1.5 rounded-full transition-all duration-200"
            >
              Features
            </a>
            <a
              href="#how-it-works"
              className="text-sm font-medium text-slate-300 hover:text-white hover:bg-indigo-500/10 px-3.5 py-1.5 rounded-full transition-all duration-200"
            >
              How It Works
            </a>
            <a
              href="#testimonials"
              className="text-sm font-medium text-slate-300 hover:text-white hover:bg-indigo-500/10 px-3.5 py-1.5 rounded-full transition-all duration-200"
            >
              Testimonials
            </a>
            <a
              href="#pricing"
              className="text-sm font-medium text-slate-300 hover:text-white hover:bg-indigo-500/10 px-3.5 py-1.5 rounded-full transition-all duration-200"
            >
              Pricing
            </a>
            <a
              href="#faq"
              className="text-sm font-medium text-slate-300 hover:text-white hover:bg-indigo-500/10 px-3.5 py-1.5 rounded-full transition-all duration-200"
            >
              FAQ
            </a>
          </nav>

          {/* Action / Auth Button — Morphing into smooth oval/pill on scroll */}
          <div className="hidden md:flex items-center gap-4">
            {user ? (
              <button
                onClick={() => navigate('/dashboard')}
                className={`text-white text-sm font-semibold transition-all duration-500 ease-in-out flex items-center gap-2 active:scale-95 shadow-lg ${
                  scrolled
                    ? 'rounded-full px-6 py-2 bg-gradient-to-r from-indigo-500 via-purple-500 to-indigo-600 hover:from-indigo-400 hover:to-purple-400 shadow-indigo-500/40 ring-2 ring-indigo-400/30'
                    : 'rounded-xl px-5 py-2.5 bg-indigo-600 hover:bg-indigo-500 shadow-indigo-600/30'
                }`}
              >
                Go to Dashboard
                <ChevronRight className="w-4 h-4" />
              </button>
            ) : (
              <Link
                to="/login"
                className={`text-white text-sm font-bold transition-all duration-500 ease-in-out active:scale-95 shadow-lg flex items-center justify-center ${
                  scrolled
                    ? 'rounded-full px-7 py-2 bg-gradient-to-r from-indigo-500 via-purple-500 to-indigo-600 hover:from-indigo-400 hover:to-purple-400 shadow-indigo-500/40 ring-2 ring-indigo-400/30 scale-105'
                    : 'rounded-xl px-6 py-2.5 bg-indigo-600 hover:bg-indigo-500 shadow-indigo-600/30'
                }`}
              >
                Login
              </Link>
            )}
          </div>

          {/* Mobile Hamburger Toggle */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden text-slate-400 hover:text-white p-2 rounded-lg"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>
        </div>

        {/* Mobile Dropdown */}
        {mobileMenuOpen && (
          <div className="md:hidden bg-[#090a17]/95 backdrop-blur-2xl border border-indigo-500/30 rounded-2xl mt-2 mx-2 p-4 space-y-3 shadow-2xl animate-fade-in">
            <div className="flex items-center gap-2.5 px-3 py-2 border-b border-indigo-500/20 mb-1">
              <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-indigo-600 to-purple-500 flex items-center justify-center text-white">
                <TrendingUp className="w-4 h-4" />
              </div>
              <span className="text-lg font-bold text-white tracking-tight">TradoX</span>
            </div>
            <a
              href="#hero"
              onClick={() => setMobileMenuOpen(false)}
              className="block text-slate-300 hover:text-white text-base font-medium py-2 px-3 rounded-lg hover:bg-indigo-500/10"
            >
              Home
            </a>
            <a
              href="#features"
              onClick={() => setMobileMenuOpen(false)}
              className="block text-slate-300 hover:text-white text-base font-medium py-2 px-3 rounded-lg hover:bg-indigo-500/10"
            >
              Features
            </a>
            <a
              href="#how-it-works"
              onClick={() => setMobileMenuOpen(false)}
              className="block text-slate-300 hover:text-white text-base font-medium py-2 px-3 rounded-lg hover:bg-indigo-500/10"
            >
              How It Works
            </a>
            <a
              href="#testimonials"
              onClick={() => setMobileMenuOpen(false)}
              className="block text-slate-300 hover:text-white text-base font-medium py-2 px-3 rounded-lg hover:bg-indigo-500/10"
            >
              Testimonials
            </a>
            <a
              href="#pricing"
              onClick={() => setMobileMenuOpen(false)}
              className="block text-slate-300 hover:text-white text-base font-medium py-2 px-3 rounded-lg hover:bg-indigo-500/10"
            >
              Pricing
            </a>
            <a
              href="#faq"
              onClick={() => setMobileMenuOpen(false)}
              className="block text-slate-300 hover:text-white text-base font-medium py-2 px-3 rounded-lg hover:bg-indigo-500/10"
            >
              FAQ
            </a>
            <div className="pt-2">
              {user ? (
                <button
                  onClick={() => {
                    setMobileMenuOpen(false);
                    navigate('/dashboard');
                  }}
                  className="w-full bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-bold py-3 rounded-full text-center shadow-lg shadow-indigo-500/30"
                >
                  Dashboard
                </button>
              ) : (
                <Link
                  to="/login"
                  onClick={() => setMobileMenuOpen(false)}
                  className="block w-full bg-gradient-to-r from-indigo-500 to-purple-600 text-white font-bold py-3 rounded-full text-center shadow-lg shadow-indigo-500/30"
                >
                  Login
                </Link>
              )}
            </div>
          </div>
        )}
      </header>

      {/* Hero Section */}
      <section id="hero" className="relative pt-28 pb-20 md:pt-36 md:pb-28 overflow-hidden">
        {/* Ambient glow backgrounds */}
        <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[650px] h-[450px] bg-indigo-600/15 rounded-full blur-[140px] pointer-events-none" />
        <div className="absolute top-1/3 right-10 w-[380px] h-[380px] bg-purple-600/10 rounded-full blur-[130px] pointer-events-none" />

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 lg:gap-8 items-center">
            
            {/* Left Column: Headline & PRD Value Proposition */}
            <div className="lg:col-span-7 space-y-6 text-left">
              {/* Badge Pill */}
              <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-indigo-950/60 border border-indigo-800/40 text-indigo-300 text-xs font-semibold tracking-wide uppercase shadow-sm">
                <span className="w-2 h-2 rounded-full bg-indigo-400 animate-pulse" />
                SMART MULTI-BROKER PORTFOLIO PLATFORM
              </div>

              {/* Main Heading */}
              <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-white tracking-tight leading-[1.15]">
                Unify All Your Brokers in <br />
                One Smart{' '}
                <span className="bg-gradient-to-r from-indigo-400 via-purple-300 to-indigo-300 bg-clip-text text-transparent">
                  Portfolio Engine
                </span>
              </h1>

              {/* Description based on PRD */}
              <p className="text-slate-400 text-base sm:text-lg leading-relaxed max-w-xl">
                Say goodbye to manual spreadsheets. TradoX automatically centralizes holdings, calculates daily/weekly/monthly P&L, tracks realized & unrealized returns, and delivers data-driven portfolio analytics across all your broker accounts.
              </p>

              {/* CTA Buttons */}
              <div className="flex flex-wrap items-center gap-4 pt-2">
                <Link
                  to={user ? '/dashboard' : '/register'}
                  className="bg-gradient-to-r from-indigo-600 via-indigo-500 to-purple-600 hover:from-indigo-500 hover:to-purple-500 text-white font-bold text-base px-7 py-3.5 rounded-xl shadow-xl shadow-indigo-600/30 flex items-center gap-2.5 transition-all duration-300 hover:scale-[1.02] active:scale-[0.98]"
                >
                  <Zap className="w-5 h-5 fill-white" />
                  Get Started Free
                </Link>
                <a
                  href="#features"
                  className="bg-slate-900/90 hover:bg-slate-800 text-slate-300 hover:text-white border border-slate-800 px-6 py-3.5 rounded-xl font-medium text-base transition-all duration-200 flex items-center gap-2"
                >
                  Explore Platform →
                </a>
              </div>

              {/* Quick Trust Badges */}
              <div className="pt-4 flex items-center gap-6 text-xs text-slate-400">
                <div className="flex items-center gap-2">
                  <ShieldCheck className="w-4 h-4 text-indigo-400" />
                  <span>Read-Only Broker APIs</span>
                </div>
                <div className="flex items-center gap-2">
                  <Lock className="w-4 h-4 text-indigo-400" />
                  <span>Zero Password Storage</span>
                </div>
              </div>
            </div>

            {/* Right Column: Hero Dashboard Overview Mockup (PRD Metrics) */}
            <div className="lg:col-span-5 relative">
              <div className="relative bg-[#0d0e1a] border border-indigo-900/50 rounded-2xl p-6 shadow-2xl shadow-indigo-950/80 backdrop-blur-xl group hover:border-indigo-700/60 transition-all duration-500">
                {/* Mockup Header Row */}
                <div className="flex items-center justify-between mb-6">
                  <div className="flex items-center gap-2">
                    <span className="w-2.5 h-2.5 rounded-full bg-emerald-400 animate-pulse" />
                    <span className="text-xs font-bold text-slate-400 tracking-wider uppercase">
                      PORTFOLIO OVERVIEW
                    </span>
                  </div>
                  <div className="bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 px-3 py-1 rounded-full text-xs font-bold flex items-center gap-1">
                    <ArrowUpRight className="w-3.5 h-3.5" />
                    +18.4%
                  </div>
                </div>

                {/* SVG Trend Chart */}
                <div className="w-full h-44 relative my-2">
                  <svg className="w-full h-full overflow-visible" viewBox="0 0 400 150">
                    <defs>
                      <linearGradient id="chartGradient" x1="0" y1="0" x2="0" y2="1">
                        <stop offset="0%" stopColor="#6366f1" stopOpacity="0.45" />
                        <stop offset="100%" stopColor="#6366f1" stopOpacity="0.0" />
                      </linearGradient>
                    </defs>

                    {/* Area fill */}
                    <path
                      d="M 0 110 Q 50 85, 90 95 T 180 65 T 270 75 T 360 35 L 360 150 L 0 150 Z"
                      fill="url(#chartGradient)"
                    />

                    {/* Trend Line */}
                    <path
                      d="M 0 110 Q 50 85, 90 95 T 180 65 T 270 75 T 360 35"
                      fill="none"
                      stroke="#818cf8"
                      strokeWidth="3.5"
                      strokeLinecap="round"
                    />

                    {/* Endpoint Dot */}
                    <circle cx="360" cy="35" r="5" fill="#10b981" />
                    <circle cx="360" cy="35" r="9" fill="#10b981" opacity="0.3" className="animate-ping" />
                  </svg>
                </div>

                {/* 3 Metric Cards Grid at Bottom (Matching PRD metrics) */}
                <div className="grid grid-cols-3 gap-3 pt-4 border-t border-slate-800/80">
                  <div className="bg-[#121424] border border-slate-800/60 rounded-xl p-3 text-center">
                    <p className="text-[11px] text-slate-400 font-medium mb-1">Total Value</p>
                    <p className="text-lg sm:text-xl font-extrabold text-emerald-400">₹14.28L</p>
                  </div>
                  <div className="bg-[#121424] border border-slate-800/60 rounded-xl p-3 text-center">
                    <p className="text-[11px] text-slate-400 font-medium mb-1">P&L Today</p>
                    <p className="text-lg sm:text-xl font-extrabold text-cyan-400">+₹12.4k</p>
                  </div>
                  <div className="bg-[#121424] border border-slate-800/60 rounded-xl p-3 text-center">
                    <p className="text-[11px] text-slate-400 font-medium mb-1">Return %</p>
                    <p className="text-lg sm:text-xl font-extrabold text-amber-400">+24.6%</p>
                  </div>
                </div>
              </div>
            </div>

          </div>
        </div>
      </section>

      {/* Features Section (Directly mapped to PRD scope) */}
      <section id="features" className="py-20 bg-[#06070b] border-t border-slate-900 relative">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative z-10">
          
          {/* Section Header */}
          <div className="text-center max-w-3xl mx-auto mb-16 space-y-4">
            <div className="inline-block px-4 py-1 rounded-full bg-indigo-950/60 border border-indigo-800/50 text-indigo-400 text-xs font-bold tracking-widest uppercase">
              PLATFORM FEATURES
            </div>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight leading-tight">
              Everything You Need to{' '}
              <span className="text-indigo-400">Centralize & Analyze</span> Your Investments
            </h2>
            <p className="text-slate-400 text-base sm:text-lg">
              Designed for individual investors, active stock traders, and multi-broker account holders.
            </p>
          </div>

          {/* 6 Feature Cards mapped to PRD requirements */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            
            {/* Feature 1: Multi-Broker Connector Architecture */}
            <div className="bg-[#0b0c16] border border-slate-800/90 rounded-2xl p-7 hover:border-indigo-600/40 transition-all duration-300 hover:shadow-xl hover:shadow-indigo-950/40 flex flex-col justify-between group">
              <div>
                <div className="w-12 h-12 rounded-xl bg-indigo-950/80 border border-indigo-800/50 flex items-center justify-center text-indigo-400 mb-6 group-hover:scale-110 transition-transform">
                  <Link2 className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Multi-Broker Connector</h3>
                <p className="text-slate-400 text-sm mb-6 leading-relaxed">
                  Connect supported broker accounts using official API mechanisms to automatically sync holdings & transactions.
                </p>
              </div>
              <ul className="space-y-3 pt-4 border-t border-slate-800/60 text-sm">
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Secure official broker API authentication</span>
                </li>
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Automatic data normalization & sync</span>
                </li>
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Safe disconnection & revocation</span>
                </li>
              </ul>
            </div>

            {/* Feature 2: Automated P&L Engine */}
            <div className="bg-[#0b0c16] border border-slate-800/90 rounded-2xl p-7 hover:border-amber-600/40 transition-all duration-300 hover:shadow-xl hover:shadow-amber-950/40 flex flex-col justify-between group">
              <div>
                <div className="w-12 h-12 rounded-xl bg-amber-950/60 border border-amber-800/50 flex items-center justify-center text-amber-400 mb-6 group-hover:scale-110 transition-transform">
                  <Calculator className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Automated P&L Engine</h3>
                <p className="text-slate-400 text-sm mb-6 leading-relaxed">
                  Track realized, unrealized, daily, weekly, and monthly P&L using standard average cost methodology.
                </p>
              </div>
              <ul className="space-y-3 pt-4 border-t border-slate-800/60 text-sm">
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Realized vs Unrealized P&L breakdowns</span>
                </li>
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Daily, weekly, & monthly return calculations</span>
                </li>
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Overall return % & cost basis tracking</span>
                </li>
              </ul>
            </div>

            {/* Feature 3: Portfolio Analytics */}
            <div className="bg-[#0b0c16] border border-slate-800/90 rounded-2xl p-7 hover:border-pink-600/40 transition-all duration-300 hover:shadow-xl hover:shadow-pink-950/40 flex flex-col justify-between group">
              <div>
                <div className="w-12 h-12 rounded-xl bg-pink-950/60 border border-pink-800/50 flex items-center justify-center text-pink-400 mb-6 group-hover:scale-110 transition-transform">
                  <BarChart3 className="w-6 h-6" />
                </div>
                <h3 className="text-xl font-bold text-white mb-2">Performance Analytics</h3>
                <p className="text-slate-400 text-sm mb-6 leading-relaxed">
                  Evaluate stock-level performance, asset allocation, historical trends, and best/worst performers.
                </p>
              </div>
              <ul className="space-y-3 pt-4 border-t border-slate-800/60 text-sm">
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Stock-wise & asset allocation charts</span>
                </li>
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Best & worst performing stock detection</span>
                </li>
                <li className="flex items-center gap-2.5 text-slate-300">
                  <Check className="w-4 h-4 text-emerald-400 shrink-0" />
                  <span>Historical portfolio trend visualization</span>
                </li>
              </ul>
            </div>

          </div>

          {/* Additional 3 Feature Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
            {/* Feature 4: Portfolio Insights & Risk Warnings */}
            <div className="bg-[#0b0c16] border border-slate-800/90 rounded-2xl p-7 hover:border-cyan-600/40 transition-all duration-300 group">
              <div className="w-12 h-12 rounded-xl bg-cyan-950/60 border border-cyan-800/50 flex items-center justify-center text-cyan-400 mb-6 group-hover:scale-110 transition-transform">
                <PieChart className="w-6 h-6" />
              </div>
              <h3 className="text-xl font-bold text-white mb-2">Diversification & Risk</h3>
              <p className="text-slate-400 text-sm mb-4 leading-relaxed">
                Spot high concentration risks, single-stock overexposure, and portfolio diversification gaps automatically.
              </p>
              <div className="text-xs text-indigo-400 font-semibold flex items-center gap-1">
                <span>Concentration & Diversification Alerts</span>
              </div>
            </div>

            {/* Feature 5: Centralized Dashboard */}
            <div className="bg-[#0b0c16] border border-slate-800/90 rounded-2xl p-7 hover:border-emerald-600/40 transition-all duration-300 group">
              <div className="w-12 h-12 rounded-xl bg-emerald-950/60 border border-emerald-800/50 flex items-center justify-center text-emerald-400 mb-6 group-hover:scale-110 transition-transform">
                <Layers className="w-6 h-6" />
              </div>
              <h3 className="text-xl font-bold text-white mb-2">Unified Dashboard</h3>
              <p className="text-slate-400 text-sm mb-4 leading-relaxed">
                View total investment, current portfolio value, net P&L, holdings table, and broker status in one place.
              </p>
              <div className="text-xs text-emerald-400 font-semibold flex items-center gap-1">
                <span>Real-time Portfolio Summary</span>
              </div>
            </div>

            {/* Feature 6: Bank-Grade Security & Non-Custodial */}
            <div className="bg-[#0b0c16] border border-slate-800/90 rounded-2xl p-7 hover:border-purple-600/40 transition-all duration-300 group">
              <div className="w-12 h-12 rounded-xl bg-purple-950/60 border border-purple-800/50 flex items-center justify-center text-purple-400 mb-6 group-hover:scale-110 transition-transform">
                <Lock className="w-6 h-6" />
              </div>
              <h3 className="text-xl font-bold text-white mb-2">Enterprise Security</h3>
              <p className="text-slate-400 text-sm mb-4 leading-relaxed">
                JWT authentication, BCrypt password hashing, read-only API permissions, and strict non-custodial privacy.
              </p>
              <div className="text-xs text-purple-400 font-semibold flex items-center gap-1">
                <span>Zero Broker Password Storage</span>
              </div>
            </div>
          </div>

        </div>
      </section>

      {/* How It Works Section */}
      <section id="how-it-works" className="py-20 bg-[#07080e] relative">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16 space-y-4">
            <div className="inline-block px-4 py-1 rounded-full bg-indigo-950/60 border border-indigo-800/50 text-indigo-400 text-xs font-bold tracking-widest uppercase">
              WORKFLOW
            </div>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
              Centralize Portfolio Data in <span className="text-indigo-400">3 Easy Steps</span>
            </h2>
            <p className="text-slate-400 text-base sm:text-lg">
              No manual calculations required. Connect your accounts or log transactions and let TradoX handle the rest.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-[#0d0e1a] border border-slate-800/80 rounded-2xl p-8 text-center relative">
              <div className="w-12 h-12 mx-auto rounded-full bg-indigo-600/20 text-indigo-400 font-bold text-xl flex items-center justify-center mb-6 border border-indigo-500/40">
                1
              </div>
              <h3 className="text-lg font-bold text-white mb-3">Connect Broker Accounts</h3>
              <p className="text-slate-400 text-sm leading-relaxed">
                Authorize official read-only broker API connectors or manually log transactions into your unified workspace.
              </p>
            </div>

            <div className="bg-[#0d0e1a] border border-slate-800/80 rounded-2xl p-8 text-center relative">
              <div className="w-12 h-12 mx-auto rounded-full bg-purple-600/20 text-purple-400 font-bold text-xl flex items-center justify-center mb-6 border border-purple-500/40">
                2
              </div>
              <h3 className="text-lg font-bold text-white mb-3">Automate P&L & Analytics</h3>
              <p className="text-slate-400 text-sm leading-relaxed">
                TradoX normalizes transaction data and computes realized/unrealized P&L, cost basis, and daily/monthly returns.
              </p>
            </div>

            <div className="bg-[#0d0e1a] border border-slate-800/80 rounded-2xl p-8 text-center relative">
              <div className="w-12 h-12 mx-auto rounded-full bg-emerald-600/20 text-emerald-400 font-bold text-xl flex items-center justify-center mb-6 border border-emerald-500/40">
                3
              </div>
              <h3 className="text-lg font-bold text-white mb-3">Monitor & Optimize</h3>
              <p className="text-slate-400 text-sm leading-relaxed">
                Gain data-driven insights, track best/worst stock performers, and eliminate concentration risks from one dashboard.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Testimonials Section */}
      <section id="testimonials" className="py-20 bg-[#06070b] border-t border-slate-900">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-16 space-y-4">
            <div className="inline-block px-4 py-1 rounded-full bg-indigo-950/60 border border-indigo-800/50 text-indigo-400 text-xs font-bold tracking-widest uppercase">
              TESTIMONIALS
            </div>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
              Trusted by Investors & Traders Across <span className="text-indigo-400">Multiple Brokers</span>
            </h2>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="bg-[#0b0c16] border border-slate-800 p-6 rounded-2xl space-y-4">
              <div className="flex text-amber-400 gap-1">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} className="w-4 h-4 fill-amber-400" />
                ))}
              </div>
              <p className="text-slate-300 text-sm leading-relaxed">
                &quot;I used to spend hours maintaining Excel spreadsheets across 3 different broker accounts. TradoX unified everything and automated my P&L effortlessly.&quot;
              </p>
              <div className="flex items-center gap-3 pt-2">
                <div className="w-10 h-10 rounded-full bg-indigo-600/30 text-indigo-400 font-bold flex items-center justify-center border border-indigo-500/40">
                  VK
                </div>
                <div>
                  <h4 className="text-white font-bold text-sm">Vikram K.</h4>
                  <p className="text-slate-500 text-xs">Multi-Broker Stock Investor</p>
                </div>
              </div>
            </div>

            <div className="bg-[#0b0c16] border border-slate-800 p-6 rounded-2xl space-y-4">
              <div className="flex text-amber-400 gap-1">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} className="w-4 h-4 fill-amber-400" />
                ))}
              </div>
              <p className="text-slate-300 text-sm leading-relaxed">
                &quot;The stock performance ranking and concentration risk warnings helped me trim over-exposed positions before market dips. Incredible platform.&quot;
              </p>
              <div className="flex items-center gap-3 pt-2">
                <div className="w-10 h-10 rounded-full bg-purple-600/30 text-purple-400 font-bold flex items-center justify-center border border-purple-500/40">
                  AP
                </div>
                <div>
                  <h4 className="text-white font-bold text-sm">Ananya Patel</h4>
                  <p className="text-slate-500 text-xs">Active Equity Trader</p>
                </div>
              </div>
            </div>

            <div className="bg-[#0b0c16] border border-slate-800 p-6 rounded-2xl space-y-4">
              <div className="flex text-amber-400 gap-1">
                {[...Array(5)].map((_, i) => (
                  <Star key={i} className="w-4 h-4 fill-amber-400" />
                ))}
              </div>
              <p className="text-slate-300 text-sm leading-relaxed">
                &quot;Realized vs Unrealized P&L clarity is top notch. The read-only API connectors make it completely secure to sync my portfolio.&quot;
              </p>
              <div className="flex items-center gap-3 pt-2">
                <div className="w-10 h-10 rounded-full bg-emerald-600/30 text-emerald-400 font-bold flex items-center justify-center border border-emerald-500/40">
                  SR
                </div>
                <div>
                  <h4 className="text-white font-bold text-sm">Siddharth Rao</h4>
                  <p className="text-slate-500 text-xs">Portfolio Manager</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Pricing Section */}
      <section id="pricing" className="py-20 bg-[#07080e] relative">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-3xl mx-auto mb-12 space-y-4">
            <div className="inline-block px-4 py-1 rounded-full bg-indigo-950/60 border border-indigo-800/50 text-indigo-400 text-xs font-bold tracking-widest uppercase">
              PRICING
            </div>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
              Flexible Plans for Every Investor
            </h2>
            <p className="text-slate-400 text-base">
              Choose the plan that fits your investment scale. Upgrade or cancel anytime.
            </p>
          </div>

          {/* Pricing Cards */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8 items-stretch">
            {/* Free Starter */}
            <div className="bg-[#0b0c16] border border-slate-800 rounded-2xl p-8 flex flex-col justify-between">
              <div>
                <h3 className="text-xl font-bold text-white mb-2">Free Starter</h3>
                <p className="text-slate-400 text-sm mb-6">Ideal for individual investors getting started.</p>
                <div className="text-4xl font-extrabold text-white mb-6">
                  ₹0 <span className="text-sm font-normal text-slate-500">/ month</span>
                </div>
                <ul className="space-y-3 text-sm text-slate-300 mb-8">
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> 1 Broker account integration
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Manual trade & CSV import
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Basic P&L & holdings overview
                  </li>
                </ul>
              </div>
              <Link
                to="/register"
                className="w-full bg-slate-800 hover:bg-slate-700 text-white font-semibold py-3 rounded-xl text-center block transition-colors"
              >
                Start Free
              </Link>
            </div>

            {/* Pro Investor (Featured) */}
            <div className="bg-[#0e0f22] border-2 border-indigo-600 rounded-2xl p-8 flex flex-col justify-between relative shadow-xl shadow-indigo-950/60 scale-[1.03]">
              <div className="absolute -top-3.5 left-1/2 -translate-x-1/2 bg-gradient-to-r from-indigo-600 to-purple-600 text-white text-xs font-bold uppercase tracking-widest px-4 py-1 rounded-full shadow-md">
                RECOMMENDED
              </div>
              <div>
                <h3 className="text-xl font-bold text-white mb-2">Pro Investor</h3>
                <p className="text-slate-400 text-sm mb-6">For multi-broker investors seeking automated analytics.</p>
                <div className="text-4xl font-extrabold text-white mb-6">
                  ₹499 <span className="text-sm font-normal text-slate-500">/ month</span>
                </div>
                <ul className="space-y-3 text-sm text-slate-300 mb-8">
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Unlimited broker connectors
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Automated holdings & transaction sync
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Realized vs Unrealized P&L engine
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Concentration & diversification alerts
                  </li>
                </ul>
              </div>
              <Link
                to="/register"
                className="w-full bg-indigo-600 hover:bg-indigo-500 text-white font-bold py-3.5 rounded-xl text-center block shadow-lg shadow-indigo-600/30 transition-all"
              >
                Get Started with Pro
              </Link>
            </div>

            {/* Wealth / Desk */}
            <div className="bg-[#0b0c16] border border-slate-800 rounded-2xl p-8 flex flex-col justify-between">
              <div>
                <h3 className="text-xl font-bold text-white mb-2">Wealth Desk</h3>
                <p className="text-slate-400 text-sm mb-6">For family offices & portfolio managers.</p>
                <div className="text-4xl font-extrabold text-white mb-6">
                  ₹1,499 <span className="text-sm font-normal text-slate-500">/ month</span>
                </div>
                <ul className="space-y-3 text-sm text-slate-300 mb-8">
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Everything in Pro Investor
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Multi-user portfolio consolidation
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> AI-assisted portfolio summaries
                  </li>
                  <li className="flex items-center gap-2">
                    <Check className="w-4 h-4 text-emerald-400" /> Custom API webhook exports
                  </li>
                </ul>
              </div>
              <Link
                to="/register"
                className="w-full bg-slate-800 hover:bg-slate-700 text-white font-semibold py-3 rounded-xl text-center block transition-colors"
              >
                Contact Sales
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section id="faq" className="py-20 bg-[#06070b] border-t border-slate-900">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14 space-y-4">
            <div className="inline-block px-4 py-1 rounded-full bg-indigo-950/60 border border-indigo-800/50 text-indigo-400 text-xs font-bold tracking-widest uppercase">
              FAQ
            </div>
            <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
              Frequently Asked Questions
            </h2>
          </div>

          <div className="space-y-4">
            {faqs.map((faq, idx) => (
              <div
                key={idx}
                className="bg-[#0b0c16] border border-slate-800/80 rounded-xl overflow-hidden transition-colors"
              >
                <button
                  onClick={() => toggleFaq(idx)}
                  className="w-full px-6 py-4 text-left flex items-center justify-between text-white font-semibold hover:text-indigo-400 transition-colors"
                >
                  <span>{faq.q}</span>
                  <ChevronDown
                    className={`w-5 h-5 text-slate-400 transition-transform duration-200 ${
                      openFaq === idx ? 'rotate-180 text-indigo-400' : ''
                    }`}
                  />
                </button>
                {openFaq === idx && (
                  <div className="px-6 pb-5 text-slate-400 text-sm leading-relaxed border-t border-slate-800/40 pt-3">
                    {faq.a}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Bottom CTA Banner */}
      <section className="py-16 bg-[#07080e] relative">
        <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="relative bg-[#0f1024] border border-indigo-900/60 rounded-3xl p-10 md:p-14 text-center shadow-2xl overflow-hidden">
            <div className="absolute -right-20 -top-20 w-80 h-80 bg-indigo-600/20 rounded-full blur-[100px] pointer-events-none" />
            <div className="relative z-10 max-w-2xl mx-auto space-y-6">
              <h2 className="text-3xl sm:text-4xl font-extrabold text-white tracking-tight">
                Ready to centralize your portfolio analytics?
              </h2>
              <p className="text-slate-300 text-base sm:text-lg">
                Join investors and traders managing multi-broker portfolios with unified automated analytics.
              </p>
              <div className="pt-2 flex justify-center">
                <Link
                  to={user ? '/dashboard' : '/register'}
                  className="bg-indigo-600 hover:bg-indigo-500 text-white font-extrabold text-base px-8 py-4 rounded-xl shadow-xl shadow-indigo-600/40 inline-flex items-center gap-2.5 transition-all duration-200 hover:scale-105 active:scale-95"
                >
                  <Zap className="w-5 h-5 fill-white" />
                  Get Started Free
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Footer Section */}
      <footer className="bg-[#040407] border-t border-slate-900/80 pt-16 pb-12 text-slate-400 text-xs relative">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="grid grid-cols-1 md:grid-cols-6 gap-10 pb-12 border-b border-slate-900">
            
            {/* Brand Column */}
            <div className="md:col-span-1 space-y-4">
              <div className="flex items-center gap-2">
                <div className="w-8 h-8 rounded-lg bg-indigo-600 flex items-center justify-center text-white">
                  <TrendingUp className="w-4 h-4 stroke-[2.5]" />
                </div>
                <span className="text-lg font-bold text-white tracking-tight">TradoX</span>
              </div>
              <p className="text-slate-400 text-xs leading-relaxed">
                Smart multi-broker stock portfolio management and analytics platform.
              </p>
            </div>

            {/* Column 1: COMPANY */}
            <div className="space-y-3">
              <h4 className="text-white font-bold text-xs uppercase tracking-wider">COMPANY</h4>
              <ul className="space-y-2">
                <li><a href="#hero" className="hover:text-white transition-colors">About Us</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Contact Us</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Careers</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Support</a></li>
              </ul>
            </div>

            {/* Column 2: LEGAL */}
            <div className="space-y-3">
              <h4 className="text-white font-bold text-xs uppercase tracking-wider">LEGAL</h4>
              <ul className="space-y-2">
                <li><a href="#hero" className="hover:text-white transition-colors">Terms & Conditions</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Privacy Policy</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Security Overview</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Disclaimer</a></li>
              </ul>
            </div>

            {/* Column 3: PLATFORM */}
            <div className="space-y-3">
              <h4 className="text-white font-bold text-xs uppercase tracking-wider">PLATFORM</h4>
              <ul className="space-y-2">
                <li><a href="#features" className="hover:text-white transition-colors">Broker Connectors</a></li>
                <li><a href="#features" className="hover:text-white transition-colors">P&L Calculation Engine</a></li>
                <li><a href="#features" className="hover:text-white transition-colors">Portfolio Analytics</a></li>
                <li><a href="#features" className="hover:text-white transition-colors">Diversification Metrics</a></li>
              </ul>
            </div>

            {/* Column 4: PRODUCT */}
            <div className="space-y-3">
              <h4 className="text-white font-bold text-xs uppercase tracking-wider">PRODUCT</h4>
              <ul className="space-y-2">
                <li><a href="#features" className="hover:text-white transition-colors">Features</a></li>
                <li><a href="#pricing" className="hover:text-white transition-colors">Pricing</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Dashboard App</a></li>
                <li><a href="#features" className="hover:text-white transition-colors">Integrations</a></li>
              </ul>
            </div>

            {/* Column 5: RESOURCES */}
            <div className="space-y-3">
              <h4 className="text-white font-bold text-xs uppercase tracking-wider">RESOURCES</h4>
              <ul className="space-y-2">
                <li><a href="#hero" className="hover:text-white transition-colors">Portfolio Guides</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">P&L Calculation Guide</a></li>
                <li><a href="#hero" className="hover:text-white transition-colors">Broker API Setup</a></li>
                <li><a href="#faq" className="hover:text-white transition-colors">FAQ</a></li>
              </ul>
            </div>

          </div>

          {/* Bottom Copyright Row & Scroll To Top */}
          <div className="pt-8 flex flex-col sm:flex-row items-center justify-between gap-4">
            <p className="text-slate-500">
              © {new Date().getFullYear()} TradoX. All rights reserved.
            </p>
          </div>
        </div>

        {/* Floating Scroll to Top button at bottom right */}
        <button
          onClick={scrollToTop}
          className="fixed bottom-6 right-6 z-40 w-11 h-11 rounded-full bg-indigo-600 hover:bg-indigo-500 text-white flex items-center justify-center shadow-lg shadow-indigo-600/40 transition-all duration-200 hover:scale-110 active:scale-95"
          aria-label="Scroll to top"
        >
          <ArrowUp className="w-5 h-5 stroke-[2.5]" />
        </button>
      </footer>

    </div>
  );
}
