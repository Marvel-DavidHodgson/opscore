import { useEffect } from 'react';
import { useTenantStore } from '../store/tenant.store';
import { useAuthStore } from '../store/auth.store';
import { getTenantById } from '../api/tenant.api';

interface TenantThemeProviderProps {
  children: React.ReactNode;
}

/**
 * TenantThemeProvider - Loads and applies tenant theming on mount
 */
export const TenantThemeProvider: React.FC<TenantThemeProviderProps> = ({ children }) => {
  const { user } = useAuthStore();
  const { tenant, setTenant } = useTenantStore();

  useEffect(() => {
    const loadTenant = async () => {
      if (user && !tenant) {
        try {
          const tenantData = await getTenantById(parseInt(user.tenantId));
          setTenant(tenantData);
        } catch (error) {
          console.error('Failed to load tenant:', error);
        }
      }
    };

    loadTenant();
  }, [user, tenant, setTenant]);

  return <>{children}</>;
};
