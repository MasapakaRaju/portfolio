import { useCallback, useEffect, useState, type FormEvent, type ReactNode } from 'react'
import type { Experience, PersonalInfo, Project, Skill } from '../../types'
import { API_BASE_URL, clearCachedPortfolio } from '../../api/portfolio'
import './AdminDashboard.css'

type ContactMessage = {
  id: number
  name: string
  email: string
  phone: string
  subject: string
  message: string
  sentAt: string
  isRead: boolean
}

type DashboardData = {
  personalInfo: PersonalInfo
  skills: Skill[]
  experiences: Experience[]
  projects: Project[]
  messages: ContactMessage[]
  summary: { skills: number; experiences: number; projects: number; messages: number; unreadMessages: number }
}

type Section = 'overview' | 'profile' | 'skills' | 'experience' | 'projects' | 'messages'
type SkillDraft = { name: string; category: string; proficiency: number }
type ExperienceDraft = {
  company: string; role: string; location: string; startDate: string; endDate: string
  isCurrent: boolean; description: string; highlights: string; displayOrder: number
}
type ProjectDraft = {
  name: string; description: string; techStack: string; githubUrl: string
  demoUrl: string; isFeatured: boolean; displayOrder: number
}

const emptySkill: SkillDraft = { name: '', category: '', proficiency: 70 }
const emptyExperience: ExperienceDraft = {
  company: '', role: '', location: '', startDate: '', endDate: '', isCurrent: false,
  description: '', highlights: '', displayOrder: 0,
}
const emptyProject: ProjectDraft = {
  name: '', description: '', techStack: '', githubUrl: '', demoUrl: '', isFeatured: false, displayOrder: 0,
}

async function adminRequest<T>(path: string, token: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    cache: 'no-store',
    headers: {
      Authorization: `Bearer ${token}`,
      ...(init.body ? { 'Content-Type': 'application/json' } : {}),
      ...init.headers,
    },
  })
  if (response.status === 204) return undefined as T
  const body = await response.json().catch(() => ({})) as Record<string, unknown>
  if (!response.ok) {
    const error = new Error(typeof body.message === 'string' ? body.message : 'Could not complete this action.') as Error & { status?: number }
    error.status = response.status
    throw error
  }
  return body as T
}

function safeLink(value: string) {
  try {
    const url = new URL(value)
    return url.protocol === 'https:' || url.protocol === 'http:' ? url.href : undefined
  } catch {
    return undefined
  }
}

function formatDate(value: string) {
  if (!value) return '—'
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : new Intl.DateTimeFormat(undefined, { dateStyle: 'medium' }).format(date)
}

interface AdminDashboardProps {
  token: string
  onSignOut: () => void
}

