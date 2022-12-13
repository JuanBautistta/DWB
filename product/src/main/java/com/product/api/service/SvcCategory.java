package com.product.api.service;

import java.util.List;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Category;

public interface SvcCategory {
	
	List<Category> getCategories();
	Category getCategory(Integer categoryId);
	ApiResponse createCategory(Category category);
	ApiResponse deleteCategory(Integer categoryId);
	ApiResponse updateCategory(Integer categoryId, Category category);

}
