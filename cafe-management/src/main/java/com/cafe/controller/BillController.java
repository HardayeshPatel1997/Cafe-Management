package com.cafe.controller;


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

import com.cafe.entity.Bill;
import com.cafe.payloads.CafeConstants;
import com.cafe.payloads.CafeUtils;
import com.cafe.service.BillService;

@RestController
@RequestMapping("/bill")
public class BillController {
	@Autowired
	private BillService billService;

	@PostMapping("/generateReport")
	ResponseEntity<String> generateReport(@RequestBody Map<String, Object> reqMap) {

		try {
               return billService.generateReport(reqMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	@GetMapping("/getBills")
	ResponseEntity<List<Bill>> getBills(){

		try {
              return billService.getBills();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PostMapping("/getPdf")
	ResponseEntity<byte[]> getPdf(@RequestBody Map<String, Object> reqMap){
		try {
			   
			return billService.getPdf(reqMap);
		}catch (Exception e) {
			
		e.printStackTrace();
		} return null;
	}
	@PostMapping("/delete/{id}")
	ResponseEntity<String> deleteBill(@PathVariable Integer id){
		try {
			return billService.deleteBill(id);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
