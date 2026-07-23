import type { PersonalInfo } from '../../types'
import './Hero.css'

interface HeroProps {
  personalInfo: PersonalInfo
}

export default function Hero({ personalInfo }: HeroProps) {
  const [firstName, lastName] = personalInfo.name.toUpperCase().split(' ')

  return (
    <section className="hero" id="hero">
      <div className="hero-bg" />
      <div className="hero-orbit hero-orbit-one" aria-hidden="true" />
      <div className="hero-orbit hero-orbit-two" aria-hidden="true" />
      <div className="container hero-content">
        <div className="hero-copy">
          <p className="hero-greeting">A backend engineer who likes systems, speed, and clean delivery.</p>
          <h1 className="hero-name" aria-label={personalInfo.name}>
            <span>{firstName}</span>
            <span>{lastName}</span>
          </h1>
          <p className="hero-tagline">{personalInfo.tagline}</p>

        </div>

        <div className="hero-visual" aria-label="Masapaka Raju portrait">
          <div className="hero-photo-frame">
            <div className="hero-photo-glow" aria-hidden="true" />
            <img src={`${import.meta.env.BASE_URL}MasapakaRajuHero.png`} alt="Masapaka Raju" className="hero-photo" />
          </div>
        </div>
      </div>

      <a href="#about" className="scroll-down" aria-label="Scroll down">
        <span />
      </a>
    </section>
  )
}
