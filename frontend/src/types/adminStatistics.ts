export type MonthlySalesResponse = {
  month: string;
  totalSales: number;
};

export type DailySalesResponse = {
  date: string;
  totalSales: number;
};

export type ProductSalesResponse = {
  productId: number;
  productName: string;
  quantity: number;
};

export type AdminStatisticsResponse = {
  monthlySales: MonthlySalesResponse[];
  recentDailySales: DailySalesResponse[];
  productSales: ProductSalesResponse[];
};
