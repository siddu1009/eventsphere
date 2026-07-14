package com.eventsphere.controller;
import com.eventsphere.entity.CategoryEntity;
import com.eventsphere.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    private final CategoryRepository categories;
    public CategoryController(CategoryRepository categories) { this.categories = categories; }
    @GetMapping public List<CategoryEntity> all() { return categories.findAll(); }
    @PostMapping public CategoryEntity create(@RequestBody CategoryEntity category) {
        if (category.getName() == null || category.getName().isBlank()) throw new IllegalArgumentException("Category name is required");
        if (categories.findByNameIgnoreCase(category.getName().trim()).isPresent()) throw new IllegalArgumentException("Category already exists");
        category.setName(category.getName().trim()); return categories.save(category);
    }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { if (!categories.existsById(id)) throw new java.util.NoSuchElementException("Category not found"); categories.deleteById(id); return ResponseEntity.noContent().build(); }
}
