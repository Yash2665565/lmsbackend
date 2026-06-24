package com.school.inventory.repository;

import com.school.inventory.entity.InventoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Long> {
}
