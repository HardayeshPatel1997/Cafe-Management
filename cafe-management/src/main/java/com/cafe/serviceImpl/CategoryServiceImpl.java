package com.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafe.JWT.JwtFilter;
import com.cafe.dao.CategoryRepo;
import com.cafe.entity.Category;
import com.cafe.entity.User;
import com.cafe.payloads.CafeConstants;
import com.cafe.payloads.CafeUtils;
import com.cafe.service.CategoryService;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;
	@Autowired
	private JwtFilter jwtFilter;

	@Override
	public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {

		try {
			if (jwtFilter.isAdmin()) {
				// if currunt user is admin

				if (validateCategoryMap(requestMap, false)) {
					categoryRepo.save(getCategoryFromMap(requestMap, false));
					return CafeUtils.getResponseEntity("Category added successfully", HttpStatus.OK);
				}

			} else {
				// if currunt user is not admin
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {

		if (requestMap.containsKey("name")) {
			if (requestMap.containsKey("id") && validateId) {
				return true;
			} else if (!validateId) {
				return true;
			}
		}
		return false;
	}

	private Category getCategoryFromMap(Map<String, String> requestMap, Boolean isAdd) {
		Category category = new Category();
		if (isAdd) {
			category.setId(Integer.parseInt(requestMap.get("id")));
		}
		category.setName(requestMap.get("name"));
		return category;
	}

	@Override
	public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
		try {
			if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
				log.info("inside if");
				return new ResponseEntity<List<Category>>(categoryRepo.getAllCategory(), HttpStatus.OK);
			}
			return new ResponseEntity<>(categoryRepo.findAll(), HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
		try {

			if (jwtFilter.isAdmin()) {

				if (validateCategoryMap(requestMap, true)) {

					Optional<Category> category = this.categoryRepo.findById(Integer.parseInt(requestMap.get("id")));
					if (!category.isEmpty()) {

						categoryRepo.save(getCategoryFromMap(requestMap, true));
						return CafeUtils.getResponseEntity("category updated successfully ", HttpStatus.OK);
					} else {
						return CafeUtils.getResponseEntity("Category id does not exists", HttpStatus.OK);
					}
				}
				return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}

			else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
