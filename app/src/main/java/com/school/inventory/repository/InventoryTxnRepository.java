package com.school.inventory.repository;

import com.school.inventory.entity.InventoryTxn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTxnRepository extends JpaRepository<InventoryTxn, Long> {
    List<InventoryTxn> findByItemIdOrderByIdDesc(Long itemId);
}
