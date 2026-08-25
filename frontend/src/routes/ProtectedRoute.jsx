import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { FullPageLoader } from '../components/ui/LoadingSpinner';

export default function ProtectedRoute({ children }) {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <FullPageLoader />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return children;
}
