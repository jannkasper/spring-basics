import { useEffect, useState, RefObject } from 'react'

interface UseScrollAnimationOptions {
  threshold?: number
  rootMargin?: string
}

export function useScrollAnimation<T extends HTMLElement>(
  ref: RefObject<T>,
  options: UseScrollAnimationOptions = {}
) {
  const [isVisible, setIsVisible] = useState(false)
  const { threshold = 0.1, rootMargin = '0px' } = options

  useEffect(() => {
    const currentRef = ref.current
    if (!currentRef) return

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsVisible(true)
          // Once the element is visible, stop observing
          if (currentRef) observer.unobserve(currentRef)
        }
      },
      {
        threshold,
        rootMargin,
      }
    )

    if (currentRef) observer.observe(currentRef)

    return () => {
      if (currentRef) observer.unobserve(currentRef)
    }
  }, [ref, rootMargin, threshold])

  return isVisible
}