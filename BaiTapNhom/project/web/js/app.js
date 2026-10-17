/* ==========================================================================
   FAST FOOD MIS — FRONTEND APPLICATION (LINEAR & NOTION MINIMALIST UI)
   Connected to Java 17 Backend REST API + MySQL / H2 Database
   ========================================================================== */

const App = (() => {
  // Application State
  const state = {
    route: 'home',
    categories: [],
    dishes: [],
    promotions: [],
    branches: [],
    orders: [],
    customers: [],
    dbMode: 'MySQL / H2',
    dashboard: null,

    // Menu Filters & Pagination
    menuFilter: {
      search: '',
      category: 'ALL',
      priceRange: 'ALL',
      type: 'ALL',
      sort: 'popular',
      page: 1,
      pageSize: 8
    },

    // Persistent Cart & Promo
    cart: JSON.parse(localStorage.getItem('ff17_cart') || '[]'),
    promoCode: localStorage.getItem('ff17_promo') || '',
    deliveryMode: localStorage.getItem('ff17_delivery_mode') || 'GIAO_HANG',
    selectedBranch: 'CN01',
    lastCreatedOrder: null,

    // Auth Sessions
    customer: JSON.parse(localStorage.getItem('ff17_customer') || 'null'),
    admin: JSON.parse(localStorage.getItem('ff17_admin') || 'null'),
    accountTab: 'orders', // 'orders' | 'profile'
    adminTab: 'dashboard', // 'dashboard' | 'orders' | 'dishes' | 'promotions' | 'queries'
    adminOrderFilter: 'ALL',
    editingDish: null,
    activeQueryNum: 11,
    activeQueryResult: null,

    // Dish Detail Modal Customization State
    modalDish: null,
    modalSize: { id: 'M', name: 'Cỡ Vừa (Tiêu chuẩn)', price: 0 },
    modalToppings: [],
    modalSauce: 'Tương ớt & Tương cà',
    modalNote: '',
    modalQty: 1
  };

  const SIZE_OPTIONS = [
    { id: 'M', name: 'Cỡ Vừa (Tiêu chuẩn)', price: 0 },
    { id: 'L', name: 'Cỡ Lớn (+10.000đ)', price: 10000 },
    { id: 'XL', name: 'Cỡ Đại (+18.000đ)', price: 18000 }
  ];

  const TOPPING_OPTIONS = [
    { id: 'cheese', name: 'Phô mai Cheddar', price: 10000 },
    { id: 'egg', name: 'Trứng ốp la', price: 80000 / 10 },
    { id: 'bacon', name: 'Thịt xông khói', price: 15000 },
    { id: 'FriesUp', name: 'Thêm khoai giòn', price: 12000 }
  ];

  const SAUCE_OPTIONS = [
    'Tương ớt & Tương cà',
    'Sốt Phô Mai Béo (+0đ)',
    'Sốt BBQ Khói (+0đ)',
    'Sốt Mayo Tỏi (+0đ)',
    'Không lấy tương/sốt'
  ];

  const CATEGORY_ICONS = {
    DM01: '🍔',
    DM02: '🍗',
    DM03: '🍕',
    DM04: '🍟',
    DM05: '🥤',
    DM06: '🍱',
    DM07: '🍝'
  };

  const ORDER_STATUS_META = {
    CHO_XAC_NHAN:  { label: 'Chờ xác nhận', badge: 'badge-warning', step: 1 },
    DA_XAC_NHAN:   { label: 'Đã xác nhận', badge: 'badge-info', step: 2 },
    DANG_CHUAN_BI: { label: 'Đang chuẩn bị', badge: 'badge-info', step: 3 },
    SAN_SANG:      { label: 'Sẵn sàng / Đang giao', badge: 'badge-brand', step: 4 },
    DA_HOAN_THANH: { label: 'Đã hoàn thành', badge: 'badge-success', step: 5 },
    DA_HUY:        { label: 'Đã hủy', badge: 'badge-danger', step: -1 }
  };

  const QUERIES_CATALOG = [
    { num: 1, author: 'Chỏi Minh Văn (01–10)', title: 'Nhân viên chưa được cấp tài khoản hệ thống' },
    { num: 2, author: 'Chỏi Minh Văn (01–10)', title: 'Món Burger có giá từ 40.000đ đến 60.000đ (giảm dần)' },
    { num: 3, author: 'Chỏi Minh Văn (01–10)', title: 'Món có tồn kho < 10 tại từng chi nhánh' },
    { num: 4, author: 'Chỏi Minh Văn (01–10)', title: 'Danh sách khách hàng tại TP.HCM' },
    { num: 5, author: 'Chỏi Minh Văn (01–10)', title: 'Các phương thức thanh toán đã từng giao dịch thành công' },
    { num: 6, author: 'Chỏi Minh Văn (01–10)', title: 'Đơn hàng hoàn thành trong tháng 02/2026' },
    { num: 7, author: 'Chỏi Minh Văn (01–10)', title: 'Đơn hàng kèm tên khách hàng & nhân viên phụ trách' },
    { num: 8, author: 'Chỏi Minh Văn (01–10)', title: 'Chi tiết các món trong đơn hàng #1001' },
    { num: 9, author: 'Chỏi Minh Văn (01–10)', title: 'Đối chiếu tổng tiền hàng chi tiết với đơn hàng' },
    { num: 10, author: 'Chỏi Minh Văn (01–10)', title: 'Tổng doanh thu thực tế từ các đơn đã hoàn thành' },
    { num: 11, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Thống kê doanh thu theo từng tháng năm 2026' },
    { num: 12, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Top 3 món ăn bán chạy nhất theo tổng số lượng' },
    { num: 13, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Tổng doanh thu đóng góp từ từng khách hàng' },
    { num: 14, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Danh sách khách hàng chưa từng đặt đơn hàng nào' },
    { num: 15, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Danh sách món ăn chưa từng bán thành công' },
    { num: 16, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Thống kê doanh số bán hàng theo từng nhân viên' },
    { num: 17, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Khách hàng thân thiết có ít nhất 2 đơn hoàn thành' },
    { num: 18, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Chi tiết đơn có đơn giá khác giá niêm yết hiện tại' },
    { num: 19, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Danh sách tài khoản nhân viên đang bị khóa (LOCKED)' },
    { num: 20, author: 'Nguyễn Quốc Tiên (11–20)', title: 'Đơn hàng có tổng tiền cao hơn mức trung bình hệ thống' },
    { num: 21, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Tài khoản nhân viên chưa từng đăng nhập lần nào' },
    { num: 22, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Các đơn hàng có áp dụng mã khuyến mãi' },
    { num: 23, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Tổng số tiền giảm của từng chương trình khuyến mãi' },
    { num: 24, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Chương trình khuyến mãi chưa từng được sử dụng' },
    { num: 25, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'So sánh doanh thu giữa đơn có và không dùng khuyến mãi' },
    { num: 26, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Đơn hàng có số tiền giảm chạm mức trần tối đa' },
    { num: 27, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Tổng tiền thu được theo từng phương thức thanh toán' },
    { num: 28, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Thống kê doanh thu theo từng chi nhánh' },
    { num: 29, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Khách hàng có mức chi tiêu cao hơn trung bình' },
    { num: 30, author: 'Nguyễn Mai Thiên Quí (21–30)', title: 'Đơn hàng từng thanh toán thất bại rồi sau đó thành công' }
  ];

  // Formatting Helpers
  function formatVND(val) {
    const num = Number(val) || 0;
    return num.toLocaleString('vi-VN') + ' ₫';
  }

  function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }

  function showToast(message) {
    const container = document.getElementById('toastContainer');
    if (!container) return;
    const el = document.createElement('div');
    el.className = 'toast';
    el.innerHTML = `<span>✓</span><span>${escapeHtml(message)}</span>`;
    container.appendChild(el);
    setTimeout(() => {
      el.style.opacity = '0';
      el.style.transition = 'opacity 200ms';
      setTimeout(() => el.remove(), 200);
    }, 2800);
  }

  // Initialization & API Calls
  async function init() {
    window.addEventListener('keydown', (e) => {
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        openQuickSearch();
      } else if (e.key === 'Escape') {
        closeDishModal();
        closeQuickSearch();
        toggleCartDrawer(false);
      }
    });

    await loadBootstrap();
    const hash = window.location.hash.replace('#', '');
    if (['home', 'menu', 'checkout', 'account', 'admin'].includes(hash)) {
      state.route = hash;
    }
    updateHeaderUI();
    render();
  }

  async function loadBootstrap() {
    try {
      const res = await fetch('/api/bootstrap');
      const data = await res.json();
      state.categories = data.categories || [];
      state.dishes = data.dishes || [];
      state.promotions = data.promotions || [];
      state.branches = data.branches || [];
      state.orders = data.orders || [];
      state.customers = data.customers || [];
      state.dbMode = data.dbMode || 'MySQL / H2';
      const badge = document.getElementById('dbModeBadge');
      if (badge) badge.textContent = state.dbMode.includes('H2') ? 'H2 Engine' : 'MySQL 8';
    } catch (err) {
      console.error('Failed to load bootstrap data:', err);
      showToast('Không thể kết nối máy chủ dữ liệu.');
    }
  }

  function saveCart() {
    localStorage.setItem('ff17_cart', JSON.stringify(state.cart));
    localStorage.setItem('ff17_promo', state.promoCode);
    localStorage.setItem('ff17_delivery_mode', state.deliveryMode);
    updateHeaderUI();
    renderCartDrawer();
  }

  function updateHeaderUI() {
    const countEl = document.getElementById('headerCartCount');
    const totalQty = state.cart.reduce((sum, item) => sum + item.soLuong, 0);
    if (countEl) countEl.textContent = totalQty;

    const accLabel = document.getElementById('headerAccountLabel');
    if (accLabel) {
      accLabel.textContent = state.customer ? state.customer.hoTen.split(' ').slice(-2).join(' ') : 'Tài khoản';
    }

    document.querySelectorAll('.nav-link').forEach(link => {
      link.classList.toggle('active', link.getAttribute('data-nav') === state.route);
    });
  }

  function navigate(route) {
    state.route = route;
    window.location.hash = route;
    window.scrollTo({ top: 0, behavior: 'smooth' });
    updateHeaderUI();
    if (route === 'admin' && state.admin) {
      loadAdminDashboard();
    }
    render();
  }

  function filterByCategory(maDM) {
    state.menuFilter.category = maDM;
    state.menuFilter.page = 1;
    navigate('menu');
  }

  // Cart Calculations
  function calculateCartTotals() {
    const subtotal = state.cart.reduce((sum, item) => sum + item.donGia * item.soLuong, 0);
    let discount = 0;
    let appliedPromo = null;
    let promoMessage = '';

    if (state.promoCode) {
      const code = state.promoCode.trim().toUpperCase();
      const km = state.promotions.find(p => p.maKM.toUpperCase() === code);
      if (!km) {
        promoMessage = 'Mã giảm giá không tồn tại.';
      } else if (km.trangThai !== 'DANG_AP_DUNG') {
        promoMessage = `Mã ${km.maKM} hiện không khả dụng (${km.trangThai}).`;
      } else if (subtotal < Number(km.donToiThieu)) {
        promoMessage = `Đơn tối thiểu ${formatVND(km.donToiThieu)} để dùng mã ${km.maKM}.`;
      } else {
        appliedPromo = km;
        discount = Math.round((subtotal * Number(km.phanTramGiam)) / 100);
        if (km.giamToiDa && discount > Number(km.giamToiDa)) {
          discount = Number(km.giamToiDa);
        }
        promoMessage = `Đã áp dụng ${km.maKM}: Giảm ${formatVND(discount)}`;
      }
    }

    const shipping = (state.cart.length > 0 && state.deliveryMode === 'GIAO_HANG' && subtotal < 200000) ? 15000 : 0;
    const total = Math.max(0, subtotal - discount + shipping);

    return { subtotal, discount, shipping, total, appliedPromo, promoMessage };
  }

  // Dish Card Component
  function renderDishCard(dish) {
    const isAvailable = dish.conMon;
    return `
      <article class="dish-card ${isAvailable ? '' : 'out-of-stock'}">
        <div class="dish-thumb-wrap" onclick="App.openDishModal('${dish.maMon}')">
          <img class="dish-thumb" src="${escapeHtml(dish.hinhAnh)}" alt="${escapeHtml(dish.tenMon)}" loading="lazy" />
          <div class="dish-badges-top">
            <div style="display:flex; gap:5px; flex-wrap:wrap;">
              ${dish.isBestSeller ? `<span class="badge badge-brand">Bán chạy</span>` : ''}
              ${dish.isNew ? `<span class="badge badge-info">Món mới</span>` : ''}
              ${dish.isPromo ? `<span class="badge badge-warning">Ưu đãi</span>` : ''}
            </div>
            <span class="badge ${isAvailable ? 'badge-success' : 'badge-danger'}">
              ${isAvailable ? 'Còn món' : 'Tạm hết'}
            </span>
          </div>
        </div>
        <div class="dish-body">
          <div class="dish-meta-top">
            <span>${escapeHtml(dish.tenDM)}</span>
            <span class="mono">${dish.soLuongBan > 0 ? `Đã bán ${dish.soLuongBan}` : dish.maMon}</span>
          </div>
          <h3 class="dish-title" onclick="App.openDishModal('${dish.maMon}')">${escapeHtml(dish.tenMon)}</h3>
          <p class="dish-desc">${escapeHtml(dish.moTa)}</p>
          <div class="dish-footer">
            <div class="price-block">
              <span class="price-current">${formatVND(dish.giaBan)}</span>
              ${dish.giaGoc ? `<span class="price-old">${formatVND(dish.giaGoc)}</span>` : ''}
            </div>
            <div style="display:flex; gap:6px;">
              <button class="btn btn-secondary btn-sm" onclick="App.openDishModal('${dish.maMon}')" title="Tùy chọn cỡ & topping">
                Tùy chọn
              </button>
              <button class="btn btn-primary btn-sm" ${!isAvailable ? 'disabled' : ''} onclick="App.quickAddToCart('${dish.maMon}')">
                + Thêm
              </button>
            </div>
          </div>
        </div>
      </article>
    `;
  }

  // ==========================================================================
  // VIEW 1: HOME PAGE
  // ==========================================================================
  function renderHome() {
    const bestSellers = [...state.dishes]
      .filter(d => d.conMon)
      .sort((a, b) => b.soLuongBan - a.soLuongBan)
      .slice(0, 4);

    const promoDishes = state.dishes.filter(d => d.isPromo && d.conMon).slice(0, 4);
    const newDishes = state.dishes.filter(d => d.isNew && d.conMon).slice(0, 4);
    const activePromos = state.promotions;

    return `
      <!-- HERO SECTION -->
      <section class="hero-section">
        <div class="hero-grid">
          <div>
            <div class="hero-eyebrow">
              <span>⚡ CHUỖI THỨC ĂN NHANH THẾ HỆ MỚI</span>
              <span>•</span>
              <span>GIAO NHANH 25 PHÚT</span>
            </div>
            <h1 class="hero-title">Ẩm thực nhanh tinh gọn, nguyên liệu tươi chuẩn vị mỗi ngày.</h1>
            <p class="hero-subtitle">
              Thưởng thức Burger bò Úc nướng lửa hồng, Gà rán giòn rụm và Pizza phô mai kéo sợi. Đặt món trực tuyến minh bạch, tùy biến khẩu vị dễ dàng và theo dõi trạng thái đơn theo thời gian thực.
            </p>
            <div class="hero-cta-row">
              <button class="btn btn-primary btn-lg" onclick="App.navigate('menu')">
                Khám phá Thực đơn (${state.dishes.length} món) →
              </button>
              <button class="btn btn-secondary btn-lg" onclick="App.openQuickSearch()">
                Tìm kiếm nhanh (Ctrl+K)
              </button>
            </div>

            <div class="promo-coupon-strip">
              <span style="font-size:0.78rem; color:var(--text-muted); width:100%; font-weight:500;">
                Chương trình khuyến mãi hiện có (Bấm để áp dụng mã vào giỏ hàng):
              </span>
              ${activePromos.map(km => `
                <button type="button" class="coupon-chip" onclick="App.applyCouponCode('${km.maKM}')">
                  <span class="coupon-code">${escapeHtml(km.maKM)}</span>
                  <span>${escapeHtml(km.tenKM)}</span>
                  <span class="badge ${km.trangThai === 'DANG_AP_DUNG' ? 'badge-success' : 'badge-neutral'}">
                    ${km.trangThai === 'DANG_AP_DUNG' ? 'Đang áp dụng' : km.trangThai}
                  </span>
                </button>
              `).join('')}
            </div>
          </div>

          <div class="hero-visual">
            <img src="https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1100&q=85" alt="Combo Burger & Gà rán" />
            <div class="hero-floating-card">
              <div>
                <div style="font-size:0.75rem; color:var(--text-muted); font-weight:500;">Combo Bán Chạy Nhất Tuần</div>
                <div style="font-weight:600; font-size:0.95rem;">Combo Burger Bò Úc + Khoai + Coca</div>
              </div>
              <button class="btn btn-brand btn-sm" onclick="App.openDishModal('M014')">
                85.000 ₫ • Đặt ngay
              </button>
            </div>
          </div>
        </div>
      </section>

      <!-- CATEGORIES RAIL -->
      <section class="section-block">
        <div class="section-header">
          <div>
            <h2 class="section-title">Danh mục thực đơn</h2>
            <p class="section-desc">Chọn nhanh nhóm món ăn yêu thích của bạn</p>
          </div>
          <button class="btn btn-ghost btn-sm" onclick="App.filterByCategory('ALL')">Xem tất cả →</button>
        </div>
        <div class="category-rail">
          ${state.categories.map(dm => {
            const count = state.dishes.filter(d => d.maDM === dm.maDM).length;
            return `
              <button class="category-card" onclick="App.filterByCategory('${dm.maDM}')">
                <span class="category-icon">${CATEGORY_ICONS[dm.maDM] || '🍽️'}</span>
                <div>
                  <div class="category-name">${escapeHtml(dm.tenDM)}</div>
                  <div class="category-count">${count} món ăn</div>
                </div>
              </button>
            `;
          }).join('')}
        </div>
      </section>

      <!-- BEST SELLERS -->
      <section class="section-block">
        <div class="section-header">
          <div>
            <h2 class="section-title">Món bán chạy nhất</h2>
            <p class="section-desc">Được khách hàng đặt nhiều nhất tại chuỗi chi nhánh TP.HCM</p>
          </div>
          <button class="btn btn-secondary btn-sm" onclick="App.navigate('menu')">Xem toàn bộ thực đơn</button>
        </div>
        <div class="dish-grid">
          ${bestSellers.map(renderDishCard).join('')}
        </div>
      </section>

      <!-- PROMOTIONAL DISHES -->
      <section class="section-block">
        <div class="section-header">
          <div>
            <h2 class="section-title">Đang ưu đãi &amp; Combo tiết kiệm</h2>
            <p class="section-desc">Lựa chọn hoàn hảo cho bữa trưa văn phòng hoặc tụ họp bạn bè</p>
          </div>
        </div>
        <div class="dish-grid">
          ${promoDishes.map(renderDishCard).join('')}
        </div>
      </section>

      <!-- NEW ARRIVALS -->
      <section class="section-block">
        <div class="section-header">
          <div>
            <h2 class="section-title">Món mới ra mắt</h2>
            <p class="section-desc">Công thức chế biến mới nhất vừa cập nhật vào thực đơn</p>
          </div>
        </div>
        <div class="dish-grid">
          ${newDishes.map(renderDishCard).join('')}
        </div>
      </section>
    `;
  }

  // ==========================================================================
  // VIEW 2: FULL MENU PAGE (Search, Filters, Sorting, Pagination)
  // ==========================================================================
  function getFilteredDishes() {
    const f = state.menuFilter;
    return state.dishes
      .filter(d => {
        if (f.category !== 'ALL' && d.maDM !== f.category) return false;
        if (f.search.trim() !== '') {
          const q = f.search.trim().toLowerCase();
          const matchName = d.tenMon.toLowerCase().includes(q);
          const matchDesc = (d.moTa || '').toLowerCase().includes(q);
          const matchCode = d.maMon.toLowerCase().includes(q);
          if (!matchName && !matchDesc && !matchCode) return false;
        }
        const price = Number(d.giaBan);
        if (f.priceRange === 'UNDER_40' && price >= 40000) return false;
        if (f.priceRange === '40_TO_80' && (price < 40000 || price > 80000)) return false;
        if (f.priceRange === 'OVER_80' && price <= 80000) return false;

        if (f.type === 'AVAILABLE' && !d.conMon) return false;
        if (f.type === 'PROMO' && !d.isPromo) return false;
        if (f.type === 'BEST' && !d.isBestSeller) return false;
        if (f.type === 'NEW' && !d.isNew) return false;
        return true;
      })
      .sort((a, b) => {
        if (f.sort === 'price_asc') return Number(a.giaBan) - Number(b.giaBan);
        if (f.sort === 'price_desc') return Number(b.giaBan) - Number(a.giaBan);
        if (f.sort === 'new') return (b.isNew ? 1 : 0) - (a.isNew ? 1 : 0);
        return b.soLuongBan - a.soLuongBan; // popular
      });
  }

  function renderMenu() {
    const f = state.menuFilter;
    const filtered = getFilteredDishes();
    const totalPages = Math.max(1, Math.ceil(filtered.length / f.pageSize));
    if (f.page > totalPages) f.page = totalPages;

    const startIdx = (f.page - 1) * f.pageSize;
    const pageItems = filtered.slice(startIdx, startIdx + f.pageSize);

    return `
      <section class="section-block">
        <div class="section-header">
          <div>
            <h1 class="section-title" style="font-size:1.55rem;">Thực đơn nhà hàng</h1>
            <p class="section-desc">Hiển thị ${filtered.length} món ăn • Giá đã bao gồm VAT</p>
          </div>
          <div style="display:flex; gap:8px;">
            <button class="btn btn-secondary btn-sm" onclick="App.resetMenuFilters()">Đặt lại bộ lọc</button>
          </div>
        </div>

        <!-- CATEGORY PILLS -->
        <div style="display:flex; gap:8px; overflow-x:auto; padding-bottom:14px;">
          <button class="btn ${f.category === 'ALL' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setMenuFilter('category', 'ALL')">
            Tất cả (${state.dishes.length})
          </button>
          ${state.categories.map(dm => {
            const count = state.dishes.filter(d => d.maDM === dm.maDM).length;
            return `
              <button class="btn ${f.category === dm.maDM ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setMenuFilter('category', '${dm.maDM}')">
                <span>${CATEGORY_ICONS[dm.maDM] || '🍽️'}</span>
                <span>${escapeHtml(dm.tenDM)} (${count})</span>
              </button>
            `;
          }).join('')}
        </div>

        <!-- FILTER & SORT TOOLBAR -->
        <div class="filter-toolbar">
          <div class="filter-group" style="flex:1; min-width:240px;">
            <input
              type="search"
              class="input-control"
              style="flex:1; min-width:220px;"
              placeholder="Tìm món ăn theo tên, mã món hoặc nguyên liệu..."
              value="${escapeHtml(f.search)}"
              oninput="App.setMenuSearch(this.value)"
            />

            <select class="select-control" onchange="App.setMenuFilter('priceRange', this.value)" aria-label="Khoảng giá">
              <option value="ALL" ${f.priceRange === 'ALL' ? 'selected' : ''}>Mọi khoảng giá</option>
              <option value="UNDER_40" ${f.priceRange === 'UNDER_40' ? 'selected' : ''}>Dưới 40.000 ₫</option>
              <option value="40_TO_80" ${f.priceRange === '40_TO_80' ? 'selected' : ''}>Từ 40.000 ₫ – 80.000 ₫</option>
              <option value="OVER_80" ${f.priceRange === 'OVER_80' ? 'selected' : ''}>Trên 80.000 ₫</option>
            </select>

            <select class="select-control" onchange="App.setMenuFilter('type', this.value)" aria-label="Loại món">
              <option value="ALL" ${f.type === 'ALL' ? 'selected' : ''}>Tất cả trạng thái</option>
              <option value="AVAILABLE" ${f.type === 'AVAILABLE' ? 'selected' : ''}>Chỉ món còn hàng</option>
              <option value="BEST" ${f.type === 'BEST' ? 'selected' : ''}>Món bán chạy</option>
              <option value="PROMO" ${f.type === 'PROMO' ? 'selected' : ''}>Đang khuyến mãi</option>
              <option value="NEW" ${f.type === 'NEW' ? 'selected' : ''}>Món mới</option>
            </select>
          </div>

          <div class="filter-group">
            <span style="font-size:0.82rem; color:var(--text-muted);">Sắp xếp:</span>
            <select class="select-control" onchange="App.setMenuFilter('sort', this.value)" aria-label="Sắp xếp theo">
              <option value="popular" ${f.sort === 'popular' ? 'selected' : ''}>Độ phổ biến (Bán chạy)</option>
              <option value="price_asc" ${f.sort === 'price_asc' ? 'selected' : ''}>Giá: Thấp đến Cao</option>
              <option value="price_desc" ${f.sort === 'price_desc' ? 'selected' : ''}>Giá: Cao đến Thấp</option>
              <option value="new" ${f.sort === 'new' ? 'selected' : ''}>Món mới nhất</option>
            </select>
          </div>
        </div>

        <!-- DISH LIST OR EMPTY STATE -->
        <div id="menuResultsContainer">
          ${renderMenuGridAndPagination(pageItems, filtered.length, totalPages)}
        </div>
      </section>
    `;
  }

  function renderMenuGridAndPagination(pageItems, totalFiltered, totalPages) {
    const f = state.menuFilter;
    if (pageItems.length === 0) {
      return `
        <div class="surface-card" style="text-align:center; padding:56px 24px;">
          <div style="font-size:2rem; margin-bottom:10px;">🔍</div>
          <h3 style="font-size:1.08rem; font-weight:600; margin-bottom:6px;">Không tìm thấy món ăn phù hợp</h3>
          <p style="color:var(--text-muted); font-size:0.88rem; margin-bottom:18px;">
            Thử thay đổi từ khóa tìm kiếm hoặc chọn khoảng giá khác.
          </p>
          <button class="btn btn-primary btn-sm" onclick="App.resetMenuFilters()">Xóa bộ lọc</button>
        </div>
      `;
    }

    return `
      <div class="dish-grid">
        ${pageItems.map(renderDishCard).join('')}
      </div>

      <div class="pagination-bar">
        <div style="font-size:0.85rem; color:var(--text-secondary);">
          Trang <strong>${f.page}</strong> / <strong>${totalPages}</strong> (Tổng ${totalFiltered} món)
        </div>
        <div class="page-buttons">
          <button class="page-btn" ${f.page <= 1 ? 'disabled' : ''} onclick="App.setMenuPage(${f.page - 1})">← Trước</button>
          ${Array.from({ length: totalPages }, (_, i) => i + 1).map(p => `
            <button class="page-btn ${p === f.page ? 'active' : ''}" onclick="App.setMenuPage(${p})">${p}</button>
          `).join('')}
          <button class="page-btn" ${f.page >= totalPages ? 'disabled' : ''} onclick="App.setMenuPage(${f.page + 1})">Tiếp →</button>
        </div>
      </div>
    `;
  }

  function setMenuFilter(key, val) {
    state.menuFilter[key] = val;
    state.menuFilter.page = 1;
    render();
  }

  function setMenuSearch(val) {
    state.menuFilter.search = val;
    state.menuFilter.page = 1;
    const container = document.getElementById('menuResultsContainer');
    if (container && state.route === 'menu') {
      const filtered = getFilteredDishes();
      const totalPages = Math.max(1, Math.ceil(filtered.length / state.menuFilter.pageSize));
      const startIdx = (state.menuFilter.page - 1) * state.menuFilter.pageSize;
      const pageItems = filtered.slice(startIdx, startIdx + state.menuFilter.pageSize);
      container.innerHTML = renderMenuGridAndPagination(pageItems, filtered.length, totalPages);
    } else {
      render();
    }
  }

  function setMenuPage(p) {
    state.menuFilter.page = p;
    render();
    window.scrollTo({ top: 140, behavior: 'smooth' });
  }

  function resetMenuFilters() {
    state.menuFilter = {
      search: '',
      category: 'ALL',
      priceRange: 'ALL',
      type: 'ALL',
      sort: 'popular',
      page: 1,
      pageSize: 8
    };
    render();
  }

  // ==========================================================================
  // DISH DETAIL & CUSTOMIZATION MODAL
  // ==========================================================================
  function openDishModal(maMon) {
    const dish = state.dishes.find(d => d.maMon === maMon);
    if (!dish) return;
    state.modalDish = dish;
    state.modalSize = SIZE_OPTIONS[0];
    state.modalToppings = [];
    state.modalSauce = SAUCE_OPTIONS[0];
    state.modalNote = '';
    state.modalQty = 1;

    renderDishModalContent();
    document.getElementById('dishModalBackdrop').classList.add('open');
  }

  function closeDishModal() {
    const bd = document.getElementById('dishModalBackdrop');
    if (bd) bd.classList.remove('open');
  }

  function getModalUnitPrice() {
    if (!state.modalDish) return 0;
    const base = Number(state.modalDish.giaBan);
    const sizeAdd = state.modalSize ? state.modalSize.price : 0;
    const toppingAdd = state.modalToppings.reduce((s, t) => s + t.price, 0);
    return base + sizeAdd + toppingAdd;
  }

  function renderDishModalContent() {
    const d = state.modalDish;
    if (!d) return;
    const unitPrice = getModalUnitPrice();
    const totalPrice = unitPrice * state.modalQty;
    const isDrink = d.maDM === 'DM05';

    const branchStockHtml = Object.entries(d.tonKhoChiNhanh || {})
      .map(([cn, qty]) => `<span class="badge ${qty > 5 ? 'badge-neutral' : 'badge-warning'}">${cn}: còn ${qty}</span>`)
      .join(' ');

    document.getElementById('dishModalContent').innerHTML = `
      <div class="modal-header">
        <div style="display:flex; align-items:center; gap:8px;">
          <span class="mono badge badge-neutral">${d.maMon}</span>
          <span style="font-size:0.85rem; color:var(--text-secondary);">${escapeHtml(d.tenDM)}</span>
        </div>
        <button class="btn btn-ghost btn-sm" onclick="App.closeDishModal()">Đóng ✕</button>
      </div>

      <div class="dish-detail-grid">
        <div>
          <div style="border-radius:var(--radius-lg); overflow:hidden; border:1px solid var(--border-subtle); aspect-ratio:4/3; margin-bottom:12px;">
            <img src="${escapeHtml(d.hinhAnh)}" alt="${escapeHtml(d.tenMon)}" style="width:100%; height:100%; object-fit:cover;" />
          </div>
          <div style="display:flex; flex-wrap:wrap; gap:6px; align-items:center; font-size:0.78rem; color:var(--text-muted);">
            <span>Tồn kho chi nhánh:</span>
            ${branchStockHtml || '<span class="badge badge-success">Sẵn sàng phục vụ</span>'}
          </div>
        </div>

        <div>
          <div style="display:flex; align-items:center; gap:8px; margin-bottom:6px;">
            <span class="badge ${d.conMon ? 'badge-success' : 'badge-danger'}">${d.conMon ? 'Còn hàng' : 'Tạm ngừng bán'}</span>
            ${d.soLuongBan > 0 ? `<span class="badge badge-brand">Đã bán ${d.soLuongBan} phần</span>` : ''}
          </div>
          <h2 style="font-size:1.35rem; font-weight:700; margin-bottom:6px;">${escapeHtml(d.tenMon)}</h2>
          <p style="font-size:0.88rem; color:var(--text-secondary); line-height:1.55; margin-bottom:12px;">
            ${escapeHtml(d.moTa)}
          </p>

          <div style="font-size:1.25rem; font-weight:700; color:var(--text-primary); font-variant-numeric:tabular-nums;">
            ${formatVND(d.giaBan)}
            ${d.giaGoc ? `<span class="price-old" style="font-size:0.88rem; margin-left:8px; font-weight:400;">${formatVND(d.giaGoc)}</span>` : ''}
          </div>

          <!-- SIZE SELECTION -->
          <div class="option-section">
            <div class="option-label">
              <span>1. Chọn kích cỡ khẩu phần</span>
              <span style="color:var(--text-muted); font-weight:400;">Bắt buộc</span>
            </div>
            <div class="option-pills">
              ${SIZE_OPTIONS.map(s => `
                <button type="button" class="option-pill ${state.modalSize.id === s.id ? 'selected' : ''}" onclick="App.selectModalSize('${s.id}')">
                  ${escapeHtml(s.name)}
                </button>
              `).join('')}
            </div>
          </div>

          <!-- TOPPING SELECTION -->
          ${!isDrink ? `
            <div class="option-section">
              <div class="option-label">
                <span>2. Topping thêm (Tùy chọn)</span>
                <span style="color:var(--text-muted); font-weight:400;">Có thể chọn nhiều</span>
              </div>
              <div class="option-pills">
                ${TOPPING_OPTIONS.map(t => {
                  const active = state.modalToppings.some(x => x.id === t.id);
                  return `
                    <button type="button" class="option-pill ${active ? 'selected' : ''}" onclick="App.toggleModalTopping('${t.id}')">
                      + ${escapeHtml(t.name)} (${formatVND(t.price)})
                    </button>
                  `;
                }).join('')}
              </div>
            </div>

            <!-- SAUCE SELECTION -->
            <div class="option-section">
              <div class="option-label">
                <span>3. Chọn sốt chấm kèm theo</span>
              </div>
              <div class="option-pills">
                ${SAUCE_OPTIONS.map(sauce => `
                  <button type="button" class="option-pill ${state.modalSauce === sauce ? 'selected' : ''}" onclick="App.selectModalSauce('${sauce}')">
                    ${escapeHtml(sauce)}
                  </button>
                `).join('')}
              </div>
            </div>
          ` : ''}

          <!-- QUANTITY & ADD TO CART -->
          <div class="option-section" style="display:flex; align-items:center; justify-content:space-between; gap:12px; padding-top:16px;">
            <div class="qty-stepper">
              <button type="button" class="qty-btn" onclick="App.changeModalQty(-1)">−</button>
              <span class="qty-val">${state.modalQty}</span>
              <button type="button" class="qty-btn" onclick="App.changeModalQty(1)">+</button>
            </div>

            <button
              type="button"
              class="btn btn-primary btn-lg"
              style="flex:1;"
              ${!d.conMon ? 'disabled' : ''}
              onclick="App.confirmModalAddToCart()"
            >
              ${d.conMon ? `Thêm vào giỏ • ${formatVND(totalPrice)}` : 'Món hiện tạm hết hàng'}
            </button>
          </div>
        </div>
      </div>
    `;
  }

  function selectModalSize(sizeId) {
    const found = SIZE_OPTIONS.find(s => s.id === sizeId);
    if (found) {
      state.modalSize = found;
      renderDishModalContent();
    }
  }

  function toggleModalTopping(topId) {
    const idx = state.modalToppings.findIndex(t => t.id === topId);
    if (idx >= 0) {
      state.modalToppings.splice(idx, 1);
    } else {
      const found = TOPPING_OPTIONS.find(t => t.id === topId);
      if (found) state.modalToppings.push(found);
    }
    renderDishModalContent();
  }

  function selectModalSauce(sauce) {
    state.modalSauce = sauce;
    renderDishModalContent();
  }

  function changeModalQty(delta) {
    state.modalQty = Math.max(1, Math.min(20, state.modalQty + delta));
    renderDishModalContent();
  }

  function confirmModalAddToCart() {
    const d = state.modalDish;
    if (!d || !d.conMon) return;
    const unitPrice = getModalUnitPrice();
    const optionsParts = [state.modalSize.name];
    if (state.modalToppings.length > 0) {
      optionsParts.push('Topping: ' + state.modalToppings.map(t => t.name).join(', '));
    }
    if (d.maDM !== 'DM05' && state.modalSauce) {
      optionsParts.push(state.modalSauce);
    }
    const tuyChon = optionsParts.join(' • ');
    const cartKey = `${d.maMon}__${tuyChon}`;

    const existing = state.cart.find(item => item.cartKey === cartKey);
    if (existing) {
      existing.soLuong += state.modalQty;
    } else {
      state.cart.push({
        cartKey,
        maMon: d.maMon,
        tenMon: d.tenMon,
        hinhAnh: d.hinhAnh,
        donGia: unitPrice,
        soLuong: state.modalQty,
        tuyChon
      });
    }

    saveCart();
    closeDishModal();
    showToast(`Đã thêm ${state.modalQty}x ${d.tenMon} vào giỏ hàng`);
  }

  function quickAddToCart(maMon) {
    const d = state.dishes.find(x => x.maMon === maMon);
    if (!d || !d.conMon) return;
    const tuyChon = 'Cỡ Vừa (Tiêu chuẩn)';
    const cartKey = `${d.maMon}__${tuyChon}`;
    const existing = state.cart.find(item => item.cartKey === cartKey);
    if (existing) {
      existing.soLuong += 1;
    } else {
      state.cart.push({
        cartKey,
        maMon: d.maMon,
        tenMon: d.tenMon,
        hinhAnh: d.hinhAnh,
        donGia: Number(d.giaBan),
        soLuong: 1,
        tuyChon
      });
    }
    saveCart();
    showToast(`Đã thêm ${d.tenMon} vào giỏ hàng`);
  }

  // ==========================================================================
  // CART DRAWER & ACTIONS
  // ==========================================================================
  function toggleCartDrawer(open) {
    const bd = document.getElementById('cartDrawerBackdrop');
    if (!bd) return;
    if (open) {
      renderCartDrawer();
      bd.classList.add('open');
    } else {
      bd.classList.remove('open');
    }
  }

  function updateCartQty(cartKey, delta) {
    const idx = state.cart.findIndex(i => i.cartKey === cartKey);
    if (idx === -1) return;
    state.cart[idx].soLuong += delta;
    if (state.cart[idx].soLuong <= 0) {
      state.cart.splice(idx, 1);
    }
    saveCart();
    if (state.route === 'checkout') render();
  }

  function removeCartItem(cartKey) {
    state.cart = state.cart.filter(i => i.cartKey !== cartKey);
    saveCart();
    if (state.route === 'checkout') render();
  }

  function applyCouponCode(code) {
    state.promoCode = (code || '').trim().toUpperCase();
    saveCart();
    const { appliedPromo, promoMessage } = calculateCartTotals();
    showToast(promoMessage || `Đã chọn mã ${state.promoCode}`);
    if (state.route === 'checkout') render();
  }

  function renderCartDrawer() {
    const bodyEl = document.getElementById('cartDrawerBody');
    const footerEl = document.getElementById('cartDrawerFooter');
    if (!bodyEl || !footerEl) return;

    if (state.cart.length === 0) {
      bodyEl.innerHTML = `
        <div style="text-align:center; padding:64px 16px; color:var(--text-muted);">
          <div style="font-size:2.2rem; margin-bottom:10px;">🛒</div>
          <div style="font-weight:600; color:var(--text-primary); margin-bottom:6px;">Giỏ hàng của bạn đang trống</div>
          <p style="font-size:0.84rem; margin-bottom:18px;">Hãy chọn các món Burger, Gà rán hoặc Combo hấp dẫn từ thực đơn.</p>
          <button class="btn btn-primary btn-sm" onclick="App.toggleCartDrawer(false); App.navigate('menu');">Xem thực đơn</button>
        </div>
      `;
      footerEl.innerHTML = '';
      return;
    }

    const { subtotal, discount, shipping, total, promoMessage } = calculateCartTotals();

    bodyEl.innerHTML = `
      <div style="display:flex; flex-direction:column; gap:14px;">
        ${state.cart.map(item => `
          <div style="display:flex; gap:12px; padding-bottom:14px; border-bottom:1px solid var(--border-subtle);">
            <img src="${escapeHtml(item.hinhAnh)}" alt="${escapeHtml(item.tenMon)}" style="width:62px; height:62px; border-radius:var(--radius-sm); object-fit:cover; border:1px solid var(--border-subtle);" />
            <div style="flex:1; min-width:0;">
              <div style="display:flex; justify-content:space-between; gap:6px;">
                <h4 style="font-size:0.9rem; font-weight:600;">${escapeHtml(item.tenMon)}</h4>
                <button class="btn btn-ghost btn-sm" style="height:22px; padding:0 6px; color:var(--text-muted);" onclick="App.removeCartItem('${escapeHtml(item.cartKey)}')">✕</button>
              </div>
              <div style="font-size:0.76rem; color:var(--text-muted); margin:2px 0 8px;">${escapeHtml(item.tuyChon)}</div>
              <div style="display:flex; align-items:center; justify-content:space-between;">
                <div class="qty-stepper">
                  <button class="qty-btn" style="width:26px; height:26px;" onclick="App.updateCartQty('${escapeHtml(item.cartKey)}', -1)">−</button>
                  <span class="qty-val" style="min-width:26px; font-size:0.82rem;">${item.soLuong}</span>
                  <button class="qty-btn" style="width:26px; height:26px;" onclick="App.updateCartQty('${escapeHtml(item.cartKey)}', 1)">+</button>
                </div>
                <span style="font-weight:600; font-size:0.9rem;" class="tabular-nums">${formatVND(item.donGia * item.soLuong)}</span>
              </div>
            </div>
          </div>
        `).join('')}
      </div>

      <!-- PROMO INPUT -->
      <div style="margin-top:16px;">
        <label class="field-label" style="margin-bottom:6px; display:block;">Mã khuyến mãi (Gợi ý: KM03 giảm 10%)</label>
        <div style="display:flex; gap:8px;">
          <input
            type="text"
            id="drawerPromoInput"
            class="input-control mono"
            style="flex:1; text-transform:uppercase;"
            placeholder="Nhập mã (VD: KM03)"
            value="${escapeHtml(state.promoCode)}"
          />
          <button class="btn btn-secondary" onclick="App.applyCouponCode(document.getElementById('drawerPromoInput').value)">Áp dụng</button>
        </div>
        ${promoMessage ? `<div style="font-size:0.78rem; color:var(--accent-brand); margin-top:6px;">${escapeHtml(promoMessage)}</div>` : ''}
      </div>
    `;

    footerEl.innerHTML = `
      <div class="summary-row">
        <span>Tạm tính (${state.cart.reduce((s, i) => s + i.soLuong, 0)} món)</span>
        <span class="tabular-nums">${formatVND(subtotal)}</span>
      </div>
      <div class="summary-row">
        <span>Giảm giá khuyến mãi</span>
        <span class="tabular-nums" style="color:var(--status-success);">−${formatVND(discount)}</span>
      </div>
      <div class="summary-row">
        <span>Phí giao hàng (${state.deliveryMode === 'GIAO_HANG' ? 'Tận nơi' : 'Nhận tại quầy'})</span>
        <span class="tabular-nums">${shipping === 0 ? 'Miễn phí' : formatVND(shipping)}</span>
      </div>
      <div class="summary-row total">
        <span>Tổng thanh toán</span>
        <span class="tabular-nums">${formatVND(total)}</span>
      </div>
      <button class="btn btn-primary btn-lg" style="width:100%; margin-top:12px;" onclick="App.toggleCartDrawer(false); App.navigate('checkout');">
        Tiến hành đặt hàng →
      </button>
    `;
  }

  // ==========================================================================
  // VIEW 3: CHECKOUT & ORDER CONFIRMATION PAGE
  // ==========================================================================
  function renderCheckout() {
    if (state.lastCreatedOrder) {
      const ord = state.lastCreatedOrder;
      return `
        <section class="section-block" style="max-width:740px; margin:0 auto;">
          <div class="surface-card" style="padding:32px;">
            <div style="display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;">
              <span class="badge badge-success" style="font-size:0.85rem; padding:4px 12px;">✓ Đặt hàng thành công</span>
              <span class="mono" style="color:var(--text-muted);">${escapeHtml(ord.ngayDat)}</span>
            </div>
            <h1 style="font-size:1.5rem; font-weight:700; margin-bottom:6px;">Mã đơn hàng #${ord.maDH}</h1>
            <p style="color:var(--text-secondary); font-size:0.9rem; margin-bottom:20px;">
              Đơn hàng của <strong>${escapeHtml(ord.tenKH)}</strong> đã được ghi nhận vào Cơ sở dữ liệu tại <strong>${escapeHtml(ord.tenCN)}</strong>.
            </p>

            ${renderOrderTimeline(ord.trangThai)}

            <div style="background:var(--bg-canvas); border:1px solid var(--border-subtle); border-radius:var(--radius-md); padding:16px; margin:20px 0;">
              <div style="font-weight:600; font-size:0.88rem; margin-bottom:10px;">Chi tiết thông tin nhận hàng &amp; thanh toán</div>
              <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; font-size:0.85rem;">
                <div><span style="color:var(--text-muted);">Người nhận:</span> <strong>${escapeHtml(ord.tenKH)} (${escapeHtml(ord.soDienThoai)})</strong></div>
                <div><span style="color:var(--text-muted);">Hình thức:</span> <strong>${ord.hinhThucNhan === 'MANG_DI' ? 'Mua mang đi (Nhận tại quầy)' : 'Giao hàng tận nơi'}</strong></div>
                <div><span style="color:var(--text-muted);">Địa chỉ:</span> ${escapeHtml(ord.diaChiGiao)}</div>
                <div><span style="color:var(--text-muted);">Thanh toán:</span> <span class="mono">${escapeHtml(ord.phuongThucThanhToan)} (${escapeHtml(ord.trangThaiThanhToan)})</span></div>
              </div>
            </div>

            <div style="margin-bottom:20px;">
              <div style="font-weight:600; font-size:0.88rem; margin-bottom:8px;">Danh sách món đã đặt</div>
              ${(ord.items || []).map(it => `
                <div class="summary-row" style="border-bottom:1px solid var(--border-subtle); padding:8px 0;">
                  <span><strong>${it.soLuong}x</strong> ${escapeHtml(it.tenMon)}</span>
                  <span class="tabular-nums font-weight:600;">${formatVND(it.thanhTien)}</span>
                </div>
              `).join('')}
              <div class="summary-row total">
                <span>Tổng tiền thanh toán</span>
                <span class="tabular-nums" style="color:var(--accent-brand);">${formatVND(ord.tongTien)}</span>
              </div>
            </div>

            <div style="display:flex; gap:10px; flex-wrap:wrap;">
              <button class="btn btn-primary" onclick="App.clearLastOrderAndGo('account')">Theo dõi đơn hàng trong Tài khoản</button>
              <button class="btn btn-secondary" onclick="App.clearLastOrderAndGo('menu')">Tiếp tục mua thêm món</button>
            </div>
          </div>
        </section>
      `;
    }

    if (state.cart.length === 0) {
      return `
        <section class="section-block" style="text-align:center; padding:64px 0;">
          <div class="surface-card" style="max-width:520px; margin:0 auto; padding:42px 24px;">
            <div style="font-size:2.4rem; margin-bottom:12px;">🛍️</div>
            <h2 style="font-size:1.25rem; font-weight:600; margin-bottom:8px;">Chưa có món ăn nào trong giỏ hàng</h2>
            <p style="color:var(--text-muted); font-size:0.9rem; margin-bottom:20px;">
              Vui lòng chọn món ăn từ thực đơn trước khi tiến hành đặt hàng và thanh toán.
            </p>
            <button class="btn btn-primary" onclick="App.navigate('menu')">Đi tới Thực đơn ngay →</button>
          </div>
        </section>
      `;
    }

    const { subtotal, discount, shipping, total, promoMessage } = calculateCartTotals();
    const kh = state.customer || {};

    return `
      <section class="section-block">
        <div class="section-header">
          <div>
            <h1 class="section-title" style="font-size:1.5rem;">Đặt hàng &amp; Thanh toán</h1>
            <p class="section-desc">Đơn hàng được lưu trực tiếp vào hệ thống CSDL Nhóm 17</p>
          </div>
        </div>

        <form class="two-col-layout" onsubmit="App.submitOrder(event)">
          <div style="display:flex; flex-direction:column; gap:18px;">
            <!-- STEP 1: DELIVERY MODE & BRANCH -->
            <div class="surface-card">
              <h3 style="font-size:1rem; font-weight:600; margin-bottom:14px;">1. Hình thức nhận món &amp; Chi nhánh phục vụ</h3>
              <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-bottom:16px;">
                <button
                  type="button"
                  class="category-card ${state.deliveryMode === 'GIAO_HANG' ? 'active' : ''}"
                  onclick="App.setDeliveryMode('GIAO_HANG')"
                >
                  <span class="category-icon">🛵</span>
                  <div>
                    <div class="category-name">Giao hàng tận nơi</div>
                    <div class="category-count">Miễn phí ship đơn từ 200.000 ₫</div>
                  </div>
                </button>

                <button
                  type="button"
                  class="category-card ${state.deliveryMode === 'MANG_DI' ? 'active' : ''}"
                  onclick="App.setDeliveryMode('MANG_DI')"
                >
                  <span class="category-icon">🏪</span>
                  <div>
                    <div class="category-name">Mua mang đi (Tại quầy)</div>
                    <div class="category-count">Nhận món sau 10 phút • 0 ₫ phí ship</div>
                  </div>
                </button>
              </div>

              <div class="form-field">
                <label class="field-label">Chọn chi nhánh chế biến gần bạn nhất</label>
                <select id="coBranch" class="select-control" onchange="App.setBranch(this.value)">
                  ${state.branches.map(cn => `
                    <option value="${cn.maCN}" ${state.selectedBranch === cn.maCN ? 'selected' : ''}>
                      ${escapeHtml(cn.tenCN)} — ${escapeHtml(cn.diaChi)} (${cn.soDienThoai})
                    </option>
                  `).join('')}
                </select>
              </div>
            </div>

            <!-- STEP 2: RECIPIENT DETAILS -->
            <div class="surface-card">
              <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:14px;">
                <h3 style="font-size:1rem; font-weight:600;">2. Thông tin người nhận</h3>
                ${!state.customer ? `
                  <button type="button" class="btn btn-ghost btn-sm" onclick="App.navigate('account')">
                    Đăng nhập để tự động điền →
                  </button>
                ` : `<span class="badge badge-success">Khách hàng: ${escapeHtml(state.customer.maKH)}</span>`}
              </div>

              <div class="form-grid">
                <div class="form-field">
                  <label class="field-label">Họ và tên người nhận *</label>
                  <input type="text" id="coHoTen" class="input-control" required placeholder="Nguyễn Văn A" value="${escapeHtml(kh.hoTen || '')}" />
                </div>
                <div class="form-field">
                  <label class="field-label">Số điện thoại liên hệ *</label>
                  <input type="tel" id="coSDT" class="input-control" required placeholder="0901234567" value="${escapeHtml(kh.soDienThoai || '')}" />
                </div>
                ${state.deliveryMode === 'GIAO_HANG' ? `
                  <div class="form-field full">
                    <label class="field-label">Địa chỉ giao hàng chi tiết *</label>
                    <input type="text" id="coDiaChi" class="input-control" required placeholder="Số nhà, tên đường, phường/xã, quận/huyện, TP.HCM" value="${escapeHtml(kh.diaChi || '')}" />
                  </div>
                ` : ''}
                <div class="form-field full">
                  <label class="field-label">Ghi chú đơn hàng (Tùy chọn)</label>
                  <textarea id="coGhiChu" class="textarea-control" rows="2" placeholder="VD: Giao giờ hành chính, lấy thêm tương ớt, không hành..."></textarea>
                </div>
              </div>
            </div>

            <!-- STEP 3: HONEST PAYMENT METHOD -->
            <div class="surface-card">
              <h3 style="font-size:1rem; font-weight:600; margin-bottom:8px;">3. Phương thức thanh toán</h3>
              <p style="font-size:0.82rem; color:var(--text-muted); margin-bottom:14px;">
                Hệ thống ghi nhận trung thực trạng thái thanh toán (Không giả lập cổng thanh toán trực tuyến tự động).
              </p>
              <div class="form-grid">
                <label class="category-card" style="cursor:pointer;">
                  <input type="radio" name="coPayment" value="TIEN_MAT" checked />
                  <div>
                    <div class="category-name">💵 Tiền mặt khi nhận hàng (COD)</div>
                    <div class="category-count">Thanh toán trực tiếp cho shipper hoặc thu ngân</div>
                  </div>
                </label>
                <label class="category-card" style="cursor:pointer;">
                  <input type="radio" name="coPayment" value="CHUYEN_KHOAN" />
                  <div>
                    <div class="category-name">🏦 Chuyển khoản khi nhận món</div>
                    <div class="category-count">Trạng thái: Chờ thu ngân đối soát thực tế</div>
                  </div>
                </label>
                <label class="category-card" style="cursor:pointer;">
                  <input type="radio" name="coPayment" value="VI_DIEN_TU" />
                  <div>
                    <div class="category-name">📱 Ví điện tử tại quầy / Shipper</div>
                    <div class="category-count">Quét mã QR khi kiểm tra món ăn</div>
                  </div>
                </label>
                <label class="category-card" style="cursor:pointer;">
                  <input type="radio" name="coPayment" value="THE" />
                  <div>
                    <div class="category-name">💳 Quẹt thẻ POS khi nhận</div>
                    <div class="category-count">Áp dụng tại quầy hoặc máy POS di động</div>
                  </div>
                </label>
              </div>
            </div>
          </div>

          <!-- RIGHT COLUMN: ORDER SUMMARY -->
          <div class="surface-card" style="position:sticky; top:78px;">
            <h3 style="font-size:1.02rem; font-weight:600; margin-bottom:14px;">Tóm tắt đơn hàng (${state.cart.reduce((s, i) => s + i.soLuong, 0)} món)</h3>
            <div style="display:flex; flex-direction:column; gap:10px; max-height:300px; overflow-y:auto; margin-bottom:14px; padding-right:4px;">
              ${state.cart.map(item => `
                <div style="display:flex; justify-content:space-between; gap:10px; font-size:0.86rem; padding-bottom:10px; border-bottom:1px solid var(--border-subtle);">
                  <div>
                    <div style="font-weight:600;">${item.soLuong}x ${escapeHtml(item.tenMon)}</div>
                    <div style="font-size:0.75rem; color:var(--text-muted);">${escapeHtml(item.tuyChon)}</div>
                  </div>
                  <div class="tabular-nums" style="font-weight:600; white-space:nowrap;">${formatVND(item.donGia * item.soLuong)}</div>
                </div>
              `).join('')}
            </div>

            <div style="margin-bottom:14px;">
              <label class="field-label" style="margin-bottom:6px; display:block;">Mã giảm giá</label>
              <div style="display:flex; gap:6px;">
                <input type="text" id="coPromoInput" class="input-control mono" style="flex:1; text-transform:uppercase;" placeholder="VD: KM03" value="${escapeHtml(state.promoCode)}" />
                <button type="button" class="btn btn-secondary btn-sm" style="height:36px;" onclick="App.applyCouponCode(document.getElementById('coPromoInput').value)">Áp dụng</button>
              </div>
              ${promoMessage ? `<div style="font-size:0.76rem; color:var(--accent-brand); margin-top:4px;">${escapeHtml(promoMessage)}</div>` : ''}
            </div>

            <div class="summary-row">
              <span>Tạm tính tiền hàng</span>
              <span class="tabular-nums">${formatVND(subtotal)}</span>
            </div>
            <div class="summary-row">
              <span>Giảm giá khuyến mãi</span>
              <span class="tabular-nums" style="color:var(--status-success);">−${formatVND(discount)}</span>
            </div>
            <div class="summary-row">
              <span>Phí giao hàng</span>
              <span class="tabular-nums">${shipping === 0 ? 'Miễn phí' : formatVND(shipping)}</span>
            </div>
            <div class="summary-row total">
              <span>Tổng cộng</span>
              <span class="tabular-nums" style="color:var(--accent-brand);">${formatVND(total)}</span>
            </div>

            <button type="submit" class="btn btn-primary btn-lg" style="width:100%; margin-top:16px;">
              Xác nhận đặt hàng • ${formatVND(total)}
            </button>
          </div>
        </form>
      </section>
    `;
  }

  function setDeliveryMode(mode) {
    state.deliveryMode = mode;
    saveCart();
    render();
  }

  function setBranch(maCN) {
    state.selectedBranch = maCN;
  }

  function clearLastOrderAndGo(route) {
    state.lastCreatedOrder = null;
    navigate(route);
  }

  async function submitOrder(e) {
    e.preventDefault();
    const hoTen = document.getElementById('coHoTen')?.value.trim();
    const soDienThoai = document.getElementById('coSDT')?.value.trim();
    const diaChi = document.getElementById('coDiaChi')?.value.trim() || 'Nhận tại quầy';
    const ghiChu = document.getElementById('coGhiChu')?.value.trim() || '';
    const maCN = document.getElementById('coBranch')?.value || state.selectedBranch;
    const paymentRadio = document.querySelector('input[name="coPayment"]:checked');
    const phuongThucThanhToan = paymentRadio ? paymentRadio.value : 'TIEN_MAT';

    const { appliedPromo } = calculateCartTotals();

    const payload = {
      hoTen,
      soDienThoai,
      hinhThucNhan: state.deliveryMode,
      diaChi,
      ghiChu,
      maCN,
      maKH: state.customer ? state.customer.maKH : null,
      maKM: appliedPromo ? appliedPromo.maKM : null,
      phuongThucThanhToan,
      items: state.cart
    };

    try {
      const res = await fetch('/api/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await res.json();
      if (!res.ok) {
        showToast(data.error || 'Lỗi khi tạo đơn hàng');
        return;
      }
      state.orders = data.orders || state.orders;
      state.lastCreatedOrder = data.order;
      state.cart = [];
      state.promoCode = '';
      saveCart();
      showToast(`Đặt hàng thành công! Mã đơn #${data.order.maDH}`);
      render();
    } catch (err) {
      showToast('Không thể gửi đơn hàng tới máy chủ.');
    }
  }

  function renderOrderTimeline(trangThai) {
    if (trangThai === 'DA_HUY') {
      return `
        <div style="padding:12px 16px; border-radius:var(--radius-sm); background:var(--status-danger-bg); border:1px solid var(--status-danger-border); color:var(--status-danger); font-weight:600; margin:12px 0;">
          ✕ Đơn hàng này đã bị hủy (DA_HUY).
        </div>
      `;
    }
    const steps = [
      { code: 'CHO_XAC_NHAN', label: '1. Chờ xác nhận' },
      { code: 'DA_XAC_NHAN', label: '2. Đã xác nhận' },
      { code: 'DANG_CHUAN_BI', label: '3. Đang chuẩn bị' },
      { code: 'SAN_SANG', label: '4. Sẵn sàng / Giao' },
      { code: 'DA_HOAN_THANH', label: '5. Hoàn thành' }
    ];
    const currentStep = ORDER_STATUS_META[trangThai]?.step || 1;
    return `
      <div class="status-timeline">
        ${steps.map((s, idx) => {
          const stepNum = idx + 1;
          const cls = stepNum < currentStep ? 'done' : (stepNum === currentStep ? 'current' : '');
          return `<div class="timeline-step ${cls}">${s.label}</div>`;
        }).join('')}
      </div>
    `;
  }

  // ==========================================================================
  // VIEW 4: CUSTOMER ACCOUNT & ORDER HISTORY / TRACKING
  // ==========================================================================
  function renderAccount() {
    if (!state.customer) {
      return `
        <section class="section-block" style="max-width:880px; margin:0 auto;">
          <div class="two-col-layout" style="grid-template-columns:1fr 1fr;">
            <!-- LOGIN CARD -->
            <div class="surface-card">
              <h2 style="font-size:1.2rem; font-weight:600; margin-bottom:6px;">Đăng nhập khách hàng</h2>
              <p style="font-size:0.84rem; color:var(--text-muted); margin-bottom:16px;">
                Nhập Số điện thoại, Email hoặc Mã khách hàng để xem lịch sử đơn hàng.
              </p>
              <form onsubmit="App.loginCustomer(event)" style="display:flex; flex-direction:column; gap:12px;">
                <div class="form-field">
                  <label class="field-label">Số điện thoại / Email / Mã KH</label>
                  <input type="text" id="custLoginId" class="input-control" required placeholder="VD: 0921000001 hoặc anh@gmail.com" value="0921000001" />
                </div>
                <div class="form-field">
                  <label class="field-label">Mật khẩu (Mặc định demo: 123456)</label>
                  <input type="password" id="custLoginPass" class="input-control" required value="123456" />
                </div>
                <button type="submit" class="btn btn-primary" style="margin-top:4px;">Đăng nhập</button>
              </form>

              <div style="margin-top:18px; padding-top:14px; border-top:1px solid var(--border-subtle);">
                <div style="font-size:0.78rem; color:var(--text-muted); margin-bottom:8px;">Chọn nhanh tài khoản mẫu trong CSDL để kiểm thử:</div>
                <div style="display:flex; flex-wrap:wrap; gap:6px;">
                  <button class="btn btn-secondary btn-sm" onclick="App.quickCustomerLogin('0921000001')">KH001 • Nguyễn Minh Anh</button>
                  <button class="btn btn-secondary btn-sm" onclick="App.quickCustomerLogin('0921000002')">KH002 • Trần Quốc Bảo</button>
                  <button class="btn btn-secondary btn-sm" onclick="App.quickCustomerLogin('0921000004')">KH004 • Phạm Hoàng Nam</button>
                </div>
              </div>
            </div>

            <!-- REGISTER CARD -->
            <div class="surface-card">
              <h2 style="font-size:1.2rem; font-weight:600; margin-bottom:6px;">Đăng ký thành viên mới</h2>
              <p style="font-size:0.84rem; color:var(--text-muted); margin-bottom:16px;">
                Tạo tài khoản khách hàng lưu trực tiếp vào bảng <code class="mono">khach_hang</code>.
              </p>
              <form onsubmit="App.registerCustomer(event)" style="display:flex; flex-direction:column; gap:12px;">
                <div class="form-field">
                  <label class="field-label">Họ và tên *</label>
                  <input type="text" id="regHoTen" class="input-control" required placeholder="Nguyễn Quốc Tiên" />
                </div>
                <div class="form-field">
                  <label class="field-label">Số điện thoại *</label>
                  <input type="tel" id="regSDT" class="input-control" required placeholder="0988123456" />
                </div>
                <div class="form-field">
                  <label class="field-label">Email</label>
                  <input type="email" id="regEmail" class="input-control" placeholder="tien@student.hcmute.edu.vn" />
                </div>
                <div class="form-field">
                  <label class="field-label">Địa chỉ mặc định</label>
                  <input type="text" id="regDiaChi" class="input-control" placeholder="Số 1 Võ Văn Ngân, TP. Thủ Đức, TP.HCM" />
                </div>
                <button type="submit" class="btn btn-secondary" style="margin-top:4px;">Tạo tài khoản mới</button>
              </form>
            </div>
          </div>
        </section>
      `;
    }

    const kh = state.customer;
    const myOrders = state.orders.filter(o => o.maKH === kh.maKH);
    const totalSpent = myOrders
      .filter(o => o.trangThai === 'DA_HOAN_THANH')
      .reduce((s, o) => s + Number(o.tongTien), 0);

    return `
      <section class="section-block">
        <div class="section-header">
          <div>
            <div style="display:flex; align-items:center; gap:10px;">
              <h1 class="section-title" style="font-size:1.5rem;">Xin chào, ${escapeHtml(kh.hoTen)}</h1>
              <span class="mono badge badge-neutral">${escapeHtml(kh.maKH)}</span>
            </div>
            <p class="section-desc">SĐT: ${escapeHtml(kh.soDienThoai)} • Địa chỉ: ${escapeHtml(kh.diaChi || 'Chưa cập nhật')} • Tổng chi tiêu hoàn tất: <strong>${formatVND(totalSpent)}</strong></p>
          </div>
          <button class="btn btn-secondary btn-sm" onclick="App.logoutCustomer()">Đăng xuất</button>
        </div>

        <div style="display:flex; gap:8px; margin-bottom:20px;">
          <button class="btn ${state.accountTab === 'orders' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAccountTab('orders')">
            Lịch sử &amp; Theo dõi đơn hàng (${myOrders.length})
          </button>
          <button class="btn ${state.accountTab === 'profile' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAccountTab('profile')">
            Thông tin cá nhân
          </button>
        </div>

        ${state.accountTab === 'profile' ? `
          <div class="surface-card" style="max-width:600px;">
            <h3 style="font-size:1.05rem; font-weight:600; margin-bottom:14px;">Cập nhật hồ sơ cá nhân</h3>
            <form onsubmit="App.updateCustomerProfile(event)" class="form-grid">
              <div class="form-field full">
                <label class="field-label">Họ và tên</label>
                <input type="text" id="profHoTen" class="input-control" required value="${escapeHtml(kh.hoTen)}" />
              </div>
              <div class="form-field">
                <label class="field-label">Số điện thoại</label>
                <input type="tel" id="profSDT" class="input-control" required value="${escapeHtml(kh.soDienThoai)}" />
              </div>
              <div class="form-field">
                <label class="field-label">Email</label>
                <input type="email" id="profEmail" class="input-control" value="${escapeHtml(kh.email || '')}" />
              </div>
              <div class="form-field full">
                <label class="field-label">Địa chỉ giao hàng mặc định</label>
                <input type="text" id="profDiaChi" class="input-control" value="${escapeHtml(kh.diaChi || '')}" />
              </div>
              <div class="form-field full">
                <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
              </div>
            </form>
          </div>
        ` : `
          <div style="display:flex; flex-direction:column; gap:16px;">
            ${myOrders.length === 0 ? `
              <div class="surface-card" style="text-align:center; padding:48px 20px;">
                <p style="color:var(--text-muted); margin-bottom:14px;">Bạn chưa có đơn hàng nào.</p>
                <button class="btn btn-primary btn-sm" onclick="App.navigate('menu')">Đặt món ngay</button>
              </div>
            ` : myOrders.map(ord => {
              const st = ORDER_STATUS_META[ord.trangThai] || { label: ord.trangThai, badge: 'badge-neutral' };
              return `
                <div class="surface-card">
                  <div style="display:flex; flex-wrap:wrap; align-items:center; justify-content:space-between; gap:10px; padding-bottom:12px; border-bottom:1px solid var(--border-subtle);">
                    <div style="display:flex; align-items:center; gap:10px;">
                      <span class="mono" style="font-weight:700; font-size:1rem;">Đơn #${ord.maDH}</span>
                      <span class="badge ${st.badge}">${st.label}</span>
                      <span style="font-size:0.82rem; color:var(--text-muted);">${escapeHtml(ord.ngayDat)} • ${escapeHtml(ord.tenCN)}</span>
                    </div>
                    <div class="tabular-nums" style="font-weight:700; font-size:1.05rem;">${formatVND(ord.tongTien)}</div>
                  </div>

                  ${renderOrderTimeline(ord.trangThai)}

                  <div style="display:flex; flex-direction:column; gap:6px; font-size:0.86rem; margin-top:10px;">
                    ${(ord.items || []).map(it => `
                      <div style="display:flex; justify-content:space-between;">
                        <span>${it.soLuong}x ${escapeHtml(it.tenMon)} <span style="color:var(--text-muted);">(${formatVND(it.donGia)}/phần)</span></span>
                        <span class="tabular-nums">${formatVND(it.thanhTien)}</span>
                      </div>
                    `).join('')}
                  </div>
                </div>
              `;
            }).join('')}
          </div>
        `}
      </section>
    `;
  }

  async function loginCustomer(e) {
    if (e) e.preventDefault();
    const identifier = document.getElementById('custLoginId')?.value.trim();
    const password = document.getElementById('custLoginPass')?.value.trim();
    await performCustomerLogin(identifier, password);
  }

  async function quickCustomerLogin(identifier) {
    await performCustomerLogin(identifier, '123456');
  }

  async function performCustomerLogin(identifier, password) {
    try {
      const res = await fetch('/api/auth/customer/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ identifier, password })
      });
      const data = await res.json();
      if (!res.ok) {
        showToast(data.error || 'Đăng nhập thất bại');
        return;
      }
      state.customer = data.customer;
      localStorage.setItem('ff17_customer', JSON.stringify(state.customer));
      updateHeaderUI();
      showToast(`Chào mừng ${state.customer.hoTen}!`);
      render();
    } catch (err) {
      showToast('Lỗi kết nối khi đăng nhập.');
    }
  }

  async function registerCustomer(e) {
    e.preventDefault();
    const payload = {
      hoTen: document.getElementById('regHoTen')?.value.trim(),
      soDienThoai: document.getElementById('regSDT')?.value.trim(),
      email: document.getElementById('regEmail')?.value.trim(),
      diaChi: document.getElementById('regDiaChi')?.value.trim()
    };
    try {
      const res = await fetch('/api/auth/customer/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await res.json();
      if (!res.ok) {
        showToast(data.error || 'Không thể đăng ký');
        return;
      }
      state.customer = data.customer;
      localStorage.setItem('ff17_customer', JSON.stringify(state.customer));
      await loadBootstrap();
      updateHeaderUI();
      showToast(`Đã tạo tài khoản ${state.customer.maKH} thành công!`);
      render();
    } catch (err) {
      showToast('Lỗi kết nối khi đăng ký.');
    }
  }

  async function updateCustomerProfile(e) {
    e.preventDefault();
    const payload = {
      maKH: state.customer.maKH,
      hoTen: document.getElementById('profHoTen')?.value.trim(),
      soDienThoai: document.getElementById('profSDT')?.value.trim(),
      email: document.getElementById('profEmail')?.value.trim(),
      diaChi: document.getElementById('profDiaChi')?.value.trim()
    };
    try {
      const res = await fetch('/api/auth/customer/profile', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await res.json();
      if (res.ok) {
        state.customer = data.customer;
        localStorage.setItem('ff17_customer', JSON.stringify(state.customer));
        updateHeaderUI();
        showToast('Đã cập nhật thông tin cá nhân!');
        render();
      }
    } catch (err) {
      showToast('Không thể cập nhật thông tin.');
    }
  }

  function logoutCustomer() {
    state.customer = null;
    localStorage.removeItem('ff17_customer');
    updateHeaderUI();
    showToast('Đã đăng xuất tài khoản khách hàng.');
    render();
  }

  function setAccountTab(tab) {
    state.accountTab = tab;
    render();
  }

  // ==========================================================================
  // VIEW 5: ADMIN PORTAL (Role-based Auth, Dashboard, Orders, Dishes, Promos, 30 Lambda Queries)
  // ==========================================================================
  async function loadAdminDashboard() {
    try {
      const res = await fetch('/api/admin/dashboard');
      state.dashboard = await res.json();
      if (!state.activeQueryResult) {
        await runLambdaQuery(state.activeQueryNum);
      }
      render();
    } catch (err) {
      console.error(err);
    }
  }

  function renderAdmin() {
    if (!state.admin) {
      return `
        <section class="section-block" style="max-width:480px; margin:24px auto;">
          <div class="surface-card" style="padding:28px;">
            <div class="badge badge-neutral" style="margin-bottom:10px;">Hệ thống Quản trị &amp; Phân quyền</div>
            <h1 style="font-size:1.35rem; font-weight:700; margin-bottom:6px;">Đăng nhập Quản trị (Admin / Quản lý)</h1>
            <p style="font-size:0.84rem; color:var(--text-muted); margin-bottom:18px;">
              Xác thực trực tiếp từ bảng <code class="mono">tai_khoan</code> và <code class="mono">nhan_vien</code>.
            </p>
            <form onsubmit="App.loginAdmin(event)" style="display:flex; flex-direction:column; gap:12px;">
              <div class="form-field">
                <label class="field-label">Tên đăng nhập</label>
                <input type="text" id="admUser" class="input-control" required value="admin01" />
              </div>
              <div class="form-field">
                <label class="field-label">Mật khẩu (Demo: 123456)</label>
                <input type="password" id="admPass" class="input-control" required value="123456" />
              </div>
              <button type="submit" class="btn btn-primary" style="margin-top:4px;">Đăng nhập Quản trị →</button>
            </form>

            <div style="margin-top:18px; padding-top:14px; border-top:1px solid var(--border-subtle); font-size:0.78rem; color:var(--text-muted);">
              <div style="margin-bottom:8px; font-weight:500;">Chọn nhanh tài khoản để kiểm thử phân quyền:</div>
              <div style="display:flex; flex-wrap:wrap; gap:6px;">
                <button class="btn btn-secondary btn-sm" onclick="App.quickAdminLogin('admin01')">admin01 (ADMIN)</button>
                <button class="btn btn-secondary btn-sm" onclick="App.quickAdminLogin('nv01')">nv01 (QUẢN LÝ CN01)</button>
                <button class="btn btn-danger btn-sm" onclick="App.quickAdminLogin('nv04')">nv04 (Test TK Bị Khóa)</button>
              </div>
            </div>
          </div>
        </section>
      `;
    }

    const adm = state.admin;
    return `
      <section class="section-block">
        <div class="section-header">
          <div>
            <div style="display:flex; align-items:center; gap:10px;">
              <h1 class="section-title" style="font-size:1.5rem;">Trung tâm Quản trị Chuỗi Cửa Hàng</h1>
              <span class="badge badge-brand">${escapeHtml(adm.chucVu)}</span>
              <span class="mono badge badge-neutral">${escapeHtml(adm.tenDangNhap)} • ${escapeHtml(adm.hoTen)}</span>
            </div>
            <p class="section-desc">Quản lý thực đơn, điều phối đơn hàng, khuyến mãi và thực thi 30 bài toán nghiệp vụ Java 17 Stream API &amp; Lambda</p>
          </div>
          <button class="btn btn-secondary btn-sm" onclick="App.logoutAdmin()">Đăng xuất Quản trị</button>
        </div>

        <!-- ADMIN TABS -->
        <div style="display:flex; flex-wrap:wrap; gap:8px; margin-bottom:22px; border-bottom:1px solid var(--border-subtle); padding-bottom:12px;">
          <button class="btn ${state.adminTab === 'dashboard' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAdminTab('dashboard')">
            📊 Dashboard Thống kê
          </button>
          <button class="btn ${state.adminTab === 'orders' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAdminTab('orders')">
            📦 Quản lý Đơn hàng (${state.orders.length})
          </button>
          <button class="btn ${state.adminTab === 'dishes' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAdminTab('dishes')">
            🍔 Quản lý Món ăn (${state.dishes.length})
          </button>
          <button class="btn ${state.adminTab === 'promotions' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAdminTab('promotions')">
            🎟️ Quản lý Khuyến mãi (${state.promotions.length})
          </button>
          <button class="btn ${state.adminTab === 'queries' ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAdminTab('queries')">
            ⚡ 30 Truy vấn Lambda (MIS)
          </button>
        </div>

        ${state.adminTab === 'dashboard' ? renderAdminDashboardTab() : ''}
        ${state.adminTab === 'orders' ? renderAdminOrdersTab() : ''}
        ${state.adminTab === 'dishes' ? renderAdminDishesTab() : ''}
        ${state.adminTab === 'promotions' ? renderAdminPromotionsTab() : ''}
        ${state.adminTab === 'queries' ? renderAdminQueriesTab() : ''}
      </section>
    `;
  }

  function renderAdminDashboardTab() {
    const d = state.dashboard;
    if (!d) {
      return `<div class="surface-card">Đang tải dữ liệu thống kê...</div>`;
    }
    const monthly = d.doanhThuTheoThang || {};
    const maxMonthVal = Math.max(1, ...Object.values(monthly).map(Number));
    const branchRev = d.doanhThuTheoChiNhanh || {};
    const topDishes = d.top3MonBanChay || [];

    return `
      <div class="kpi-grid">
        <div class="kpi-card">
          <div class="kpi-label">Tổng doanh thu hoàn thành</div>
          <div class="kpi-value" style="color:var(--status-success);">${formatVND(d.tongDoanhThu)}</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Tổng số đơn hàng</div>
          <div class="kpi-value">${d.tongSoDon} đơn</div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Đơn hoàn thành / Đang xử lý</div>
          <div class="kpi-value">${d.donHoanThanh} <span style="font-size:0.95rem; color:var(--text-muted); font-weight:500;">/ ${d.donDangXuLy} đang xử lý</span></div>
        </div>
        <div class="kpi-card">
          <div class="kpi-label">Giá trị đơn trung bình (AOV)</div>
          <div class="kpi-value">${formatVND(d.giaTriDonTrungBinh)}</div>
        </div>
      </div>

      <div class="two-col-layout">
        <!-- MONTHLY REVENUE CHART -->
        <div class="surface-card">
          <h3 style="font-size:1rem; font-weight:600; margin-bottom:16px;">Biểu đồ Doanh thu theo tháng (Năm 2026 — Câu 11 Lambda)</h3>
          <div style="display:flex; flex-direction:column; gap:12px;">
            ${Object.entries(monthly).map(([m, val]) => {
              const pct = Math.round((Number(val) / maxMonthVal) * 100);
              return `
                <div>
                  <div style="display:flex; justify-content:space-between; font-size:0.84rem; margin-bottom:4px;">
                    <span style="font-weight:600;">Tháng 0${m}/2026</span>
                    <span class="tabular-nums" style="font-weight:600;">${formatVND(val)}</span>
                  </div>
                  <div style="height:10px; background:var(--bg-subtle); border-radius:var(--radius-full); overflow:hidden;">
                    <div style="width:${pct}%; height:100%; background:var(--text-primary); border-radius:var(--radius-full);"></div>
                  </div>
                </div>
              `;
            }).join('')}
          </div>
        </div>

        <!-- TOP 3 SELLING DISHES & BRANCH REVENUE -->
        <div style="display:flex; flex-direction:column; gap:18px;">
          <div class="surface-card">
            <h3 style="font-size:1rem; font-weight:600; margin-bottom:12px;">Top Món ăn bán chạy nhất (Câu 12 Lambda)</h3>
            <div style="display:flex; flex-direction:column; gap:10px;">
              ${topDishes.map((item, i) => `
                <div style="display:flex; align-items:center; justify-content:space-between; padding:8px 12px; background:var(--bg-canvas); border:1px solid var(--border-subtle); border-radius:var(--radius-sm);">
                  <div style="display:flex; align-items:center; gap:10px;">
                    <span class="badge badge-brand">#${i + 1}</span>
                    <span style="font-weight:600;">${escapeHtml(item.tenMon)}</span>
                    <span class="mono" style="color:var(--text-muted);">(${item.maMon})</span>
                  </div>
                  <span class="tabular-nums" style="font-weight:700;">${item.soLuong} phần</span>
                </div>
              `).join('')}
            </div>
          </div>

          <div class="surface-card">
            <h3 style="font-size:1rem; font-weight:600; margin-bottom:12px;">Doanh thu theo Chi nhánh (Câu 28 Lambda)</h3>
            <div style="display:flex; flex-direction:column; gap:8px;">
              ${Object.entries(branchRev).map(([cn, val]) => `
                <div class="summary-row" style="border-bottom:1px solid var(--border-subtle);">
                  <span style="font-weight:600;">Chi nhánh ${escapeHtml(cn)}</span>
                  <span class="tabular-nums" style="font-weight:600;">${formatVND(val)}</span>
                </div>
              `).join('')}
            </div>
          </div>
        </div>
      </div>
    `;
  }

  function renderAdminOrdersTab() {
    const filtered = state.orders.filter(o => state.adminOrderFilter === 'ALL' || o.trangThai === state.adminOrderFilter);
    const statuses = ['ALL', 'CHO_XAC_NHAN', 'DA_XAC_NHAN', 'DANG_CHUAN_BI', 'SAN_SANG', 'DA_HOAN_THANH', 'DA_HUY'];

    return `
      <div style="display:flex; flex-wrap:wrap; gap:6px; margin-bottom:16px;">
        ${statuses.map(st => `
          <button class="btn ${state.adminOrderFilter === st ? 'btn-primary' : 'btn-secondary'} btn-sm" onclick="App.setAdminOrderFilter('${st}')">
            ${st === 'ALL' ? `Tất cả (${state.orders.length})` : (ORDER_STATUS_META[st]?.label || st)}
          </button>
        `).join('')}
      </div>

      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>Mã đơn</th>
              <th>Thời gian</th>
              <th>Khách hàng</th>
              <th>Chi nhánh</th>
              <th>Món đã đặt</th>
              <th>Tổng tiền</th>
              <th>Trạng thái hiện tại</th>
              <th>Cập nhật trạng thái</th>
            </tr>
          </thead>
          <tbody>
            ${filtered.map(ord => {
              const st = ORDER_STATUS_META[ord.trangThai] || { label: ord.trangThai, badge: 'badge-neutral' };
              return `
                <tr>
                  <td class="mono" style="font-weight:700;">#${ord.maDH}</td>
                  <td style="font-size:0.8rem; color:var(--text-secondary);">${escapeHtml(ord.ngayDat)}</td>
                  <td>
                    <div style="font-weight:600;">${escapeHtml(ord.tenKH)}</div>
                    <div style="font-size:0.76rem; color:var(--text-muted);">${escapeHtml(ord.soDienThoai || ord.maKH || 'Vãng lai')}</div>
                  </td>
                  <td><span class="badge badge-neutral">${escapeHtml(ord.maCN)}</span></td>
                  <td style="font-size:0.8rem; max-width:240px;">
                    ${(ord.items || []).map(i => `${i.soLuong}x ${escapeHtml(i.tenMon)}`).join(', ')}
                  </td>
                  <td class="tabular-nums" style="font-weight:700;">${formatVND(ord.tongTien)}</td>
                  <td><span class="badge ${st.badge}">${st.label}</span></td>
                  <td>
                    <select class="select-control" style="height:30px; font-size:0.8rem; padding:0 8px;" onchange="App.updateOrderStatus(${ord.maDH}, this.value)">
                      ${Object.entries(ORDER_STATUS_META).map(([k, v]) => `
                        <option value="${k}" ${ord.trangThai === k ? 'selected' : ''}>${v.label}</option>
                      `).join('')}
                    </select>
                  </td>
                </tr>
              `;
            }).join('')}
          </tbody>
        </table>
      </div>
    `;
  }

  function renderAdminDishesTab() {
    const ed = state.editingDish || {};
    return `
      <div class="two-col-layout" style="grid-template-columns: 0.9fr 1.4fr;">
        <!-- ADD / EDIT DISH FORM -->
        <div class="surface-card">
          <h3 style="font-size:1.02rem; font-weight:600; margin-bottom:14px;">
            ${ed.maMon ? `Chỉnh sửa món ${ed.maMon}` : 'Thêm món ăn mới vào CSDL'}
          </h3>
          <form onsubmit="App.saveDishForm(event)" style="display:flex; flex-direction:column; gap:12px;">
            <div class="form-field">
              <label class="field-label">Tên món ăn *</label>
              <input type="text" id="dishFormName" class="input-control" required placeholder="VD: Burger Bò Phô Mai Đặc Biệt" value="${escapeHtml(ed.tenMon || '')}" />
            </div>
            <div class="form-grid">
              <div class="form-field">
                <label class="field-label">Danh mục *</label>
                <select id="dishFormDM" class="select-control">
                  ${state.categories.map(dm => `
                    <option value="${dm.maDM}" ${ed.maDM === dm.maDM ? 'selected' : ''}>${escapeHtml(dm.tenDM)} (${dm.maDM})</option>
                  `).join('')}
                </select>
              </div>
              <div class="form-field">
                <label class="field-label">Giá bán (VNĐ) *</label>
                <input type="number" id="dishFormPrice" class="input-control" required min="5000" step="1000" placeholder="55000" value="${ed.giaBan || ''}" />
              </div>
            </div>
            <div class="form-field">
              <label class="field-label">Trạng thái kinh doanh</label>
              <select id="dishFormStatus" class="select-control">
                <option value="DANG_BAN" ${ed.trangThai !== 'NGUNG_BAN' ? 'selected' : ''}>ĐANG BÁN (Còn phục vụ)</option>
                <option value="NGUNG_BAN" ${ed.trangThai === 'NGUNG_BAN' ? 'selected' : ''}>NGỪNG BÁN (Tạm ẩn / Hết hàng)</option>
              </select>
            </div>
            <div class="form-field">
              <label class="field-label">URL Hình ảnh món ăn</label>
              <input type="url" id="dishFormImg" class="input-control" placeholder="https://images.unsplash.com/..." value="${escapeHtml(ed.hinhAnh || '')}" />
            </div>
            <div class="form-field">
              <label class="field-label">Mô tả nguyên liệu &amp; hương vị</label>
              <textarea id="dishFormDesc" class="textarea-control" rows="3" placeholder="Mô tả chi tiết món ăn...">${escapeHtml(ed.moTa || '')}</textarea>
            </div>
            <div style="display:flex; gap:8px;">
              <button type="submit" class="btn btn-primary" style="flex:1;">
                ${ed.maMon ? 'Lưu cập nhật' : '+ Thêm món mới'}
              </button>
              ${ed.maMon ? `<button type="button" class="btn btn-secondary" onclick="App.cancelEditDish()">Hủy</button>` : ''}
            </div>
          </form>
        </div>

        <!-- DISHES TABLE -->
        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>Mã</th>
                <th>Món ăn</th>
                <th>Danh mục</th>
                <th>Giá bán</th>
                <th>Trạng thái</th>
                <th>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              ${state.dishes.map(d => `
                <tr>
                  <td class="mono">${d.maMon}</td>
                  <td>
                    <div style="display:flex; align-items:center; gap:10px;">
                      <img src="${escapeHtml(d.hinhAnh)}" alt="" style="width:40px; height:40px; border-radius:6px; object-fit:cover;" />
                      <div>
                        <div style="font-weight:600;">${escapeHtml(d.tenMon)}</div>
                        <div style="font-size:0.75rem; color:var(--text-muted);">Đã bán: ${d.soLuongBan} • Tồn: ${d.tonKho}</div>
                      </div>
                    </div>
                  </td>
                  <td>${escapeHtml(d.tenDM)}</td>
                  <td class="tabular-nums" style="font-weight:600;">${formatVND(d.giaBan)}</td>
                  <td>
                    <span class="badge ${d.trangThai === 'DANG_BAN' ? 'badge-success' : 'badge-danger'}">
                      ${d.trangThai === 'DANG_BAN' ? 'Đang bán' : 'Ngừng bán'}
                    </span>
                  </td>
                  <td>
                    <div style="display:flex; gap:6px;">
                      <button class="btn btn-secondary btn-sm" onclick="App.startEditDish('${d.maMon}')">Sửa</button>
                      <button class="btn btn-danger btn-sm" onclick="App.deleteDish('${d.maMon}')">Xóa</button>
                    </div>
                  </td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }

  function renderAdminPromotionsTab() {
    return `
      <div class="two-col-layout" style="grid-template-columns: 0.9fr 1.4fr;">
        <div class="surface-card">
          <h3 style="font-size:1.02rem; font-weight:600; margin-bottom:14px;">Thêm / Cập nhật Mã Khuyến Mãi</h3>
          <form onsubmit="App.savePromoForm(event)" style="display:flex; flex-direction:column; gap:12px;">
            <div class="form-grid">
              <div class="form-field">
                <label class="field-label">Mã KM *</label>
                <input type="text" id="kmCode" class="input-control mono" required placeholder="VD: KM05" />
              </div>
              <div class="form-field">
                <label class="field-label">Phần trăm giảm (%) *</label>
                <input type="number" id="kmPercent" class="input-control" required min="1" max="90" value="15" />
              </div>
            </div>
            <div class="form-field">
              <label class="field-label">Tên chương trình khuyến mãi *</label>
              <input type="text" id="kmName" class="input-control" required placeholder="Ưu đãi cuối tuần giảm 15%" />
            </div>
            <div class="form-grid">
              <div class="form-field">
                <label class="field-label">Đơn tối thiểu (VNĐ)</label>
                <input type="number" id="kmMin" class="input-control" value="100000" step="10000" />
              </div>
              <div class="form-field">
                <label class="field-label">Giảm tối đa (VNĐ)</label>
                <input type="number" id="kmMax" class="input-control" value="30000" step="5000" />
              </div>
            </div>
            <div class="form-field">
              <label class="field-label">Trạng thái</label>
              <select id="kmStatus" class="select-control">
                <option value="DANG_AP_DUNG">DANG_AP_DUNG (Đang hoạt động)</option>
                <option value="TAM_DUNG">TAM_DUNG (Tạm dừng)</option>
                <option value="HET_HAN">HET_HAN (Hết hạn)</option>
              </select>
            </div>
            <button type="submit" class="btn btn-primary">Lưu chương trình khuyến mãi</button>
          </form>
        </div>

        <div class="table-wrap">
          <table class="data-table">
            <thead>
              <tr>
                <th>Mã KM</th>
                <th>Tên chương trình</th>
                <th>Mức giảm</th>
                <th>Đơn tối thiểu</th>
                <th>Trần giảm</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              ${state.promotions.map(km => `
                <tr>
                  <td class="mono" style="font-weight:700; color:var(--accent-brand);">${escapeHtml(km.maKM)}</td>
                  <td style="font-weight:500;">${escapeHtml(km.tenKM)}</td>
                  <td class="tabular-nums"><strong>${km.phanTramGiam}%</strong></td>
                  <td class="tabular-nums">${formatVND(km.donToiThieu)}</td>
                  <td class="tabular-nums">${km.giamToiDa ? formatVND(km.giamToiDa) : 'Không giới hạn'}</td>
                  <td>
                    <span class="badge ${km.trangThai === 'DANG_AP_DUNG' ? 'badge-success' : 'badge-neutral'}">
                      ${escapeHtml(km.trangThai)}
                    </span>
                  </td>
                </tr>
              `).join('')}
            </tbody>
          </table>
        </div>
      </div>
    `;
  }

  function renderAdminQueriesTab() {
    const qInfo = QUERIES_CATALOG.find(q => q.num === state.activeQueryNum) || QUERIES_CATALOG[10];
    const results = state.activeQueryResult || [];

    return `
      <div class="two-col-layout" style="grid-template-columns: 0.95fr 1.25fr;">
        <div class="surface-card" style="max-height:560px; overflow-y:auto;">
          <h3 style="font-size:0.98rem; font-weight:600; margin-bottom:10px;">Danh sách 30 Bài toán Nghiệp vụ (Java Stream &amp; Lambda)</h3>
          <div style="display:flex; flex-direction:column; gap:6px;">
            ${QUERIES_CATALOG.map(q => `
              <button
                type="button"
                class="category-card ${state.activeQueryNum === q.num ? 'active' : ''}"
                style="padding:9px 12px;"
                onclick="App.runLambdaQuery(${q.num})"
              >
                <span class="mono" style="font-weight:700; min-width:36px;">Q${String(q.num).padStart(2, '0')}</span>
                <div style="flex:1;">
                  <div style="font-size:0.84rem; font-weight:600;">${escapeHtml(q.title)}</div>
                  <div class="category-count" style="font-size:0.72rem;">Phụ trách: ${escapeHtml(q.author)}</div>
                </div>
              </button>
            `).join('')}
          </div>
        </div>

        <div class="surface-card">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:12px;">
            <span class="badge badge-brand">Câu truy vấn #${qInfo.num}</span>
            <span style="font-size:0.8rem; color:var(--text-muted);">${escapeHtml(qInfo.author)}</span>
          </div>
          <h3 style="font-size:1.15rem; font-weight:700; margin-bottom:14px;">${escapeHtml(qInfo.title)}</h3>
          <div style="background:#18181B; color:#F4F4F5; padding:18px; border-radius:var(--radius-md); font-family:var(--font-mono); font-size:0.83rem; line-height:1.65; overflow-x:auto; min-height:260px;">
            <div style="color:#A1A1AA; margin-bottom:10px; border-bottom:1px solid #27272A; padding-bottom:6px;">
              // Kết quả thực thi trực tiếp từ QueryLambdaService.java (${results.length} dòng)
            </div>
            ${results.length === 0
              ? `<div style="color:#FBBF24;">(Tập kết quả rỗng — 0 bản ghi thỏa mãn điều kiện)</div>`
              : results.map(line => `<div>${escapeHtml(line)}</div>`).join('')}
          </div>
        </div>
      </div>
    `;
  }

  async function loginAdmin(e) {
    if (e) e.preventDefault();
    const username = document.getElementById('admUser')?.value.trim();
    const password = document.getElementById('admPass')?.value.trim();
    await performAdminLogin(username, password);
  }

  async function quickAdminLogin(username) {
    await performAdminLogin(username, '123456');
  }

  async function performAdminLogin(username, password) {
    try {
      const res = await fetch('/api/auth/admin/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      const data = await res.json();
      if (!res.ok) {
        showToast(data.error || 'Đăng nhập quản trị thất bại');
        return;
      }
      state.admin = data.admin;
      localStorage.setItem('ff17_admin', JSON.stringify(state.admin));
      showToast(`Xin chào ${state.admin.hoTen} (${state.admin.chucVu})`);
      await loadAdminDashboard();
    } catch (err) {
      showToast('Lỗi kết nối máy chủ.');
    }
  }

  function logoutAdmin() {
    state.admin = null;
    localStorage.removeItem('ff17_admin');
    showToast('Đã đăng xuất phiên quản trị.');
    render();
  }

  function setAdminTab(tab) {
    state.adminTab = tab;
    if (tab === 'dashboard') loadAdminDashboard();
    else render();
  }

  function setAdminOrderFilter(filter) {
    state.adminOrderFilter = filter;
    render();
  }

  async function updateOrderStatus(maDH, trangThai) {
    try {
      const res = await fetch(`/api/orders/${maDH}/status`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ trangThai })
      });
      const data = await res.json();
      if (res.ok) {
        state.orders = data.orders || state.orders;
        showToast(data.message || `Đã cập nhật đơn #${maDH}`);
        render();
      }
    } catch (err) {
      showToast('Không thể cập nhật trạng thái đơn.');
    }
  }

  function startEditDish(maMon) {
    state.editingDish = state.dishes.find(d => d.maMon === maMon) || null;
    render();
  }

  function cancelEditDish() {
    state.editingDish = null;
    render();
  }

  async function saveDishForm(e) {
    e.preventDefault();
    const payload = {
      tenMon: document.getElementById('dishFormName')?.value.trim(),
      maDM: document.getElementById('dishFormDM')?.value,
      giaBan: document.getElementById('dishFormPrice')?.value,
      trangThai: document.getElementById('dishFormStatus')?.value,
      hinhAnh: document.getElementById('dishFormImg')?.value.trim(),
      moTa: document.getElementById('dishFormDesc')?.value.trim()
    };

    const isEdit = state.editingDish && state.editingDish.maMon;
    const url = isEdit ? `/api/dishes/${state.editingDish.maMon}` : '/api/dishes';
    const method = isEdit ? 'PUT' : 'POST';

    try {
      const res = await fetch(url, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await res.json();
      if (!res.ok) {
        showToast(data.error || 'Lỗi khi lưu món ăn');
        return;
      }
      state.dishes = data.dishes || state.dishes;
      state.editingDish = null;
      showToast(data.message || 'Đã lưu món ăn');
      render();
    } catch (err) {
      showToast('Không thể lưu món ăn.');
    }
  }

  async function deleteDish(maMon) {
    try {
      const res = await fetch(`/api/dishes/${maMon}`, { method: 'DELETE' });
      const data = await res.json();
      if (res.ok) {
        state.dishes = data.dishes || state.dishes;
        showToast(data.message || `Đã xử lý món ${maMon}`);
        render();
      }
    } catch (err) {
      showToast('Lỗi khi xóa món ăn.');
    }
  }

  async function savePromoForm(e) {
    e.preventDefault();
    const payload = {
      maKM: document.getElementById('kmCode')?.value.trim(),
      tenKM: document.getElementById('kmName')?.value.trim(),
      phanTramGiam: document.getElementById('kmPercent')?.value,
      donToiThieu: document.getElementById('kmMin')?.value,
      giamToiDa: document.getElementById('kmMax')?.value,
      trangThai: document.getElementById('kmStatus')?.value
    };
    try {
      const res = await fetch('/api/promotions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      const data = await res.json();
      if (res.ok) {
        state.promotions = data.promotions || state.promotions;
        showToast('Đã lưu chương trình khuyến mãi!');
        render();
      }
    } catch (err) {
      showToast('Không thể lưu khuyến mãi.');
    }
  }

  async function runLambdaQuery(qNum) {
    state.activeQueryNum = qNum;
    try {
      const res = await fetch(`/api/admin/query/${qNum}`);
      const data = await res.json();
      state.activeQueryResult = data.results || [];
      render();
    } catch (err) {
      showToast('Lỗi khi chạy truy vấn.');
    }
  }

  // ==========================================================================
  // QUICK SEARCH MODAL (CTRL + K)
  // ==========================================================================
  function openQuickSearch() {
    const bd = document.getElementById('searchModalBackdrop');
    const inp = document.getElementById('quickSearchInput');
    if (!bd || !inp) return;
    bd.classList.add('open');
    inp.value = '';
    renderQuickSearchResults('');
    setTimeout(() => inp.focus(), 50);
  }

  function closeQuickSearch() {
    const bd = document.getElementById('searchModalBackdrop');
    if (bd) bd.classList.remove('open');
  }

  function renderQuickSearchResults(query) {
    const container = document.getElementById('quickSearchResults');
    if (!container) return;
    const q = (query || '').trim().toLowerCase();
    const matches = state.dishes
      .filter(d => !q || d.tenMon.toLowerCase().includes(q) || d.maMon.toLowerCase().includes(q) || d.tenDM.toLowerCase().includes(q))
      .slice(0, 8);

    if (matches.length === 0) {
      container.innerHTML = `<div style="padding:24px; text-align:center; color:var(--text-muted);">Không tìm thấy món ăn nào khớp với "${escapeHtml(query)}"</div>`;
      return;
    }

    container.innerHTML = matches.map(d => `
      <div
        style="display:flex; align-items:center; justify-content:space-between; padding:10px 12px; border-radius:var(--radius-sm); cursor:pointer;"
        onmouseover="this.style.background='var(--bg-subtle)'"
        onmouseout="this.style.background='transparent'"
        onclick="App.closeQuickSearch(); App.openDishModal('${d.maMon}');"
      >
        <div style="display:flex; align-items:center; gap:12px;">
          <img src="${escapeHtml(d.hinhAnh)}" alt="" style="width:42px; height:42px; border-radius:6px; object-fit:cover;" />
          <div>
            <div style="font-weight:600; font-size:0.9rem;">${escapeHtml(d.tenMon)}</div>
            <div style="font-size:0.76rem; color:var(--text-muted);">${escapeHtml(d.tenDM)} • ${d.conMon ? 'Còn món' : 'Tạm hết'}</div>
          </div>
        </div>
        <div class="tabular-nums" style="font-weight:700;">${formatVND(d.giaBan)}</div>
      </div>
    `).join('');
  }

  // Main Render Switch
  function render() {
    const root = document.getElementById('appRoot');
    if (!root) return;
    if (state.route === 'home') root.innerHTML = renderHome();
    else if (state.route === 'menu') root.innerHTML = renderMenu();
    else if (state.route === 'checkout') root.innerHTML = renderCheckout();
    else if (state.route === 'account') root.innerHTML = renderAccount();
    else if (state.route === 'admin') root.innerHTML = renderAdmin();
    else root.innerHTML = renderHome();
  }

  document.addEventListener('DOMContentLoaded', init);

  return {
    navigate,
    filterByCategory,
    setMenuFilter,
    setMenuSearch,
    setMenuPage,
    resetMenuFilters,
    openDishModal,
    closeDishModal,
    selectModalSize,
    toggleModalTopping,
    selectModalSauce,
    changeModalQty,
    confirmModalAddToCart,
    quickAddToCart,
    toggleCartDrawer,
    updateCartQty,
    removeCartItem,
    applyCouponCode,
    setDeliveryMode,
    setBranch,
    submitOrder,
    clearLastOrderAndGo,
    loginCustomer,
    quickCustomerLogin,
    registerCustomer,
    updateCustomerProfile,
    logoutCustomer,
    setAccountTab,
    loginAdmin,
    quickAdminLogin,
    logoutAdmin,
    setAdminTab,
    setAdminOrderFilter,
    updateOrderStatus,
    startEditDish,
    cancelEditDish,
    saveDishForm,
    deleteDish,
    savePromoForm,
    runLambdaQuery,
    openQuickSearch,
    closeQuickSearch,
    renderQuickSearchResults
  };
})();
