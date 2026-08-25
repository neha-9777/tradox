import { TrendingUp, TrendingDown } from 'lucide-react';

export default function StatCard({
  title,
  value,
  change,
  changeType = 'neutral', // 'gain' | 'loss' | 'neutral'
  icon: Icon,
  prefix = '',
  suffix = '',
  className = '',
}) {
  const changeColors = {
    gain: 'text-emerald-400',
    loss: 'text-red-400',
    neutral: 'text-slate-400',
  };

  const changeBgColors = {
    gain: 'bg-emerald-400/10',
    loss: 'bg-red-400/10',
    neutral: 'bg-slate-400/10',
  };

  const ChangeIcon = changeType === 'gain' ? TrendingUp : changeType === 'loss' ? TrendingDown : null;

  return (
    <div
      className={`glass-card-hover p-5 animate-fade-in ${className}`}
    >
      <div className="flex items-start justify-between mb-3">
        <p className="text-sm font-medium text-slate-400">{title}</p>
        {Icon && (
          <div className="p-2 rounded-lg bg-indigo-500/10">
            <Icon className="h-4.5 w-4.5 text-indigo-400" />
          </div>
        )}
      </div>
      <p className="text-2xl font-bold text-white mb-2">
        {prefix}{value}{suffix}
      </p>
      {change !== undefined && (
        <div className="flex items-center gap-1.5">
          {ChangeIcon && (
            <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium ${changeBgColors[changeType]} ${changeColors[changeType]}`}>
              <ChangeIcon className="h-3 w-3" />
              {change}
            </span>
          )}
          {!ChangeIcon && (
            <span className={`text-xs font-medium ${changeColors[changeType]}`}>
              {change}
            </span>
          )}
        </div>
      )}
    </div>
  );
}
