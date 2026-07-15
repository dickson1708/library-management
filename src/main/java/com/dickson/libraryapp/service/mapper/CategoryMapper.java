package com.dickson.libraryapp.service.mapper;

import com.dickson.libraryapp.domain.Category;
import com.dickson.libraryapp.service.dto.CategoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Category} and its DTO {@link CategoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper extends EntityMapper<CategoryDTO, Category> {}
