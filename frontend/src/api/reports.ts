import apiClient from '@/lib/api-client';
import { KpiSummary } from '@/types';

export const reportsApi = {
  getKpiSummary: async (): Promise<KpiSummary> => {
    const response = await apiClient.get<KpiSummary>('/reports/kpi');
    return response.data;
  },

  exportItems: async (params?: {
    status?: string;
    category?: string;
  }): Promise<Blob> => {
    const response = await apiClient.get('/reports/items/export', {
      params,
      responseType: 'blob',
    });
    return response.data;
  },

  getItemsByCategory: async (): Promise<Record<string, number>> => {
    const response = await apiClient.get<Record<string, number>>(
      '/reports/categories'
    );
    return response.data;
  },
};
