import { Outlet } from 'react-router-dom';
import { Sidebar } from '../components/Sidebar';

interface DashboardLayoutProps {
  children?: React.ReactNode;
}

/**
 * DashboardLayout - Main layout for authenticated pages with sidebar
 */
export const DashboardLayout: React.FC<DashboardLayoutProps> = ({ children }) => {
  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar />
      <main className="flex-1 overflow-y-auto">
        <div className="container mx-auto p-6 max-w-7xl">
          {children || <Outlet />}
        </div>
      </main>
    </div>
  );
};
