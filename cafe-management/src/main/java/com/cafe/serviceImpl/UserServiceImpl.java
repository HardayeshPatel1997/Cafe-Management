package com.cafe.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.cafe.JWT.CustomUserDetailService;
import com.cafe.JWT.JwtFilter;
import com.cafe.JWT.JwtUtil;
import com.cafe.dao.UserRepo;
import com.cafe.entity.User;
import com.cafe.payloads.CafeConstants;
import com.cafe.payloads.CafeUtils;
import com.cafe.payloads.EmailUtils;
import com.cafe.service.UserService;
import com.cafe.wrapper.UserWrapper;
import com.google.common.base.Optional;

import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;
	@Autowired
	private EmailUtils emailUtils;
	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private CustomUserDetailService customUserDetailService;

	@Override
	public ResponseEntity<String> signUp(Map<String, String> requestMap) {
		log.info("Inside signUp {}", requestMap);

		try {

			if (validateSignUpMap(requestMap)) {
				User user = this.userRepo.findByEmailId(requestMap.get("email"));

				// if user not found with same email id then only you can register as new user
				if (Objects.isNull(user)) {

					userRepo.save(getUserFromMap(requestMap));

					return CafeUtils.getResponseEntity("Successfully Registerd", HttpStatus.OK);
				} else {
					return CafeUtils.getResponseEntity("Email already exits", HttpStatus.BAD_REQUEST);
				}
			} else {
				return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private boolean validateSignUpMap(Map<String, String> requestMap) {
		if (requestMap.containsKey("name") && requestMap.containsKey("mobile") && requestMap.containsKey("email")
				&& requestMap.containsKey("password")) {
			return true;

		} else
			return false;

	}

	private User getUserFromMap(Map<String, String> requMap) {
		User user = new User();
		user.setName(requMap.get("name"));
		user.setMobile(requMap.get("mobile"));
		user.setEmail(requMap.get("email"));
		user.setPassword(requMap.get("password"));
		user.setStatus("false");
		user.setRole("user");
		return user;
	}

	@Override
	public ResponseEntity<String> login(Map<String, String> requestMap) {

		log.info("inside login");

		try {

			Authentication auth = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));

			if (auth.isAuthenticated()) {
				if (customUserDetailService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
					return new ResponseEntity<String>(
							"{\"token\":\"" + jwtUtil.generateToken(customUserDetailService.getUserDetail().getEmail(),
									customUserDetailService.getUserDetail().getRole()) + "\"}",
							HttpStatus.OK);
				} else {
					return new ResponseEntity<String>("{\"message\":\"" + "wait for admin approval" + "\"}",
							HttpStatus.BAD_REQUEST);
				}
			}

		} catch (Exception e) {
			log.error("{}", e);
		}

		return new ResponseEntity<String>("{\"message\":\"" + "Bad Credantials." + "\"}", HttpStatus.BAD_REQUEST);

	}

	@Override
	public ResponseEntity<List<UserWrapper>> getAllUser() {
		try {
			if (jwtFilter.isAdmin()) {

				return new ResponseEntity<>(userRepo.getAllUser(), HttpStatus.OK);

			} else {
				return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> update(Map<String, String> requestMap) {
		try {
			if (jwtFilter.isAdmin()) {
				// user exist or not
				java.util.Optional<User> user = userRepo.findById(Integer.parseInt(requestMap.get("id")));
				if (!user.isEmpty()) {
					userRepo.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
					sendMailToAllAdmin(requestMap.get("status"), user.get().getEmail(), userRepo.getAllAdmin());
					return CafeUtils.getResponseEntity("User Status Updated Successfully ", HttpStatus.OK);
				} else {
					CafeUtils.getResponseEntity("User Id doesn't exist", HttpStatus.OK);
				}

			} else {
				return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
		// we dont want to send mail to himself which admin has been aprroved user
		allAdmin.remove(jwtFilter.getCurrentUser());

		if (status != null && status.equalsIgnoreCase("true")) {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved",
					"USER:- " + user + "\n is approved by \n ADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		} else {
			emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account disabled",
					"USER:- " + user + "\n is disabled by \n ADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
		}

	}

	@Override
	public ResponseEntity<String> chakeTkoen() {

		return CafeUtils.getResponseEntity("true", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<String> changePassword(Map<String, String> requestMap) {

		try {
			User userObj = userRepo.findByEmail(jwtFilter.getCurrentUser());
			if (!userObj.equals(null)) {
				if (userObj.getPassword().equals(requestMap.get("oldPassword"))) {
					userObj.setPassword(requestMap.get("newPassword"));
					userRepo.save(userObj);
					return CafeUtils.getResponseEntity("Password Updated Successfully", HttpStatus.OK);
				}
				return CafeUtils.getResponseEntity("Incorecct Old Password", HttpStatus.BAD_REQUEST);
			}
			return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@Override
	public ResponseEntity<String> forgotPssword(Map<String, String> requestMap) {
		try {
			User user=userRepo.findByEmail(requestMap.get("email"));
			if(!Objects.isNull(user) && !com.google.common.base.Strings.isNullOrEmpty(user.getEmail())) 
			       emailUtils.forgotMail(user.getEmail(), "Creadential for your login", user.getPassword());
				
				return CafeUtils.getResponseEntity("Chake your mail for OTP", HttpStatus.OK);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
