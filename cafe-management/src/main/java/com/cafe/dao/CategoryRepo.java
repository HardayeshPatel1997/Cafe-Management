package com.cafe.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cafe.entity.Category;

public interface CategoryRepo extends JpaRepository<Category, Integer>{
	
List<Category> getAllCategory();
}
