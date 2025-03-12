import { Card, CardContent } from '@/components/ui/card'

interface TestimonialProps {
  quote: string
  author: string
  role: string
  company: string
}

const Testimonial = ({ quote, author, role, company }: TestimonialProps) => {
  return (
    <Card className="h-full">
      <CardContent className="p-6 flex flex-col h-full">
        <div className="mb-4">
          {/* Star rating */}
          <div className="flex">
            {[...Array(5)].map((_, i) => (
              <svg
                key={i}
                className="w-5 h-5 text-yellow-400 fill-current"
                viewBox="0 0 24 24"
              >
                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
              </svg>
            ))}
          </div>
        </div>
        <p className="text-neutral-600 italic mb-6 flex-grow">{quote}</p>
        <div className="mt-auto">
          <p className="font-medium text-neutral-900">{author}</p>
          <p className="text-sm text-neutral-500">
            {role}, {company}
          </p>
        </div>
      </CardContent>
    </Card>
  )
}

const TestimonialsSection = () => {
  const testimonials = [
    {
      quote: "This platform has completely transformed how our team works. We've seen a 40% increase in productivity since we started using it.",
      author: "Sarah Johnson",
      role: "CTO",
      company: "TechInnovate",
    },
    {
      quote: "The simplicity and power of this solution is impressive. Our developers love how easy it is to integrate with our existing systems.",
      author: "Mark Williams",
      role: "Lead Developer",
      company: "DevSphere",
    },
    {
      quote: "We've tried many solutions before, but none compare to the quality and support we've received. Highly recommended for any serious team.",
      author: "Aisha Patel",
      role: "Product Manager",
      company: "Nexus Solutions",
    },
  ]

  return (
    <section id="testimonials" className="py-20 bg-neutral-50">
      <div className="container mx-auto px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-16">
          <h2 className="text-3xl md:text-4xl font-bold text-neutral-900 mb-4">
            What Our Customers Say
          </h2>
          <p className="text-lg text-neutral-600 max-w-2xl mx-auto">
            Don't just take our word for it - hear from some of our satisfied customers
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          {testimonials.map((testimonial, index) => (
            <Testimonial
              key={index}
              quote={testimonial.quote}
              author={testimonial.author}
              role={testimonial.role}
              company={testimonial.company}
            />
          ))}
        </div>
      </div>
    </section>
  )
}

export default TestimonialsSection