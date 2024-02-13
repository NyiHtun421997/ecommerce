package com.sysystem.ecommerce.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * SalesClass
 * 
 * @author NyiNyiHtun
 */
public class Sales implements Serializable {

	private static final long serialVersionUID = 2L;

	private int id;
	private Product product;
	private int quantity;
	private LocalDate registerDatetime;
	private LocalDate updateDatetime;

	// All Args Constructor
	public Sales(int id, Product product, int quantity, LocalDate registerDatetime, LocalDate updateDatetime) {
		super();
		this.id = id;
		this.product = product;
		this.quantity = quantity;
		this.registerDatetime = registerDatetime;
		this.updateDatetime = updateDatetime;
	}

	// idなしConstructor
	public Sales(Product product, int quantity, LocalDate registerDatetime, LocalDate updateDatetime) {
		super();
		this.product = product;
		this.quantity = quantity;
		this.registerDatetime = registerDatetime;
		this.updateDatetime = updateDatetime;
	}

	// No Args Constructor
	public Sales() {
		super();
	}

	// Getters and Setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public LocalDate getRegisterDatetime() {
		return registerDatetime;
	}

	public void setRegisterDatetime(LocalDate registerDatetime) {
		this.registerDatetime = registerDatetime;
	}

	public LocalDate getUpdateDatetime() {
		return updateDatetime;
	}

	public void setUpdateDatetime(LocalDate updateDatetime) {
		this.updateDatetime = updateDatetime;
	}

}
