export default function GlassCard({ children, hover = false, className = '', ...props }) {
  const baseClasses = 'bg-slate-800/50 backdrop-blur-xl border border-slate-700/50 rounded-2xl';
  const hoverClasses = hover
    ? 'transition-all duration-300 hover:bg-slate-800/70 hover:border-slate-600/50 hover:shadow-lg hover:shadow-indigo-500/5 hover:scale-[1.01]'
    : '';

  return (
    <div className={`${baseClasses} ${hoverClasses} ${className}`} {...props}>
      {children}
    </div>
  );
}
