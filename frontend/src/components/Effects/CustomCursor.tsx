import { useEffect, useState } from 'react'
import './Effects.css'

export default function CustomCursor() {
  const [position, setPosition] = useState({ x: -100, y: -100 })
  const [active, setActive] = useState(false)

  useEffect(() => {
    const handlePointerMove = (event: PointerEvent) => {
      setPosition({ x: event.clientX, y: event.clientY })
      setActive(Boolean((event.target as Element | null)?.closest('a, button, input, textarea, .project-card, .skill-category, .timeline-card, .stat-card')))
    }

    window.addEventListener('pointermove', handlePointerMove)
    return () => window.removeEventListener('pointermove', handlePointerMove)
  }, [])

  return (
    <>
      <div
        className={`cursor-dot ${active ? 'active' : ''}`}
        style={{ transform: `translate3d(${position.x}px, ${position.y}px, 0)` }}
      />
      <div
        className={`cursor-ring ${active ? 'active' : ''}`}
        style={{ transform: `translate3d(${position.x}px, ${position.y}px, 0)` }}
      />
    </>
  )
}
