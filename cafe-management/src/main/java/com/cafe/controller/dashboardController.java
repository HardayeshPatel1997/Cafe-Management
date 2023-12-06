package com.cafe.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cafe.service.DashboardService;

@RestController
@RequestMapping("/dashboard")
public class dashboardController {
	@Autowired
	private DashboardService dashboardService; 
   @GetMapping("/details")
   ResponseEntity<Map<String, Object>> getCount(){
	   return dashboardService.getCount();
   }
   
}
