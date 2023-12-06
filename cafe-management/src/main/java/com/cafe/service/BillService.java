package com.cafe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.cafe.entity.Bill;

public interface BillService {

	ResponseEntity<String> generateReport(Map<String, Object> reqMap);

	ResponseEntity<List<Bill>> getBills();

	ResponseEntity<byte[]> getPdf(Map<String, Object> reqMap);

	ResponseEntity<String> deleteBill(Integer id);

	

}
