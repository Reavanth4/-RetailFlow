package com.retailflow.salesservice.client;

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
    public int getAvailableStock(Long productId, Long warehouseId) {
        try {
            ApiEnvelope<InventoryData> envelope = restClient.get()
                    .uri("/api/v1/inventory/product/{productId}/warehouse/{warehouseId}",
                            productId, warehouseId)
                    .retrieve()
                    .body(new org.springframework.core.ParameterizedTypeReference<>() {
                    });

            if (envelope != null && envelope.data() != null && envelope.data().availableQuantity() != null) {
                return envelope.data().availableQuantity();
            }
            return 0;
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public void stockOut(Long productId,
                         Long warehouseId,
                         Integer quantity,
                         String movementType,
                         String referenceType,
                         Long referenceId,
                         String remarks) {
        MovementPayload payload = new MovementPayload(
                productId, warehouseId, quantity, movementType, referenceType, referenceId, remarks);

        restClient.post()
                .uri("/api/v1/inventory/stock-out")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void stockIn(Long productId,
                        Long warehouseId,
                        Integer quantity,
                        String movementType,
                        String referenceType,
                        Long referenceId,
                        String remarks) {
        MovementPayload payload = new MovementPayload(
                productId, warehouseId, quantity, movementType, referenceType, referenceId, remarks);

        restClient.post()
                .uri("/api/v1/inventory/stock-in")
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    private record MovementPayload(Long productId,
                                   Long warehouseId,
                                   Integer quantity,
                                   String movementType,
                                   String referenceType,
                                   Long referenceId,
                                   String remarks) {
    }

    private record InventoryData(Long id,
                                 Long productId,
                                 Long warehouseId,
                                 Integer quantity,
                                 Integer reservedQuantity,
                                 Integer availableQuantity,
                                 Integer reorderLevel,
                                 Boolean active) {
    }

    private record ApiEnvelope<T>(boolean success,
                                  String message,
                                  T data,
                                  String timestamp) {
    }
}
