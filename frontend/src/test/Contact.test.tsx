import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import Contact from '../components/Contact/Contact'
import type { PersonalInfo } from '../types'

const personalInfo: PersonalInfo = {
  id: 1,
  name: 'Masapaka Raju',
  title: 'Full Stack Developer',
  tagline: 'Building clean systems',
  location: 'Hyderabad, India',
  email: 'raj@example.com',
  phone: '+919876543210',
  githubUrl: 'https://github.com/test',
  linkedinUrl: 'https://linkedin.com/test',
  leetcodeUrl: 'https://leetcode.com/test',
  resumeUrl: '',
  about: 'Engineer',
}

describe('Contact', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
    vi.clearAllMocks()
  })

  it('renders all form fields', () => {
    render(<Contact personalInfo={personalInfo} />)

    expect(screen.getByLabelText(/^name$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^email$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/mobile number/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^subject$/i)).toBeInTheDocument()
    expect(screen.getByLabelText(/^message$/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /send message/i })).toBeInTheDocument()
  })

  it('renders country code selector with default IN +91', () => {
    render(<Contact personalInfo={personalInfo} />)

    const select = screen.getByLabelText(/country code/i)
    expect(select).toBeInTheDocument()
    expect(select).toHaveValue('+91')
  })

  it('shows validation errors when submitting empty form', async () => {
    const user = userEvent.setup()
    render(<Contact personalInfo={personalInfo} />)

    await user.click(screen.getByRole('button', { name: /send message/i }))

    const errors = await screen.findAllByText('This field is required')
    expect(errors.length).toBeGreaterThanOrEqual(1)
  })

  it('shows email validation error for invalid email', async () => {
    const user = userEvent.setup()
    render(<Contact personalInfo={personalInfo} />)

    await user.type(screen.getByLabelText(/^name$/i), 'John')
    await user.type(screen.getByLabelText(/^email$/i), 'bad-email')
    await user.type(screen.getByLabelText(/mobile number/i), '9876543210')
    await user.type(screen.getByLabelText(/^subject$/i), 'Hi')
    await user.type(screen.getByLabelText(/^message$/i), 'Hello')
    await user.click(screen.getByRole('button', { name: /send message/i }))

    expect(await screen.findByText('Enter a valid email address')).toBeInTheDocument()
  })

  it('shows phone validation error for short number', async () => {
    const user = userEvent.setup()
    render(<Contact personalInfo={personalInfo} />)

    await user.type(screen.getByLabelText(/^name$/i), 'John')
    await user.type(screen.getByLabelText(/^email$/i), 'john@test.com')
    await user.type(screen.getByLabelText(/mobile number/i), '123')
    await user.type(screen.getByLabelText(/^subject$/i), 'Hi')
    await user.type(screen.getByLabelText(/^message$/i), 'Hello')
    await user.click(screen.getByRole('button', { name: /send message/i }))

    expect(await screen.findByText('Enter a valid mobile number')).toBeInTheDocument()
  })

  it('submits form and shows success message', async () => {
    const user = userEvent.setup()
    vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => ({ message: 'Your message has been sent successfully.' }),
    } as Response)

    render(<Contact personalInfo={personalInfo} />)

    await user.type(screen.getByLabelText(/^name$/i), 'John')
    await user.type(screen.getByLabelText(/^email$/i), 'john@test.com')
    await user.type(screen.getByLabelText(/mobile number/i), '9876543210')
    await user.type(screen.getByLabelText(/^subject$/i), 'Project')
    await user.type(screen.getByLabelText(/^message$/i), 'Hello world')
    await user.click(screen.getByRole('button', { name: /send message/i }))

    expect(await screen.findByText('Your message has been sent successfully.')).toBeInTheDocument()
  })

  it('sends phone with country code prefix via handleSubmit', async () => {
    const user = userEvent.setup()
    const fetchSpy = vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => ({ message: 'Sent' }),
    } as Response)

    render(<Contact personalInfo={personalInfo} />)

    await user.type(screen.getByLabelText(/^name$/i), 'Alice')
    await user.type(screen.getByLabelText(/^email$/i), 'alice@test.com')
    await user.type(screen.getByLabelText(/mobile number/i), '7911123456')
    await user.type(screen.getByLabelText(/^subject$/i), 'Hi')
    await user.type(screen.getByLabelText(/^message$/i), 'Hey')
    await user.click(screen.getByRole('button', { name: /send message/i }))

    await waitFor(() => {
      expect(fetchSpy).toHaveBeenCalled()
      const body = JSON.parse(fetchSpy.mock.calls[0][1]?.body as string)
      expect(body.phone).toContain('7911123456')
      expect(body.phone).toMatch(/^\+\d+/)
    })
  })

  it('has country code selector defaulting to +91', () => {
    render(<Contact personalInfo={personalInfo} />)
    const select = screen.getByLabelText(/country code/i) as HTMLSelectElement
    expect(select.value).toBe('+91')
    expect(select.options.length).toBeGreaterThan(5)
  })

  it('shows error message when API call fails', async () => {
    const user = userEvent.setup()
    vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: false,
      json: async () => ({ message: 'Server error' }),
    } as Response)

    render(<Contact personalInfo={personalInfo} />)

    await user.type(screen.getByLabelText(/^name$/i), 'John')
    await user.type(screen.getByLabelText(/^email$/i), 'john@test.com')
    await user.type(screen.getByLabelText(/mobile number/i), '9876543210')
    await user.type(screen.getByLabelText(/^subject$/i), 'Hi')
    await user.type(screen.getByLabelText(/^message$/i), 'Hello')
    await user.click(screen.getByRole('button', { name: /send message/i }))

    expect(await screen.findByText('Server error')).toBeInTheDocument()
  })

  it('resets form after successful submission', async () => {
    const user = userEvent.setup()
    vi.spyOn(globalThis, 'fetch').mockResolvedValueOnce({
      ok: true,
      json: async () => ({ message: 'Sent' }),
    } as Response)

    render(<Contact personalInfo={personalInfo} />)

    const nameInput = screen.getByLabelText(/^name$/i)
    await user.type(nameInput, 'John')
    await user.type(screen.getByLabelText(/^email$/i), 'john@test.com')
    await user.type(screen.getByLabelText(/mobile number/i), '9876543210')
    await user.type(screen.getByLabelText(/^subject$/i), 'Hi')
    await user.type(screen.getByLabelText(/^message$/i), 'Hello')
    await user.click(screen.getByRole('button', { name: /send message/i }))

    await waitFor(() => {
      expect(nameInput).toHaveValue('')
    })
  })

  it('displays personal info in contact sidebar', () => {
    render(<Contact personalInfo={personalInfo} />)

    expect(screen.getByText(personalInfo.email)).toBeInTheDocument()
    expect(screen.getByText(personalInfo.phone)).toBeInTheDocument()
    expect(screen.getByText(personalInfo.location)).toBeInTheDocument()
  })

  it('does not have a verification panel', () => {
    render(<Contact personalInfo={personalInfo} />)

    expect(screen.queryByText(/verification code/i)).not.toBeInTheDocument()
  })
})
