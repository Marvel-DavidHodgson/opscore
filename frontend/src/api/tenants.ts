import apiClient from '@/lib/api-client';
import { Tenant } from '@/types';

export const tenantsApi = {
  getCurrentTenant: async (): Promise<Tenant> => {
    const response = await apiClient.get<Tenant>('/tenants/me');
    return response.data;
  },

  updateCurrentTenant: async (data: {
    logoUrl?: string;
    primaryColor?: string;
    moduleConfig?: Record<string, any>;
    labelOverrides?: Record<string, string>;
  }): Promise<Tenant> => {
    const response = await apiClient.put<Tenant>('/tenants/me', data);
    return response.data;
  },
};
