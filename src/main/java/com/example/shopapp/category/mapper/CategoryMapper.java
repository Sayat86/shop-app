package com.example.shopapp.category.mapper;

import com.example.shopapp.category.dto.CategoryResponse;
import com.example.shopapp.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "children", ignore = true)
    CategoryResponse toResponse(Category category);

}
