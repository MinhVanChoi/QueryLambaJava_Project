import React, { createContext, useContext, useState, useCallback, ReactNode } from 'react';

export interface CartItem {
  maMon: string;
  tenMon: string;
  giaBan: number;
  soLuong: number;
}

interface CartContextValue {
  items: CartItem[];
  maCN: string;
  maKM: string;
  addItem: (item: Omit<CartItem, 'soLuong'>) => void;
  updateQty: (maMon: string, qty: number) => void;
  removeItem: (maMon: string) => void;
  clearCart: () => void;
  setBranch: (maCN: string) => void;
  setPromotion: (maKM: string) => void;
  subtotal: number;
  totalItems: number;
}

const CartContext = createContext<CartContextValue | null>(null);

const CART_KEY = 'ff_cart';

const loadCart = (): CartItem[] => {
  try {
    const s = localStorage.getItem(CART_KEY);
    return s ? JSON.parse(s) : [];
  } catch {
    return [];
  }
};

export const CartProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>(loadCart);
  const [maCN, setMaCN] = useState('');
  const [maKM, setMaKM] = useState('');

  const persist = (next: CartItem[]) => {
    setItems(next);
    localStorage.setItem(CART_KEY, JSON.stringify(next));
  };

  const addItem = useCallback((item: Omit<CartItem, 'soLuong'>) => {
    setItems(prev => {
      const existing = prev.find(i => i.maMon === item.maMon);
      const next = existing
        ? prev.map(i => i.maMon === item.maMon ? { ...i, soLuong: i.soLuong + 1 } : i)
        : [...prev, { ...item, soLuong: 1 }];
      localStorage.setItem(CART_KEY, JSON.stringify(next));
      return next;
    });
  }, []);

  const updateQty = useCallback((maMon: string, qty: number) => {
    setItems(prev => {
      const next = qty <= 0
        ? prev.filter(i => i.maMon !== maMon)
        : prev.map(i => i.maMon === maMon ? { ...i, soLuong: qty } : i);
      localStorage.setItem(CART_KEY, JSON.stringify(next));
      return next;
    });
  }, []);

  const removeItem = useCallback((maMon: string) => {
    setItems(prev => {
      const next = prev.filter(i => i.maMon !== maMon);
      localStorage.setItem(CART_KEY, JSON.stringify(next));
      return next;
    });
  }, []);

  const clearCart = useCallback(() => {
    persist([]);
    setMaCN('');
    setMaKM('');
  }, []);

  const setBranch = useCallback((id: string) => setMaCN(id), []);
  const setPromotion = useCallback((id: string) => setMaKM(id), []);

  const subtotal = items.reduce((s, i) => s + i.giaBan * i.soLuong, 0);
  const totalItems = items.reduce((s, i) => s + i.soLuong, 0);

  return (
    <CartContext.Provider value={{
      items, maCN, maKM,
      addItem, updateQty, removeItem, clearCart,
      setBranch, setPromotion,
      subtotal, totalItems
    }}>
      {children}
    </CartContext.Provider>
  );
};

export const useCart = (): CartContextValue => {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error('useCart must be used within CartProvider');
  return ctx;
};