export default function AdminDashboard({ token, onSignOut }: AdminDashboardProps) {
  const [data, setData] = useState<DashboardData | null>(null)
  const [section, setSection] = useState<Section>('overview')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [profileEditing, setProfileEditing] = useState(false)
  const [profileDraft, setProfileDraft] = useState<PersonalInfo | null>(null)
  const [skillDraft, setSkillDraft] = useState<SkillDraft | null>(null)
  const [editingSkillId, setEditingSkillId] = useState<number | null>(null)
  const [experienceDraft, setExperienceDraft] = useState<ExperienceDraft | null>(null)
  const [editingExperienceId, setEditingExperienceId] = useState<number | null>(null)
  const [projectDraft, setProjectDraft] = useState<ProjectDraft | null>(null)
  const [editingProjectId, setEditingProjectId] = useState<number | null>(null)

  const loadDashboard = useCallback(async () => {
    setLoading(true)
    try {
      const dashboard = await adminRequest<DashboardData>('/admin/dashboard', token)
      setData(dashboard)
      setError('')
    } catch (loadError) {
      const status = (loadError as Error & { status?: number }).status
      if (status === 401) {
        onSignOut()
        return
      }
      setError(loadError instanceof Error ? loadError.message : 'Could not load admin data.')
    } finally {
      setLoading(false)
    }
  }, [onSignOut, token])

  useEffect(() => { void loadDashboard() }, [loadDashboard])

  const perform = async (action: () => Promise<unknown>, successMessage: string, reload = true): Promise<boolean> => {
    setBusy(true)
    setError('')
    setNotice('')
    try {
      await action()
      clearCachedPortfolio()
      if (reload) await loadDashboard()
      setNotice(successMessage)
      return true
    } catch (actionError) {
      const status = (actionError as Error & { status?: number }).status
      if (status === 401) {
        onSignOut()
        return false
      }
      setError(actionError instanceof Error ? actionError.message : 'Could not save your changes.')
      return false
    } finally {
      setBusy(false)
    }
  }

  const saveProfile = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!profileDraft) return
    const { id: _id, ...profile } = profileDraft
    void _id
    if (await perform(() => adminRequest('/admin/profile', token, { method: 'PUT', body: JSON.stringify(profile) }), 'Profile saved.')) setProfileEditing(false)
  }

  const saveSkill = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!skillDraft) return
    const path = editingSkillId === null ? '/admin/skills' : `/admin/skills/${editingSkillId}`
    if (await perform(() => adminRequest(path, token, {
      method: editingSkillId === null ? 'POST' : 'PUT', body: JSON.stringify(skillDraft),
    }), editingSkillId === null ? 'Skill added.' : 'Skill updated.')) {
    setSkillDraft(null)
    setEditingSkillId(null)
    }
  }

  const saveExperience = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!experienceDraft) return
    const request = {
      ...experienceDraft,
      endDate: experienceDraft.isCurrent ? '' : experienceDraft.endDate,
      highlights: experienceDraft.highlights.split('\n').map(value => value.trim()).filter(Boolean),
    }
    const path = editingExperienceId === null ? '/admin/experience' : `/admin/experience/${editingExperienceId}`
    if (await perform(() => adminRequest(path, token, {
      method: editingExperienceId === null ? 'POST' : 'PUT', body: JSON.stringify(request),
    }), editingExperienceId === null ? 'Experience added.' : 'Experience updated.')) {
    setExperienceDraft(null)
    setEditingExperienceId(null)
    }
  }

  const saveProject = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (!projectDraft) return
    const path = editingProjectId === null ? '/admin/projects' : `/admin/projects/${editingProjectId}`
    if (await perform(() => adminRequest(path, token, {
      method: editingProjectId === null ? 'POST' : 'PUT', body: JSON.stringify(projectDraft),
    }), editingProjectId === null ? 'Project added.' : 'Project updated.')) {
    setProjectDraft(null)
    setEditingProjectId(null)
    }
  }

  const remove = (label: string, path: string) => {
    if (!window.confirm(`Delete this ${label}? This cannot be undone.`)) return
    void perform(() => adminRequest(path, token, { method: 'DELETE' }), `${label[0].toUpperCase()}${label.slice(1)} deleted.`)
  }

  const navItems: { id: Section; label: string }[] = [
    { id: 'overview', label: 'Overview' }, { id: 'profile', label: 'Profile' },
    { id: 'skills', label: 'Skills' }, { id: 'experience', label: 'Experience' },
    { id: 'projects', label: 'Projects' }, { id: 'messages', label: 'Messages' },
  ]

  if (loading && !data) return <div className="admin-loading"><span className="admin-spinner" />Loading your portfolio data…</div>

  return (
    <div className="admin-dashboard">
      <header className="admin-topbar">
        <a className="admin-dashboard-brand" href={`${import.meta.env.BASE_URL}`}>MR <span>PORTFOLIO ADMIN</span></a>
        <div className="admin-topbar-actions">
          <span className="admin-session-indicator"><span /> Admin session</span>
          <button className="admin-signout" onClick={onSignOut}>Sign out</button>
        </div>
      </header>

      <div className="admin-dashboard-layout">
        <aside className="admin-sidebar" aria-label="Admin sections">
          <p className="admin-sidebar-label">MANAGE PORTFOLIO</p>
          <nav>
            {navItems.map(item => (
              <button key={item.id} className={`admin-nav-item ${section === item.id ? 'selected' : ''}`}
                aria-current={section === item.id ? 'page' : undefined} onClick={() => setSection(item.id)}>
                {item.label}{item.id === 'messages' && data?.summary.unreadMessages ? <span className="admin-unread-count">{data.summary.unreadMessages}</span> : null}
              </button>
            ))}
          </nav>
          <a className="admin-sidebar-back" href={`${import.meta.env.BASE_URL}`}>← View public portfolio</a>
        </aside>

        <main className="admin-main">
          <div className="admin-page-heading">
            <div>
              <p className="admin-eyebrow">PORTFOLIO MANAGEMENT</p>
              <h1>{navItems.find(item => item.id === section)?.label}</h1>
              <p className="admin-page-description">Changes save directly to the portfolio database.</p>
            </div>
            <button className="admin-refresh-button" disabled={busy || loading} onClick={() => void loadDashboard()}>
              {loading ? 'Refreshing…' : 'Refresh data'}
            </button>
          </div>

          {notice && <div className="admin-banner success" role="status">{notice}</div>}
          {error && <div className="admin-banner error" role="alert">{error}</div>}
          {loading && data && <div className="admin-inline-loading">Updating portfolio data…</div>}

          {data && section === 'overview' && <Overview data={data} onSelect={setSection} />}
          {data && section === 'profile' && <ProfileSection data={data} editing={profileEditing} busy={busy}
            draft={profileDraft} setDraft={setProfileDraft} onEdit={() => { setProfileDraft({ ...data.personalInfo }); setProfileEditing(true) }}
            onCancel={() => setProfileEditing(false)} onSave={saveProfile} />}
          {data && section === 'skills' && <SkillsSection data={data} draft={skillDraft} setDraft={setSkillDraft}
            editingId={editingSkillId} setEditingId={setEditingSkillId} busy={busy} onSave={saveSkill} onDelete={remove} />}
          {data && section === 'experience' && <ExperienceSection data={data} draft={experienceDraft} setDraft={setExperienceDraft}
            editingId={editingExperienceId} setEditingId={setEditingExperienceId} busy={busy} onSave={saveExperience} onDelete={remove} />}
          {data && section === 'projects' && <ProjectsSection data={data} draft={projectDraft} setDraft={setProjectDraft}
            editingId={editingProjectId} setEditingId={setEditingProjectId} busy={busy} onSave={saveProject} onDelete={remove} />}
          {data && section === 'messages' && <MessagesSection data={data} token={token} busy={busy} perform={perform} onDelete={remove} />}
        </main>
      </div>
    </div>
  )
}

