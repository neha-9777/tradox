export default function LoadingSpinner({ size = 'md', className = '' }) {
  const sizes = {
    sm: 'h-4 w-4',
    md: 'h-8 w-8',
    lg: 'h-12 w-12',
    xl: 'h-16 w-16',
  };

  return (
    <div className={`flex items-center justify-center ${className}`}>
      <div className={`${sizes[size]} relative`}>
        <div
          className={`${sizes[size]} rounded-full border-2 border-slate-700 border-t-indigo-500 animate-spin`}
        />
      </div>
    </div>
  );
}

export function FullPageLoader() {
  return (
    <div className="min-h-screen bg-slate-950 flex items-center justify-center">
      <div className="flex flex-col items-center gap-4">
        <LoadingSpinner size="lg" />
        <p className="text-slate-400 text-sm animate-pulse">Loading TradoX...</p>
      </div>
    </div>
  );
}
