export type Role = 'ADMIN' | 'MANAGER' | 'OPERATOR' | 'VIEWER';

export type ItemStatus = 'DRAFT' | 'ACTIVE' | 'PENDING' | 'APPROVED' | 'REJECTED' | 'CLOSED';

export type IndustryType = 'ERP' | 'EXPENSE' | 'MANUFACTURING' | 'LOGISTICS' | 'SAP';

export interface User {
  id: string;
  tenantId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  isActive: boolean;
  lastLoginAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface Tenant {
  id: string;
  name: string;
  slug: string;
  logoUrl: string | null;
  primaryColor: string;
  industryType: IndustryType;
  moduleConfig: Record<string, any>;
  labelOverrides: Record<string, string>;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Item {
  id: string;
  tenantId: string;
  code: string;
  title: string;
  description: string | null;
  category: string | null;
  status: ItemStatus;
  assignedToUserId: string | null;
  assignedToUserName: string | null;
  createdByUserId: string;
  createdByUserName: string;
  metadata: Record<string, any> | null;
  createdAt: string;
  updatedAt: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  tenantId: string;
  tenantName: string;
  expiresIn: number;
}

export interface CreateItemRequest {
  title: string;
  description?: string;
  category?: string;
  assignedToUserId?: string;
  metadata?: Record<string, any>;
}

export interface UpdateItemRequest {
  title?: string;
  description?: string;
  category?: string;
  assignedToUserId?: string;
  metadata?: Record<string, any>;
}

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role: Role;
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  role?: Role;
  isActive?: boolean;
}

export interface KpiSummary {
  totalItems: number;
  itemsByStatus: Record<string, number>;
  itemsByAssignee: Record<string, number>;
  statusBreakdown: Array<{
    status: ItemStatus;
    count: number;
  }>;
}

export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
}
