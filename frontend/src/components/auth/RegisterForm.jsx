import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Mail, Lock, User } from 'lucide-react';
import Input from '../ui/Input';
import Button from '../ui/Button';
import { toast } from '../ui/Toast';
import { useAuth } from '../../hooks/useAuth';

export default function RegisterForm() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [errors, setErrors] = useState({});

  const validate = () => {
    const newErrors = {};
    if (!formData.firstName.trim()) newErrors.firstName = 'First name is required';
    if (!formData.lastName.trim()) newErrors.lastName = 'Last name is required';
    if (!formData.email) newErrors.email = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(formData.email)) newErrors.email = 'Invalid email format';
    if (!formData.password) newErrors.password = 'Password is required';
    else if (formData.password.length < 8) newErrors.password = 'Password must be at least 8 characters';
    if (formData.password !== formData.confirmPassword) newErrors.confirmPassword = 'Passwords do not match';
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
      const { confirmPassword, ...submitData } = formData;
      await register(submitData);
      toast.success('Your account has been created!', { title: 'Welcome to TradoX' });
      navigate('/dashboard');
    } catch (err) {
      const message = err.response?.data?.message || 'Registration failed. Please try again.';
      toast.error(message, { title: 'Registration Failed' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <Input
          id="register-firstName"
          label="First Name"
          name="firstName"
          placeholder="John"
          value={formData.firstName}
          onChange={handleChange}
          error={errors.firstName}
          icon={User}
          autoComplete="given-name"
        />
        <Input
          id="register-lastName"
          label="Last Name"
          name="lastName"
          placeholder="Doe"
          value={formData.lastName}
          onChange={handleChange}
          error={errors.lastName}
          icon={User}
          autoComplete="family-name"
        />
      </div>
      <Input
        id="register-email"
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
        id="register-password"
        label="Password"
        type="password"
        name="password"
        placeholder="Min. 8 characters"
        value={formData.password}
        onChange={handleChange}
        error={errors.password}
        icon={Lock}
        autoComplete="new-password"
      />
      <Input
        id="register-confirmPassword"
        label="Confirm Password"
        type="password"
        name="confirmPassword"
        placeholder="Re-enter password"
        value={formData.confirmPassword}
        onChange={handleChange}
        error={errors.confirmPassword}
        icon={Lock}
        autoComplete="new-password"
      />
      <Button
        type="submit"
        loading={loading}
        className="w-full py-3"
        size="lg"
      >
        Create Account
      </Button>
      <p className="text-center text-sm text-slate-400">
        Already have an account?{' '}
        <Link to="/login" className="text-indigo-400 hover:text-indigo-300 font-medium transition-colors">
          Sign in
        </Link>
      </p>
    </form>
  );
}
