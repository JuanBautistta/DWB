package com.product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.entity.Category;
import com.product.api.repository.RepoCategory;
import com.product.exception.ApiException;

@Service
public class SvcCategoryImp implements SvcCategory{
	
	@Autowired
	RepoCategory repo;

	@Override
	public List<Category> getCategories() {
		return repo.findByStatus(1);
	}

	@Override
	public Category getCategory(Integer categoryId) {
		Category category = repo.findByCategoryId(categoryId);
		if (category == null) throw new ApiException(HttpStatus.NOT_FOUND, "Category does not exist");
		else return category;
	}

	@Override
	public ApiResponse createCategory(Category category) {
		Category categorySaved = repo.findByCategory(category.getCategory());
		if (categorySaved != null) {
			if (categorySaved.getStatus() == 0) {
				repo.activateCategory(categorySaved.getCategoryId());
				return new ApiResponse("Category has been activated");
			}
			else throw new ApiException(HttpStatus.BAD_REQUEST, "Category already exist");
		} else {
			repo.createCategory(category.getCategory());
			return new ApiResponse("Category created");
		}
	}

	@Override
	public ApiResponse deleteCategory(Integer categoryId) {
		Category categorySaved = repo.findByCategoryId(categoryId);
		if (categorySaved == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Category does not exist");
		} else {
			repo.deleteById(categoryId);
			return new ApiResponse("Category removed");
		}
	}

	@Override
	public ApiResponse updateCategory(Integer categoryId, Category category) {
		Category categorySaved = repo.findByCategoryId(categoryId);
		if (categorySaved == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Category does not exist");
		} else {
			if (categorySaved.getStatus() == 0) throw new ApiException(HttpStatus.BAD_REQUEST, "Category isn't active");
			else { 
				categorySaved = repo.findByCategory(category.getCategory());
				if (categorySaved != null) throw new ApiException(HttpStatus.BAD_REQUEST, "Category already exist");
				repo.updateCategory(category.getCategory(), categoryId);
				return new ApiResponse("category updated");
			}
		}
	}

}
