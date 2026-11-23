import type { ContactForm, PortfolioData } from '../types';

const RAW_BASE = import.meta.env.VITE_API_URL || '';
const BASE_URL = RAW_BASE ? RAW_BASE.replace(/\/+$/, '') + '/api' : '/api';

if (!RAW_BASE && import.meta.env.PROD) {
  console.error('[portfolio] VITE_API_URL is not set — API calls will fail in production.');
}
const PORTFOLIO_CACHE_KEY = 'portfolio:data';
export const PORTFOLIO_CACHE_TTL_MS = 5 * 60 * 1000;
const DEFAULT_RETRIES = 3;
// Render may need up to a couple of minutes to wake a sleeping service.
const REQUEST_TIMEOUT_MS = 120000;

function sleep(ms: number) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function fetchWithTimeout(url: string, options?: RequestInit) {
  const controller = new AbortController();
  const timeout = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  try {
    return await fetch(url, {
      ...options,
      signal: controller.signal,
    });
  } finally {
    window.clearTimeout(timeout);
  }
}

export async function fetchPortfolio(): Promise<PortfolioData> {
  let lastError: unknown;

  for (let attempt = 0; attempt <= DEFAULT_RETRIES; attempt += 1) {
    try {
      const res = await fetchWithTimeout(`${BASE_URL}/portfolio`);
      if (!res.ok) throw new Error('Failed to fetch portfolio data');
      return res.json();
    } catch (error) {
      lastError = error;
      if (attempt < DEFAULT_RETRIES) {
        await sleep(Math.min(2000 * (2 ** attempt), 15000));
      }
    }
  }

  throw lastError instanceof Error ? lastError : new Error('Failed to fetch portfolio data');
}

type CachedPortfolio = {
  data: PortfolioData;
  cachedAt: number;
};

export function getCachedPortfolio(): CachedPortfolio | null {
  try {
    const raw = window.sessionStorage.getItem(PORTFOLIO_CACHE_KEY);
    if (!raw) return null;

    const cached = JSON.parse(raw) as Partial<CachedPortfolio>;
    if (!cached.data || typeof cached.cachedAt !== 'number') return null;
    return cached as CachedPortfolio;
  } catch {
    return null;
  }
}

export function cachePortfolio(data: PortfolioData) {
  try {
    window.sessionStorage.setItem(PORTFOLIO_CACHE_KEY, JSON.stringify({
      data,
      cachedAt: Date.now(),
    } satisfies CachedPortfolio));
  } catch {
    // Ignore storage failures; live API data can still render the app.
  }
}

export async function submitContact(data: ContactForm): Promise<{ message: string }> {
  const res = await fetch(`${BASE_URL}/contact`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });

  let body: Record<string, unknown>;
  try {
    body = (await res.json()) as Record<string, unknown>;
  } catch {
    throw new Error('Backend is unavailable. Please try again later.');
  }

  if (!res.ok) {
    throw new Error(
      typeof body.message === 'string' && body.message
        ? body.message
        : 'Failed to send message. Please try again.'
    );
  }
  return body as { message: string };
}
