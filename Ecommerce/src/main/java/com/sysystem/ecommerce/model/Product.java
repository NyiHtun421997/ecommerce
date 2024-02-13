package com.sysystem.ecommerce.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * ProductClass
 * 
 * @author NyiNyiHtun
 */
public class Product implements Serializable {

	private static final long serialVersionUID = 1L;

	private int code;
	private String name;
	private int price;
	private LocalDate registerDatetime;
	private LocalDate updateDatetime;
	private LocalDate deleteDatetime;

	// All Args Constructor
	public Product(int code, String name, int price, LocalDate registerDatetime, LocalDate updateDatetime,
			LocalDate deleteDatetime) {
		super();
		this.code = code;
		this.name = name;
		this.price = price;
		this.registerDatetime = registerDatetime;
		this.updateDatetime = updateDatetime;
		this.deleteDatetime = deleteDatetime;
	}

	// codeなしConstructor
	public Product(String name, int price, LocalDate registerDatetime, LocalDate updateDatetime,
			LocalDate deleteDatetime) {
		super();
		this.name = name;
		this.price = price;
		this.registerDatetime = registerDatetime;
		this.updateDatetime = updateDatetime;
		this.deleteDatetime = deleteDatetime;
	}
	
	public Product(String name) {
		this.name = name;
	}

	// No Arg Constructor
	public Product() {
		super();
	}

	// Getters and Setters

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
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

	public LocalDate getDeleteDatetime() {
		return deleteDatetime;
	}

	public void setDeleteDatetime(LocalDate deleteDatetime) {
		this.deleteDatetime = deleteDatetime;
	}

}
