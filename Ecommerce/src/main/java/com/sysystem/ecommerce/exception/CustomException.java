package com.sysystem.ecommerce.exception;

import java.sql.SQLException;

public class CustomException extends Exception {
	public CustomException (String  message) {
		super (message);
	}
	
	public CustomException(Exception e) {
		if (e instanceof SQLException) {
			throw new RuntimeException("接続が失敗しました。");
		}
	}
}
