package com.jordiproject.inventoryservice.service;

import com.jordiproject.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInStock(String sku_code) {
        return inventoryRepository.findBySkuCode(sku_code).isPresent();
    }
}
