import apiClient from './axios';
import { KpiSummary, StatusBreakdownItem, ReportFilters } from '../types/report.types';
import { AuditLog } from '../types/item.types';

// Get KPI summary
export const getKpiSummary = async (filters?: ReportFilters): Promise<KpiSummary> => {
  const response = await apiClient.get<KpiSummary>('/reports/kpis', {
    params: filters,
  });
  return response.data;
};

// Get status breakdown
export const getStatusBreakdown = async (
  filters?: ReportFilters
): Promise<StatusBreakdownItem[]> => {
  const response = await apiClient.get<StatusBreakdownItem[]>('/reports/status-breakdown', {
    params: filters,
  });
  return response.data;
};

// Get audit logs
export const getAuditLogs = async (params?: {
  entityType?: string;
  userId?: string;
  startDate?: string;
  endDate?: string;
}): Promise<AuditLog[]> => {
  const response = await apiClient.get<{ content: AuditLog[] }>('/audit', { params });
  // Backend returns Spring Page object with content array
  return response.data.content || [];
};

// Export report (download CSV/Excel)
export const exportReport = async (
  format: 'csv' | 'excel',
  filters?: ReportFilters
): Promise<Blob> => {
  const response = await apiClient.get(`/reports/export/${format}`, {
    params: filters,
    responseType: 'blob',
  });
  return response.data;
};
