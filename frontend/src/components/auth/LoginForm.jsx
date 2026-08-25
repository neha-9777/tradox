import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock } from 'lucide-react';
import Input from '../ui/Input';
import Button from '../ui/Button';
import { toast } from '../ui/Toast';
import { useAuth } from '../../hooks/useAuth';

export default function LoginForm() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!formData.email) newErrors.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Invalid email format';
    if (!formData.password) newErrors.password = 'Password is required';
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: '' }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validate()) return;

    setLoading(true);
    try {
      await login(formData);
      toast.success('Welcome back!', { title: 'Login Successful' });
      navigate('/dashboard');
    } catch (err) {
      const message = err.response?.data?.message || 'Invalid email or password';
      toast.error(message, { title: 'Login Failed' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <Input
        id="login-email"
        label="Email"
        type="email"
        name="email"
        placeholder="you@example.com"
        value={formData.email}
        onChange={handleChange}
        error={errors.email}
        icon={Mail}
        autoComplete="email"
      />
      <Input
        id="login-password"
        label="Password"
        type="password"
        name="password"
        placeholder="Enter your password"
        value={formData.password}
        onChange={handleChange}
        error={errors.password}
        icon={Lock}
        autoComplete="current-password"
      />
      <Button
        type="submit"
        loading={loading}
        className="w-full py-3"
        size="lg"
      >
        Sign In
      </Button>
      <p className="text-center text-sm text-slate-400">
        Don't have an account?{' '}
        <Link to="/register" className="text-indigo-400 hover:text-indigo-300 font-medium transition-colors">
          Create one
        </Link>
      </p>
    </form>
  );
}
