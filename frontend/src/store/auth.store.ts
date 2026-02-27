import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { AuthUser, LoginResponse } from '../types/auth.types';

interface AuthState {
  isAuthenticated: boolean;
  user: AuthUser | null;
  accessToken: string | null;
  refreshToken: string | null;
  login: (data: LoginResponse) => void;
  updateTokens: (accessToken: string, refreshToken: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      isAuthenticated: false,
      user: null,
      accessToken: null,
      refreshToken: null,

      login: (data) => {
        set({
          isAuthenticated: true,
          user: data.user,
          accessToken: data.accessToken,
          refreshToken: data.refreshToken,
        });
      },

      updateTokens: (accessToken, refreshToken) => {
        set({
          accessToken,
          refreshToken,
        });
      },

      logout: () => {
        set({
          isAuthenticated: false,
          user: null,
          accessToken: null,
          refreshToken: null,
        });
      },
    }),
    {
      name: 'auth-storage',
    }
  )
);
