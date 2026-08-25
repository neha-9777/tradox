import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import ToastContainer from './components/ui/Toast';
import ProtectedRoute from './routes/ProtectedRoute';
import AppLayout from './components/layout/AppLayout';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import PortfolioPage from './pages/PortfolioPage';
import TransactionsPage from './pages/TransactionsPage';
import AnalyticsPage from './pages/AnalyticsPage';
import InsightsPage from './pages/InsightsPage';
import BrokersPage from './pages/BrokersPage';
import SettingsPage from './pages/SettingsPage';

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastContainer />
        <Routes>
          {/* Public routes */}
          <Route path="/" element={<HomePage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Protected routes */}
          <Route
            element={
              <ProtectedRoute>
                <AppLayout />
              </ProtectedRoute>
            }
          >
            <Route path="/dashboard" element={<DashboardPage />} />
            <Route path="/portfolio" element={<PortfolioPage />} />
            <Route path="/transactions" element={<TransactionsPage />} />
            <Route path="/analytics" element={<AnalyticsPage />} />
            <Route path="/insights" element={<InsightsPage />} />
            <Route path="/brokers" element={<BrokersPage />} />
            <Route path="/settings" element={<SettingsPage />} />
          </Route>

          {/* Default redirect */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

// Temporary placeholder for future pages
function PlaceholderPage({ title }) {
  return (
    <div className="animate-fade-in">
      <h1 className="text-2xl font-bold text-white mb-2">{title}</h1>
      <p className="text-slate-400 text-sm">This page is coming soon in a future phase.</p>
      <div className="mt-8 glass-card p-12 flex items-center justify-center">
        <p className="text-slate-500 text-lg">🚧 Under Construction</p>
      </div>
    </div>
  );
}

export default App;
