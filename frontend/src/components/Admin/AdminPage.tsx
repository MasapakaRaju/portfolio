import { useCallback, useEffect, useState, type FormEvent } from 'react'
import { API_BASE_URL } from '../../api/portfolio'
import AdminDashboard from './AdminDashboard'
import './AdminPage.css'

type AdminStep = 'name' | 'otp' | 'welcome'

async function readResponse(response: Response) {
  const body = await response.json().catch(() => ({})) as Record<string, unknown>
  if (!response.ok) {
    throw new Error(typeof body.message === 'string' ? body.message : 'Could not complete admin sign in.')
  }
  return body
}

export default function AdminPage() {
  const [step, setStep] = useState<AdminStep>('name')
  const [adminName, setAdminName] = useState('')
  const [otp, setOtp] = useState('')
  const [challengeId, setChallengeId] = useState('')
  const [sessionToken, setSessionToken] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  useEffect(() => {
    document.title = step === 'welcome' ? 'Admin · Masapaka Raju' : 'Admin sign in · Masapaka Raju'
    return () => { document.title = 'Masapaka Raju' }
  }, [step])

  const requestOtp = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setBusy(true)
    setError('')
    setNotice('')
    try {
      const response = await fetch(`${API_BASE_URL}/admin/auth/challenge`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ adminName }),
      })
      const body = await readResponse(response)
      setChallengeId(String(body.challengeId ?? ''))
      setNotice(String(body.message ?? 'Check Slack for your verification code.'))
      setStep('otp')
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'Could not request a verification code.')
    } finally {
      setBusy(false)
    }
  }

  const verifyOtp = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setBusy(true)
    setError('')
    try {
      const response = await fetch(`${API_BASE_URL}/admin/auth/verify`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ challengeId, otp }),
      })
      const body = await readResponse(response)
      const token = String(body.token ?? '')
      if (!token) throw new Error('The server did not return a valid admin session.')

      const sessionResponse = await fetch(`${API_BASE_URL}/admin/auth/me`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      await readResponse(sessionResponse)
      setSessionToken(token)
      setStep('welcome')
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : 'The code could not be verified.')
    } finally {
      setBusy(false)
    }
  }

  const logout = useCallback(async () => {
    if (sessionToken) {
      await fetch(`${API_BASE_URL}/admin/auth/logout`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${sessionToken}` },
      }).catch(() => undefined)
    }
    setSessionToken('')
    setAdminName('')
    setOtp('')
    setChallengeId('')
    setNotice('')
    setError('')
    setStep('name')
  }, [sessionToken])

  if (step === 'welcome' && sessionToken) return <AdminDashboard token={sessionToken} onSignOut={logout} />

  return (
    <main className="admin-shell">
      <div className="admin-glow admin-glow-one" aria-hidden="true" />
      <div className="admin-glow admin-glow-two" aria-hidden="true" />
      <a className="admin-back-link" href={`${import.meta.env.BASE_URL}`}>← Back to portfolio</a>

      <section className="admin-card" aria-labelledby="admin-heading">
        {step !== 'welcome' && <div className="admin-mark" aria-hidden="true">MR</div>}
        <p className="admin-eyebrow">PRIVATE AREA</p>
        <h1 id="admin-heading">{step === 'welcome' ? 'Welcome, Admin' : 'Admin sign in'}</h1>
        <p className="admin-intro">
          {step === 'name' && 'Enter your admin name to receive a one-time code in Slack.'}
          {step === 'otp' && 'Enter the six-digit code sent to your private Slack channel.'}
          {step === 'welcome' && 'You are signed in to the portfolio admin area.'}
        </p>

        {step === 'name' && (
          <form className="admin-form" onSubmit={requestOtp}>
            <label htmlFor="admin-name">Admin name</label>
            <input
              id="admin-name" autoComplete="username" maxLength={100} required
              value={adminName} onChange={event => setAdminName(event.target.value)}
              placeholder="Enter your admin name"
            />
            <button className="btn btn-primary admin-submit" disabled={busy}>
              {busy ? 'Sending code…' : 'Send login code'}
            </button>
          </form>
        )}

        {step === 'otp' && (
          <form className="admin-form" onSubmit={verifyOtp}>
            <label htmlFor="admin-otp">Verification code</label>
            <input
              id="admin-otp" autoComplete="one-time-code" inputMode="numeric" pattern="[0-9]{6}"
              maxLength={6} required value={otp} onChange={event => setOtp(event.target.value.replace(/\D/g, '').slice(0, 6))}
              placeholder="000000" aria-describedby="otp-help"
            />
            <p className="admin-help" id="otp-help">The code expires in five minutes and can only be used once.</p>
            <button className="btn btn-primary admin-submit" disabled={busy || otp.length !== 6}>
              {busy ? 'Verifying…' : 'Verify and continue'}
            </button>
            <button type="button" className="admin-text-button" disabled={busy} onClick={() => {
              setStep('name')
              setOtp('')
              setError('')
              setNotice('')
            }}>Use a different admin name</button>
          </form>
        )}

        {notice && <p className="admin-notice" role="status">{notice}</p>}
        {error && <p className="admin-error" role="alert">{error}</p>}
        <p className="admin-security-note">One-time codes are delivered privately and never shown in this page or stored in browser storage.</p>
      </section>
    </main>
  )
}
