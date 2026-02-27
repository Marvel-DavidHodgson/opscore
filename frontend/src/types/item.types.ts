export type ItemStatus = 'DRAFT' | 'ACTIVE' | 'PENDING' | 'APPROVED' | 'REJECTED' | 'CLOSED';

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

export interface WorkflowActionRequest {
  comment?: string;
}

export interface ApprovalEvent {
  id: string;
  itemId: string;
  actorUserId: string;
  fromStatus: ItemStatus;
  toStatus: ItemStatus;
  comment: string | null;
  createdAt: string;
}

export interface AuditLog {
  id: string;
  entityType: string;
  entityId: string;
  action: string;
  username: string;
  timestamp: string;
  details?: string;
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
  first: boolean;
  empty: boolean;
}
