package com.cafe.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Data
@Entity
@DynamicUpdate
@DynamicInsert

@NamedQuery(name = "User.findByEmailId", query = "select u from User u where u.email=:email")
@NamedQuery(name = "User.getAllUser", query = "select new com.cafe.wrapper.UserWrapper(u.id,u.name,u.email,u.mobile,u.status) from User u where u.role='user'")
@NamedQuery(name = "User.getAllAdmin", query = "select u.email from User u where u.role='admin'")
@NamedQuery(name = "User.updateStatus", query = "update User u set u.status=: status where u.id=:id")
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	private String mobile;
	private String email;
	private String password;
	private String status;
	private String role;
}
