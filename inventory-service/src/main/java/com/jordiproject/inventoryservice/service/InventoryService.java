package com.jordiproject.inventoryservice.service;

import com.jordiproject.inventoryservice.model.Inventory;
import com.jordiproject.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(List<String> skuCodes) {
        return skuCodes.stream().allMatch(skuCode -> {
            Optional<Inventory> inventoryProduct = inventoryRepository.findBySkuCode(skuCode);
            return inventoryProduct.map(product -> product.getQuantity() > 0).orElse(false);
        });
    }
}
