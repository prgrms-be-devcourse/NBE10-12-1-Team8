export type OrderStatus =
  | "ORDERED"
  | "CONFIRMED"
  | "PREPARING_SHIPMENT"
  | "SHIPPED"
  | "DELIVERED"
  | "CANCELED";

export type AdminOrderResponse = {
  id: number;
  email: string;
  shippingDate: string;
  address: string;
  zipcode: string;
  totalPrice: number;
  status: OrderStatus;
  orderAt: string;
};

export type AdminOrderItemResponse = {
  productId: number;
  productName: string;
  quantity: number;
  orderPrice: number;
  totalPrice: number;
};

export type AdminOrderDetailResponse = {
  id: number;
  email: string;
  shippingDate: string;
  address: string;
  zipcode: string;
  orderAt: string;
  status: OrderStatus;
  totalPrice: number;
  items: AdminOrderItemResponse[];
};

export type AdminOrderStatusResponse = {
  id: number;
  status: OrderStatus;
};

export type AdminOrderStatusUpdateRequest = {
  status: OrderStatus;
};

export type AdminOrderBulkShippedRequest = {
  orderIds: number[];
};

export type AdminOrderBulkShippedResponse = {
  processedCount: number;
  orderIds: number[];
};
