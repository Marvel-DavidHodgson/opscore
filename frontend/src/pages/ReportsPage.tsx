import { useState } from 'react';
import { useKpiSummary, useStatusBreakdown, useExportReport } from '../hooks/useReports';
import { Download, FileText } from 'lucide-react';

export const ReportsPage: React.FC = () => {
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  const filters = {
    startDate: startDate || undefined,
    endDate: endDate || undefined,
  };

  const { data: kpiData, isLoading: kpisLoading } = useKpiSummary(filters);
  const { data: statusBreakdown = [], isLoading: breakdownLoading } = useStatusBreakdown(filters);
  const { mutate: exportReport, isPending: isExporting } = useExportReport();

  const handleExport = (format: 'csv' | 'excel') => {
    exportReport({ format, filters });
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Reports</h1>
        <p className="text-gray-600 mt-1">Generate and export reports</p>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label htmlFor="startDate" className="block text-sm font-medium text-gray-700 mb-2">
              Start Date
            </label>
            <input
              id="startDate"
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
            />
          </div>

          <div>
            <label htmlFor="endDate" className="block text-sm font-medium text-gray-700 mb-2">
              End Date
            </label>
            <input
              id="endDate"
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
            />
          </div>
        </div>
      </div>

      {/* Export buttons */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">Export Reports</h2>
        <div className="flex gap-4">
          <button
            onClick={() => handleExport('csv')}
            disabled={isExporting}
            className="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-md hover:bg-primary/90 disabled:opacity-50 transition-colors"
          >
            <Download className="w-4 h-4" />
            Export as CSV
          </button>
          <button
            onClick={() => handleExport('excel')}
            disabled={isExporting}
            className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50 transition-colors"
          >
            <FileText className="w-4 h-4" />
            Export as Excel
          </button>
        </div>
      </div>

      {/* KPI Summary */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">KPI Summary</h2>
        {kpisLoading ? (
          <p className="text-gray-500">Loading...</p>
        ) : (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="text-center p-4 bg-gray-50 rounded-lg">
              <p className="text-sm text-gray-600">Total Items</p>
              <p className="text-2xl font-bold text-gray-900 mt-1">{kpiData?.totalItems || 0}</p>
            </div>
            <div className="text-center p-4 bg-green-50 rounded-lg">
              <p className="text-sm text-gray-600">Approved</p>
              <p className="text-2xl font-bold text-green-600 mt-1">{kpiData?.approved || 0}</p>
            </div>
            <div className="text-center p-4 bg-yellow-50 rounded-lg">
              <p className="text-sm text-gray-600">Pending</p>
              <p className="text-2xl font-bold text-yellow-600 mt-1">{kpiData?.pending || 0}</p>
            </div>
            <div className="text-center p-4 bg-red-50 rounded-lg">
              <p className="text-sm text-gray-600">Rejected</p>
              <p className="text-2xl font-bold text-red-600 mt-1">{kpiData?.rejected || 0}</p>
            </div>
          </div>
        )}
      </div>

      {/* Status Breakdown */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">Status Breakdown</h2>
        {breakdownLoading ? (
          <p className="text-gray-500">Loading...</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">
                    Status
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                    Count
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                    Percentage
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {statusBreakdown.map((item) => {
                  const total = statusBreakdown.reduce((sum, i) => sum + (i.count || 0), 0);
                  const percent = total > 0 ? ((item.count || 0) / total) * 100 : 0;
                  return (
                    <tr key={item.status}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {item.label}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">
                        {item.count || 0}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">
                        {percent.toFixed(1)}%
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};
