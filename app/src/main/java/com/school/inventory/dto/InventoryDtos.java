package com.school.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

public class InventoryDtos {

    @Data
    @Builder
    public static class CategoryDto {
        private Long id;
        private String name;
        private long itemCount;
    }

    @Data
    @Builder
    public static class ItemDto {
        private Long id;
        private String name;
        private Long categoryId;
        private String categoryName;
        private String sku;
        private String unit;
        private Integer quantity;
        private Integer reorderLevel;
        private String location;
        private String notes;
        private boolean lowStock;
    }

    @Data
    @Builder
    public static class TxnDto {
        private Long id;
        private Long itemId;
        private String type;
        private Integer quantity;
        private String note;
        private LocalDateTime createdAt;
    }
}
