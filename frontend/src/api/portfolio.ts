import type { ContactForm, PortfolioData } from '../types';

const BASE_URL = import.meta.env.VITE_API_URL || '/api';

export async function fetchPortfolio(): Promise<PortfolioData> {
  const res = await fetch(`${BASE_URL}/portfolio`);
  if (!res.ok) throw new Error('Failed to fetch portfolio data');
  return res.json();
}

export async function submitContact(data: ContactForm): Promise<{ message: string }> {
  const res = await fetch(`${BASE_URL}/contact`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  if (!res.ok) {
    const err = await res.json();
    throw new Error(err.message || 'Failed to send message');
  }
  return res.json();
}
