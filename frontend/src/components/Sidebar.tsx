import { Link, useLocation } from 'react-router-dom';
import { useAuthStore } from '../store/auth.store';
import { useTenantStore } from '../store/tenant.store';
import { hasPermission } from '../router/RoleRoute';
import { useTranslation } from 'react-i18next';
import { LanguageSwitcher } from './LanguageSwitcher';
import {
  LayoutDashboard,
  Package,
  Users,
  FileText,
  BarChart3,
  Settings,
  LogOut,
} from 'lucide-react';

interface NavItem {
  path: string;
  label: string;
  icon: React.ReactNode;
  roles?: Array<'VIEWER' | 'OPERATOR' | 'MANAGER' | 'ADMIN'>;
}

const navItems: NavItem[] = [
  {
    path: '/dashboard',
    label: 'Dashboard',
    icon: <LayoutDashboard className="w-5 h-5" />,
  },
  {
    path: '/items',
    label: 'Items',
    icon: <Package className="w-5 h-5" />,
  },
  {
    path: '/users',
    label: 'Users',
    icon: <Users className="w-5 h-5" />,
    roles: ['MANAGER', 'ADMIN'],
  },
  {
    path: '/audit',
    label: 'Audit Logs',
    icon: <FileText className="w-5 h-5" />,
    roles: ['MANAGER', 'ADMIN'],
  },
  {
    path: '/reports',
    label: 'Reports',
    icon: <BarChart3 className="w-5 h-5" />,
    roles: ['MANAGER', 'ADMIN'],
  },
  {
    path: '/settings',
    label: 'Settings',
    icon: <Settings className="w-5 h-5" />,
    roles: ['ADMIN'],
  },
];

export const Sidebar: React.FC = () => {
  const location = useLocation();
  const { user, logout } = useAuthStore();
  const { tenant } = useTenantStore();
  const { t } = useTranslation();

  const navItems: NavItem[] = [
    {
      path: '/dashboard',
      label: t('nav.dashboard'),
      icon: <LayoutDashboard className="w-5 h-5" />,
    },
    {
      path: '/items',
      label: t('nav.items'),
      icon: <Package className="w-5 h-5" />,
    },
    {
      path: '/users',
      label: t('nav.users'),
      icon: <Users className="w-5 h-5" />,
      roles: ['MANAGER', 'ADMIN'],
    },
    {
      path: '/audit',
      label: t('nav.audit'),
      icon: <FileText className="w-5 h-5" />,
      roles: ['MANAGER', 'ADMIN'],
    },
    {
      path: '/reports',
      label: t('nav.reports'),
      icon: <BarChart3 className="w-5 h-5" />,
      roles: ['MANAGER', 'ADMIN'],
    },
    {
      path: '/settings',
      label: t('nav.settings'),
      icon: <Settings className="w-5 h-5" />,
      roles: ['ADMIN'],
    },
  ];

  // Filter nav items based on user role
  const visibleNavItems = navItems.filter((item) => {
    if (!item.roles || !user) return true;
    return hasPermission(user.role, item.roles);
  });

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="w-64 bg-white border-r border-gray-200 flex flex-col h-screen">
      {/* Logo / Tenant Name */}
      <div className="h-16 flex items-center px-6 border-b border-gray-200">
        <h1 className="text-xl font-bold text-primary">
          {tenant?.name || 'OpsCore'}
        </h1>
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto py-4">
        <ul className="space-y-1 px-3">
          {visibleNavItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <li key={item.path}>
                <Link
                  to={item.path}
                  className={`flex items-center gap-3 px-3 py-2 rounded-md transition-colors ${
                    isActive
                      ? 'bg-primary text-white'
                      : 'text-gray-700 hover:bg-gray-100'
                  }`}
                >
                  {item.icon}
                  <span className="font-medium">{item.label}</span>
                </Link>
              </li>
            );
          })}
        </ul>
      </nav>

      {/* User info and logout */}
      <div className="border-t border-gray-200 p-4 space-y-3">
        <div className="flex items-center gap-2">
          <LanguageSwitcher />
        </div>
        <div className="flex items-center justify-between">
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-900 truncate">
              {user?.username || user?.email}
            </p>
            <p className="text-xs text-gray-500">{user?.role}</p>
          </div>
          <button
            onClick={handleLogout}
            className="ml-2 p-2 text-gray-400 hover:text-gray-600 transition-colors"
            title={t('common.logout')}
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
};
