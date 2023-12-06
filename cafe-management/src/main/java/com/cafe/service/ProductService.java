package com.cafe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafe.wrapper.ProductWrapper;

@Service
public interface ProductService {

	ResponseEntity<String> addNewProduct(Map<String, String> reqMap);

	ResponseEntity<List<ProductWrapper>> getAllProduct();

	ResponseEntity<String> updateProduct(Map<String, String> reqMap);

	ResponseEntity<String> deleteProduct(Integer id);

	ResponseEntity<String> updateStatus(Map<String, String> reqMap);

	ResponseEntity<List<ProductWrapper>> getByCategory(Integer id);

	ResponseEntity<ProductWrapper> getById(Integer id);

	

}
