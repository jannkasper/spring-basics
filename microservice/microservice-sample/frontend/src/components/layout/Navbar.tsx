import { Menu, X } from 'lucide-react'
import { Link, useLocation } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { useAppStore } from '@/store/useAppStore'

const Navbar = () => {
  const { isMobileMenuOpen, toggleMobileMenu, setMobileMenuOpen } = useAppStore()
  const location = useLocation()
  const isHomePage = location.pathname === '/'

  return (
    <header className="fixed top-0 left-0 w-full bg-white/80 backdrop-blur-md z-50 shadow-sm">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <div className="flex-shrink-0">
            <Link to="/" className="font-bold text-xl text-neutral-900">Company</Link>
          </div>
          
          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center space-x-8">
            {isHomePage ? (
              <>
                <a href="#features" className="text-neutral-600 hover:text-neutral-900 transition-colors">
                  Features
                </a>
                <a href="#testimonials" className="text-neutral-600 hover:text-neutral-900 transition-colors">
                  Testimonials
                </a>
                <a href="#contact" className="text-neutral-600 hover:text-neutral-900 transition-colors">
                  Contact
                </a>
                <div className="flex items-center">
                  <Link to="/login">
                    <Button variant="outline" size="sm" className="mr-2">Sign in</Button>
                  </Link>
                  <Link to="/signup">
                    <Button variant="default" size="sm">Sign up</Button>
                  </Link>
                </div>
              </>
            ) : (
              <>
                <Link to="/" className="text-neutral-600 hover:text-neutral-900 transition-colors">
                  Home
                </Link>
                <div className="flex items-center">
                  <Link to="/login">
                    <Button variant="outline" size="sm" className="mr-2">Sign in</Button>
                  </Link>
                  <Link to="/signup">
                    <Button variant="default" size="sm">Sign up</Button>
                  </Link>
                </div>
              </>
            )}
          </nav>
          
          {/* Mobile Menu Button */}
          <div className="md:hidden">
            <Button 
              onClick={toggleMobileMenu} 
              variant="ghost" 
              size="icon"
              aria-label="Toggle menu"
            >
              {isMobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </Button>
          </div>
        </div>
      </div>
      
      {/* Mobile Menu */}
      {isMobileMenuOpen && (
        <div className="md:hidden bg-white border-t border-neutral-100">
          <div className="px-2 pt-2 pb-3 space-y-1 sm:px-3">
            {isHomePage ? (
              <>
                <a
                  href="#features"
                  className="block px-3 py-2 text-base font-medium text-neutral-600 hover:text-neutral-900 hover:bg-neutral-50 rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Features
                </a>
                <a
                  href="#testimonials"
                  className="block px-3 py-2 text-base font-medium text-neutral-600 hover:text-neutral-900 hover:bg-neutral-50 rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Testimonials
                </a>
                <a
                  href="#contact"
                  className="block px-3 py-2 text-base font-medium text-neutral-600 hover:text-neutral-900 hover:bg-neutral-50 rounded-md"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Contact
                </a>
              </>
            ) : (
              <Link
                to="/"
                className="block px-3 py-2 text-base font-medium text-neutral-600 hover:text-neutral-900 hover:bg-neutral-50 rounded-md"
                onClick={() => setMobileMenuOpen(false)}
              >
                Home
              </Link>
            )}
            <div className="px-3 py-2 space-y-2">
              <Link to="/login" onClick={() => setMobileMenuOpen(false)}>
                <Button variant="outline" className="w-full">Sign in</Button>
              </Link>
              <Link to="/signup" onClick={() => setMobileMenuOpen(false)}>
                <Button className="w-full">Sign up</Button>
              </Link>
            </div>
          </div>
        </div>
      )}
    </header>
  )
}

export default Navbar