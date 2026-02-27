import { useState } from 'react';
import { Item } from '../types/item.types';
import { useAuthStore } from '../store/auth.store';
import { Check, X, Send } from 'lucide-react';

interface ApprovalActionsProps {
  item: Item;
  onSubmit: () => void;
  onApprove: (comments?: string) => void;
  onReject: (comments: string) => void;
  isLoading?: boolean;
}

/**
 * ApprovalActions - Workflow action buttons based on item status and user role
 */
export const ApprovalActions: React.FC<ApprovalActionsProps> = ({
  item,
  onSubmit,
  onApprove,
  onReject,
  isLoading = false,
}) => {
  const { user } = useAuthStore();
  const [showCommentInput, setShowCommentInput] = useState(false);
  const [comments, setComments] = useState('');

  if (!user) return null;

  // OPERATOR or higher can submit drafts
  const canSubmit =
    item.status === 'DRAFT' &&
    ['OPERATOR', 'MANAGER', 'ADMIN'].includes(user.role);

  // MANAGER or higher can approve/reject
  const canApproveReject =
    item.status === 'PENDING' &&
    ['MANAGER', 'ADMIN'].includes(user.role);

  const handleApprove = () => {
    onApprove(comments || undefined);
    setComments('');
    setShowCommentInput(false);
  };

  const handleReject = () => {
    if (!comments.trim()) {
      alert('Comments are required when rejecting an item');
      return;
    }
    onReject(comments);
    setComments('');
    setShowCommentInput(false);
  };

  if (!canSubmit && !canApproveReject) {
    return null;
  }

  return (
    <div className="bg-white rounded-lg border border-gray-200 p-6">
      <h3 className="text-lg font-semibold mb-4">Workflow Actions</h3>

      <div className="space-y-4">
        {/* Submit for approval */}
        {canSubmit && (
          <button
            onClick={onSubmit}
            disabled={isLoading}
            className="w-full flex items-center justify-center gap-2 px-4 py-2 bg-primary text-white rounded-md hover:bg-primary/90 disabled:opacity-50 transition-colors"
          >
            <Send className="w-4 h-4" />
            Submit for Approval
          </button>
        )}

        {/* Approve/Reject */}
        {canApproveReject && (
          <>
            <div className="flex gap-2">
              <button
                onClick={handleApprove}
                disabled={isLoading}
                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 disabled:opacity-50 transition-colors"
              >
                <Check className="w-4 h-4" />
                Approve
              </button>
              <button
                onClick={() => setShowCommentInput(!showCommentInput)}
                disabled={isLoading}
                className="flex-1 flex items-center justify-center gap-2 px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50 transition-colors"
              >
                <X className="w-4 h-4" />
                Reject
              </button>
            </div>

            {showCommentInput && (
              <div className="space-y-2">
                <textarea
                  value={comments}
                  onChange={(e) => setComments(e.target.value)}
                  placeholder="Add comments (required for rejection)"
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                  rows={3}
                />
                <button
                  onClick={handleReject}
                  disabled={isLoading || !comments.trim()}
                  className="w-full px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 disabled:opacity-50 transition-colors"
                >
                  Confirm Rejection
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
};
