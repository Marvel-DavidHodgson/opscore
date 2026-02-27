import apiClient from './axios';
import {
  LoginRequest,
  LoginResponse,
  RefreshTokenRequest,
  RefreshTokenResponse,
  AuthUser,
} from '../types/auth.types';

// Login
export const login = async (data: LoginRequest): Promise<LoginResponse> => {
  const response = await apiClient.post<any>('/auth/login', data);
  
  // Transform backend response to frontend format
  // Backend returns flat fields, frontend expects nested user object
  return {
    accessToken: response.data.accessToken,
    refreshToken: response.data.refreshToken,
    user: {
      id: response.data.userId,
      username: response.data.email, // Use email as username
      email: response.data.email,
      role: response.data.role,
      tenantId: response.data.tenantId,
    },
  };
};

// Refresh token
export const refreshToken = async (
  data: RefreshTokenRequest
): Promise<RefreshTokenResponse> => {
  const response = await apiClient.post<RefreshTokenResponse>('/auth/refresh', data);
  return response.data;
};

// Get current user profile
export const getMe = async (): Promise<AuthUser> => {
  const response = await apiClient.get<AuthUser>('/auth/me');
  return response.data;
};

// Logout (invalidate refresh token)
export const logout = async (): Promise<void> => {
  await apiClient.post('/auth/logout');
};
