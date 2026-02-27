import apiClient from './axios';
import { ApprovalEvent } from '../types/item.types';

// Get approval history for an item
export const getApprovalHistory = async (itemId: string): Promise<ApprovalEvent[]> => {
  const response = await apiClient.get<ApprovalEvent[]>(`/workflow/history/${itemId}`);
  return response.data;
};

// Submit item for approval
export const submitForApproval = async (itemId: string): Promise<void> => {
  await apiClient.post(`/workflow/submit/${itemId}`);
};

// Approve item
export const approveItem = async (
  itemId: string,
  data: { comments?: string }
): Promise<void> => {
  await apiClient.post(`/workflow/approve/${itemId}`, data);
};

// Reject item
export const rejectItem = async (
  itemId: string,
  data: { comments: string }
): Promise<void> => {
  await apiClient.post(`/workflow/reject/${itemId}`, data);
};
