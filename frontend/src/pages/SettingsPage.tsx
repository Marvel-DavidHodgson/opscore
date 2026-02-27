import { useTenantStore } from '../store/tenant.store';
import { useAuthStore } from '../store/auth.store';

export const SettingsPage: React.FC = () => {
  const { tenant } = useTenantStore();
  const { user } = useAuthStore();

  if (!tenant) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">Loading tenant settings...</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Settings</h1>
        <p className="text-gray-600 mt-1">Manage tenant and system settings</p>
      </div>

      {/* Tenant Information */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">Tenant Information</h2>
        <dl className="space-y-4">
          <div>
            <dt className="text-sm font-medium text-gray-500">Tenant Name</dt>
            <dd className="mt-1 text-sm text-gray-900">{tenant.name}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">Industry Type</dt>
            <dd className="mt-1 text-sm text-gray-900">{tenant.industryType}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">Primary Color</dt>
            <dd className="mt-1 flex items-center gap-2">
              <div
                className="w-6 h-6 rounded border border-gray-300"
                style={{ backgroundColor: tenant.primaryColor }}
              />
              <span className="text-sm text-gray-900">{tenant.primaryColor}</span>
            </dd>
          </div>
        </dl>
      </div>

      {/* Module Configuration */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">Enabled Modules</h2>
        <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
          {Object.entries(tenant.moduleConfig || {}).map(([module, enabled]) => (
            <div
              key={module}
              className={`p-4 rounded-lg border ${
                enabled
                  ? 'bg-green-50 border-green-200'
                  : 'bg-gray-50 border-gray-200'
              }`}
            >
              <p className="text-sm font-medium text-gray-900">{module}</p>
              <p className="text-xs text-gray-600 mt-1">
                {enabled ? 'Enabled' : 'Disabled'}
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Label Overrides */}
      {tenant.labelOverrides && Object.keys(tenant.labelOverrides).length > 0 && (
        <div className="bg-white rounded-lg border border-gray-200 p-6">
          <h2 className="text-xl font-semibold mb-4">Custom Labels</h2>
          <dl className="space-y-2">
            {Object.entries(tenant.labelOverrides).map(([key, value]) => (
              <div key={key} className="flex items-center justify-between py-2 border-b border-gray-100">
                <dt className="text-sm font-medium text-gray-500">{key}</dt>
                <dd className="text-sm text-gray-900">{value}</dd>
              </div>
            ))}
          </dl>
        </div>
      )}

      {/* User Information */}
      <div className="bg-white rounded-lg border border-gray-200 p-6">
        <h2 className="text-xl font-semibold mb-4">Current User</h2>
        <dl className="space-y-4">
          <div>
            <dt className="text-sm font-medium text-gray-500">Username</dt>
            <dd className="mt-1 text-sm text-gray-900">{user?.username}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">Email</dt>
            <dd className="mt-1 text-sm text-gray-900">{user?.email}</dd>
          </div>
          <div>
            <dt className="text-sm font-medium text-gray-500">Role</dt>
            <dd className="mt-1">
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                {user?.role}
              </span>
            </dd>
          </div>
        </dl>
      </div>
    </div>
  );
};
