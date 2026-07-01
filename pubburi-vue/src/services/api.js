const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api'

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {}),
    },
    ...options,
  })

  if (response.status === 204) {
    return null
  }

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    const message = data?.message || response.statusText || 'Request failed'
    throw new Error(message)
  }

  return data
}

export const api = {
  me: () => request('/auth/me'),
  login: (payload) => request('/auth/login', { method: 'POST', body: JSON.stringify(payload) }),
  logout: () => request('/auth/logout', { method: 'POST' }),
  join: (payload) => request('/users', { method: 'POST', body: JSON.stringify(payload) }),
  idAvailable: (id) => request(`/users/id-available?id=${encodeURIComponent(id)}`),
  profile: (id) => request(`/users/${encodeURIComponent(id)}/profile`),

  products: (params = {}) => request(`/products?${new URLSearchParams(params)}`),
  popularProducts: () => request('/products/popular'),
  product: (id) => request(`/products/${id}`),
  createProduct: (payload) => request('/admin/products', { method: 'POST', body: JSON.stringify(payload) }),
  updateProduct: (id, payload) => request(`/admin/products/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteProduct: (id) => request(`/admin/products/${id}`, { method: 'DELETE' }),

  comments: (productId) => request(`/products/${productId}/comments`),
  createComment: (payload) => request('/comments', { method: 'POST', body: JSON.stringify(payload) }),
  updateComment: (id, payload) => request(`/comments/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteComment: (id) => request(`/comments/${id}`, { method: 'DELETE' }),
  adminComments: () => request('/admin/comments'),

  markets: () => request('/markets'),
  createMarket: (payload) => request('/admin/markets', { method: 'POST', body: JSON.stringify(payload) }),
  updateMarket: (id, payload) => request(`/admin/markets/${id}`, { method: 'PUT', body: JSON.stringify(payload) }),
  deleteMarket: (id) => request(`/admin/markets/${id}`, { method: 'DELETE' }),

  createOrder: (payload) => request('/orders', { method: 'POST', body: JSON.stringify(payload) }),
  userOrders: (userId) => request(`/users/${encodeURIComponent(userId)}/orders`),
  adminOrders: () => request('/admin/orders'),
  completeOrder: (id) => request(`/admin/orders/${id}/complete`, { method: 'PATCH' }),
  deleteOrder: (id) => request(`/admin/orders/${id}`, { method: 'DELETE' }),

  adminUsers: () => request('/admin/users'),
}
