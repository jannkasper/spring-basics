// API base URL
const API_URL = 'http://localhost:8040/api/auth';

// Types
export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
}

export interface SignupResponse {
  message: string;
}

export interface ApiError {
  message: string;
  status?: number;
}

// Auth service functions
const authService = {
  // Login user and get JWT token
  async login(credentials: LoginRequest): Promise<LoginResponse> {
    try {
      const response = await fetch(`${API_URL}/signin`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw {
          message: errorData.message || 'Login failed. Please check your credentials.',
          status: response.status,
        };
      }

      return await response.json();
    } catch (error) {
      if (error instanceof Error) {
        throw { message: error.message };
      }
      throw error;
    }
  },

  // Register a new user
  async signup(userData: SignupRequest): Promise<SignupResponse> {
    try {
      const response = await fetch(`${API_URL}/signup`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw {
          message: errorData.message || 'Registration failed. Please try again.',
          status: response.status,
        };
      }

      return await response.json();
    } catch (error) {
      if (error instanceof Error) {
        throw { message: error.message };
      }
      throw error;
    }
  },

  // Store authentication token
  storeToken(token: string): void {
    localStorage.setItem('auth_token', token);
  },

  // Get stored authentication token
  getToken(): string | null {
    return localStorage.getItem('auth_token');
  },

  // Remove authentication token (logout)
  removeToken(): void {
    localStorage.removeItem('auth_token');
  },

  // Check if user is authenticated
  isAuthenticated(): boolean {
    return !!this.getToken();
  },
};

export default authService;