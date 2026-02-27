export type IndustryType = 'ERP' | 'EXPENSE' | 'MANUFACTURING' | 'LOGISTICS' | 'SAP';

export interface Tenant {
  id: string;
  name: string;
  slug: string;
  logoUrl: string | null;
  primaryColor: string;
  industryType: IndustryType;
  moduleConfig: ModuleConfig;
  labelOverrides: Record<string, string>;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ModuleConfig {
  modules: string[];
}

export interface UpdateTenantRequest {
  logoUrl?: string;
  primaryColor?: string;
  moduleConfig?: ModuleConfig;
  labelOverrides?: Record<string, string>;
}
