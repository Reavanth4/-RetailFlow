package com.retailflow.reportservice.client;

import java.util.List;

public interface ReportDataClient {

    List<ProductRecord> fetchProducts();

    List<SaleRecord> fetchSales();

    List<PurchaseRecord> fetchPurchases();

    List<InventoryRecord> fetchInventory();

    List<InventoryRecord> fetchLowStock(int threshold);
}
