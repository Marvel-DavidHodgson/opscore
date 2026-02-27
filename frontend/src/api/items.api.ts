import apiClient from './axios';
import {
  Item,
  ItemStatus,
  CreateItemRequest,
  UpdateItemRequest,
  PageResponse,
} from '../types/item.types';

// Get all items (with optional filters)
export const getItems = async (params?: {
  search?: string;
  status?: ItemStatus;
  page?: number;
  size?: number;
}): Promise<PageResponse<Item>> => {
  const response = await apiClient.get<PageResponse<Item>>('/items', { params });
  return response.data;
};

// Get item by ID
export const getItemById = async (id: string): Promise<Item> => {
  const response = await apiClient.get<Item>(`/items/${id}`);
  return response.data;
};

// Create item
export const createItem = async (data: CreateItemRequest): Promise<Item> => {
  const response = await apiClient.post<Item>('/items', data);
  return response.data;
};

// Update item
export const updateItem = async (id: string, data: UpdateItemRequest): Promise<Item> => {
  const response = await apiClient.put<Item>(`/items/${id}`, data);
  return response.data;
};

// Delete item
export const deleteItem = async (id: string): Promise<void> => {
  await apiClient.delete(`/items/${id}`);
};

// Get items by status
export const getItemsByStatus = async (status: ItemStatus): Promise<Item[]> => {
  const response = await apiClient.get<Item[]>(`/items/status/${status}`);
  return response.data;
};

// Search items
export const searchItems = async (keyword: string): Promise<Item[]> => {
  const response = await apiClient.get<Item[]>('/items/search', {
    params: { keyword },
  });
  return response.data;
};
