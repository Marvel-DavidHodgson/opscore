import apiClient from '@/lib/api-client';
import { CreateUserRequest, UpdateUserRequest, User } from '@/types';

export const usersApi = {
  getUsers: async (): Promise<User[]> => {
    const response = await apiClient.get<User[]>('/users');
    return response.data;
  },

  getUserById: async (id: string): Promise<User> => {
    const response = await apiClient.get<User>(`/users/${id}`);
    return response.data;
  },

  createUser: async (data: CreateUserRequest): Promise<User> => {
    const response = await apiClient.post<User>('/users', data);
    return response.data;
  },

  updateUser: async (id: string, data: UpdateUserRequest): Promise<User> => {
    const response = await apiClient.put<User>(`/users/${id}`, data);
    return response.data;
  },

  deactivateUser: async (id: string): Promise<void> => {
    await apiClient.delete(`/users/${id}`);
  },
};
