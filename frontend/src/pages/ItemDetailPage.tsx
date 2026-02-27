import { useParams, useNavigate } from 'react-router-dom';
import { useItem, useApprovalHistory, useSubmitForApproval, useApproveItem, useRejectItem } from '../hooks/useItems';
import { useTenantLabel } from '../hooks/useTenantLabel';
import { StatusBadge } from '../components/StatusBadge';
import { ApprovalActions } from '../components/ApprovalActions';
import { format } from 'date-fns';
import { ArrowLeft } from 'lucide-react';

export const ItemDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const itemLabel = useTenantLabel('item', 'Item');

  const itemId = id || '';

  // Fetch item and approval history
  const { data: item, isLoading: itemLoading } = useItem(itemId);
  const { data: approvalHistory = [], isLoading: historyLoading } = useApprovalHistory(itemId);

  // Mutations
  const { mutate: submitForApproval, isPending: isSubmitting } = useSubmitForApproval();
  const { mutate: approveItem, isPending: isApproving } = useApproveItem();
  const { mutate: rejectItem, isPending: isRejecting } = useRejectItem();

  const isLoading = itemLoading || historyLoading;
  const isMutating = isSubmitting || isApproving || isRejecting;

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-gray-500">Loading {itemLabel.toLowerCase()}...</div>
      </div>
    );
  }

  if (!item) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">{itemLabel} not found</p>
      </div>
    );
  }

  const handleSubmit = () => {
    submitForApproval(itemId);
  };

  const handleApprove = (comments?: string) => {
    approveItem({ itemId, comments });
  };

  const handleReject = (comments: string) => {
    rejectItem({ itemId, comments });
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <button
          onClick={() => navigate('/items')}
          className="flex items-center gap-2 text-gray-600 hover:text-gray-900 mb-4"
        >
          <ArrowLeft className="w-4 h-4" />
          Back to {itemLabel}s
        </button>
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">{item.title}</h1>
            <p className="text-gray-600 mt-1">{item.code}</p>
          </div>
          <StatusBadge status={item.status} />
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Item details */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white rounded-lg border border-gray-200 p-6">
            <h2 className="text-xl font-semibold mb-4">Details</h2>
            <dl className="space-y-4">
              <div>
                <dt className="text-sm font-medium text-gray-500">Description</dt>
                <dd className="mt-1 text-sm text-gray-900">{item.description || 'No description'}</dd>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <dt className="text-sm font-medium text-gray-500">Created At</dt>
                  <dd className="mt-1 text-sm text-gray-900">
                    {format(new Date(item.createdAt), 'MMM d, yyyy HH:mm')}
                  </dd>
                </div>
                <div>
                  <dt className="text-sm font-medium text-gray-500">Updated At</dt>
                  <dd className="mt-1 text-sm text-gray-900">
                    {format(new Date(item.updatedAt), 'MMM d, yyyy HH:mm')}
                  </dd>
                </div>
              </div>
            </dl>
          </div>

          {/* Approval History */}
          <div className="bg-white rounded-lg border border-gray-200 p-6">
            <h2 className="text-xl font-semibold mb-4">Approval History</h2>
            {approvalHistory.length === 0 ? (
              <p className="text-gray-500">No approval history yet</p>
            ) : (
              <div className="space-y-4">
                {approvalHistory.map((event) => (
                  <div key={event.id} className="border-l-4 border-primary pl-4">
                    <div className="flex items-center justify-between">
                      <p className="font-medium text-gray-900">
                        {event.fromStatus} → {event.toStatus}
                      </p>
                      <p className="text-sm text-gray-500">
                        {format(new Date(event.createdAt), 'MMM d, yyyy HH:mm')}
                      </p>
                    </div>
                    <p className="text-sm text-gray-600 mt-1">By: {event.actorUserId}</p>
                    {event.comment && (
                      <p className="text-sm text-gray-700 mt-2 italic">"{event.comment}"</p>
                    )}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Approval actions */}
        <div className="lg:col-span-1">
          <ApprovalActions
            item={item}
            onSubmit={handleSubmit}
            onApprove={handleApprove}
            onReject={handleReject}
            isLoading={isMutating}
          />
        </div>
      </div>
    </div>
  );
};
