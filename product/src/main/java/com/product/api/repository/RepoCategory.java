package com.product.api.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.product.api.entity.Category;

@Repository
public interface RepoCategory extends JpaRepository<Category, Integer> {
	
	@Query(value = "SELECT * FROM category WHERE status = :status", nativeQuery = true)
	List<Category> findByStatus(@Param("status") Integer status);
	
	@Query(value = "SELECT * FROM category WHERE category_id = :category_id AND status = 1", nativeQuery = true)
	Category findByCategoryId(@Param("category_id") Integer categoryId);
	
	@Query(value = "SELECT * FROM category WHERE category = :category", nativeQuery = true)
	Category findByCategory(@Param("category") String category);
	
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO category (category, status) VALUES (:category,1)", nativeQuery = true)
	void createCategory(@Param("category") String category);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE category SET category = :category WHERE category_id = :category_id", nativeQuery = true)
	Integer updateCategory(@Param("category") String category, @Param("category_id") Integer categoryId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE category SET status = 1 WHERE category_id = :category_id", nativeQuery = true)
	Integer activateCategory(@Param("category_id") Integer categoryId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE category SET status = 0 WHERE category_id = :category_id", nativeQuery = true)
	void deleteById(@Param("category_id") Integer categoryId);
}
