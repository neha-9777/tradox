import { TrendingUp } from 'lucide-react';
import RegisterForm from '../components/auth/RegisterForm';

export default function RegisterPage() {
  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center p-4 relative overflow-hidden">
      {/* Background gradient orbs */}
      <div className="absolute top-1/4 -left-32 w-96 h-96 bg-violet-500/10 rounded-full blur-3xl" />
      <div className="absolute bottom-1/4 -right-32 w-96 h-96 bg-indigo-500/10 rounded-full blur-3xl" />
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-purple-500/5 rounded-full blur-3xl" />

      {/* Register card */}
      <div className="w-full max-w-md animate-fade-in">
        <div className="glass-card p-8 animate-pulse-glow">
          {/* Logo */}
          <div className="flex flex-col items-center mb-8">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-indigo-500 to-violet-500 flex items-center justify-center mb-4 shadow-lg shadow-indigo-500/25">
              <TrendingUp className="h-7 w-7 text-white" />
            </div>
            <h1 className="text-3xl font-bold gradient-text">TradoX</h1>
            <p className="text-slate-400 text-sm mt-2">Create your account to get started</p>
          </div>

          <RegisterForm />
        </div>

        {/* Footer */}
        <p className="text-center text-xs text-slate-600 mt-6">
          © {new Date().getFullYear()} TradoX. Smart portfolio tracking.
        </p>
      </div>
    </div>
  );
}
