import { Role } from './auth.types';

export interface User {
  id: string;
  tenantId: string;
  name: string;
  email: string;
  role: Role;
  isActive: boolean;
  lastLoginAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  name: string;
  email: string;
  password: string;
  role: Role;
}

export interface UpdateUserRequest {
  name?: string;
  email?: string;
  role?: Role;
  isActive?: boolean;
}
