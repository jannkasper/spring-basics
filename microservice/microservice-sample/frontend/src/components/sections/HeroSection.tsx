import { ArrowRight } from 'lucide-react'
import { Button } from '@/components/ui/button'

const HeroSection = () => {
  return (
    <section id="hero" className="pt-32 pb-20 md:pt-40 md:pb-28 bg-gradient-to-b from-neutral-50 to-white">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-4xl md:text-5xl lg:text-6xl font-bold text-neutral-900 leading-tight mb-6">
            Modern Solutions for <span className="bg-clip-text text-transparent bg-gradient-to-r from-blue-500 to-purple-500">Modern Problems</span>
          </h1>
          <p className="text-lg md:text-xl text-neutral-600 mb-10 max-w-2xl mx-auto">
            A minimal and powerful platform to help you build better products faster.
            Streamline your workflow and boost productivity.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center space-y-4 sm:space-y-0 sm:space-x-4">
            <Button size="lg" className="px-8 font-medium">
              Get Started
            </Button>
            <Button variant="outline" size="lg" className="px-8 font-medium">
              Learn More <ArrowRight className="ml-2 h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>
    </section>
  )
}

export default HeroSection