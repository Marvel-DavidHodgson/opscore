import { create } from 'zustand';
import type { Tenant } from '@/types/tenant.types';

interface TenantState {
  tenant: Tenant | null;
  isLoading: boolean;
  setTenant: (tenant: Tenant) => void;
  clearTenant: () => void;
  setLoading: (loading: boolean) => void;
  getLabelOverride: (key: string) => string;
}

export const useTenantStore = create<TenantState>((set, get) => ({
  tenant: null,
  isLoading: false,

  setTenant: (tenant) => {
    // Apply tenant primary color to CSS variable
    if (tenant.primaryColor) {
      document.documentElement.style.setProperty('--color-primary', tenant.primaryColor);
    }
    set({ tenant, isLoading: false });
  },

  clearTenant: () => {
    document.documentElement.style.removeProperty('--color-primary');
    set({ tenant: null });
  },

  setLoading: (loading) => set({ isLoading: loading }),

  getLabelOverride: (key: string) => {
    const { tenant } = get();
    if (!tenant) return key;
    return tenant.labelOverrides[key] || key;
  },
}));
