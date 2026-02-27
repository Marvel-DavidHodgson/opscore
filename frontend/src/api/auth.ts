import apiClient from '@/lib/api-client';
import { LoginRequest, LoginResponse } from '@/types';

export const authApi = {
  login: async (data: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/auth/login', data, {
      headers: {
        'X-Tenant-ID': '00000000-0000-0000-0000-000000000001',
      },
    });
    return response.data;
  },

  refresh: async (refreshToken: string): Promise<LoginResponse> => {
    const response = await apiClient.post<LoginResponse>('/auth/refresh', {
      refreshToken,
    });
    return response.data;
  },

  logout: async (refreshToken: string): Promise<void> => {
    await apiClient.post('/auth/logout', { refreshToken });
  },
};
