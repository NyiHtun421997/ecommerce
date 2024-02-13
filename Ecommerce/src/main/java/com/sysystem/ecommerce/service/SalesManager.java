package com.sysystem.ecommerce.service;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.sysystem.ecommerce.model.Product;
import com.sysystem.ecommerce.model.Sales;
import com.sysystem.ecommerce.repository.ProductDao;
import com.sysystem.ecommerce.repository.SalesDao;
import com.sysystem.ecommerce.repository.Status;

/**
 * Sales Manager Class 売上データの取得・登録を処理するサービスクラス
 * 
 * @author NyiNyiHtun
 */
public class SalesManager {

	public SalesManager() {
	}

	/**
	 * データベースから現在発生した既存の売上データを取得する
	 * @return 既に発生した売上データをMap->売上Id,InnerMap
	 * @throws SQLException
	 */
	public Map<String, Integer> getExistingSales() throws SQLException {
		String registerDatetime = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<Sales> allExistingSales = SalesDao.getExistingSales(registerDatetime);
		return allExistingSales.stream()
				.collect(Collectors.toMap(sale -> sale.getProduct().getName(),
						Sales::getQuantity));
	}

	/**
	 * データベースから商品名のリストを取得する
	 * 
	 * @return 削除されていない商品名のリスト
	 * @throws SQLException
	 */
	public List<String> getActiveProductNames() throws SQLException {
		List<Product> allProducts = ProductDao.searchActiveProductsOrderByCode("");
		return allProducts.stream().map(Product::getName).collect(Collectors.toList());
	}

	/**
	 * データベースに売上を登録する
	 * @param 登録したい売上データを商品名 key, 数量 value のMap
	 * @return 登録処理後更新処理が必要な売上データ及び削除された商品のため登録できないという情報が含まれているMap
	 * @throws SQLException
	 */
	public Map<String, Integer> registerSales(Map<String, Integer> salesToRegister) throws SQLException {
		// このMapは売上登録処理後のStatusを保存する
		Map<String, Integer> salesRegisterStatusMap = new LinkedHashMap<>();
		// 商品名＋数量 key,value pair毎にSalesオブジェクトを作成してデータベースに登録する
		Status result;
		
		for (Entry<String, Integer> entry : salesToRegister.entrySet()) {
			
			Sales sales = new Sales(new Product(entry.getKey()), entry.getValue(), LocalDate.now(), LocalDate.now());
			result  = SalesDao.persistSalesData(sales);
			boolean isProductDeleted = false, isProductRegistered = false;
			
			if (result == Status.PRODUCT_DELETED && !isProductDeleted) {
				// YOU CANNOT SELL WHAT IS ALREADY BEING DELETED
				// 登録しようとする商品の売上データは既に削除されたことが確認できたため、ユーザーに登録処理が失敗したことを知らせる必要がある
				// 但し、削除された商品がせめて1個含まれただけでこの警告メッセージを表示するように処理して
				// 以降の商品の中に削除されたものが含まれてもこの　if文を実行する必要はない			
				
				salesRegisterStatusMap.put("ProductAlreadyDeleted", 1);
				isProductDeleted = true;
				
			} else if (result == Status.SALES_REGISTERED && !isProductRegistered) { 
				
				// 商品売上データが1つ以上登録されたら、ユーザーに登録が成功したメッセージを表示するように処理する
				// 以降の商品が登録できたとしてもこの if文を実行する必要はない
				salesRegisterStatusMap.put("ProductRegistered", 1);
				isProductRegistered = true;
				
			} else {
				// 登録しようとする売上データは当日既に売上が発生しているため、更新処理が求められることをユーザーに知れせる必要がある
				salesRegisterStatusMap.put(entry.getKey(), entry.getValue());
			} 
		}
		return salesRegisterStatusMap;
	}
	
	/**
	 * データベースに売上を更新する
	 * @param 更新したい売上データを商品名 key, 数量 value のMap
	 * @return 更新処理の結果を示す論理値
	 * @throws SQLException
	 */
	public boolean updateSales(Map<String, Integer> salesToUpdate) throws SQLException {
		boolean result = true;
		
		for (Entry<String, Integer> entry : salesToUpdate.entrySet()) {
			// isUpdatedがtrueが戻されたらそのまま、falseが戻されたら警告メッセージが表示されますように処理する
			result = (SalesDao.updateSalesData(entry.getKey(), entry.getValue())) ? result : false ;
		}
		return result;
	}
	
	/**
	 * 商品ごとの売上集計。売上のない商品も含める売上レコードも
	 * @param root path to store csv files
	 * @exception SQLException
	 */
	public void createAllSalesRecordCsv(String rootPath) throws SQLException {
		String query = "SELECT P.product_code,product_name,price,SUM(quantity) AS quantity,(price*SUM(quantity)) AS total_amount"
				+ " FROM m_product AS P LEFT JOIN t_sales AS T ON P.product_code=T.product_code"
				+ " GROUP BY P.product_code";
		File path = new File(this.createFilePath(rootPath), "商品別売上集計.csv");
		SalesDao.createCSV(query, path);
	}
	
	/**
	 * 指定した年月に売上のある商品の売上集計。売上のない商品は含めない
	 * @param root path to store csv files, 指定した年月String
	 * @exception SQLException
	 */
	public void createSalesRecordCsvSpecifiedDate(String rootPath, String date) throws SQLException {
		String query = "SELECT P.product_code,product_name,price,SUM(quantity) AS quantity,(price*SUM(quantity)) AS total_amount"
				+ " FROM m_product AS p JOIN t_sales AS T ON P.product_code=T.product_code"
				+ " WHERE T.register_datetime LIKE '" + date + "-%' OR T.update_datetime LIKE '" + date + "-%'"
				+ " GROUP BY P.product_code;";
		File path = new File(this.createFilePath(rootPath), "指定年月商品別売上集計_" + date + ".csv");
		SalesDao.createCSV(query, path);
	}
	
	/**
	 * CSVファイルをサーバーサイドに一旦保存するためファイルパースを作成するHelperメゾット
	 * @param root path to store csv files
	 * @return File
	 */
	private File createFilePath(String rootPath) {
		File resources = new File(rootPath + File.separator + "resources");
		if (!resources.exists())
			resources.mkdir();
		
		File allSalesRecord = new File(resources, "sales_record");
		if (!allSalesRecord.exists())
			allSalesRecord.mkdir();
		
		return allSalesRecord;
	}
}
