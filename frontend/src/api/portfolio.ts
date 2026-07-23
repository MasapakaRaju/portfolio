import type { ContactForm, PortfolioData } from '../types';

const RAW_BASE = import.meta.env.VITE_API_URL || '';
const BASE_URL = RAW_BASE ? RAW_BASE.replace(/\/+$/, '') + '/api' : '/api';

if (!RAW_BASE && import.meta.env.PROD) {
  console.error('[portfolio] VITE_API_URL is not set — API calls will fail in production.');
}
const PORTFOLIO_CACHE_KEY = 'portfolio:data';
const DEFAULT_RETRIES = 6;
const REQUEST_TIMEOUT_MS = 30000;

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
        await sleep(3000 * (attempt + 1));
      }
    }
  }

  throw lastError instanceof Error ? lastError : new Error('Failed to fetch portfolio data');
}

export function getCachedPortfolio(): PortfolioData | null {
  try {
    const cached = window.localStorage.getItem(PORTFOLIO_CACHE_KEY);
    return cached ? JSON.parse(cached) as PortfolioData : null;
  } catch {
    return null;
  }
}

export function cachePortfolio(data: PortfolioData) {
  try {
    window.localStorage.setItem(PORTFOLIO_CACHE_KEY, JSON.stringify(data));
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
