import type { Product, Order } from './_types';

export const MOCK_PRODUCTS: Product[] = [
  {
    id: 1,
    name: 'Colombia Narino',
    description: '달콤한 카라멜과 견과류의 부드러운 맛. 균형 잡힌 미디엄 로스트.',
    price: 12000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1698093135407-8f50f3a52fe2?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
  {
    id: 2,
    name: 'Ethiopia Yirgacheffe',
    description: '꽃향기와 베리의 과일향이 풍부한 밝은 산미. 라이트 로스트.',
    price: 15000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1666873903780-396269c73a54?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
  {
    id: 3,
    name: 'Guatemala Antigua',
    description: '다크 초콜릿과 스파이시한 향의 미디엄-다크 로스트.',
    price: 13000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1765896977022-3079fd6bce8d?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
  {
    id: 4,
    name: 'Sumatra Mandheling',
    description: '흙내음과 깊은 풀바디, 스모키한 다크 로스트.',
    price: 14000,
    category: 'COFFEE_BEAN_PACKAGE',
    imageUrl:
      'https://images.unsplash.com/photo-1666873975263-0c0e24c1a2f4?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080',
  },
];

const MOCK_ORDERS: Order[] = [
  {
    id: 1001,
    email: 'user@example.com',
    status: 'ORDERED',
    orderAt: '2026-06-07T10:30:00',
    shippingDate: '2026-06-08T14:00:00',
    address: '서울시 강남구 테헤란로 123',
    zipcode: '06234',
    totalPrice: 27000,
    items: [
      { productId: 1, productName: 'Colombia Narino', quantity: 1, orderPrice: 12000, totalPrice: 12000 },
      { productId: 2, productName: 'Ethiopia Yirgacheffe', quantity: 1, orderPrice: 15000, totalPrice: 15000 },
    ],
  },
  {
    id: 1002,
    email: 'user@example.com',
    status: 'SHIPPED',
    orderAt: '2026-06-05T09:15:00',
    shippingDate: '2026-06-06T14:00:00',
    address: '경기도 성남시 분당구 판교로 456',
    zipcode: '13529',
    totalPrice: 14000,
    items: [
      { productId: 4, productName: 'Sumatra Mandheling', quantity: 1, orderPrice: 14000, totalPrice: 14000 },
    ],
  },
  {
    id: 1003,
    email: 'user@example.com',
    status: 'ORDERED',
    orderAt: '2026-06-08T08:00:00',
    shippingDate: '2026-06-09T14:00:00',
    address: '부산시 해운대구 해운대로 789',
    zipcode: '48094',
    totalPrice: 39000,
    items: [
      { productId: 3, productName: 'Guatemala Antigua', quantity: 3, orderPrice: 13000, totalPrice: 39000 },
    ],
  },
];

export function formatDateTime(value: string) {
  return value.replace('T', ' ').slice(0, 16);
}

export function formatPrice(value: number | undefined | null) {
  return (value ?? 0).toLocaleString('ko-KR');
}

// GET /api/v1/orders?email={email}
export async function fetchOrdersByEmail(email: string): Promise<Order[]> {
  await new Promise((r) => setTimeout(r, 600));
  return MOCK_ORDERS.filter((o) => o.email.toLowerCase() === email.toLowerCase());
}

// DELETE /api/v1/orders/{id}
export async function cancelOrder(orderId: number): Promise<void> {
  await new Promise((r) => setTimeout(r, 500));
  void orderId;
}

// PATCH /api/v1/orders/{id}
export async function updateOrderAddress(
  orderId: number,
  address: string,
  zipcode: string,
): Promise<void> {
  await new Promise((r) => setTimeout(r, 500));
  void orderId; void address; void zipcode;
}
