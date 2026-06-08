export type AdminProductResponse = {
  id: number;
  name: string;
  price: number;
  description: string;
  imageUrl: string;
};

export type AdminProductDetailResponse = AdminProductResponse & {
  createDate: string;
  modifyDate: string;
};

export type AdminProductRequest = {
  name: string;
  price: number;
  description: string;
  imageUrl: string;
};