function Overview({ data, onSelect }: { data: DashboardData; onSelect: (section: Section) => void }) {
  const summaries: { section: Section; label: string; value: number; caption: string }[] = [
    { section: 'skills', label: 'Skills', value: data.summary.skills, caption: 'Technology entries' },
    { section: 'experience', label: 'Experience', value: data.summary.experiences, caption: 'Career entries' },
    { section: 'projects', label: 'Projects', value: data.summary.projects, caption: 'Portfolio projects' },
    { section: 'messages', label: 'Messages', value: data.summary.messages, caption: `${data.summary.unreadMessages} unread` },
  ]
  return (
    <div className="admin-overview">
      <div className="admin-stat-grid">
        {summaries.map(item => <button key={item.section} className="admin-stat-card" onClick={() => onSelect(item.section)}>
          <span>{item.label}</span><strong>{item.value}</strong><small>{item.caption}</small>
        </button>)}
      </div>
      <section className="admin-panel">
        <PanelHeading title="Portfolio profile" description="The information visitors see first." action={<button className="admin-action-button" onClick={() => onSelect('profile')}>Edit profile</button>} />
        <div className="admin-summary-table-wrap"><table className="admin-table"><tbody>
          <InfoRow label="Name" value={data.personalInfo.name} />
          <InfoRow label="Title" value={data.personalInfo.title} />
          <InfoRow label="Location" value={data.personalInfo.location} />
          <InfoRow label="Email" value={data.personalInfo.email} />
          <InfoRow label="Tagline" value={data.personalInfo.tagline} />
        </tbody></table></div>
      </section>
      <section className="admin-panel">
        <PanelHeading title="Quick edits" description="Jump straight to a portfolio section." />
        <div className="admin-quick-links">{(['skills', 'experience', 'projects', 'messages'] as Section[]).map(item =>
          <button key={item} className="admin-quick-link" onClick={() => onSelect(item)}>{item[0].toUpperCase() + item.slice(1)} <span>→</span></button>
        )}</div>
      </section>
    </div>
  )
}

