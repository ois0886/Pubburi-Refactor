import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useAdminStore } from './admin'
import { api } from '../services/api'

vi.mock('../services/api', () => ({
  api: {
    adminOrders: vi.fn(),
    adminComments: vi.fn(),
    adminUsers: vi.fn(),
    completeOrder: vi.fn(),
    deleteOrder: vi.fn(),
    deleteComment: vi.fn(),
    deleteUser: vi.fn(),
  },
}))

const page = (items = []) => ({ items, page: 1, size: 20, total: items.length, totalPages: 1, hasNext: false })

describe('admin store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    api.adminOrders.mockResolvedValue(page([{ oId: 1 }]))
    api.adminComments.mockResolvedValue(page([{ id: 1 }]))
    api.adminUsers.mockResolvedValue(page([{ id: 'id01' }]))
  })

  it('loads only the active admin tab data', async () => {
    const admin = useAdminStore()
    admin.tab = 'comments'

    await admin.loadActive({ page: 2 })

    expect(api.adminComments).toHaveBeenCalledWith({ page: 2, size: 20 })
    expect(api.adminOrders).not.toHaveBeenCalled()
    expect(api.adminUsers).not.toHaveBeenCalled()
  })

  it('reloads only orders after completing an order', async () => {
    const admin = useAdminStore()
    api.completeOrder.mockResolvedValue(true)

    await admin.completeOrder(1)

    expect(api.completeOrder).toHaveBeenCalledWith(1)
    expect(api.adminOrders).toHaveBeenCalledTimes(1)
    expect(api.adminComments).not.toHaveBeenCalled()
    expect(api.adminUsers).not.toHaveBeenCalled()
  })
})
