import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../store/auth.store';

interface PrivateRouteProps {
  children: React.ReactNode;
}

/**
 * PrivateRoute - Protects routes that require authentication
 */
export const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
  const { accessToken } = useAuthStore();

  if (!accessToken) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};
