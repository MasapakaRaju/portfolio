import { vi } from 'vitest'
import '@testing-library/jest-dom/vitest'

class MockIntersectionObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

Object.defineProperty(globalThis, 'IntersectionObserver', {
  writable: true,
  value: MockIntersectionObserver,
})

Object.defineProperty(globalThis, 'fetch', {
  writable: true,
  value: vi.fn(),
})
