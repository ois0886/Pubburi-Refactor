import { defineStore } from 'pinia'
import { api } from '../services/api'

const emptyPage = (size = 12) => ({ items: [], page: 1, size, total: 0, totalPages: 0, hasNext: false })

export const carouselImages = [
  '/images/carousel/banner1.png',
  '/images/carousel/banner2.png',
  '/images/carousel/banner3.png',
  '/images/carousel/banner4.png',
  '/images/carousel/banner5.png',
  '/images/carousel/banner6.png',
]

export const categories = [
  { image: '/images/category/탁주.png', label: '탁주' },
  { image: '/images/category/청주.png', label: '약 · 청주' },
  { image: '/images/category/과실주.png', label: '과실주' },
  { image: '/images/category/증류주.png', label: '증류주' },
  { image: '/images/category/맥주.png', label: '맥주' },
  { image: '/images/category/양주.png', label: '양주' },
  { image: '/images/category/위스키.png', label: '위스키' },
  { image: '/images/category/사케.png', label: '사케' },
]

export const useCatalogStore = defineStore('catalog', {
  state: () => ({
    productsPage: emptyPage(12),
    popularProducts: [],
    marketsPage: emptyPage(12),
    selectedProduct: null,
    commentsPage: emptyPage(10),
    filters: { type: '', q: '', sort: 'popular', page: 1, size: 12 },
  }),
  getters: {
    products: (state) => state.productsPage.items,
    markets: (state) => state.marketsPage.items,
    comments: (state) => state.commentsPage.items,
  },
  actions: {
    async loadProducts(params = {}) {
      this.filters = { ...this.filters, ...params }
      this.productsPage = await api.products(this.filters)
      return this.productsPage
    },
    async loadPopularProducts() {
      this.popularProducts = await api.popularProducts()
      return this.popularProducts
    },
    async loadMarkets(params = {}) {
      this.marketsPage = await api.markets({ page: 1, size: 12, ...params })
      return this.marketsPage
    },
    async loadProduct(id) {
      this.selectedProduct = await api.product(id)
      return this.selectedProduct
    },
    async loadComments(productId, params = {}) {
      this.commentsPage = await api.comments(productId, { page: 1, size: 10, ...params })
      return this.commentsPage
    },
    async createComment(payload) {
      const comment = await api.createComment(payload)
      await this.loadComments(payload.productId)
      return comment
    },
    async deleteComment(commentId) {
      await api.deleteComment(commentId)
      this.commentsPage = { ...this.commentsPage, items: this.comments.filter((item) => item.id !== commentId) }
    },
  },
})
