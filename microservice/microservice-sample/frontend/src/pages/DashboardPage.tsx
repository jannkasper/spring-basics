import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { LogOut, User } from 'lucide-react'
import { useAuthStore } from '@/store/useAuthStore'
import { Button } from '@/components/ui/button'

const DashboardPage = () => {
  const { user, logout, isAuthenticated, checkAuth } = useAuthStore()
  const navigate = useNavigate()

  useEffect(() => {
    // Check authentication status when the component mounts
    checkAuth()

    // Redirect to login if not authenticated
    if (!isAuthenticated) {
      navigate('/login')
    }
  }, [isAuthenticated, navigate, checkAuth])

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-5xl">
      <div className="bg-white rounded-xl shadow-sm border border-neutral-200 p-8">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
          <div>
            <h1 className="text-2xl font-bold tracking-tight text-neutral-900">Dashboard</h1>
            <p className="text-neutral-600">Welcome to your account dashboard</p>
          </div>
          <Button 
            variant="outline" 
            onClick={handleLogout}
            className="flex items-center gap-2"
          >
            <LogOut className="h-4 w-4" />
            Sign out
          </Button>
        </div>

        <div className="bg-neutral-50 p-6 rounded-lg mb-8 border border-neutral-200">
          <div className="flex items-center gap-4">
            <div className="bg-neutral-200 rounded-full p-3">
              <User className="h-6 w-6 text-neutral-700" />
            </div>
            <div>
              <h2 className="font-medium text-neutral-900">Account Information</h2>
              <p className="text-neutral-600 text-sm">{user?.email || 'Loading...'}</p>
            </div>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {Array.from({ length: 3 }).map((_, index) => (
            <div key={index} className="border border-neutral-200 rounded-lg p-6 bg-white">
              <h3 className="font-medium text-neutral-900 mb-2">Card Title {index + 1}</h3>
              <p className="text-neutral-600 text-sm">This is a sample card for the dashboard. It can contain any content.</p>
            </div>
          ))}
        </div>

        <div className="mt-8 pt-6 border-t border-neutral-200">
          <p className="text-sm text-neutral-500">
            This is a protected dashboard page that requires authentication. 
            Only logged-in users can access this page.
          </p>
        </div>
      </div>
    </div>
  )
}

export default DashboardPage