package com.sysystem.ecommerce.service;

import java.sql.SQLException;
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
	 * @return 処理の結果メッセージ
	 * @param productName, productPrice
	 * @throws CustomException 
	 */
	public String registerProduct(String productName, int productPrice) throws CustomException {

		// 既に存在する商品の場合
		if (ProductDao.getActiveProductByName(productName) != null)
			return "登録失敗しました。既に存在する商品です。";

		productCode++;
		Product newProduct = new Product(productCode, productName, productPrice,
				LocalDate.now(), LocalDate.now(), null);

		ProductDao.registerProductData(newProduct);
		return "商品の登録が成功しました。";
	}

	/**
	 * 商品の変更処理
	 * @return 処理の結果メッセージ
	 * @param productCode, productName, productPrice
	 * @throws CustomException 
	 */
	public String updateProductData(int productCode, String productName, int price) throws CustomException {
		try {
			// ここからこの商品は売上テーブルに関連しているかを検索する
			if (ProductDao.isProductSold(productCode))
				return "変更失敗しました。既に販売中の商品です。";

			// 同じ商品名があるか確認する
			if (ProductDao.getActiveProductByName(productName) != null)
				return "変更失敗しました。既に存在する商品です.";

			// 商品が既に売上発生していない場合、変更処理を続ける
			ProductDao.updateProductData(productCode, productName, price);
			return "商品の変更が成功しました。";

		} catch (SQLException | CustomException e) {
			
			if (e instanceof SQLException)
				throw new CustomException("接続が失敗しました。");
			else
				// rollback処理したことのメッセージ
				return e.getMessage();
		}
	}

	/**
	 * 商品の削除処理
	 * @return 処理の結果メッセージ
	 * @param productCode
	 * @throws CustomException 
	 */
	public String deleteProductData(int productCode) throws CustomException {
		try {
			ProductDao.deleteProductData(productCode);
			return "商品の削除が成功しました。";

		} catch (SQLException | CustomException e) {
			
			if (e instanceof SQLException)
				throw new CustomException(e.getMessage());
			else
				// rollback処理したことのメッセージ
				return e.getMessage();
		}
	}
}
