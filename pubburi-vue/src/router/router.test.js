import { describe, expect, it } from 'vitest'
import router from './index'

describe('router', () => {
  it('registers customer and admin routes', () => {
    const names = router.getRoutes().map((route) => route.name)

    expect(names).toEqual(expect.arrayContaining(['home', 'products', 'product-detail', 'cart', 'account', 'profile', 'admin']))
  })

  it('resolves product detail routes', () => {
    expect(router.resolve('/products/1').name).toBe('product-detail')
  })
})
