package com.school.inventory.service;

import com.school.inventory.dto.InventoryDtos.CategoryDto;
import com.school.inventory.dto.InventoryDtos.ItemDto;
import com.school.inventory.dto.InventoryDtos.TxnDto;
import com.school.inventory.entity.InventoryCategory;
import com.school.inventory.entity.InventoryItem;
import com.school.inventory.entity.InventoryTxn;
import com.school.inventory.repository.InventoryCategoryRepository;
import com.school.inventory.repository.InventoryItemRepository;
import com.school.inventory.repository.InventoryTxnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryCategoryRepository categoryRepo;
    private final InventoryItemRepository itemRepo;
    private final InventoryTxnRepository txnRepo;

    /* ── Categories ── */
    public List<CategoryDto> listCategories() {
        Map<Long, Long> counts = itemRepo.findAll().stream()
                .filter(i -> i.getCategoryId() != null)
                .collect(Collectors.groupingBy(InventoryItem::getCategoryId, Collectors.counting()));
        return categoryRepo.findAll().stream()
                .map(c -> CategoryDto.builder().id(c.getId()).name(c.getName())
                        .itemCount(counts.getOrDefault(c.getId(), 0L)).build())
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDto createCategory(String name) {
        InventoryCategory c = new InventoryCategory(); c.setName(name);
        c = categoryRepo.save(c);
        return CategoryDto.builder().id(c.getId()).name(c.getName()).itemCount(0).build();
    }

    @Transactional
    public void deleteCategory(Long id) { categoryRepo.deleteById(id); }

    /* ── Items ── */
    public List<ItemDto> listItems() {
        Map<Long, String> names = categoryRepo.findAll().stream()
                .collect(Collectors.toMap(InventoryCategory::getId, InventoryCategory::getName));
        return itemRepo.findAll().stream().map(i -> toItemDto(i, names)).collect(Collectors.toList());
    }

    @Transactional
    public ItemDto createItem(InventoryItem body) {
        if (body.getQuantity() == null) body.setQuantity(0);
        InventoryItem saved = itemRepo.save(body);
        return toItemDto(saved, catNames());
    }

    @Transactional
    public ItemDto updateItem(Long id, InventoryItem body) {
        InventoryItem i = itemRepo.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        i.setName(body.getName());
        i.setCategoryId(body.getCategoryId());
        i.setSku(body.getSku());
        i.setUnit(body.getUnit());
        i.setReorderLevel(body.getReorderLevel());
        i.setLocation(body.getLocation());
        i.setNotes(body.getNotes());
        // quantity is changed only via adjustStock, not here
        return toItemDto(itemRepo.save(i), catNames());
    }

    @Transactional
    public void deleteItem(Long id) { itemRepo.deleteById(id); }

    @Transactional
    public ItemDto adjustStock(Long itemId, String type, int qty, String note) {
        InventoryItem item = itemRepo.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));
        int current = item.getQuantity() == null ? 0 : item.getQuantity();
        int delta;
        int newQty;
        switch (type == null ? "" : type.toUpperCase()) {
            case "IN"  -> { delta = qty;  newQty = current + qty; }
            case "OUT" -> { delta = -qty; newQty = Math.max(0, current - qty); }
            case "SET" -> { newQty = qty; delta = qty - current; }
            default    -> throw new RuntimeException("Invalid adjustment type");
        }
        item.setQuantity(newQty);
        itemRepo.save(item);

        InventoryTxn t = new InventoryTxn();
        t.setItemId(itemId);
        t.setType(type.toUpperCase());
        t.setQuantity(delta);
        t.setNote(note);
        txnRepo.save(t);

        return toItemDto(item, catNames());
    }

    public List<TxnDto> listTxns(Long itemId) {
        return txnRepo.findByItemIdOrderByIdDesc(itemId).stream()
                .map(t -> TxnDto.builder().id(t.getId()).itemId(t.getItemId()).type(t.getType())
                        .quantity(t.getQuantity()).note(t.getNote()).createdAt(t.getCreatedAt()).build())
                .collect(Collectors.toList());
    }

    /* ── helpers ── */
    private Map<Long, String> catNames() {
        return categoryRepo.findAll().stream()
                .collect(Collectors.toMap(InventoryCategory::getId, InventoryCategory::getName));
    }

    private ItemDto toItemDto(InventoryItem i, Map<Long, String> names) {
        int q = i.getQuantity() == null ? 0 : i.getQuantity();
        int rl = i.getReorderLevel() == null ? 0 : i.getReorderLevel();
        return ItemDto.builder()
                .id(i.getId()).name(i.getName()).categoryId(i.getCategoryId())
                .categoryName(i.getCategoryId() == null ? null : names.get(i.getCategoryId()))
                .sku(i.getSku()).unit(i.getUnit()).quantity(q).reorderLevel(rl)
                .location(i.getLocation()).notes(i.getNotes())
                .lowStock(rl > 0 && q <= rl)
                .build();
    }
}
