import { useState, useEffect, useCallback } from 'react';
import { X, CheckCircle, AlertCircle, Info, AlertTriangle } from 'lucide-react';

const TOAST_VARIANTS = {
  success: {
    icon: CheckCircle,
    classes: 'bg-emerald-500/10 border-emerald-500/30 text-emerald-400',
    iconClass: 'text-emerald-400',
  },
  error: {
    icon: AlertCircle,
    classes: 'bg-red-500/10 border-red-500/30 text-red-400',
    iconClass: 'text-red-400',
  },
  warning: {
    icon: AlertTriangle,
    classes: 'bg-amber-500/10 border-amber-500/30 text-amber-400',
    iconClass: 'text-amber-400',
  },
  info: {
    icon: Info,
    classes: 'bg-indigo-500/10 border-indigo-500/30 text-indigo-400',
    iconClass: 'text-indigo-400',
  },
};

function ToastItem({ toast, onDismiss }) {
  const variant = TOAST_VARIANTS[toast.type] || TOAST_VARIANTS.info;
  const IconComponent = variant.icon;

  useEffect(() => {
    const timer = setTimeout(() => onDismiss(toast.id), toast.duration || 4000);
    return () => clearTimeout(timer);
  }, [toast.id, toast.duration, onDismiss]);

  return (
    <div
      className={`flex items-start gap-3 p-4 rounded-xl border backdrop-blur-xl animate-slide-up ${variant.classes}`}
    >
      <IconComponent className={`h-5 w-5 mt-0.5 flex-shrink-0 ${variant.iconClass}`} />
      <div className="flex-1 min-w-0">
        {toast.title && (
          <p className="font-semibold text-sm text-white mb-0.5">{toast.title}</p>
        )}
        <p className="text-sm">{toast.message}</p>
      </div>
      <button
        onClick={() => onDismiss(toast.id)}
        className="flex-shrink-0 text-slate-500 hover:text-slate-300 transition-colors"
      >
        <X className="h-4 w-4" />
      </button>
    </div>
  );
}

// Singleton toast manager
let toastListeners = [];
let toastIdCounter = 0;

export function toast(message, type = 'info', options = {}) {
  const newToast = {
    id: ++toastIdCounter,
    message,
    type,
    title: options.title,
    duration: options.duration || 4000,
  };
  toastListeners.forEach((listener) => listener(newToast));
}

toast.success = (message, options) => toast(message, 'success', options);
toast.error = (message, options) => toast(message, 'error', options);
toast.warning = (message, options) => toast(message, 'warning', options);
toast.info = (message, options) => toast(message, 'info', options);

export const showToast = (message, type = 'info') => toast(message, type);


export default function ToastContainer() {
  const [toasts, setToasts] = useState([]);

  useEffect(() => {
    const listener = (newToast) => {
      setToasts((prev) => [...prev, newToast]);
    };
    toastListeners.push(listener);
    return () => {
      toastListeners = toastListeners.filter((l) => l !== listener);
    };
  }, []);

  const dismiss = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  if (toasts.length === 0) return null;

  return (
    <div className="fixed top-4 right-4 z-50 flex flex-col gap-2 w-96 max-w-[calc(100vw-2rem)]">
      {toasts.map((t) => (
        <ToastItem key={t.id} toast={t} onDismiss={dismiss} />
      ))}
    </div>
  );
}
