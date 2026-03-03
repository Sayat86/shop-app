package com.example.shopapp.category.service;

import com.example.shopapp.category.dto.CategoryRequest;
import com.example.shopapp.category.dto.CategoryResponse;
import com.example.shopapp.category.entity.Category;
import com.example.shopapp.category.mapper.CategoryMapper;
import com.example.shopapp.category.repository.CategoryRepository;
import com.example.shopapp.exception.BadRequestException;
import com.example.shopapp.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryResponse create(CategoryRequest request) {

        if (repository.existsBySlug(request.slug())) {
            throw new BadRequestException("Slug already exists");
        }

        Category category = Category.builder()
                .name(request.name())
                .slug(request.slug())
                .description(request.description())
                .build();

        if (request.parentId() != null) {
            Category parent = getEntity(request.parentId());
            category.setParent(parent);
        }

        return mapper.toResponse(repository.save(category));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getTree() {

        List<Category> all = repository.findAll();

        Map<Long, CategoryResponse> map = new HashMap<>(all.size());

        // 1️⃣ создаём DTO без связей
        for (Category category : all) {

            Long parentId = category.getParent() != null
                    ? category.getParent().getId()
                    : null;

            List<CategoryResponse> children = new ArrayList<>();

            CategoryResponse response = new CategoryResponse(
                    category.getId(),
                    category.getName(),
                    category.getSlug(),
                    category.getDescription(),
                    parentId,
                    children,
                    category.getCreatedAt(),
                    category.getUpdatedAt()
            );

            map.put(category.getId(), response);
        }

        // 2️⃣ собираем дерево
        List<CategoryResponse> roots = new ArrayList<>();

        for (Category category : all) {

            CategoryResponse current = map.get(category.getId());
            Long parentId = category.getParent() != null
                    ? category.getParent().getId()
                    : null;

            if (parentId == null) {
                roots.add(current);
            } else {
                CategoryResponse parent = map.get(parentId);

                if (parent != null) { // защита от битых данных
                    parent.children().add(current);
                }
            }
        }

        return roots;
    }


    public CategoryResponse update(Long id, CategoryRequest request) {

        Category category = getEntity(id);

        if (!category.getSlug().equals(request.slug())
                && repository.existsBySlug(request.slug())) {
            throw new BadRequestException("Slug already exists");
        }

        category.setName(request.name());
        category.setSlug(request.slug());
        category.setDescription(request.description());

        if (request.parentId() != null) {

            if (request.parentId().equals(id)) {
                throw new BadRequestException("Category cannot be parent of itself");
            }

            Category parent = getEntity(request.parentId());

            if (isChildOf(parent, category)) {
                throw new BadRequestException("Circular hierarchy detected");
            }

            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return mapper.toResponse(repository.save(category));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private Category getEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private boolean isChildOf(Category potentialParent, Category category) {

        Category current = potentialParent;

        while (current != null) {
            if (current.getId().equals(category.getId())) {
                return true;
            }
            current = current.getParent();
        }

        return false;
    }
}
