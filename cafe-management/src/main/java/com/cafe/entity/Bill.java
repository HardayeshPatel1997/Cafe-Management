package com.cafe.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Entity
@Data
@DynamicUpdate
@DynamicInsert
@NamedQuery(name = "Bill.getAllBills",query = "select b from Bill b order by b.id desc")
@NamedQuery(name = "Bill.getBillByUsername", query = "select b from Bill b where b.createdBy=:username order by b.id desc")
public class Bill implements Serializable {

	private static final long serialVersionUID=1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
    private String uuid;	
    private String name;
    private String email;
    private String mobile;
    @Column(name = "paymentmethod")
    private String paymentMethod;  
    private Integer total;
    @Column(name="productdetails",columnDefinition = "json")
    private String productDetails;
    @Column(name = "createdby")
    private String createdBy;
    
}

