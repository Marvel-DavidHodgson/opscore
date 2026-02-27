import { useAuthStore } from '../store/auth.store';
import { useTenantStore } from '../store/tenant.store';
import { useMutation } from '@tanstack/react-query';
import { login as loginApi } from '../api/auth.api';
import { LoginRequest } from '../types/auth.types';
import { getTenantById } from '../api/tenant.api';

export const useAuth = () => {
  const { user, accessToken, login, logout } = useAuthStore();
  const { setTenant } = useTenantStore();

  // Login mutation
  const loginMutation = useMutation({
    mutationFn: async (credentials: LoginRequest) => {
      const response = await loginApi(credentials);
      return response;
    },
    onSuccess: async (data) => {
      // Store auth tokens and user
      login(data);

      // Fetch and set tenant information for theming
      try {
        const tenant = await getTenantById(data.user.tenantId);
        setTenant(tenant);
      } catch (error) {
        console.error('Failed to fetch tenant information:', error);
      }
    },
  });

  // Logout handler
  const handleLogout = () => {
    logout();
  };

  return {
    user,
    isAuthenticated: !!accessToken,
    login: loginMutation.mutate,
    logout: handleLogout,
    isLoggingIn: loginMutation.isPending,
    loginError: loginMutation.error,
  };
};
