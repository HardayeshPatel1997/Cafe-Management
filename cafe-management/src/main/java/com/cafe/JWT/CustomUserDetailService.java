package com.cafe.JWT;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cafe.dao.UserRepo;
import com.cafe.entity.User;


@Service

public class CustomUserDetailService implements UserDetailsService{
 
	
	@Autowired
 private UserRepo userRepo;
	
	private User userDetail;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		userDetail=userRepo.findByEmailId(username);
		if(!Objects.isNull(userDetail))
			return 
					new org.springframework.security.core.userdetails.User(userDetail.getEmail(),userDetail.getPassword(),new ArrayList<>());
		else 
			throw new UsernameNotFoundException("User not found");
	}
      public com.cafe.entity.User getUserDetail(){
    	
    	  return userDetail;
      }
}
