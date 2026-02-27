import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
  getItems,
  getItemById,
  createItem,
  updateItem,
  deleteItem,
  getItemsByStatus,
  searchItems,
} from '../api/items.api';
import {
  submitForApproval,
  approveItem,
  rejectItem,
  getApprovalHistory,
} from '../api/workflow.api';
import {
  ItemStatus,
  CreateItemRequest,
  UpdateItemRequest,
} from '../types/item.types';

// Query keys
export const itemKeys = {
  all: ['items'] as const,
  lists: () => [...itemKeys.all, 'list'] as const,
  list: (filters?: Record<string, unknown>) => [...itemKeys.lists(), filters] as const,
  details: () => [...itemKeys.all, 'detail'] as const,
  detail: (id: string) => [...itemKeys.details(), id] as const,
  byStatus: (status: ItemStatus) => [...itemKeys.all, 'status', status] as const,
  search: (keyword: string) => [...itemKeys.all, 'search', keyword] as const,
  approvalHistory: (id: string) => [...itemKeys.detail(id), 'approval-history'] as const,
};

// Get all items with filters
export const useItems = (filters?: {
  search?: string;
  status?: ItemStatus;
  page?: number;
  size?: number;
}) => {
  return useQuery({
    queryKey: itemKeys.list(filters),
    queryFn: () => getItems(filters),
  });
};

// Get single item by ID
export const useItem = (id: string) => {
  return useQuery({
    queryKey: itemKeys.detail(id),
    queryFn: () => getItemById(id),
    enabled: !!id,
  });
};

// Get items by status
export const useItemsByStatus = (status: ItemStatus) => {
  return useQuery({
    queryKey: itemKeys.byStatus(status),
    queryFn: () => getItemsByStatus(status),
  });
};

// Search items
export const useSearchItems = (keyword: string) => {
  return useQuery({
    queryKey: itemKeys.search(keyword),
    queryFn: () => searchItems(keyword),
    enabled: keyword.length > 0,
  });
};

// Get approval history
export const useApprovalHistory = (itemId: string) => {
  return useQuery({
    queryKey: itemKeys.approvalHistory(itemId),
    queryFn: () => getApprovalHistory(itemId),
    enabled: !!itemId,
  });
};

// Create item mutation
export const useCreateItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateItemRequest) => createItem(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: itemKeys.lists() });
    },
  });
};

// Update item mutation
export const useUpdateItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateItemRequest }) =>
      updateItem(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: itemKeys.detail(variables.id) });
      queryClient.invalidateQueries({ queryKey: itemKeys.lists() });
    },
  });
};

// Delete item mutation
export const useDeleteItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: string) => deleteItem(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: itemKeys.lists() });
    },
  });
};

// Submit for approval mutation
export const useSubmitForApproval = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (itemId: string) => submitForApproval(itemId),
    onSuccess: (_, itemId) => {
      queryClient.invalidateQueries({ queryKey: itemKeys.detail(itemId) });
      queryClient.invalidateQueries({ queryKey: itemKeys.lists() });
      queryClient.invalidateQueries({ queryKey: itemKeys.approvalHistory(itemId) });
    },
  });
};

// Approve item mutation
export const useApproveItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ itemId, comments }: { itemId: string; comments?: string }) =>
      approveItem(itemId, { comments }),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: itemKeys.detail(variables.itemId) });
      queryClient.invalidateQueries({ queryKey: itemKeys.lists() });
      queryClient.invalidateQueries({ queryKey: itemKeys.approvalHistory(variables.itemId) });
    },
  });
};

// Reject item mutation
export const useRejectItem = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ itemId, comments }: { itemId: string; comments: string }) =>
      rejectItem(itemId, { comments }),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: itemKeys.detail(variables.itemId) });
      queryClient.invalidateQueries({ queryKey: itemKeys.lists() });
      queryClient.invalidateQueries({ queryKey: itemKeys.approvalHistory(variables.itemId) });
    },
  });
};
