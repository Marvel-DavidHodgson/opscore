import { ItemStatus } from './item.types';

export interface KpiSummary {
  totalItems: number;
  approved: number;
  pending: number;
  rejected: number;
}

export interface StatusBreakdownItem {
  status: ItemStatus;
  label: string;
  count: number;
  percent: number;
}

export interface ReportFilters {
  status?: ItemStatus;
  category?: string;
  startDate?: string;
  endDate?: string;
}
