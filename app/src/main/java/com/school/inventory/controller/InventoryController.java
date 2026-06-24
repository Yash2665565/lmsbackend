package com.school.inventory.controller;

import com.school.common.dto.ApiResponse;
import com.school.inventory.dto.InventoryDtos.CategoryDto;
import com.school.inventory.dto.InventoryDtos.ItemDto;
import com.school.inventory.dto.InventoryDtos.TxnDto;
import com.school.inventory.entity.InventoryItem;
import com.school.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    /* ── Categories ── */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> categories() {
        return ResponseEntity.ok(ApiResponse.ok(service.listCategories()));
    }

    @PostMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@RequestBody CategoryRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.createCategory(req.name())));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.ok("Category deleted", null));
    }

    /* ── Items ── */
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ItemDto>>> items() {
        return ResponseEntity.ok(ApiResponse.ok(service.listItems()));
    }

    @PostMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ItemDto>> createItem(@RequestBody ItemRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.createItem(req.toEntity())));
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ItemDto>> updateItem(@PathVariable Long id, @RequestBody ItemRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.updateItem(id, req.toEntity())));
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Item deleted", null));
    }

    @PostMapping("/items/{id}/adjust")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ItemDto>> adjust(@PathVariable Long id, @RequestBody AdjustRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(service.adjustStock(id, req.type(), req.quantity(), req.note())));
    }

    @GetMapping("/items/{id}/txns")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<TxnDto>>> txns(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(service.listTxns(id)));
    }

    /* ── Request bodies ── */
    public record CategoryRequest(String name) {}
    public record AdjustRequest(String type, int quantity, String note) {}
    public record ItemRequest(String name, Long categoryId, String sku, String unit,
                              Integer quantity, Integer reorderLevel, String location, String notes) {
        InventoryItem toEntity() {
            InventoryItem i = new InventoryItem();
            i.setName(name);
            i.setCategoryId(categoryId);
            i.setSku(sku);
            i.setUnit(unit);
            i.setQuantity(quantity);
            i.setReorderLevel(reorderLevel);
            i.setLocation(location);
            i.setNotes(notes);
            return i;
        }
    }
}
