import { create } from 'zustand'
import authService from '@/services/authService'

export interface User {
  email: string;
  name?: string;
}

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  
  // Actions
  login: (email: string, password: string) => Promise<void>;
  signup: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  clearError: () => void;
  checkAuth: () => void;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,

  // Initialize the store with token check
  checkAuth: () => {
    const isAuthenticated = authService.isAuthenticated();
    
    if (isAuthenticated) {
      // Get user info from token or storage if available
      // In a real app, you might want to decode the JWT or make an API call to get user info
      // For simplicity, we just set isAuthenticated to true
      set({ isAuthenticated: true });
    }
  },

  // Login action
  login: async (email: string, password: string) => {
    set({ isLoading: true, error: null });
    
    try {
      const response = await authService.login({ username: email, password });
      
      // Store the token
      authService.storeToken(response.accessToken);
      
      // Update the store
      set({ 
        isAuthenticated: true,
        user: { email },
        isLoading: false
      });

    } catch (error) {
      // Handle login error
      const apiError = error as { message: string };
      set({ 
        error: apiError.message,
        isLoading: false,
        isAuthenticated: false,
        user: null 
      });
      throw error;
    }
  },

  // Signup action
  signup: async (name: string, email: string, password: string) => {
    set({ isLoading: true, error: null });
    
    try {
      await authService.signup({ name, email, password });
      
      // Just return success, don't log in automatically
      set({ isLoading: false });

    } catch (error) {
      // Handle signup error
      const apiError = error as { message: string };
      set({ 
        error: apiError.message,
        isLoading: false 
      });
      throw error;
    }
  },

  // Logout action
  logout: () => {
    // Remove the token
    authService.removeToken();
    
    // Update the store
    set({ 
      isAuthenticated: false,
      user: null
    });
  },

  // Clear error message
  clearError: () => {
    set({ error: null });
  }
}));