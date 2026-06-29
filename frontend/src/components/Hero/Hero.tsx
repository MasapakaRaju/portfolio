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
  const lastInitial = lastName?.charAt(0) ?? 'M'
  const lastRest = lastName?.slice(1) ?? ''

  return (
    <section className="hero" id="hero">
      <div className="hero-bg" />
      <div className="hero-dot-field" aria-hidden="true" />
      <div className="container hero-content">
        <div className="hero-copy">
          <p className="hero-greeting">Java Backend Engineer. Builder of RESTFull-APIs.</p>
          <h1 className="hero-name" aria-label={personalInfo.name}>
            <span>{firstName}</span>
            <span>
              <em>{lastInitial}</em>{lastRest}
            </span>
          </h1>
          <p className="hero-tagline">
            {personalInfo.tagline}
          </p>

          <div className="hero-actions">
            <a href="#projects" className="btn btn-primary">See the Work</a>
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

        <div className="hero-visual" aria-label="Developer profile visual">
          <div className="portrait-halo" />
          <div className="portrait">
            <div className="portrait-head">
              <span>MR</span>
            </div>
            <div className="portrait-neck" />
          </div>
          <div className="hero-code-strip">
            <span>SPRING_BOOT</span>
            <span>POSTGRESQL</span>
            <span>DOCKER</span>
            <span>AI_WORKFLOWS</span>
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
