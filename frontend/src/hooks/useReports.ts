import { useQuery, useMutation } from '@tanstack/react-query';
import {
  getKpiSummary,
  getStatusBreakdown,
  getAuditLogs,
  exportReport,
} from '../api/reports.api';
import { ReportFilters } from '../types/report.types';

// Query keys
export const reportKeys = {
  all: ['reports'] as const,
  kpis: (filters?: ReportFilters) => [...reportKeys.all, 'kpis', filters] as const,
  statusBreakdown: (filters?: ReportFilters) =>
    [...reportKeys.all, 'status-breakdown', filters] as const,
  auditLogs: (filters?: Record<string, unknown>) =>
    [...reportKeys.all, 'audit-logs', filters] as const,
};

// Get KPI summary
export const useKpiSummary = (filters?: ReportFilters) => {
  return useQuery({
    queryKey: reportKeys.kpis(filters),
    queryFn: () => getKpiSummary(filters),
  });
};

// Get status breakdown
export const useStatusBreakdown = (filters?: ReportFilters) => {
  return useQuery({
    queryKey: reportKeys.statusBreakdown(filters),
    queryFn: () => getStatusBreakdown(filters),
  });
};

// Get audit logs
export const useAuditLogs = (filters?: {
  entityType?: string;
  userId?: string;
  startDate?: string;
  endDate?: string;
}) => {
  return useQuery({
    queryKey: reportKeys.auditLogs(filters),
    queryFn: () => getAuditLogs(filters),
  });
};

// Export report mutation
export const useExportReport = () => {
  return useMutation({
    mutationFn: ({
      format,
      filters,
    }: {
      format: 'csv' | 'excel';
      filters?: ReportFilters;
    }) => exportReport(format, filters),
    onSuccess: (blob, variables) => {
      // Download the file
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `report_${new Date().toISOString()}.${variables.format === 'csv' ? 'csv' : 'xlsx'}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    },
  });
};
