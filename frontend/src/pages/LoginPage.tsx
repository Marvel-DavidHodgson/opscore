import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { useTranslation } from 'react-i18next';
import { Lock, Mail } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const { login, isAuthenticated, isLoggingIn, loginError } = useAuth();
  const { t } = useTranslation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  // Redirect if already authenticated
  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    login({ username, password });
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-8">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold text-gray-900">OpsCore</h1>
        <p className="text-gray-600 mt-2">{t('auth.loginTitle')}</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Username */}
        <div>
          <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">
            {t('auth.username')}
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Mail className="h-5 w-5 text-gray-400" />
            </div>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
              className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder={t('auth.enterUsername')}
            />
          </div>
        </div>

        {/* Password */}
        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-2">
            {t('auth.password')}
          </label>
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Lock className="h-5 w-5 text-gray-400" />
            </div>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder={t('auth.enterPassword')}
            />
          </div>
        </div>

        {/* Error message */}
        {loginError && (
          <div className="bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-md text-sm">
            {t('auth.invalidCredentials')}
          </div>
        )}

        {/* Submit button */}
        <button
          type="submit"
          disabled={isLoggingIn}
          className="w-full bg-primary text-white py-2 px-4 rounded-md hover:bg-primary/90 disabled:opacity-50 transition-colors"
        >
          {isLoggingIn ? t('auth.loggingIn') : t('auth.login')}
        </button>
      </form>

      {/* Demo credentials hint */}
      <div className="mt-6 p-4 bg-gray-50 rounded-md text-sm text-gray-600">
        <p className="font-medium mb-1">{t('auth.demoCredentials')}:</p>
        <p>{t('auth.username')}: <span className="font-mono">admin@demo.com</span></p>
        <p>{t('auth.password')}: <span className="font-mono">password123</span></p>
      </div>
    </div>
  );
};
