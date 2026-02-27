import { useTenantLabel } from '../hooks/useTenantLabel';
import { useKpiSummary, useStatusBreakdown } from '../hooks/useReports';
import { KpiCard } from '../components/KpiCard';
import { Package, CheckCircle, Clock, XCircle } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { useTranslation } from 'react-i18next';

export const DashboardPage: React.FC = () => {
  const { t } = useTranslation();
  const itemLabel = useTenantLabel('item', t('common.item'));

  // Fetch KPI data
  const { data: kpiData, isLoading: kpisLoading } = useKpiSummary();
  const { data: statusBreakdown, isLoading: breakdownLoading } = useStatusBreakdown();

  if (kpisLoading || breakdownLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">{t('common.loading')}</div>
      </div>
    );
  }

  // Prepare chart data
  const chartData = statusBreakdown?.map((item) => ({
    name: item.label,
    count: item.count,
  })) || [];

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">{t('dashboard.title')}</h1>
        <p className="text-gray-600 mt-1">{t('dashboard.subtitle')}</p>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <KpiCard
          title={`${t('dashboard.totalItems')}`}
          value={kpiData?.totalItems || 0}
          icon={Package}
        />
        <KpiCard
          title={t('dashboard.approved')}
          value={kpiData?.approved || 0}
          icon={CheckCircle}
        />
        <KpiCard
          title={t('dashboard.pendingApproval')}
          value={kpiData?.pending || 0}
          icon={Clock}
        />
        <KpiCard
          title={t('dashboard.rejected')}
          value={kpiData?.rejected || 0}
          icon={XCircle}
        />
      </div>

      {/* Status Breakdown Chart */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">{itemLabel} {t('dashboard.statusBreakdown')}</h2>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={chartData}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="name" />
            <YAxis />
            <Tooltip />
            <Legend />
            <Bar dataKey="count" fill="hsl(var(--color-primary))" name={t('dashboard.count')} />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Recent Activity placeholder */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">{t('dashboard.recentActivity')}</h2>
        <p className="text-gray-500">{t('dashboard.activityPlaceholder')}</p>
      </div>
    </div>
  );
};
