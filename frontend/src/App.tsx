import { useEffect, useState } from 'react'
import { cachePortfolio, fetchPortfolio, getCachedPortfolio } from './api/portfolio'
import type { PortfolioData } from './types'
import Navbar from './components/Navbar/Navbar'
import Hero from './components/Hero/Hero'
import About from './components/About/About'
import Skills from './components/Skills/Skills'
import Experience from './components/Experience/Experience'
import Projects from './components/Projects/Projects'
import Contact from './components/Contact/Contact'
import Footer from './components/Footer/Footer'
import ScrollProgress from './components/Effects/ScrollProgress'

export default function App() {
  const [data, setData] = useState<PortfolioData | null>(null)
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [retry, setRetry] = useState(0)

  useEffect(() => {
    const cached = getCachedPortfolio()

    if (cached) {
      setData(cached)
      setLoading(false)
      setRefreshing(true)
    }

    fetchPortfolio()
      .then(freshData => {
        cachePortfolio(freshData)
        setData(freshData)
        setError(null)
      })
      .catch(() => {
        if (!cached) {
          setError('The portfolio service is taking longer than expected to start. Try again in a moment.')
        }
      })
      .finally(() => {
        setLoading(false)
        setRefreshing(false)
      })
  }, [retry])

  useEffect(() => {
    if (loading || !data) return

    const hash = window.location.hash
    if (!hash) return

    const id = hash.replace('#', '')
    const el = document.getElementById(id)
    if (el) {
      setTimeout(() => el.scrollIntoView({ behavior: 'smooth' }), 100)
    }
  }, [loading, data])

  if (loading) {
    return (
      <div className="loading-screen">
        <div className="loader" />
        <p>Loading portfolio...</p>
      </div>
    )
  }

  if (error || !data) {
    return (
      <div className="error-screen">
        <h2>Oops!</h2>
        <p>{error ?? 'Something went wrong.'}</p>
        <button className="btn btn-primary" onClick={() => {
          setError(null)
          setLoading(true)
          setRetry(count => count + 1)
        }}>
          Try again
        </button>
      </div>
    )
  }

  return (
    <>
      <ScrollProgress />
      {refreshing && <div className="refresh-pill">Refreshing backend data...</div>}
      <Navbar name={data.personalInfo.name} />
      <main>
        <Hero personalInfo={data.personalInfo} />
        <About personalInfo={data.personalInfo} />
        <Skills skills={data.skills} />
        <Experience experiences={data.experiences} />
        <Projects projects={data.projects} />
        <Contact personalInfo={data.personalInfo} />
      </main>
      <Footer personalInfo={data.personalInfo} />
    </>
  )
}
