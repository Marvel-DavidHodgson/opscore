import { useState } from 'react';
import { useItems, useDeleteItem, useCreateItem } from '../hooks/useItems';
import { useTenantLabel } from '../hooks/useTenantLabel';
import { ItemTable } from '../components/ItemTable';
import { ItemStatus } from '../types/item.types';
import { Plus, Search } from 'lucide-react';

export const ItemsPage: React.FC = () => {
  const itemLabel = useTenantLabel('item', 'Item');
  const itemsLabel = useTenantLabel('items', 'Items');

  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState<ItemStatus | ''>('');
  const [showCreateModal, setShowCreateModal] = useState(false);

  // Fetch items with filters
  const { data: itemsData, isLoading } = useItems({
    search: search || undefined,
    status: statusFilter || undefined,
  });

  const { mutate: deleteItem, isPending: isDeleting } = useDeleteItem();
  const { mutate: createItem, isPending: isCreating } = useCreateItem();

  const handleDelete = (id: string) => {
    if (confirm(`Are you sure you want to delete this ${itemLabel.toLowerCase()}?`)) {
      deleteItem(id);
    }
  };

  const handleCreateItem = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    
    createItem(
      {
        title: formData.get('name') as string,
        description: formData.get('description') as string,
      },
      {
        onSuccess: () => {
          setShowCreateModal(false);
          e.currentTarget.reset();
        },
      }
    );
  };

  const items = itemsData?.content || [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{itemsLabel}</h1>
          <p className="text-gray-600 mt-1">Manage your {itemsLabel.toLowerCase()}</p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-md hover:bg-primary/90 transition-colors"
        >
          <Plus className="w-4 h-4" />
          Create {itemLabel}
        </button>
      </div>

      {/* Filters */}
      <div className="bg-white rounded-lg border border-gray-200 p-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Search */}
          <div>
            <label htmlFor="search" className="block text-sm font-medium text-gray-700 mb-2">
              Search
            </label>
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="h-5 w-5 text-gray-400" />
              </div>
              <input
                id="search"
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search by name or code..."
                className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
              />
            </div>
          </div>

          {/* Status filter */}
          <div>
            <label htmlFor="status" className="block text-sm font-medium text-gray-700 mb-2">
              Status
            </label>
            <select
              id="status"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value as ItemStatus | '')}
              className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
            >
              <option value="">All Statuses</option>
              <option value="DRAFT">Draft</option>
              <option value="ACTIVE">Active</option>
              <option value="PENDING">Pending</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
              <option value="CLOSED">Closed</option>
            </select>
          </div>
        </div>
      </div>

      {/* Items table */}
      {isLoading ? (
        <div className="text-center py-12">
          <p className="text-gray-500">Loading {itemsLabel.toLowerCase()}...</p>
        </div>
      ) : (
        <ItemTable items={items} onDelete={handleDelete} isDeleting={isDeleting} />
      )}

      {/* Create modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-xl font-semibold mb-4">Create {itemLabel}</h2>
            <form onSubmit={handleCreateItem} className="space-y-4">
              <div>
                <label htmlFor="name" className="block text-sm font-medium text-gray-700 mb-2">
                  Name *
                </label>
                <input
                  id="name"
                  name="name"
                  type="text"
                  required
                  className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </div>

              <div>
                <label htmlFor="description" className="block text-sm font-medium text-gray-700 mb-2">
                  Description
                </label>
                <textarea
                  id="description"
                  name="description"
                  rows={4}
                  className="block w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-2 focus:ring-primary focus:border-transparent"
                />
              </div>

              <div className="flex gap-2">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isCreating}
                  className="flex-1 px-4 py-2 bg-primary text-white rounded-md hover:bg-primary/90 disabled:opacity-50 transition-colors"
                >
                  {isCreating ? 'Creating...' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