function ProfileSection({ data, editing, busy, draft, setDraft, onEdit, onCancel, onSave }: {
  data: DashboardData; editing: boolean; busy: boolean; draft: PersonalInfo | null
  setDraft: (draft: PersonalInfo | null) => void; onEdit: () => void; onCancel: () => void
  onSave: (event: FormEvent<HTMLFormElement>) => void
}) {
  const fields: { key: Exclude<keyof PersonalInfo, 'id'>; label: string; multiline?: boolean; type?: string }[] = [
    { key: 'name', label: 'Name' }, { key: 'title', label: 'Professional title' },
    { key: 'tagline', label: 'Tagline', multiline: true }, { key: 'location', label: 'Location' },
    { key: 'email', label: 'Email', type: 'email' }, { key: 'phone', label: 'Phone', type: 'tel' },
    { key: 'about', label: 'About', multiline: true }, { key: 'githubUrl', label: 'GitHub URL', type: 'url' },
    { key: 'linkedinUrl', label: 'LinkedIn URL', type: 'url' }, { key: 'leetcodeUrl', label: 'LeetCode URL', type: 'url' },
    { key: 'resumeUrl', label: 'Résumé URL', type: 'url' },
  ]
  return <section className="admin-panel">
    <PanelHeading title="Profile details" description="Edit your public name, links, contact information, and bio."
      action={!editing && <button className="admin-action-button" onClick={onEdit}>Edit profile</button>} />
    {editing && draft ? <form className="admin-editor-grid" onSubmit={onSave}>
      {fields.map(field => <label key={field.key} className={`admin-field ${field.key === 'about' || field.key === 'tagline' ? 'wide' : ''}`}>
        <span>{field.label}</span>
        {field.multiline ? <textarea rows={field.key === 'about' ? 7 : 3} required maxLength={field.key === 'about' ? 10000 : 500}
          value={draft[field.key]} onChange={event => setDraft({ ...draft, [field.key]: event.target.value })} /> :
          <input type={field.type ?? 'text'} required={['name', 'title', 'location', 'email', 'phone'].includes(field.key)}
            maxLength={field.key === 'name' ? 120 : 500} value={draft[field.key]}
            onChange={event => setDraft({ ...draft, [field.key]: event.target.value })} />}
      </label>)}
      <EditorActions busy={busy} onCancel={onCancel} />
    </form> : <div className="admin-summary-table-wrap"><table className="admin-table"><tbody>
      {fields.map(field => <InfoRow key={field.key} label={field.label} value={data.personalInfo[field.key] || '—'} />)}
    </tbody></table></div>}
  </section>
}

function SkillsSection({ data, draft, setDraft, editingId, setEditingId, busy, onSave, onDelete }: {
  data: DashboardData; draft: SkillDraft | null; setDraft: (draft: SkillDraft | null) => void
  editingId: number | null; setEditingId: (id: number | null) => void; busy: boolean
  onSave: (event: FormEvent<HTMLFormElement>) => void; onDelete: (label: string, path: string) => void
}) {
  const beginEdit = (skill: Skill) => {
    setEditingId(skill.id)
    setDraft({ name: skill.name, category: skill.category, proficiency: skill.proficiency })
  }
  return <div className="admin-section-stack">
    <section className="admin-panel">
      <PanelHeading title="Skills" description="Add skills and adjust their proficiency bars." action={!draft && <button className="admin-action-button" onClick={() => { setEditingId(null); setDraft({ ...emptySkill }) }}>+ Add skill</button>} />
      {draft && <SkillEditor draft={draft} setDraft={setDraft} busy={busy} onSave={onSave} onCancel={() => { setDraft(null); setEditingId(null) }} editing={editingId !== null} />}
      <div className="admin-table-scroll"><table className="admin-table admin-data-table"><thead><tr><th>Skill</th><th>Category</th><th>Proficiency</th><th>Actions</th></tr></thead><tbody>
        {data.skills.map(skill => <tr key={skill.id}><td className="strong-cell">{skill.name}</td><td>{skill.category}</td><td><Meter value={skill.proficiency} /></td>
          <td><RowActions onEdit={() => beginEdit(skill)} onDelete={() => onDelete('skill', `/admin/skills/${skill.id}`)} /></td></tr>)}
        {!data.skills.length && <EmptyRow columns={4} text="No skills yet. Add your first skill above." />}
      </tbody></table></div>
    </section>
  </div>
}

