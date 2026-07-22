import type { PersonalInfo } from '../../types'
import './Hero.css'

interface HeroProps {
  personalInfo: PersonalInfo
}

const metrics = [
  { value: '12+', label: 'REST APIs' },
  { value: '20+', label: 'SQL Wins' },
  { value: '10K+', label: 'Records' },
]

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

          <div className="hero-actions">
            <a href="#projects" className="btn btn-primary">View Projects</a>
            <a href={personalInfo.leetcodeUrl} target="_blank" rel="noopener noreferrer" className="btn btn-outline">LeetCode</a>
          </div>

          <div className="hero-socials">
            <a href={personalInfo.githubUrl} target="_blank" rel="noopener noreferrer" className="social-link">
              GitHub
            </a>
            <a href={personalInfo.linkedinUrl} target="_blank" rel="noopener noreferrer" className="social-link">
              LinkedIn
            </a>
            <a href={personalInfo.leetcodeUrl} target="_blank" rel="noopener noreferrer" className="social-link">
              LeetCode
            </a>
          </div>
        </div>

        <div className="hero-visual" aria-label="Masapaka Raju portrait">
          <div className="hero-photo-frame">
            <div className="hero-photo-glow" aria-hidden="true" />
            <img src="/MasapakaRajuHero.png" alt="Masapaka Raju" className="hero-photo" />
          </div>
          <div className="hero-metrics">
            {metrics.map(metric => (
              <div key={metric.label} className="hero-metric">
                <strong>{metric.value}</strong>
                <span>{metric.label}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      <a href="#about" className="scroll-down" aria-label="Scroll down">
        <span />
      </a>
    </section>
  )
}
