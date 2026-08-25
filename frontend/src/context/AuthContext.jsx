import { createContext, useContext, useState, useEffect, useCallback } from 'react';
import authApi from '../api/authApi';
import { TOKEN_KEY, USER_KEY } from '../utils/constants';

export const AuthContext = createContext(null);

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem(USER_KEY);
    return stored ? JSON.parse(stored) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem(TOKEN_KEY));
  const [loading, setLoading] = useState(true);

  // On mount, verify token by fetching current user
  useEffect(() => {
    const verifyToken = async () => {
      if (!token) {
        setLoading(false);
        return;
      }
      try {
        const response = await authApi.getMe();
        const userData = response.data;
        setUser(userData);
        localStorage.setItem(USER_KEY, JSON.stringify(userData));
      } catch {
        // Token is invalid or expired
        logout();
      } finally {
        setLoading(false);
      }
    };
    verifyToken();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const login = useCallback(async (credentials) => {
    const response = await authApi.login(credentials);
    const { token: newToken, userId, email, firstName, lastName } = response.data;
    const userData = { id: userId, email, firstName, lastName };
    setToken(newToken);
    setUser(userData);
    localStorage.setItem(TOKEN_KEY, newToken);
    localStorage.setItem(USER_KEY, JSON.stringify(userData));
    return userData;
  }, []);

  const register = useCallback(async (data) => {
    const response = await authApi.register(data);
    const { token: newToken, userId, email, firstName, lastName } = response.data;
    const userData = { id: userId, email, firstName, lastName };
    setToken(newToken);
    setUser(userData);
    localStorage.setItem(TOKEN_KEY, newToken);
    localStorage.setItem(USER_KEY, JSON.stringify(userData));
    return userData;
  }, []);

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  }, []);

  const value = {
    user,
    token,
    loading,
    isAuthenticated: !!token && !!user,
    login,
    register,
    logout,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}