function SkillEditor({ draft, setDraft, busy, onSave, onCancel, editing }: {
  draft: SkillDraft; setDraft: (draft: SkillDraft | null) => void; busy: boolean
  onSave: (event: FormEvent<HTMLFormElement>) => void; onCancel: () => void; editing: boolean
}) {
  return <form className="admin-inline-editor" onSubmit={onSave}>
    <div className="admin-editor-grid compact">
      <label className="admin-field"><span>Skill name</span><input required maxLength={100} value={draft.name} onChange={event => setDraft({ ...draft, name: event.target.value })} /></label>
      <label className="admin-field"><span>Category</span><input required maxLength={100} placeholder="e.g. Backend" value={draft.category} onChange={event => setDraft({ ...draft, category: event.target.value })} /></label>
      <label className="admin-field wide"><span>Proficiency: {draft.proficiency}%</span>
        <input className="admin-range" type="range" min={0} max={100} value={draft.proficiency} onChange={event => setDraft({ ...draft, proficiency: Number(event.target.value) })} />
        <Meter value={draft.proficiency} /></label>
    </div>
    <EditorActions busy={busy} onCancel={onCancel} saveLabel={editing ? 'Save skill' : 'Add skill'} />
  </form>
}

function ExperienceSection({ data, draft, setDraft, editingId, setEditingId, busy, onSave, onDelete }: {
  data: DashboardData; draft: ExperienceDraft | null; setDraft: (draft: ExperienceDraft | null) => void
  editingId: number | null; setEditingId: (id: number | null) => void; busy: boolean
  onSave: (event: FormEvent<HTMLFormElement>) => void; onDelete: (label: string, path: string) => void
}) {
  const beginEdit = (experience: Experience) => {
    setEditingId(experience.id)
    setDraft({ company: experience.company, role: experience.role, location: experience.location,
      startDate: experience.startDate, endDate: experience.endDate ?? '', isCurrent: experience.isCurrent,
      description: experience.description, highlights: experience.highlights.join('\n'), displayOrder: experience.displayOrder })
  }
  return <section className="admin-panel">
    <PanelHeading title="Work experience" description="Manage roles, descriptions, dates, and highlights." action={!draft && <button className="admin-action-button" onClick={() => { setEditingId(null); setDraft({ ...emptyExperience }) }}>+ Add experience</button>} />
    {draft && <ExperienceEditor draft={draft} setDraft={setDraft} busy={busy} onSave={onSave} onCancel={() => { setDraft(null); setEditingId(null) }} editing={editingId !== null} />}
    <div className="admin-table-scroll"><table className="admin-table admin-data-table"><thead><tr><th>Role</th><th>Company</th><th>Dates</th><th>Highlights</th><th>Actions</th></tr></thead><tbody>
      {data.experiences.map(experience => <tr key={experience.id}><td className="strong-cell">{experience.role}</td><td>{experience.company}</td>
        <td>{experience.startDate} — {experience.isCurrent ? 'Present' : experience.endDate}</td><td>{experience.highlights.length}</td>
        <td><RowActions onEdit={() => beginEdit(experience)} onDelete={() => onDelete('experience', `/admin/experience/${experience.id}`)} /></td></tr>)}
      {!data.experiences.length && <EmptyRow columns={5} text="No experience entries yet." />}
    </tbody></table></div>
  </section>
}

function ExperienceEditor({ draft, setDraft, busy, onSave, onCancel, editing }: {
  draft: ExperienceDraft; setDraft: (draft: ExperienceDraft | null) => void; busy: boolean
  onSave: (event: FormEvent<HTMLFormElement>) => void; onCancel: () => void; editing: boolean
}) {
  const update = (key: keyof ExperienceDraft, value: string | number | boolean) => setDraft({ ...draft, [key]: value } as ExperienceDraft)
  return <form className="admin-inline-editor" onSubmit={onSave}><div className="admin-editor-grid compact">
    <Field label="Role"><input required maxLength={180} value={draft.role} onChange={event => update('role', event.target.value)} /></Field>
    <Field label="Company"><input required maxLength={180} value={draft.company} onChange={event => update('company', event.target.value)} /></Field>
    <Field label="Location"><input required maxLength={180} value={draft.location} onChange={event => update('location', event.target.value)} /></Field>
    <Field label="Start date"><input required maxLength={100} placeholder="e.g. Jan 2025" value={draft.startDate} onChange={event => update('startDate', event.target.value)} /></Field>
    <Field label="End date"><input disabled={draft.isCurrent} maxLength={100} placeholder="e.g. Present" value={draft.isCurrent ? '' : draft.endDate} onChange={event => update('endDate', event.target.value)} /></Field>
    <Field label="Display order"><input type="number" min={0} value={draft.displayOrder} onChange={event => update('displayOrder', Number(event.target.value))} /></Field>
    <label className="admin-check-field wide"><input type="checkbox" checked={draft.isCurrent} onChange={event => update('isCurrent', event.target.checked)} /> This is my current role</label>
    <Field label="Description" wide><textarea required rows={4} maxLength={10000} value={draft.description} onChange={event => update('description', event.target.value)} /></Field>
    <Field label="Highlights (one per line)" wide><textarea rows={5} maxLength={10000} value={draft.highlights} onChange={event => update('highlights', event.target.value)} /></Field>
  </div><EditorActions busy={busy} onCancel={onCancel} saveLabel={editing ? 'Save experience' : 'Add experience'} /></form>
}

