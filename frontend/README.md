# OpsCore Frontend

Complete React frontend for the OpsCore white-label operations management platform.

## 🚀 Features

### Core Functionality
- **Multi-tenant White-Label System**: Dynamic theming with tenant-specific primary colors and label overrides
- **JWT Authentication**: Secure login with automatic token refresh
- **Role-Based Access Control**: Four user roles (VIEWER, OPERATOR, MANAGER, ADMIN) with hierarchical permissions
- **Approval Workflow**: Submit, approve, and reject items with full audit trail
- **Real-time Dashboard**: KPI cards and interactive charts using Recharts
- **Complete CRUD Operations**: Items, Users, Tenants management
- **Audit Logging**: Track all system activities
- **Reports & Analytics**: Generate and export reports as CSV/Excel

### Technical Features
- **TypeScript**: Full type safety throughout the application
- **React 18**: Latest React features with hooks
- **TanStack Query v5**: Smart data fetching, caching, and synchronization
- **Zustand**: Lightweight state management with persistence
- **React Router v6**: Type-safe routing with role guards
- **Tailwind CSS**: Utility-first styling with custom theming
- **Axios**: HTTP client with JWT interceptor and refresh token queue
- **Recharts**: Beautiful, composable charts

## 📁 Project Structure

```
frontend/
├── src/
│   ├── api/                    # API client layer
│   │   ├── axios.ts           # Axios instance with interceptors
│   │   ├── auth.api.ts        # Authentication endpoints
│   │   ├── tenant.api.ts      # Tenant management
│   │   ├── users.api.ts       # User management
│   │   ├── items.api.ts       # Items CRUD
│   │   ├── workflow.api.ts    # Approval workflow
│   │   └── reports.api.ts     # Reports and audit logs
│   │
│   ├── store/                  # Zustand state management
│   │   ├── auth.store.ts      # Auth state with persistence
│   │   └── tenant.store.ts    # Tenant state with theming
│   │
│   ├── types/                  # TypeScript type definitions
│   │   ├── auth.types.ts      # Auth and user types
│   │   ├── tenant.types.ts    # Tenant types
│   │   ├── user.types.ts      # User types
│   │   ├── item.types.ts      # Item and workflow types
│   │   └── report.types.ts    # Report types
│   │
│   ├── hooks/                  # Custom React hooks
│   │   ├── useAuth.ts         # Authentication hook
│   │   ├── useTenantLabel.ts  # White-label overrides
│   │   ├── useItems.ts        # Items CRUD hooks
│   │   ├── useUsers.ts        # Users CRUD hooks
│   │   └── useReports.ts      # Reports hooks
│   │
│   ├── router/                 # Routing configuration
│   │   ├── AppRouter.tsx      # Main router setup
│   │   ├── PrivateRoute.tsx   # Auth guard
│   │   └── RoleRoute.tsx      # Role-based guard
│   │
│   ├── layouts/                # Layout components
│   │   ├── AuthLayout.tsx     # Login page layout
│   │   └── DashboardLayout.tsx # Main app layout with sidebar
│   │
│   ├── components/             # Reusable components
│   │   ├── Sidebar.tsx        # Navigation sidebar
│   │   ├── TenantThemeProvider.tsx # Theme injection
│   │   ├── KpiCard.tsx        # Dashboard KPI cards
│   │   ├── StatusBadge.tsx    # Item status badge
│   │   ├── ItemTable.tsx      # Items table
│   │   └── ApprovalActions.tsx # Workflow action buttons
│   │
│   ├── pages/                  # Page components
│   │   ├── LoginPage.tsx      # Login form
│   │   ├── DashboardPage.tsx  # Dashboard with KPIs
│   │   ├── ItemsPage.tsx      # Items list with filters
│   │   ├── ItemDetailPage.tsx # Item details with workflow
│   │   ├── UsersPage.tsx      # User management
│   │   ├── AuditPage.tsx      # Audit logs
│   │   ├── ReportsPage.tsx    # Reports and export
│   │   ├── SettingsPage.tsx   # Tenant settings
│   │   └── UnauthorizedPage.tsx # 403 page
│   │
│   ├── App.tsx                 # Root component
│   ├── main.tsx               # App entry point
│   └── index.css              # Global styles
│
├── package.json
├── tsconfig.json
├── vite.config.ts
└── tailwind.config.ts
```

## 🎨 White-Label Theming

The frontend supports complete white-labeling through tenant configuration:

### Primary Color Theming
Tenant primary colors are dynamically injected as CSS variables:
```typescript
// In tenant.store.ts
document.documentElement.style.setProperty('--color-primary', tenant.primaryColor);
```

