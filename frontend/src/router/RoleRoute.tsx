import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/auth.store';
import { Role } from '../types/auth.types';

interface RoleRouteProps {
  children: React.ReactNode;
  allowedRoles: Role[];
}

// Role hierarchy for permission checks
const roleHierarchy: Record<Role, number> = {
  VIEWER: 1,
  OPERATOR: 2,
  MANAGER: 3,
  ADMIN: 4,
};

/**
 * RoleRoute - Protects routes based on user roles
 * Higher roles have access to lower role routes (e.g., ADMIN can access OPERATOR routes)
 */
export const RoleRoute: React.FC<RoleRouteProps> = ({ children, allowedRoles }) => {
  const { user } = useAuthStore();

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  const userRoleLevel = roleHierarchy[user.role];
  const minRequiredLevel = Math.min(...allowedRoles.map((role) => roleHierarchy[role]));

  // Check if user's role level meets or exceeds the minimum required level
  if (userRoleLevel < minRequiredLevel) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <>{children}</>;
};

/**
 * Helper function to check if user has permission
 */
export const hasPermission = (userRole: Role, allowedRoles: Role[]): boolean => {
  const userRoleLevel = roleHierarchy[userRole];
  const minRequiredLevel = Math.min(...allowedRoles.map((role) => roleHierarchy[role]));
  return userRoleLevel >= minRequiredLevel;
};
