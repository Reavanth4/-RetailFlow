package com.retailflow.reportservice.client;

import com.retailflow.reportservice.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class RestReportDataClient implements ReportDataClient {

    private final RestClient productClient;
    private final RestClient salesClient;
    private final RestClient purchaseClient;
    private final RestClient inventoryClient;

    public RestReportDataClient(@Value("${product-service.url}") String productServiceUrl,
                                @Value("${sales-service.url}") String salesServiceUrl,
                                @Value("${purchase-service.url}") String purchaseServiceUrl,
                                @Value("${inventory-service.url}") String inventoryServiceUrl) {
        this.productClient = RestClient.builder().baseUrl(productServiceUrl).build();
        this.salesClient = RestClient.builder().baseUrl(salesServiceUrl).build();
        this.purchaseClient = RestClient.builder().baseUrl(purchaseServiceUrl).build();
        this.inventoryClient = RestClient.builder().baseUrl(inventoryServiceUrl).build();
    }

    @Override
    public List<ProductRecord> fetchProducts() {
        try {
            return productClient.get()
                    .uri("/api/v1/products?page=0&size=2000&sort=id,asc")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<PageRecord<ProductRecord>>>() {
                    })
                    .getData().content();
        } catch (Exception e) {
            log.warn("Could not fetch products from product-service: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<SaleRecord> fetchSales() {
        try {
            return salesClient.get()
                    .uri("/api/v1/sales")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<SaleRecord>>>() {
                    })
                    .getData();
        } catch (Exception e) {
            log.warn("Could not fetch sales from sales-service: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<PurchaseRecord> fetchPurchases() {
        try {
            return purchaseClient.get()
                    .uri("/api/v1/purchases")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<PurchaseRecord>>>() {
                    })
                    .getData();
        } catch (Exception e) {
            log.warn("Could not fetch purchases from purchase-service: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<InventoryRecord> fetchInventory() {
        try {
            return inventoryClient.get()
                    .uri("/api/v1/inventory?page=0&size=2000&sort=id,asc")
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<PageRecord<InventoryRecord>>>() {
                    })
                    .getData().content();
        } catch (Exception e) {
            log.warn("Could not fetch inventory from inventory-service: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<InventoryRecord> fetchLowStock(int threshold) {
        try {
            return inventoryClient.get()
                    .uri("/api/v1/inventory/low-stock?threshold=" + threshold)
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<List<InventoryRecord>>>() {
                    })
                    .getData();
        } catch (Exception e) {
            log.warn("Could not fetch low-stock inventory: {}", e.getMessage());
            return List.of();
        }
    }
}
