import { useState, useEffect } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Eye, EyeOff, AlertCircle } from 'lucide-react'

import { useAuthStore } from '@/store/useAuthStore'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { 
  Form, 
  FormControl, 
  FormField, 
  FormItem, 
  FormLabel, 
  FormMessage 
} from '@/components/ui/form/form'
import { Alert, AlertDescription } from '@/components/ui/alert'

// Define form validation schema
const loginFormSchema = z.object({
  username: z.string().min(4, { message: 'Please enter a valid username address' }),
  password: z.string().min(6, { message: 'Password must be at least 6 characters' }),
})

type LoginFormValues = z.infer<typeof loginFormSchema>

const LoginPage = () => {
  const [showPassword, setShowPassword] = useState(false)
  const [loginError, setLoginError] = useState<string | null>(null)
  const navigate = useNavigate()
  const location = useLocation()
  
  // Get auth state and functions from our store
  const { login, isLoading, isAuthenticated, error, clearError } = useAuthStore()

  // Redirect if already authenticated
  useEffect(() => {
    if (isAuthenticated) {
      // If user was trying to access a specific page, redirect there
      const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/'
      navigate(from, { replace: true })
    }
  }, [isAuthenticated, navigate, location])

  // Set error from store to local state
  useEffect(() => {
    if (error) {
      setLoginError(error)
      clearError()
    }
  }, [error, clearError])

  // Initialize the form
  const form = useForm<LoginFormValues>({
    resolver: zodResolver(loginFormSchema),
    defaultValues: {
      username: '',
      password: '',
    },
  })

  const togglePasswordVisibility = () => {
    setShowPassword(!showPassword)
  }

  const onSubmit = async (data: LoginFormValues) => {
    setLoginError(null)
    
    try {
      // Call the login function from our auth store
      await login(data.username, data.password)
      
      // The redirect will happen automatically via the useEffect above
    } catch (error) {
      // Error is already handled by the store and set to local state via useEffect
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-neutral-50 px-4">
      <div className="w-full max-w-md space-y-8 bg-white p-8 rounded-xl shadow-sm border border-neutral-200">
        <div className="text-center">
          <h1 className="text-2xl font-bold tracking-tight text-neutral-900">Sign in to your account</h1>
          <p className="mt-2 text-sm text-neutral-600">
            Or{' '}
            <Link to="/signup" className="font-medium text-neutral-900 hover:text-neutral-700">
              create a new account
            </Link>
          </p>
        </div>

        {loginError && (
          <Alert variant="destructive" className="mt-4">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{loginError}</AlertDescription>
          </Alert>
        )}

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Username</FormLabel>
                  <FormControl>
                    <Input 
                      // type="username"
                      placeholder="username"
                      // autoComplete="email"
                      disabled={isLoading}
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Password</FormLabel>
                  <FormControl>
                    <div className="relative">
                      <Input 
                        type={showPassword ? "text" : "password"} 
                        placeholder="••••••••" 
                        autoComplete="current-password"
                        disabled={isLoading}
                        {...field} 
                      />
                      <button
                        type="button"
                        className="absolute right-3 top-2.5 text-neutral-500 hover:text-neutral-800"
                        onClick={togglePasswordVisibility}
                        aria-label={showPassword ? "Hide password" : "Show password"}
                      >
                        {showPassword ? (
                          <EyeOff className="h-5 w-5" />
                        ) : (
                          <Eye className="h-5 w-5" />
                        )}
                      </button>
                    </div>
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex items-center justify-between">
              <div className="text-sm">
                <Link to="/reset-password" className="font-medium text-neutral-900 hover:text-neutral-700">
                  Forgot your password?
                </Link>
              </div>
            </div>

            <Button
              type="submit"
              className="w-full"
              disabled={isLoading}
            >
              {isLoading ? 'Signing in...' : 'Sign in'}
            </Button>
          </form>
        </Form>
      </div>
    </div>
  )
}

export default LoginPage