import { useEffect, useRef, useState } from 'react'
import type { ContactForm, PersonalInfo } from '../../types'
import { submitContact, verifyContact } from '../../api/portfolio'
import './Contact.css'

interface ContactProps {
  personalInfo: PersonalInfo
}

const initialForm: ContactForm = { name: '', email: '', phone: '', subject: '', message: '' }
const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const phonePattern = /^\+?[1-9]\d{7,14}$/

export default function Contact({ personalInfo }: ContactProps) {
  const ref = useRef<HTMLElement>(null)
  const [form, setForm] = useState<ContactForm>(initialForm)
  const [status, setStatus] = useState<'idle' | 'loading' | 'success' | 'error'>('idle')
  const [statusMsg, setStatusMsg] = useState('')
  const [formErrors, setFormErrors] = useState<Partial<Record<keyof ContactForm, string>>>({})
  const [verificationPending, setVerificationPending] = useState(false)
  const [verificationCode, setVerificationCode] = useState('')

  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => entries.forEach(e => e.target.classList.toggle('visible', e.isIntersecting)),
      { threshold: 0.1 }
    )
    ref.current?.querySelectorAll('.fade-in').forEach(el => observer.observe(el))
    return () => observer.disconnect()
  }, [])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const field = e.target.name as keyof ContactForm
    setForm(f => ({ ...f, [field]: e.target.value }))
    setFormErrors(errors => ({ ...errors, [field]: undefined }))
    if (verificationPending) {
      setVerificationPending(false)
      setVerificationCode('')
      setStatus('idle')
      setStatusMsg('')
    }
  }

  const validateForm = () => {
    const errors: Partial<Record<keyof ContactForm, string>> = {}
    const phone = form.phone.replace(/[\s()-]/g, '')

    ;(['name', 'email', 'phone', 'subject', 'message'] as const).forEach(field => {
      if (!form[field].trim()) errors[field] = 'This field is required'
    })
    if (form.email && !emailPattern.test(form.email.trim())) errors.email = 'Enter a valid email address'
    if (form.phone && !phonePattern.test(phone)) errors.phone = 'Enter a valid mobile number'

    setFormErrors(errors)
    return Object.keys(errors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validateForm()) {
      setStatus('error')
      setStatusMsg('Complete all required fields using valid details.')
      return
    }

    const contactData = {
      name: form.name.trim(),
      email: form.email.trim().toLowerCase(),
      phone: form.phone.replace(/[\s()-]/g, ''),
      subject: form.subject.trim(),
      message: form.message.trim(),
    }

    setStatus('loading')
    try {
      const res = await submitContact(contactData)
      setStatus('success')
      setStatusMsg(res.message)
      setForm(contactData)
      setVerificationPending(true)
    } catch (err) {
      setStatus('error')
      setStatusMsg(err instanceof Error ? err.message : 'Something went wrong.')
    }
  }

  const handleVerification = async () => {
    if (!/^\d{6}$/.test(verificationCode)) {
      setStatus('error')
      setStatusMsg('Enter the six-digit verification code from your email.')
      return
    }

    setStatus('loading')
    try {
      const res = await verifyContact(form.email, verificationCode)
      setStatus('success')
      setStatusMsg(res.message)
      setForm(initialForm)
      setVerificationCode('')
      setVerificationPending(false)
    } catch (err) {
      setStatus('error')
      setStatusMsg(err instanceof Error ? err.message : 'Unable to verify your email.')
    }
  }

  return (
    <section id="contact" ref={ref}>
      <div className="container">
        <h2 className="section-title fade-in">Get In Touch</h2>
        <p className="section-subtitle fade-in">Have a project in mind? Let's talk!</p>

        <div className="contact-grid">
          <div className="contact-info fade-in">
            <h3>Contact Information</h3>
            <p>Feel free to reach out through any of these channels. I'll get back to you as soon as possible.</p>

            <div className="contact-items">
              <a href={`mailto:${personalInfo.email}`} className="contact-item">
                <div className="contact-item-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z" />
                    <polyline points="22,6 12,13 2,6" />
                  </svg>
                </div>
                <div>
                  <span className="contact-item-label">Email</span>
                  <span className="contact-item-value">{personalInfo.email}</span>
                </div>
              </a>

              <a href={`tel:${personalInfo.phone}`} className="contact-item">
                <div className="contact-item-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07A19.5 19.5 0 0 1 4.69 12a19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 3.6 1.27h3a2 2 0 0 1 2 1.72c.13.96.36 1.9.7 2.81a2 2 0 0 1-.45 2.11L7.91 8.91A16 16 0 0 0 15.09 16.1l.91-.91a2 2 0 0 1 2.11-.45c.9.34 1.85.57 2.81.7a2 2 0 0 1 1.72 2.01z" />
                  </svg>
                </div>
                <div>
                  <span className="contact-item-label">Phone</span>
                  <span className="contact-item-value">{personalInfo.phone}</span>
                </div>
              </a>

              <div className="contact-item">
                <div className="contact-item-icon">
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                    <circle cx="12" cy="10" r="3" />
                  </svg>
                </div>
                <div>
                  <span className="contact-item-label">Location</span>
                  <span className="contact-item-value">{personalInfo.location}</span>
                </div>
              </div>
            </div>

            <div className="contact-socials">
              <a href={personalInfo.githubUrl} target="_blank" rel="noopener noreferrer" className="social-btn">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.44 9.8 8.2 11.38.6.11.82-.26.82-.58v-2.03c-3.34.73-4.04-1.61-4.04-1.61-.55-1.39-1.34-1.76-1.34-1.76-1.09-.75.08-.73.08-.73 1.2.08 1.84 1.24 1.84 1.24 1.07 1.83 2.81 1.3 3.49 1 .11-.78.42-1.3.76-1.6-2.67-.3-5.47-1.33-5.47-5.93 0-1.31.47-2.38 1.24-3.22-.14-.3-.54-1.52.1-3.18 0 0 1.01-.32 3.3 1.23a11.5 11.5 0 0 1 3-.4c1.02.01 2.04.14 3 .4 2.28-1.55 3.29-1.23 3.29-1.23.65 1.66.24 2.88.12 3.18.77.84 1.23 1.91 1.23 3.22 0 4.61-2.81 5.63-5.48 5.92.43.37.81 1.1.81 2.22v3.29c0 .32.22.7.83.58C20.57 21.8 24 17.3 24 12c0-6.63-5.37-12-12-12z" />
                </svg>
              </a>
              <a href={personalInfo.linkedinUrl} target="_blank" rel="noopener noreferrer" className="social-btn">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M20.45 20.45h-3.55v-5.57c0-1.33-.03-3.04-1.85-3.04-1.85 0-2.14 1.45-2.14 2.94v5.67H9.36V9h3.41v1.56h.05c.48-.9 1.63-1.85 3.35-1.85 3.58 0 4.24 2.36 4.24 5.43v6.31zM5.34 7.43a2.06 2.06 0 1 1 0-4.12 2.06 2.06 0 0 1 0 4.12zm1.78 13.02H3.56V9h3.56v11.45zM22.23 0H1.77C.79 0 0 .77 0 1.72v20.56C0 23.23.79 24 1.77 24h20.46C23.2 24 24 23.23 24 22.28V1.72C24 .77 23.2 0 22.23 0z" />
                </svg>
              </a>
            </div>
          </div>

          <form className="contact-form fade-in" onSubmit={handleSubmit} noValidate>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="name">Name</label>
                <input
                  id="name" name="name" type="text" placeholder="Your name"
                  value={form.name} onChange={handleChange} required
                  aria-invalid={Boolean(formErrors.name)} aria-describedby={formErrors.name ? 'name-error' : undefined}
                />
                {formErrors.name && <span className="form-error" id="name-error">{formErrors.name}</span>}
              </div>
              <div className="form-group">
                <label htmlFor="email">Email</label>
                <input
                  id="email" name="email" type="email" placeholder="your@email.com"
                  value={form.email} onChange={handleChange} required
                  aria-invalid={Boolean(formErrors.email)} aria-describedby={formErrors.email ? 'email-error' : undefined}
                />
                {formErrors.email && <span className="form-error" id="email-error">{formErrors.email}</span>}
              </div>
            </div>
            <div className="form-row">
              <div className="form-group">
                <label htmlFor="phone">Mobile Number</label>
                <input
                  id="phone" name="phone" type="tel" inputMode="tel" placeholder="+91 98765 43210"
                  value={form.phone} onChange={handleChange} required
                  aria-invalid={Boolean(formErrors.phone)} aria-describedby={formErrors.phone ? 'phone-error' : undefined}
                />
                {formErrors.phone && <span className="form-error" id="phone-error">{formErrors.phone}</span>}
              </div>
              <div className="form-group">
                <label htmlFor="subject">Subject</label>
                <input
                  id="subject" name="subject" type="text" placeholder="What's this about?"
                  value={form.subject} onChange={handleChange} required
                  aria-invalid={Boolean(formErrors.subject)} aria-describedby={formErrors.subject ? 'subject-error' : undefined}
                />
                {formErrors.subject && <span className="form-error" id="subject-error">{formErrors.subject}</span>}
              </div>
            </div>
            <div className="form-group">
              <label htmlFor="message">Message</label>
              <textarea
                id="message" name="message" rows={6} placeholder="Your message..."
                value={form.message} onChange={handleChange} required
                aria-invalid={Boolean(formErrors.message)} aria-describedby={formErrors.message ? 'message-error' : undefined}
              />
              {formErrors.message && <span className="form-error" id="message-error">{formErrors.message}</span>}
            </div>

            {status !== 'idle' && (
              <div className={`form-status ${status}`}>
                {status === 'loading' ? 'Sending...' : statusMsg}
              </div>
            )}

            {verificationPending ? (
              <div className="verification-panel">
                <label htmlFor="verification-code">Email Verification Code</label>
                <div className="verification-actions">
                  <input
                    id="verification-code" type="text" inputMode="numeric" autoComplete="one-time-code"
                    maxLength={6} placeholder="000000" value={verificationCode}
                    onChange={event => setVerificationCode(event.target.value.replace(/\D/g, ''))}
                  />
                  <button type="button" className="btn btn-primary" onClick={handleVerification} disabled={status === 'loading'}>
                    {status === 'loading' ? 'Verifying...' : 'Verify & Send'}
                  </button>
                </div>
              </div>
            ) : (
              <button type="submit" className="btn btn-primary submit-btn" disabled={status === 'loading'}>
                {status === 'loading' ? 'Sending...' : 'Send Message'}
              </button>
            )}
          </form>
        </div>
      </div>
    </section>
  )
}
