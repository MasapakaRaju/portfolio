import { useEffect, useState } from 'react'
import { fetchPortfolio } from './api/portfolio'
import type { PortfolioData } from './types'
import Navbar from './components/Navbar/Navbar'
import Hero from './components/Hero/Hero'
import About from './components/About/About'
import Skills from './components/Skills/Skills'
import Experience from './components/Experience/Experience'
import Projects from './components/Projects/Projects'
import Contact from './components/Contact/Contact'
import Footer from './components/Footer/Footer'
import CustomCursor from './components/Effects/CustomCursor'
import ScrollProgress from './components/Effects/ScrollProgress'

export default function App() {
  const [data, setData] = useState<PortfolioData | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetchPortfolio()
      .then(setData)
      .catch(() => setError('Could not connect to the server. Please make sure the backend is running.'))
      .finally(() => setLoading(false))
  }, [])

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
      </div>
    )
  }

  return (
    <>
      <ScrollProgress />
      <CustomCursor />
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
