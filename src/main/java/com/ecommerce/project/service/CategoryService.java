package com.ecommerce.project.service;

import com.ecommerce.project.entity.Category;
import java.util.List;

public interface CategoryService {

    Category createCategory(String name);

    Category getCategory(String categoryId);

    List<Category> getAllCategories();

    Category updateCategory(String categoryId, String name);

    void deleteCategory(String categoryId);
}
