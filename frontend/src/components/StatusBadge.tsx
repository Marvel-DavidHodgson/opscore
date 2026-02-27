import { ItemStatus } from '../types/item.types';

interface StatusBadgeProps {
  status: ItemStatus;
  className?: string;
}

const statusConfig: Record<
  ItemStatus,
  { label: string; className: string }
> = {
  DRAFT: {
    label: 'Draft',
    className: 'bg-gray-100 text-gray-800',
  },
  ACTIVE: {
    label: 'Active',
    className: 'bg-blue-100 text-blue-800',
  },
  PENDING: {
    label: 'Pending',
    className: 'bg-yellow-100 text-yellow-800',
  },
  APPROVED: {
    label: 'Approved',
    className: 'bg-green-100 text-green-800',
  },
  REJECTED: {
    label: 'Rejected',
    className: 'bg-red-100 text-red-800',
  },
  CLOSED: {
    label: 'Closed',
    className: 'bg-gray-100 text-gray-600',
  },
};

/**
 * StatusBadge - Display item status with color coding
 */
export const StatusBadge: React.FC<StatusBadgeProps> = ({ status, className = '' }) => {
  const config = statusConfig[status];

  return (
    <span
      className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${config.className} ${className}`}
    >
      {config.label}
    </span>
  );
};
