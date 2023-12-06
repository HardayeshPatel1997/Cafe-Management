package com.cafe.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Entity
@Data
@DynamicInsert
@DynamicUpdate
@NamedQuery(name = "Product.getAllProduct", query = "select new com.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price, p.status, p.category.id, p.category.name) from Product p")
@NamedQuery(name="Product.updateProductStatus", query = "update Product p set p.status=:status where p.id=:id")
@NamedQuery(name="Product.getProductById",query = "select new com.cafe.wrapper.ProductWrapper(p.id, p.name, p.description, p.price) from Product p where p.id=:id")
@NamedQuery(name="Product.getProductByCategory",query = "select new com.cafe.wrapper.ProductWrapper(p.id, p.name) from Product p where p.category.id=:id and p.status='true'")
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;

	@ManyToOne
	@JoinColumn(name = "category_fk", nullable = false)
	private Category category;

	private String description;

	private Integer price;

	private String status;

}
