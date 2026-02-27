import apiClient from './axios';
import { Tenant, UpdateTenantRequest } from '../types/tenant.types';

// Get current user's tenant
export const getCurrentTenant = async (): Promise<Tenant> => {
  const response = await apiClient.get<Tenant>('/tenants/me');
  return response.data;
};

// Get all tenants (ADMIN only)
export const getTenants = async (): Promise<Tenant[]> => {
  const response = await apiClient.get<Tenant[]>('/tenants');
  return response.data;
};

// Get tenant by ID (kept for compatibility)
export const getTenantById = async (id: string): Promise<Tenant> => {
  return getCurrentTenant(); // Use /tenants/me instead
};

// Create tenant (ADMIN only)
export const createTenant = async (data: Omit<Tenant, 'id'>): Promise<Tenant> => {
  const response = await apiClient.post<Tenant>('/tenants', data);
  return response.data;
};

// Update tenant
export const updateTenant = async (
  id: number,
  data: UpdateTenantRequest
): Promise<Tenant> => {
  const response = await apiClient.put<Tenant>(`/tenants/${id}`, data);
  return response.data;
};

// Delete tenant (ADMIN only)
export const deleteTenant = async (id: number): Promise<void> => {
  await apiClient.delete(`/tenants/${id}`);
};
