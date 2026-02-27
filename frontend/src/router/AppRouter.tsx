import { createBrowserRouter, Navigate } from 'react-router-dom';
import { PrivateRoute } from './PrivateRoute';
import { RoleRoute } from './RoleRoute';

// Layouts
import { AuthLayout } from '../layouts/AuthLayout';
import { DashboardLayout } from '../layouts/DashboardLayout';

// Pages
import { LoginPage } from '../pages/LoginPage';
import { DashboardPage } from '../pages/DashboardPage';
import { ItemsPage } from '../pages/ItemsPage';
import { ItemDetailPage } from '../pages/ItemDetailPage';
import { UsersPage } from '../pages/UsersPage';
import { AuditPage } from '../pages/AuditPage';
import { ReportsPage } from '../pages/ReportsPage';
import { SettingsPage } from '../pages/SettingsPage';
import { UnauthorizedPage } from '../pages/UnauthorizedPage';

export const router = createBrowserRouter([
  // Public routes
  {
    path: '/login',
    element: (
      <AuthLayout>
        <LoginPage />
      </AuthLayout>
    ),
  },

  // Protected routes
  {
    path: '/',
    element: (
      <PrivateRoute>
        <DashboardLayout />
      </PrivateRoute>
    ),
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: <DashboardPage />,
      },
      {
        path: 'items',
        element: <ItemsPage />,
      },
      {
        path: 'items/:id',
        element: <ItemDetailPage />,
      },
      {
        path: 'users',
        element: (
          <RoleRoute allowedRoles={['MANAGER', 'ADMIN']}>
            <UsersPage />
          </RoleRoute>
        ),
      },
      {
        path: 'audit',
        element: (
          <RoleRoute allowedRoles={['MANAGER', 'ADMIN']}>
            <AuditPage />
          </RoleRoute>
        ),
      },
      {
        path: 'reports',
        element: (
          <RoleRoute allowedRoles={['MANAGER', 'ADMIN']}>
            <ReportsPage />
          </RoleRoute>
        ),
      },
      {
        path: 'settings',
        element: (
          <RoleRoute allowedRoles={['ADMIN']}>
            <SettingsPage />
          </RoleRoute>
        ),
      },
    ],
  },

  // Unauthorized
  {
    path: '/unauthorized',
    element: (
      <PrivateRoute>
        <DashboardLayout>
          <UnauthorizedPage />
        </DashboardLayout>
      </PrivateRoute>
    ),
  },

  // Catch all - redirect to dashboard
  {
    path: '*',
    element: <Navigate to="/" replace />,
  },
]);
