package com.cafe.serviceImpl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cafe.dao.BillRepo;
import com.cafe.dao.CategoryRepo;
import com.cafe.dao.ProductRepo;
import com.cafe.service.DashboardService;
@Service
public class DashboardServiceImpl implements DashboardService {
    
	@Autowired
	private CategoryRepo categoryRepo;
	@Autowired
	private ProductRepo productRepo;
	@Autowired
	private BillRepo billRepo;
	
	
	@Override
	public ResponseEntity<Map<String, Object>> getCount() {
		Map<String, Object> map= new HashMap<>();
		map.put("category", categoryRepo.count());
		map.put("product", productRepo.count());
		map.put("bill", billRepo.count());
		return new ResponseEntity<>(map,HttpStatus.OK);
	}

}