function ProjectsSection({ data, draft, setDraft, editingId, setEditingId, busy, onSave, onDelete }: {
  data: DashboardData; draft: ProjectDraft | null; setDraft: (draft: ProjectDraft | null) => void
  editingId: number | null; setEditingId: (id: number | null) => void; busy: boolean
  onSave: (event: FormEvent<HTMLFormElement>) => void; onDelete: (label: string, path: string) => void
}) {
  const beginEdit = (project: Project) => {
    setEditingId(project.id)
    setDraft({ name: project.name, description: project.description, techStack: project.techStack,
      githubUrl: project.githubUrl, demoUrl: project.demoUrl, isFeatured: project.isFeatured, displayOrder: project.displayOrder })
  }
  return <section className="admin-panel">
    <PanelHeading title="Projects" description="Add a project with its stack, repository, and demo links." action={!draft && <button className="admin-action-button" onClick={() => { setEditingId(null); setDraft({ ...emptyProject }) }}>+ Add project</button>} />
    {draft && <ProjectEditor draft={draft} setDraft={setDraft} busy={busy} onSave={onSave} onCancel={() => { setDraft(null); setEditingId(null) }} editing={editingId !== null} />}
    <div className="admin-table-scroll"><table className="admin-table admin-data-table"><thead><tr><th>Project</th><th>Tech stack</th><th>Links</th><th>Featured</th><th>Actions</th></tr></thead><tbody>
      {data.projects.map(project => <tr key={project.id}><td className="strong-cell">{project.name}</td><td>{project.techStack}</td>
        <td className="admin-project-links">{safeLink(project.githubUrl) && <a href={safeLink(project.githubUrl)} target="_blank" rel="noopener noreferrer">GitHub</a>}
          {safeLink(project.demoUrl) && <a href={safeLink(project.demoUrl)} target="_blank" rel="noopener noreferrer">Demo</a>}
          {!safeLink(project.githubUrl) && !safeLink(project.demoUrl) && '—'}</td><td>{project.isFeatured ? 'Featured' : '—'}</td>
        <td><RowActions onEdit={() => beginEdit(project)} onDelete={() => onDelete('project', `/admin/projects/${project.id}`)} /></td></tr>)}
      {!data.projects.length && <EmptyRow columns={5} text="No projects yet. Add a project above." />}
    </tbody></table></div>
  </section>
}

function ProjectEditor({ draft, setDraft, busy, onSave, onCancel, editing }: {
  draft: ProjectDraft; setDraft: (draft: ProjectDraft | null) => void; busy: boolean
  onSave: (event: FormEvent<HTMLFormElement>) => void; onCancel: () => void; editing: boolean
}) {
  const update = (key: keyof ProjectDraft, value: string | number | boolean) => setDraft({ ...draft, [key]: value } as ProjectDraft)
  return <form className="admin-inline-editor" onSubmit={onSave}><div className="admin-editor-grid compact">
    <Field label="Project name"><input required maxLength={180} value={draft.name} onChange={event => update('name', event.target.value)} /></Field>
    <Field label="Tech stack" hint="Separate technologies with commas."><input required maxLength={1000} placeholder="Java, Spring Boot, PostgreSQL" value={draft.techStack} onChange={event => update('techStack', event.target.value)} /></Field>
    <Field label="GitHub URL"><input type="url" maxLength={500} value={draft.githubUrl} onChange={event => update('githubUrl', event.target.value)} /></Field>
    <Field label="Live demo URL"><input type="url" maxLength={500} value={draft.demoUrl} onChange={event => update('demoUrl', event.target.value)} /></Field>
    <Field label="Description" wide><textarea required rows={5} maxLength={10000} value={draft.description} onChange={event => update('description', event.target.value)} /></Field>
    <Field label="Display order"><input type="number" min={0} value={draft.displayOrder} onChange={event => update('displayOrder', Number(event.target.value))} /></Field>
    <label className="admin-check-field"><input type="checkbox" checked={draft.isFeatured} onChange={event => update('isFeatured', event.target.checked)} /> Feature this project</label>
  </div><EditorActions busy={busy} onCancel={onCancel} saveLabel={editing ? 'Save project' : 'Add project'} /></form>
}

