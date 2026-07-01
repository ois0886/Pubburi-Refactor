import { afterEach, describe, expect, it, vi } from 'vitest'
import { ApiClientError, api, pageQuery } from './api'

afterEach(() => {
  vi.restoreAllMocks()
})

function mockResponse(body, ok = true, status = 200) {
  global.fetch = vi.fn().mockResolvedValue({
    ok,
    status,
    statusText: ok ? 'OK' : 'Bad Request',
    text: () => Promise.resolve(JSON.stringify(body)),
  })
}

describe('api client', () => {
  it('unwraps successful ApiResponse data', async () => {
    mockResponse({ data: { id: 'id01' }, message: 'ok', error: null })

    await expect(api.me()).resolves.toEqual({ id: 'id01' })
  })

  it('throws structured ApiClientError on wrapped errors', async () => {
    mockResponse(
      {
        data: null,
        message: null,
        error: { code: 'VALIDATION_FAILED', message: 'Invalid request', fields: { id: 'required' } },
      },
      false,
      400,
    )

    await expect(api.me()).rejects.toMatchObject({
      name: 'ApiClientError',
      status: 400,
      code: 'VALIDATION_FAILED',
      fields: { id: 'required' },
    })
  })

  it('builds compact page query strings', () => {
    expect(pageQuery({ page: 2, size: 12, q: '', type: '탁주' })).toBe('page=2&size=12&type=%ED%83%81%EC%A3%BC')
  })
})
