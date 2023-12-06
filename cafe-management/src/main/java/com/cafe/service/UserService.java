package com.cafe.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.cafe.entity.User;
import com.cafe.wrapper.UserWrapper;

public interface UserService {

	public ResponseEntity<String> signUp(Map<String, String> requestMap);

	public ResponseEntity<String> login(Map<String, String> requestMap);

	public ResponseEntity<List<UserWrapper>> getAllUser();

	public ResponseEntity<String> update(Map<String, String> requestMap);

	public ResponseEntity<String> chakeTkoen();

	public ResponseEntity<String> changePassword(Map<String, String> requestMap);

	public ResponseEntity<String> forgotPssword(Map<String, String> requestMap);
}
