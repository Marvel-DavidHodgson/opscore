import apiClient from '@/lib/api-client';
import { CreateItemRequest, Item, PageResponse, UpdateItemRequest } from '@/types';

export const itemsApi = {
  getItems: async (params?: {
    status?: string;
    category?: string;
    assignedToUserId?: string;
    page?: number;
    size?: number;
  }): Promise<PageResponse<Item>> => {
    const response = await apiClient.get<PageResponse<Item>>('/items', {
      params,
    });
    return response.data;
  },

  getItemById: async (id: string): Promise<Item> => {
    const response = await apiClient.get<Item>(`/items/${id}`);
    return response.data;
  },

  createItem: async (data: CreateItemRequest): Promise<Item> => {
    const response = await apiClient.post<Item>('/items', data);
    return response.data;
  },

  updateItem: async (id: string, data: UpdateItemRequest): Promise<Item> => {
    const response = await apiClient.put<Item>(`/items/${id}`, data);
    return response.data;
  },

  deleteItem: async (id: string): Promise<void> => {
    await apiClient.delete(`/items/${id}`);
  },

  submitForApproval: async (id: string, comment?: string): Promise<Item> => {
    const response = await apiClient.post<Item>(`/items/${id}/submit`, {
      comment,
    });
    return response.data;
  },

  approveItem: async (id: string, comment?: string): Promise<Item> => {
    const response = await apiClient.post<Item>(`/items/${id}/approve`, {
      comment,
    });
    return response.data;
  },

  rejectItem: async (id: string, comment: string): Promise<Item> => {
    const response = await apiClient.post<Item>(`/items/${id}/reject`, {
      comment,
    });
    return response.data;
  },

  closeItem: async (id: string, comment?: string): Promise<Item> => {
    const response = await apiClient.post<Item>(`/items/${id}/close`, {
      comment,
    });
    return response.data;
  },
};
