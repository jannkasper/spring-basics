import { LucideIcon, Zap, Shield, BarChart, Globe } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

interface FeatureCardProps {
  title: string
  description: string
  icon: LucideIcon
}

const FeatureCard = ({ title, description, icon: Icon }: FeatureCardProps) => {
  return (
    <Card className="border-none">
      <CardHeader>
        <div className="bg-neutral-100 w-12 h-12 rounded-lg flex items-center justify-center mb-4">
          <Icon className="h-6 w-6 text-neutral-900" />
        </div>
        <CardTitle className="text-xl">{title}</CardTitle>
      </CardHeader>
      <CardContent>
        <CardDescription className="text-neutral-600">{description}</CardDescription>
      </CardContent>
    </Card>
  )
}

const FeaturesSection = () => {
  const features = [
    {
      title: 'Lightning Fast',
      description: 'Our platform is optimized for speed, ensuring your applications run efficiently.',
      icon: Zap,
    },
    {
      title: 'Secure by Default',
      description: 'Built with security at its core, your data is always protected.',
      icon: Shield,
    },
    {
      title: 'Advanced Analytics',
      description: 'Gain insights with powerful analytics tools to make data-driven decisions.',
      icon: BarChart,
    },
    {
      title: 'Global Reach',
      description: 'Deploy your applications anywhere in the world with our global infrastructure.',
      icon: Globe,
    },
  ]

  return (
    <section id="features" className="py-20 bg-white">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold text-neutral-900 mb-4">Features</h2>
          <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
            Our platform offers everything you need to build modern applications
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
          {features.map((feature, index) => (
            <FeatureCard
              key={index}
              title={feature.title}
              description={feature.description}
              icon={feature.icon}
            />
          ))}
        </div>
      </div>
    </section>
  )
}

export default FeaturesSection