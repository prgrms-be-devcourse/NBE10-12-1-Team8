export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  category: string;
  imageUrl?: string;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export type OrderStatus = 'ORDERED' | 'SHIPPED';

export interface OrderItem {
  productId: number;
  productName: string;
  quantity: number;
  orderPrice: number;
  totalPrice: number;
}

export interface Order {
  id: number;
  email: string;
  shippingDate: string;
  address: string;
  zipcode: string;
  totalPrice: number;
  status: OrderStatus;
  orderAt: string;
  items: OrderItem[];
}