function MessagesSection({ data, token, busy, perform, onDelete }: {
  data: DashboardData; token: string; busy: boolean
  perform: (action: () => Promise<unknown>, successMessage: string, reload?: boolean) => Promise<boolean>
  onDelete: (label: string, path: string) => void
}) {
  return <section className="admin-panel">
    <PanelHeading title="Contact messages" description="Messages submitted through the public contact form. New submissions continue to notify your existing contact Slack webhook." />
    <div className="admin-table-scroll"><table className="admin-table admin-data-table"><thead><tr><th>Status</th><th>From</th><th>Subject</th><th>Received</th><th>Actions</th></tr></thead><tbody>
      {data.messages.map(message => <tr key={message.id} className={!message.isRead ? 'unread-row' : ''}>
        <td><span className={`admin-read-badge ${message.isRead ? 'read' : 'unread'}`}>{message.isRead ? 'Read' : 'New'}</span></td>
        <td><span className="strong-cell">{message.name}</span><small className="admin-cell-subtext">{message.email} · {message.phone}</small></td>
        <td><details className="admin-message-details"><summary>{message.subject}</summary><p>{message.message}</p></details></td>
        <td>{formatDate(message.sentAt)}</td>
        <td><RowActions editLabel={message.isRead ? 'Mark new' : 'Mark read'} onEdit={() => void perform(
          () => adminRequest(`/admin/messages/${message.id}/read`, token, { method: 'PATCH', body: JSON.stringify({ isRead: !message.isRead }) }),
          message.isRead ? 'Message marked as new.' : 'Message marked as read.',
        )} onDelete={() => onDelete('message', `/admin/messages/${message.id}`)} /></td>
      </tr>)}
      {!data.messages.length && <EmptyRow columns={5} text="No contact messages yet." />}
    </tbody></table></div>
    {busy && <p className="admin-inline-loading">Saving…</p>}
  </section>
}

function PanelHeading({ title, description, action }: { title: string; description: string; action?: ReactNode }) {
  return <div className="admin-panel-heading"><div><h2>{title}</h2><p>{description}</p></div>{action}</div>
}

function InfoRow({ label, value }: { label: string; value: string }) {
  const link = safeLink(value)
  return <tr><th scope="row">{label}</th><td>{link ? <a className="admin-safe-link" href={link} target="_blank" rel="noopener noreferrer">{value}</a> : value}</td></tr>
}

function Field({ label, hint, wide, children }: { label: string; hint?: string; wide?: boolean; children: ReactNode }) {
  return <label className={`admin-field ${wide ? 'wide' : ''}`}><span>{label}</span>{children}{hint && <small>{hint}</small>}</label>
}

function EditorActions({ busy, onCancel, saveLabel = 'Save changes' }: { busy: boolean; onCancel: () => void; saveLabel?: string }) {
  return <div className="admin-editor-actions wide"><button type="button" className="admin-cancel-button" disabled={busy} onClick={onCancel}>Cancel</button>
    <button type="submit" className="admin-action-button" disabled={busy}>{busy ? 'Saving…' : saveLabel}</button></div>
}

function Meter({ value }: { value: number }) {
  return <div className="admin-meter"><span><i style={{ width: `${Math.max(0, Math.min(100, value))}%` }} /></span><b>{value}%</b></div>
}

function RowActions({ onEdit, onDelete, editLabel = 'Edit' }: { onEdit: () => void; onDelete: () => void; editLabel?: string }) {
  return <div className="admin-row-actions"><button className="admin-row-button" onClick={onEdit}>{editLabel}</button><button className="admin-row-button danger" onClick={onDelete}>Delete</button></div>
}

function EmptyRow({ columns, text }: { columns: number; text: string }) {
  return <tr><td className="admin-empty-row" colSpan={columns}>{text}</td></tr>
}
