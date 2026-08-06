package com.retailflow.purchaseservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient implements InventoryGateway {

    private final RestClient restClient;

    public InventoryClient(@Value("${inventory-service.url}") String inventoryServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(inventoryServiceUrl).build();
    }

    @Override
    public void stockIn(Long productId,
                        Long warehouseId,
                        Integer quantity,
                        String movementType,
                        String referenceType,
                        Long referenceId,
                        String remarks) {

        StockInPayload payload = new StockInPayload(
                productId, warehouseId, quantity, movementType, referenceType, referenceId, remarks);

        restClient.post()
                .uri("/api/v1/inventory/stock-in")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void stockOut(Long productId,
                         Long warehouseId,
                         Integer quantity,
                         String movementType,
                         String referenceType,
                         Long referenceId,
                         String remarks) {

        StockInPayload payload = new StockInPayload(
                productId, warehouseId, quantity, movementType, referenceType, referenceId, remarks);

        restClient.post()
                .uri("/api/v1/inventory/stock-out")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    private record StockInPayload(Long productId,
                                  Long warehouseId,
                                  Integer quantity,
                                  String movementType,
                                  String referenceType,
                                  Long referenceId,
                                  String remarks) {
    }
}