Tailwind config references the CSS variable:
```typescript
// In tailwind.config.ts
primary: {
  DEFAULT: 'hsl(var(--color-primary))',
  // ...
}
```

### Label Overrides
Use the `useTenantLabel` hook to get white-label text:
```typescript
const itemLabel = useTenantLabel('item', 'Item');
// Returns tenant override if exists, otherwise 'Item'
```

## 🔐 Authentication Flow

1. **Login**: User submits credentials → API returns JWT access token (15min) + refresh token (7 days)
2. **Token Storage**: Zustand store persists tokens to localStorage
3. **API Requests**: Axios interceptor attaches access token to all requests
4. **Token Refresh**: On 401 error, interceptor automatically refreshes token using refresh token
5. **Request Queue**: Multiple concurrent 401s are queued during token refresh to prevent race conditions

## 🛡️ Role-Based Access Control

Role hierarchy: **VIEWER** < **OPERATOR** < **MANAGER** < **ADMIN**

Higher roles have access to all lower role routes:

```typescript
// Example: Only MANAGER and ADMIN can access Users page
<RoleRoute allowedRoles={['MANAGER', 'ADMIN']}>
  <UsersPage />
</RoleRoute>
```

### Role Permissions
- **VIEWER**: Read-only access to items and dashboard
- **OPERATOR**: Can create items and submit for approval
- **MANAGER**: Can approve/reject items, view reports, manage users
- **ADMIN**: Full system access including tenant settings

## 🔄 Workflow System

Items follow this state machine:

```
DRAFT → PENDING_APPROVAL → APPROVED
                ↓
            REJECTED → DRAFT
```

### Workflow Actions
- **Submit for Approval** (OPERATOR+): Move item from DRAFT to PENDING_APPROVAL
- **Approve** (MANAGER+): Move item from PENDING_APPROVAL to APPROVED
- **Reject** (MANAGER+): Move item from PENDING_APPROVAL to REJECTED (requires comments)

## 📊 Dashboard

- **KPI Cards**: Total items, approved, pending, rejected
- **Status Breakdown Chart**: Bar chart showing item distribution by status
- **Recent Activity**: Activity timeline (placeholder)

## 🚀 Getting Started

### Prerequisites
- Node.js 18+ and npm
- Backend server running on http://localhost:8080

### Installation
```bash
cd frontend
npm install
```

### Development
```bash
npm run dev
```
Open http://localhost:5173

### Production Build
```bash
npm run build
npm run preview  # Preview production build
```

### Demo Credentials
- **Username**: `admin`
- **Password**: `password123`

## 🔧 Configuration

### Vite Proxy
The Vite dev server proxies API requests to the backend:
```typescript
// vite.config.ts
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
}
```

### TanStack Query Configuration
```typescript
// App.tsx
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});
```

## 📝 Code Examples

### Using Data Fetching Hooks
```typescript
// Fetch items with filters
const { data, isLoading, error } = useItems({
  search: 'example',
  status: 'APPROVED',
});

// Create item mutation
const { mutate: createItem } = useCreateItem();
createItem({ name: 'New Item', description: 'Description' });
```

### Role-Based UI Rendering
```typescript
const { user } = useAuthStore();

// Show button only for MANAGER and above
{hasPermission(user.role, ['MANAGER']) && (
  <button>Approve</button>
)}
```

### White-Label Labels
```typescript
const itemLabel = useTenantLabel('item', 'Item');
// If tenant has override: labelOverrides: { item: 'Request' }
// Returns 'Request', otherwise 'Item'
```

## 🐛 Common Issues

### Module not found errors
Run `npm install` to install all dependencies.

### API connection refused
Ensure backend is running on http://localhost:8080

### Login fails
Check that demo user exists in database (created by Flyway migration V2)

### Styles not applying
Run `npm run dev` to ensure Vite is compiling Tailwind correctly

## 📚 Dependencies

### Core
- `react` ^18.3.1
- `react-dom` ^18.3.1
- `react-router-dom` ^6.21.0
- `typescript` ^5.3.3

### State & Data
- `@tanstack/react-query` ^5.17.9
- `zustand` ^4.5.0
- `axios` ^1.6.5

### UI & Styling
- `tailwindcss` ^3.4.0
- `lucide-react` ^0.344.0
- `recharts` ^2.10.4

### Forms
- `react-hook-form` ^7.49.3
- `date-fns` ^3.3.1

## 🏗️ Build Configuration

### TypeScript
- `target`: ES2020
- `module`: ESNext
- `strict`: true
- Path aliases: `@/*` points to `src/*`

### Vite
- Port: 5173
- HMR enabled
- API proxy to backend

### Tailwind
- Custom primary color from CSS variable
- shadcn/ui compatible configuration

## 📄 License

Part of the OpsCore platform - see main project README for details.
