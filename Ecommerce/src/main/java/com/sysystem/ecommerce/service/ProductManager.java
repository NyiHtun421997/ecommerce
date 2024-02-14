package com.sysystem.ecommerce.service;

import java.time.LocalDate;

import com.sysystem.ecommerce.exception.CustomException;
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
	private ProductManager() throws CustomException {
		productCode = getMaxProductCode();
	}

	public static ProductManager getInstance() throws CustomException {
		if (productManager == null) {
			productManager = new ProductManager();
		}
		return productManager;
	}

	/**
	 * データベースから最大のProductCodeを取得するメゾット
	 * Singleton Classであるため、Server Lifecycleでこのメゾット
	 * を1回のみ実行する
	 * @throws CustomException 
	 */
	private int getMaxProductCode() throws CustomException {
		return ProductDao.getMaxProductCode();
	}

	/**
	 * 商品をデータベースに保存するメゾット
	 * @param productName, productPrice
	 * @throws CustomException 
	 */
	public boolean registerProduct(String productName, int productPrice) throws CustomException {
		productCode++;
		Product newProduct = new Product(productCode, productName, productPrice,
				LocalDate.now(), LocalDate.now(), null);
		return ProductDao.registerProductData(newProduct);
	}
	
	/**
	 * 商品の変更処理
	 * @param productCode, productName, productPrice
	 * @throws CustomException 
	 */
	public boolean updateProductData(int productCode, String productName, int price) throws CustomException {
		try {
			// ここからこの商品は売上テーブルに関連しているかを検索する
			if (!ProductDao.isProductSold(productCode)) {
				// 商品が既に売上発生していない場合
				return ProductDao.updateProductData(productCode, productName, price);
			} else {
				return false;
			}
		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
		}
	}
	
	/**
	 * 商品の削除処理
	 * @param productCode
	 * @throws CustomException 
	 */
	public boolean deleteProductData(int productCode) throws CustomException {
		try {
			return ProductDao.deleteProductData(productCode);
		} catch (CustomException e) {
			throw new CustomException(e.getMessage());
		}
	}
}
