export function productImage(product) {
  if (!product?.img) return '/images/icon/mainIcon.png'
  return product.img.startsWith('/') ? product.img : `/images/pub/${product.img}`
}

export function webpSource(src) {
  if (!src || !/\.(png|jpe?g)$/i.test(src)) return ''
  return src.replace(/\.(png|jpe?g)$/i, '.webp')
}

export function money(value) {
  return Number(value || 0).toLocaleString('ko-KR')
}

export function date(value) {
  return value ? new Date(value).toLocaleString('ko-KR') : ''
}
