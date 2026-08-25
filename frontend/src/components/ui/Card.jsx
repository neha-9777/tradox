export default function Card({ children, className = '', ...props }) {
  return (
    <div
      className={`bg-slate-800/50 backdrop-blur-xl border border-slate-700/50 rounded-2xl ${className}`}
      {...props}
    >
      {children}
    </div>
  );
}
