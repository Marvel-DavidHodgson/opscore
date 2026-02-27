import { Navigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/auth-store';
import { Role } from '@/types';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: Role[];
}

export default function ProtectedRoute({
  children,
  requiredRoles,
}: ProtectedRouteProps) {
  const { isAuthenticated, role } = useAuthStore();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRoles && role && !requiredRoles.includes(role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return <>{children}</>;
}
