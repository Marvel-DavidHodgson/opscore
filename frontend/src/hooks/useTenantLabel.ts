import { useTenantStore } from '../store/tenant.store';

/**
 * Hook to get white-label override for UI labels
 * Returns overridden label if exists, otherwise returns default label
 */
export const useTenantLabel = (key: string, defaultLabel: string): string => {
  const { tenant } = useTenantStore();
  
  if (!tenant) {
    return defaultLabel;
  }

  return tenant.labelOverrides?.[key] || defaultLabel;
};
