package com.sysystem.ecommerce.service;

import java.sql.SQLException;
import java.time.LocalDate;

import com.sysystem.ecommerce.model.Product;
import com.sysystem.ecommerce.repository.ProductDao;

/**
 * Product Manager Singleton Class
 * 商品をデータベースに保管する前に商品コードを自動的に採番する
 * Product Manager　インスタンスを一つしか作れないようにSingletonクラスにする
 * @author NyiNyiHtun
 */
public class ProductManager {

	private static int productCode;
	private static ProductManager productManager;

	// 外部からProductManagerインスタンスを作るのを防ぐため、privateにする
	private ProductManager() throws SQLException {
		productCode = getMaxProductCode();
	}

	public static ProductManager getInstance() throws SQLException {
		if (productManager == null) {
			productManager = new ProductManager();
		}
		return productManager;
	}

	/**
	 * データベースから最大のProductCodeを取得するメゾット
	 * Singleton Classであるため、Server Lifecycleでこのメゾット
	 * を1回のみ実行する
	 * @throws SQLException 
	 */
	private int getMaxProductCode() throws SQLException {
		return ProductDao.getMaxProductCode();
	}

	/**
	 * 商品をデータベースに保存するメゾット
	 * @param productName, productPrice
	 * @throws SQLException 
	 */
	public boolean registerProduct(String productName, int productPrice) throws SQLException {
		productCode++;
		Product newProduct = new Product(productCode, productName, productPrice,
				LocalDate.now(), LocalDate.now(), null);
		return ProductDao.registerProductData(newProduct);
	}
}
