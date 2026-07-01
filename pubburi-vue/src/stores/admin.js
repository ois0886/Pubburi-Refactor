import { defineStore } from 'pinia'
import { api } from '../services/api'

const emptyPage = (size = 20) => ({ items: [], page: 1, size, total: 0, totalPages: 0, hasNext: false })

export const adminTabs = [
  { key: 'products', label: '상품' },
  { key: 'markets', label: '매장' },
  { key: 'orders', label: '주문' },
  { key: 'comments', label: '댓글' },
  { key: 'users', label: '회원' },
]

export const useAdminStore = defineStore('admin', {
  state: () => ({
    tab: 'products',
    ordersPage: emptyPage(),
    commentsPage: emptyPage(),
    usersPage: emptyPage(),
  }),
  getters: {
    orders: (state) => state.ordersPage.items,
    comments: (state) => state.commentsPage.items,
    users: (state) => state.usersPage.items,
  },
  actions: {
    async load(params = {}) {
      const page = { page: 1, size: 20, ...params }
      const [orders, comments, users] = await Promise.all([
        api.adminOrders(page),
        api.adminComments(page),
        api.adminUsers(page),
      ])
      this.ordersPage = orders
      this.commentsPage = comments
      this.usersPage = users
    },
    async completeOrder(id) {
      await api.completeOrder(id)
      await this.load()
    },
    async deleteOrder(id) {
      await api.deleteOrder(id)
      await this.load()
    },
    async deleteComment(id) {
      await api.deleteComment(id)
      await this.load()
    },
    async deleteUser(id) {
      await api.deleteUser(id)
      await this.load()
    },
  },
})
