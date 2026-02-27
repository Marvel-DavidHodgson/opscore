interface AuthLayoutProps {
  children: React.ReactNode;
}

/**
 * AuthLayout - Layout for authentication pages (login, register, etc.)
 */
export const AuthLayout: React.FC<AuthLayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        {children}
      </div>
    </div>
  );
};
