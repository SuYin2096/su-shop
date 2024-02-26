package com.scarlettech.inventoryservice.service;

import com.scarlettech.inventoryservice.model.Inventory;
import com.scarlettech.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean isInstock(String skuCode){
        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }
}
