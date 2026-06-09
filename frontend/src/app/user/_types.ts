export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  category?: string;
  imageUrl?: string;
}

export interface CartItem {
  product: Product;
  quantity: number;
}

export type OrderStatus = 'ORDERED' | 'SHIPPED';

export interface ApiProduct {
  id: number;
  name: string;
  price: number;
  description: string;
  imageUrl?: string;
}

export interface ApiOrderItem {
  id: number;
  product: ApiProduct;
  quantity: number;
}

export interface Order {
  id: number;
  email: string;
  shippingDate: string;
  address: string;
  zipcode: string;
  status: OrderStatus;
  orderAt: string;
  orderItems?: ApiOrderItem[];
}
