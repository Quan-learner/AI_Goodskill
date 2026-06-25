/**
 * Validates a product image URL. If it's a local filesystem path,
 * points to local MinIO (which might be unreachable or empty),
 * or is invalid/null, it falls back to a high-quality name-matched image.
 * 
 * @param {string} url - The image URL to validate
 * @param {string} name - The product name to match for fallback category
 * @returns {string} The validated URL or a fallback placeholder URL
 */
export const formatProductImage = (url, name) => {
  const defaultImg = 'https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=400&h=300&fit=crop'
  
  if (!url || typeof url !== 'string') {
    return getFallbackImageByName(name, defaultImg)
  }
  
  const trimmed = url.trim()
  
  // Check if it is a local file path (e.g., /Users/..., /doc/..., C:\..., or relative paths)
  if (
    trimmed.startsWith('/Users/') || 
    trimmed.startsWith('/') && !trimmed.startsWith('http') || 
    /^[a-zA-Z]:\\/.test(trimmed) || 
    trimmed.includes('\\')
  ) {
    return getFallbackImageByName(name, defaultImg)
  }
  
  // Check if it's pointing to localhost/127.0.0.1 MinIO which won't resolve correctly or be empty
  if (trimmed.includes('localhost:19000') || trimmed.includes('127.0.0.1:19000')) {
    return getFallbackImageByName(name, defaultImg)
  }
  
  return trimmed
}

/**
 * Gets a beautiful fallback image based on the product name keywords.
 */
const getFallbackImageByName = (name, defaultImg) => {
  if (!name) return defaultImg
  const lower = name.toLowerCase()
  if (lower.includes('iphone') || lower.includes('苹果') || lower.includes('phone') || lower.includes('手机')) {
    return 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop'
  }
  if (lower.includes('ipad') || lower.includes('平板') || lower.includes('tablet')) {
    return 'https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=300&fit=crop'
  }
  if (lower.includes('小米') || lower.includes('红米') || lower.includes('xiaomi') || lower.includes('redmi')) {
    return 'https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=400&h=300&fit=crop'
  }
  if (lower.includes('macbook') || lower.includes('笔记本') || lower.includes('电脑') || lower.includes('laptop')) {
    return 'https://images.unsplash.com/photo-1496181130204-7552cc14b1e0?w=400&h=300&fit=crop'
  }
  if (lower.includes('耳机') || lower.includes('airpods') || lower.includes('earphone') || lower.includes('headphone')) {
    return 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=300&fit=crop'
  }
  if (lower.includes('oppo') || lower.includes('vivo') || lower.includes('华为') || lower.includes('huawei')) {
    return 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop'
  }
  return defaultImg
}
