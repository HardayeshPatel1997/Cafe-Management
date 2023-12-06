 package com.cafe.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafe.payloads.CafeConstants;
import com.cafe.payloads.CafeUtils;

import com.cafe.service.ProductService;
import com.cafe.wrapper.ProductWrapper;

@RestController
@RequestMapping("/product")
public class ProductController {
	@Autowired
	private ProductService productService;

	@PostMapping("/add")
	ResponseEntity<String> addNewProduct(@RequestBody Map<String, String> reqMap) {
		try {
			return productService.addNewProduct(reqMap);
		} catch (Exception e) {

			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/get")
	ResponseEntity<List<ProductWrapper>> getAllProduct() {
		try {
			return productService.getAllProduct();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/update")
	ResponseEntity<String> updateProduct(@RequestBody Map<String, String> reqMap) {
		try {
			return productService.updateProduct(reqMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@PostMapping("/delete/{id}")
	ResponseEntity<String> deleteProduct(@PathVariable Integer id) {

		try {
			return productService.deleteProduct(id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);

	}

	@PostMapping("/updateStatus")
	ResponseEntity<String> updateStatus(@RequestBody Map<String, String> reqMap) {
		try {
			return productService.updateStatus(reqMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/getByCategory/{id}")
	ResponseEntity<List<ProductWrapper>> getByCategory(@PathVariable Integer id) {
		try {
			return productService.getByCategory(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@GetMapping("/getById/{id}")
	ResponseEntity<ProductWrapper> getById(@PathVariable Integer id) {
		try {
			return productService.getById(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ProductWrapper(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
