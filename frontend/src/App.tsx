import { useEffect, useState } from 'react'
import {
  cachePortfolio,
  fetchPortfolio,
  getCachedPortfolio,
  PORTFOLIO_CACHE_TTL_MS,
} from './api/portfolio'
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
import AdminPage from './components/Admin/AdminPage'

function getNavigationType() {
  return performance.getEntriesByType('navigation')[0]?.toJSON().type
}

function getBuildResources(documentToCheck: Document, baseUrl: string) {
  return Array.from(documentToCheck.querySelectorAll<HTMLScriptElement | HTMLLinkElement>(
    'script[type="module"][src], link[rel="stylesheet"][href]'
  ))
    .map(resource => {
      const path = resource instanceof HTMLScriptElement ? resource.src : resource.href
      return path ? new URL(path, baseUrl).pathname : ''
    })
    .filter(Boolean)
    .sort()
    .join('|')
}

function PortfolioApp() {
  const [data, setData] = useState<PortfolioData | null>(null)
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [retry, setRetry] = useState(0)

  useEffect(() => {
    const cached = getCachedPortfolio()
    const forceRefresh = retry > 0 || getNavigationType() === 'reload'

    if (cached) {
      setData(cached.data)
      setLoading(false)
      setError(null)

      const cacheIsFresh = Date.now() - cached.cachedAt < PORTFOLIO_CACHE_TTL_MS
      if (cacheIsFresh && !forceRefresh) {
        setRefreshing(false)
        return
      }

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
    let checkingForUpdate = false

    const checkForUpdate = async () => {
      if (checkingForUpdate || document.visibilityState !== 'visible') return
      checkingForUpdate = true

      try {
        const url = new URL(window.location.pathname, window.location.origin)
        url.searchParams.set('__build_check', String(Date.now()))
        const response = await fetch(url, { cache: 'no-store' })
        if (!response.ok) return

        const html = await response.text()
        const latestDocument = new DOMParser().parseFromString(html, 'text/html')
        const currentBuild = getBuildResources(document, document.baseURI)
        const latestBuild = getBuildResources(latestDocument, url.href)

        if (currentBuild && latestBuild && currentBuild !== latestBuild) {
          window.location.reload()
        }
      } catch {
        // The next check can retry if the network is temporarily unavailable.
      } finally {
        checkingForUpdate = false
      }
    }

    const interval = window.setInterval(checkForUpdate, 60_000)
    document.addEventListener('visibilitychange', checkForUpdate)
    return () => {
      window.clearInterval(interval)
      document.removeEventListener('visibilitychange', checkForUpdate)
    }
  }, [])

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

export default function App() {
  const basePath = import.meta.env.BASE_URL.replace(/\/+$/, '')
  const adminPath = `${basePath}/admin`.replace(/\/+$/, '').toLowerCase()
  const currentPath = window.location.pathname.replace(/\/+$/, '').toLowerCase()

  return currentPath === adminPath ? <AdminPage /> : <PortfolioApp />
}
