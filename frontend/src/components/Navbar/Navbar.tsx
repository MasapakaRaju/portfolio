import { useEffect, useState } from 'react'
import './Navbar.css'

interface NavbarProps {
  name: string
}

const navLinks = [
  { label: 'My Story', href: '#about' },
  { label: 'Stack', href: '#skills' },
  { label: 'Experience', href: '#experience' },
  { label: 'Projects', href: '#projects' },
  { label: 'Contacts', href: '#contact' },
]

export default function Navbar({ name }: NavbarProps) {
  const [scrolled, setScrolled] = useState(false)
  const [menuOpen, setMenuOpen] = useState(false)

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 50)
    window.addEventListener('scroll', onScroll)
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  const handleLinkClick = () => setMenuOpen(false)

  return (
    <header className={`navbar ${scrolled ? 'scrolled' : ''}`}>
      <div className="navbar-container">
        <a href="#hero" className="navbar-logo">
          {name.split(' ')[0].toUpperCase()}-
          <span>{name.split(' ')[1]?.charAt(0) ?? 'M'}</span>
        </a>

        <nav className={`navbar-nav ${menuOpen ? 'open' : ''}`}>
          {navLinks.map(link => (
            <a key={link.href} href={link.href} className="nav-link" onClick={handleLinkClick}>
              {link.label}
            </a>
          ))}
        </nav>

        <button
          className={`hamburger ${menuOpen ? 'active' : ''}`}
          onClick={() => setMenuOpen(o => !o)}
          aria-label="Toggle menu"
        >
          <span />
          <span />
          <span />
        </button>
      </div>
    </header>
  )
}
