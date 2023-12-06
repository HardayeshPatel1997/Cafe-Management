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
import com.cafe.dao.ProductRepo;
import com.cafe.entity.Category;
import com.cafe.entity.Product;
import com.cafe.payloads.CafeConstants;
import com.cafe.payloads.CafeUtils;
import com.cafe.service.ProductService;
import com.cafe.wrapper.ProductWrapper;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private JwtFilter jwtFilter;
	@Autowired
	private ProductRepo productRepo;

	@Override
	public ResponseEntity<String> addNewProduct(Map<String, String> reqMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (validateProductMap(reqMap, false)) {
					productRepo.save(getProductFromMap(reqMap, false));
					return CafeUtils.getResponseEntity("Product Added Successfully", HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			} else
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateProductMap(Map<String, String> reqMap, boolean validateId) {
		if (reqMap.containsKey("name")) {
			if (reqMap.containsKey("id") && validateId) {
				return true;
			} else if (!validateId) {
				return true;
			}
		}
		return false;
	}

	private Product getProductFromMap(Map<String, String> reqMap, boolean isAdd) {
		Category category = new Category();
		category.setId(Integer.parseInt(reqMap.get("categoryId")));

		Product product = new Product();
		if (isAdd) {
			product.setId(Integer.parseInt(reqMap.get("id")));
		} else {
			product.setStatus("true");
		}

		product.setCategory(category);
		product.setName(reqMap.get("name"));
		product.setDescription(reqMap.get("description"));
		product.setPrice(Integer.parseInt(reqMap.get("price")));

		return product;
	}

	@Override
	public ResponseEntity<List<ProductWrapper>> getAllProduct() {
		try {
			return new ResponseEntity<>(productRepo.getAllProduct(), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateProduct(Map<String, String> reqMap) {
		try {
			if (jwtFilter.isAdmin()) {
				if (validateProductMap(reqMap, true)) {
					Optional<Product> product = productRepo.findById(Integer.parseInt(reqMap.get("id")));
					if (!product.isEmpty()) {
						Product productUpdatable = getProductFromMap(reqMap, true);
						productUpdatable.setStatus(product.get().getStatus());
						productRepo.save(productUpdatable);
						return CafeUtils.getResponseEntity("Product Updated SuccesFully", HttpStatus.OK);

					} else {
						return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);
					}

				} else {
					return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
				}

			} else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> deleteProduct(Integer id) {
		try {
			if (jwtFilter.isAdmin()) {
				Optional<Product> product = productRepo.findById(id);
				if (!product.isEmpty()) {
					productRepo.deleteById(id);
					return CafeUtils.getResponseEntity("Product deleted Successfully.", HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> updateStatus(Map<String, String> reqMap) {
		try {
			if (jwtFilter.isAdmin()) {
				Optional<Product> product = productRepo.findById(Integer.parseInt(reqMap.get("id")));
				if (!product.isEmpty()) {
					productRepo.updateProductStatus(reqMap.get("status"), Integer.parseInt(reqMap.get("id")));
					return CafeUtils.getResponseEntity("Product Status Updated SuccesFully", HttpStatus.OK);

				}
				return CafeUtils.getResponseEntity("Product id does not exist.", HttpStatus.OK);

			} else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
		try {
                 return new ResponseEntity<>(productRepo.getProductByCategory(id),HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<ProductWrapper> getById(Integer id) {
		try {
			return new ResponseEntity<>(productRepo.getProductById(id),HttpStatus.OK);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
