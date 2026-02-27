import apiClient from './axios';
import { User, CreateUserRequest, UpdateUserRequest } from '../types/user.types';

// Get all users
export const getUsers = async (): Promise<User[]> => {
  const response = await apiClient.get<User[]>('/users');
  return response.data;
};

// Get user by ID
export const getUserById = async (id: string): Promise<User> => {
  const response = await apiClient.get<User>(`/users/${id}`);
  return response.data;
};

// Create user
export const createUser = async (data: CreateUserRequest): Promise<User> => {
  const response = await apiClient.post<User>('/users', data);
  return response.data;
};

// Update user
export const updateUser = async (id: string, data: UpdateUserRequest): Promise<User> => {
  const response = await apiClient.put<User>(`/users/${id}`, data);
  return response.data;
};

// Delete user
export const deleteUser = async (id: string): Promise<void> => {
  await apiClient.delete(`/users/${id}`);
};

// Get users by tenant (ADMIN only)
export const getUsersByTenant = async (tenantId: string): Promise<User[]> => {
  const response = await apiClient.get<User[]>(`/users/tenant/${tenantId}`);
  return response.data;
};
