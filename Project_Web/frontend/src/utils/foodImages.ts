// Product food images matching WebServer.java mappings
export const DISH_IMAGES: Record<string, string> = {
  // Burger
  M001: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=85', // Burger Bò Phô Mai
  M002: 'https://images.unsplash.com/photo-1606755962773-d324e0a13086?auto=format&fit=crop&w=900&q=85', // Burger Gà Giòn Cay
  M003: 'https://images.unsplash.com/photo-1586190848861-99aa4a171e90?auto=format&fit=crop&w=900&q=85', // Burger Bò Nướng Double Cheese
  M017: 'https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=900&q=85', // Burger Phi Lê Cá Tuyết

  // Gà rán
  M004: 'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?auto=format&fit=crop&w=900&q=85', // Gà Rán Giòn (1 miếng)
  M005: 'https://images.unsplash.com/photo-1562967914-608f82629710?auto=format&fit=crop&w=900&q=85', // Gà Rán Giòn (2 miếng)
  M006: 'https://images.unsplash.com/photo-1513639776629-7b61b0ac49cb?auto=format&fit=crop&w=900&q=85', // Combo Gà Rán Gia Đình (4 miếng)

  // Khoai tây chiên
  M007: 'https://images.unsplash.com/photo-1576107232684-1279f3908594?auto=format&fit=crop&w=900&q=85', // Khoai Tây Chiên (Vừa)
  M008: 'https://images.unsplash.com/photo-1630384060421-cb20d0e0649d?auto=format&fit=crop&w=900&q=85', // Khoai Tây Lắc Phô Mai

  // Pizza
  M009: 'https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=900&q=85', // Pizza Hải Sản Pesto
  M010: 'https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=900&q=85', // Pizza Bò Nướng BBQ
  M018: 'https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=900&q=85', // Pizza Chay Rau Củ

  // Nước uống
  M011: 'https://images.unsplash.com/photo-1622483767028-3f66f32aef97?auto=format&fit=crop&w=900&q=85', // Coca-Cola Tươi (Ly Lớn)
  M012: 'https://images.unsplash.com/photo-1629203851122-3726ecdf080e?auto=format&fit=crop&w=900&q=85', // Pepsi Mát Lạnh (Ly Lớn)
  M013: 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?auto=format&fit=crop&w=900&q=85', // Trà Đào Cam Sả

  // Combo
  M014: 'https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?auto=format&fit=crop&w=900&q=85', // Combo Tiết Kiệm Burger
  M015: 'https://images.unsplash.com/photo-1615937657715-bc7b4b7962c1?auto=format&fit=crop&w=900&q=85', // Combo No Nê Gà Rán

  // Mì & Khác
  M016: 'https://images.unsplash.com/photo-1551183053-bf91a1d81141?auto=format&fit=crop&w=900&q=85', // Mì Ý Sốt Bò Bằm
};

export const DEFAULT_FOOD_IMAGE = 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=900&q=85';

export function getFoodImage(maMon?: string, tenMon?: string): string {
  if (maMon && DISH_IMAGES[maMon]) {
    return DISH_IMAGES[maMon];
  }
  if (tenMon) {
    const t = tenMon.toLowerCase();
    if (t.includes('burger') || t.includes('bò') || t.includes('bo')) return DISH_IMAGES.M001;
    if (t.includes('ga') || t.includes('gà') || t.includes('chicken')) return DISH_IMAGES.M004;
    if (t.includes('pizza')) return DISH_IMAGES.M009;
    if (t.includes('khoai') || t.includes('fry') || t.includes('fries')) return DISH_IMAGES.M007;
    if (t.includes('tra') || t.includes('trà')) return DISH_IMAGES.M013;
    if (t.includes('coca') || t.includes('pepsi') || t.includes('drink') || t.includes('nuoc') || t.includes('nước')) return DISH_IMAGES.M011;
    if (t.includes('mi') || t.includes('mì') || t.includes('spaghetti') || t.includes('pasta')) return DISH_IMAGES.M016;
    if (t.includes('combo')) return DISH_IMAGES.M014;
  }
  return DEFAULT_FOOD_IMAGE;
}

export function getFoodEmoji(name: string): string {
  const n = (name || '').toLowerCase();
  if (n.includes('burger') || n.includes('ham')) return '🍔';
  if (n.includes('pizza')) return '🍕';
  if (n.includes('chicken') || n.includes('ga') || n.includes('gà')) return '🍗';
  if (n.includes('noodle') || n.includes('pho') || n.includes('bun') || n.includes('mi') || n.includes('mì')) return '🍝';
  if (n.includes('rice') || n.includes('com') || n.includes('cơm')) return '🍚';
  if (n.includes('drink') || n.includes('juice') || n.includes('soda') || n.includes('nuoc') || n.includes('nước') || n.includes('tra') || n.includes('trà') || n.includes('pepsi') || n.includes('coca')) return '🥤';
  if (n.includes('fry') || n.includes('khoai')) return '🍟';
  if (n.includes('cake') || n.includes('banh') || n.includes('bánh')) return '🍰';
  if (n.includes('salad') || n.includes('rau')) return '🥗';
  return '🍽️';
}
