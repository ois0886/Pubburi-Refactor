const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

export class ApiClientError extends Error {
  constructor(error, status) {
    super(error?.message || 'Request failed')
    this.name = 'ApiClientError'
    this.status = status
    this.code = error?.code || 'REQUEST_FAILED'
    this.fields = error?.fields || {}
  }
}

export function pageQuery(params = {}, defaults = {}) {
  const query = new URLSearchParams()
  const merged = { ...defaults, ...params }
  Object.entries(merged).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.set(key, value)
    }
  })
  return query.toString()
}

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  })

  const text = await response.text()
  const envelope = text ? JSON.parse(text) : null
  const hasEnvelope = envelope && Object.prototype.hasOwnProperty.call(envelope, 'data')

  if (!response.ok || envelope?.error) {
    const error = envelope?.error || { code: response.statusText, message: response.statusText }
    throw new ApiClientError(error, response.status)
  }

  return hasEnvelope ? envelope.data : envelope
}

export const api = {
  me: () => request('/auth/me'),
  login: (payload) => request('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),
  logout: () => request('/auth/logout', { method: 'POST' }),
  join: (payload) => request('/users', { method: 'POST', body: JSON.stringify(payload) }),
  idAvailable: (id) => request(`/users/id-available?id=${encodeURIComponent(id)}`),
  profile: (id) => request(`/users/${encodeURIComponent(id)}/profile`),

  products: (params = {}) => request(`/products?${pageQuery(params, { page: 1, size: 12 })}`),
  popularProducts: () => request('/products/popular'),
  product: (id) => request(`/products/${id}`),
  createProduct: (payload) => request('/admin/products', { method: 'POST', body: JSON.stringify(payload) }),
  updateProduct: (id, payload) => request(`/admin/products/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteProduct: (id) => request(`/admin/products/${id}`, { method: 'DELETE' }),

  comments: (productId, params = {}) =>
    request(`/products/${productId}/comments?${pageQuery(params, { page: 1, size: 10 })}`),
  createComment: (payload) => request('/comments', { method: 'POST', body: JSON.stringify(payload) }),
  updateComment: (id, payload) => request(`/comments/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteComment: (id) => request(`/comments/${id}`, { method: 'DELETE' }),
  adminComments: (params = {}) => request(`/admin/comments?${pageQuery(params, { page: 1, size: 20 })}`),

  markets: (params = {}) => request(`/markets?${pageQuery(params, { page: 1, size: 12 })}`),
  createMarket: (payload) => request('/admin/markets', { method: 'POST', body: JSON.stringify(payload) }),
  updateMarket: (id, payload) => request(`/admin/markets/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteMarket: (id) => request(`/admin/markets/${id}`, { method: 'DELETE' }),

  createOrder: (payload) => request('/orders', { method: 'POST', body: JSON.stringify(payload) }),
  userOrders: (userId, params = {}) =>
    request(`/users/${encodeURIComponent(userId)}/orders?${pageQuery(params, { page: 1, size: 10 })}`),
  adminOrders: (params = {}) => request(`/admin/orders?${pageQuery(params, { page: 1, size: 20 })}`),
  completeOrder: (id) => request(`/admin/orders/${id}/complete`, { method: 'PATCH' }),
  deleteOrder: (id) => request(`/admin/orders/${id}`, { method: 'DELETE' }),

  adminUsers: (params = {}) => request(`/admin/users?${pageQuery(params, { page: 1, size: 20 })}`),
  updateUser: (id, payload) => request(`/admin/users/${encodeURIComponent(id)}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteUser: (id) => request(`/admin/users/${encodeURIComponent(id)}`, { method: 'DELETE' }),
}
