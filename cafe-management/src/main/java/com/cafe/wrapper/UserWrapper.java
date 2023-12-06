package com.cafe.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class UserWrapper {

	private Integer id;
	private String name;
	private String email;
	private String mobile;
	private String status;

	public UserWrapper(Integer id, String name, String email, String mobile, String status) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.status = status;
	}
	
	
}
