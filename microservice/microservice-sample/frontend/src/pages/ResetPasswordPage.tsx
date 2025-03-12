import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { AlertCircle, ArrowLeft, CheckCircle2 } from 'lucide-react'

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
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'

// Define form validation schema
const resetPasswordSchema = z.object({
  email: z.string().email({ message: 'Please enter a valid email address' }),
})

type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>

const ResetPasswordPage = () => {
  const [isLoading, setIsLoading] = useState(false)
  const [resetError, setResetError] = useState<string | null>(null)
  const [resetSuccess, setResetSuccess] = useState(false)

  // Initialize the form
  const form = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
    defaultValues: {
      email: '',
    },
  })

  const onSubmit = async (data: ResetPasswordFormValues) => {
    setIsLoading(true)
    setResetError(null)
    setResetSuccess(false)
    
    try {
      // Simulate API call delay
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      // For demo purposes - normally you'd make an API call here
      console.log('Reset password request submitted:', data)
      
      // Show success message
      setResetSuccess(true)
      form.reset()
    } catch (error) {
      // Handle reset error
      setResetError('An error occurred while processing your request. Please try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="flex items-center justify-center min-h-screen bg-neutral-50 px-4">
      <div className="w-full max-w-md space-y-8 bg-white p-8 rounded-xl shadow-sm border border-neutral-200">
        <div className="text-center">
          <h1 className="text-2xl font-bold tracking-tight text-neutral-900">Reset your password</h1>
          <p className="mt-2 text-sm text-neutral-600">
            Enter your email address and we'll send you a link to reset your password.
          </p>
        </div>

        {resetError && (
          <Alert variant="destructive" className="mt-4">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{resetError}</AlertDescription>
          </Alert>
        )}

        {resetSuccess && (
          <Alert variant="success" className="mt-4">
            <CheckCircle2 className="h-4 w-4" />
            <AlertTitle>Reset link sent!</AlertTitle>
            <AlertDescription>
              If an account exists with that email, we've sent instructions to reset your password.
            </AlertDescription>
          </Alert>
        )}

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            <FormField
              control={form.control}
              name="email"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Email</FormLabel>
                  <FormControl>
                    <Input 
                      type="email" 
                      placeholder="name@example.com" 
                      autoComplete="email"
                      disabled={isLoading || resetSuccess}
                      {...field} 
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <Button
              type="submit"
              className="w-full"
              disabled={isLoading || resetSuccess}
            >
              {isLoading ? 'Sending reset link...' : 'Send reset link'}
            </Button>
          </form>
        </Form>

        <div className="mt-4 text-center">
          <Link 
            to="/login" 
            className="inline-flex items-center text-sm font-medium text-neutral-900 hover:text-neutral-700"
          >
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to login
          </Link>
        </div>
      </div>
    </div>
  )
}

export default ResetPasswordPage