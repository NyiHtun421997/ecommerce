package com.sysystem.ecommerce.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.sysystem.ecommerce.model.Product;

/**
 * Dao Class
 * 商品データを扱うDataAccessObjectクラス
 * @author NyiNyiHtun
 */
public class ProductDao {

	private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";
	private static String jdbcUrl = "jdbc:mysql://localhost:3306/exercise_b";
	private static String jdbcUsername = "nnhsys";
	private static String jdbcPassword = "root";

	/**
	 * Mysql ドライバーのテストメゾット
	 * 
	 * @exception ClassNotFoundException
	 */
	public static void loadDriver() {
		try {
			Class.forName(jdbcDriver);
		} catch (ClassNotFoundException e) {
			System.out.println("ドライバーのロードが失敗しました。");
		}
	}

	/**
	 * 商品マスタと売上テーブルのデータを全件削除
	 * 
	 * @exception SQLException
	 */
	public static void deleteAllData() throws SQLException {

		String allProductQuery = "SELECT * FROM m_product";
		String allSalesQuery = "SELECT * FROM t_sales WHERE product_code=";

		String productDeleteQuery = "DELETE FROM m_product WHERE product_code=?";
		String salesDeleteQuery = "DELETE FROM t_sales WHERE sales_id=?";

		int productCode, salesId;

		// 商品マスターのデータを削除する
		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				Statement getAllProductsStatement = connection.createStatement();
				Statement getAllSalesStatement = connection.createStatement();
				PreparedStatement deleteProductStatement = connection.prepareStatement(productDeleteQuery);
				PreparedStatement deleteSalesStatement = connection.prepareStatement(salesDeleteQuery)) {

			// 商品テーブルにある全てのレコードを取得する
			try (ResultSet productResultSet = getAllProductsStatement.executeQuery(allProductQuery);) {
				while (productResultSet.next()) {
					// 商品テーブルのレコードが削除されるたびに
					// その商品が関連する売上テーブルのレコードが削除されなければならない
					// ProductResultSetからproduct codeを一つずつ取る
					productCode = productResultSet.getInt("product_code");
					// product_codeがt_salesテーブルに外部キーとして存在するt_salesのレコードを取得する
					try (ResultSet salesResultSet = getAllSalesStatement.executeQuery(allSalesQuery + productCode)) {

						// sales_idで検索してレコードごとを削除する
						while (salesResultSet.next()) {
							salesId = salesResultSet.getInt("sales_id");
							deleteSalesStatement.setInt(1, salesId);
							deleteSalesStatement.executeUpdate();
						}
					} catch (SQLException e) {
						throw new SQLException("商売上データを削除しようとする際、予期しない問題が発生しました。");
					}

					// 商品テーブルのレコードが削除できるようになる
					deleteProductStatement.setInt(1, productCode);
					deleteProductStatement.executeUpdate();
				}

				System.out.println("商品マスタのデータを削除しました。");
				System.out.println("\n売上テーブルのデータを削除しました。");

			} catch (SQLException e) {
				throw new SQLException(e.getMessage());
			}

		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * 商品マスタにデータを登録
	 * 
	 * @param 商品オブジェクト
	 * @return boolean値 データベースへの登録が成功したかを判定する
	 * @exception SQLException
	 */
	public static boolean registerProductData(Product product) throws SQLException {
		boolean isRegistered = false;
		String insertQuery = "INSERT INTO m_product "
				+ "(product_code,product_name,price,register_datetime,update_datetime,delete_datetime)"
				+ "VALUES(?,?,?,?,?,NULL)";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {

			String registerDatetime = product.getRegisterDatetime()
					.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

			String updateDatetime = product.getUpdateDatetime()
					.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

			preparedStatement.setInt(1, product.getCode());
			preparedStatement.setString(2, product.getName());
			preparedStatement.setInt(3, product.getPrice());
			preparedStatement.setString(4, registerDatetime);
			preparedStatement.setString(5, updateDatetime);

			isRegistered = preparedStatement.executeUpdate() > 0;

		} catch (SQLException e) {
			throw new SQLException("商品マスタテーブルにデータを登録しようとする際、予期しない問題が発生しました。");
		}
		return isRegistered;
	}

	

	/**
	 * 商品マスターテーブルの全件を取得する
	 * @return List of Products 商品オブジェクトが格納されたリスト
	 * @exception SQLException
	 */
	public static List<Product> getAllProducts() throws SQLException {
		return ProductDao.getProducts("SELECT * FROM m_product");
	}

	/**
	 * 商品マスターテーブルの全件を取得する
	 * @param 商品名の一部で検索するため、商品名の一部文字列
	 * @return List of Products 商品オブジェクトが格納されたリスト、商品コードの昇順にソート
	 * @exception SQLException
	 */
	public static List<Product> searchActiveProductsOrderByCode(String searchWord) throws SQLException {
		String searchQuery = "SELECT * FROM m_product WHERE product_name LIKE '%"
				+ searchWord + "%'AND delete_datetime IS NULL"
				+ " ORDER BY product_code";

		return ProductDao.getProducts(searchQuery);
	}

	/**
	 * 商品マスターテーブルの全件を取得するprivateメゾット
	 * @param 種類の違いselect query 文字列
	 * @return List of Products 商品オブジェクトが格納されたリスト
	 * @exception SQLException
	 */
	private static List<Product> getProducts(String selectQuery) throws SQLException {

		List<Product> allProducts = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				int productCode = resultSet.getInt("product_code");
				String name = resultSet.getString("product_name");
				int price = resultSet.getInt("price");
				String registerDatetimeText = resultSet.getString("register_datetime");
				String updateDatetimeText = resultSet.getString("update_datetime");
				String deleteDatetimeText = resultSet.getString("delete_datetime");

				LocalDate registerDatetime = null, updateDatetime = null, deleteDatetime = null;
				if (registerDatetimeText != null && registerDatetimeText != "")
					registerDatetime = LocalDate.parse(registerDatetimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				if (updateDatetimeText != null && updateDatetimeText != "")
					updateDatetime = LocalDate.parse(updateDatetimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				if (deleteDatetimeText != null && deleteDatetimeText != "")
					deleteDatetime = LocalDate.parse(deleteDatetimeText, DateTimeFormatter.ofPattern("yyyy-MMdd"));

				Product product = new Product(productCode, name, price, registerDatetime, updateDatetime,
						deleteDatetime);
				allProducts.add(product);
			}

		} catch (SQLException e) {
			throw new SQLException("商品マスターテーブルからデータを取得しようとする際、予期しない問題が発生しました。");
		}
		return allProducts;
	}

	/**
	 * 商品マスターテーブルから1つのレコードを取得する
	 * @return Product 商品オブジェクト
	 * @exception SQLException
	 */
	public static Product getProduct(int productCode) throws SQLException {

		Product product = null;
		String selectQuery = "SELECT * FROM m_product WHERE product_code=?";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

			preparedStatement.setInt(1, productCode);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				while (resultSet.next()) {

					String name = resultSet.getString("product_name");
					int price = resultSet.getInt("price");
					String registerDatetimeText = resultSet.getString("register_datetime");
					String updateDatetimeText = resultSet.getString("update_datetime");
					String deleteDatetimeText = resultSet.getString("delete_datetime");

					LocalDate registerDatetime = null, updateDatetime = null, deleteDatetime = null;
					if (registerDatetimeText != null && registerDatetimeText != "")
						registerDatetime = LocalDate.parse(registerDatetimeText,
								DateTimeFormatter.ofPattern("yyyy-MM-dd"));

					if (updateDatetimeText != null && updateDatetimeText != "")
						updateDatetime = LocalDate.parse(updateDatetimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

					if (deleteDatetimeText != null && deleteDatetimeText != "")
						deleteDatetime = LocalDate.parse(deleteDatetimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

					product = new Product(productCode, name, price, registerDatetime, updateDatetime, deleteDatetime);
				}
			} catch (SQLException e) {
				throw new SQLException("商品マスターテーブルからデータを取得しようとする際、予期しない問題が発生しました。");
			}

		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
		return product;
	}
		
	/**
	 * 商品テーブルから現在最大のproduct codeを取得する
	 * @return int 現在最大のproduct code
	 * @exception SQLException
	 */
	public static int getMaxProductCode() throws SQLException {
		int maxProductCode = -1;
		String maxProductCodeQuery = "SELECT MAX(product_code) FROM m_product";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				PreparedStatement preparedStatement = connection.prepareStatement(maxProductCodeQuery);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			resultSet.next();
			maxProductCode = resultSet.getInt(1);
		} catch (SQLException e) {
			throw new SQLException("データベースへの接続の際、問題が発生しました。");
		}
		return maxProductCode;
	}

	/**
	 * 商品マスタのデータを編集
	 * @param 商品コード、商品名、単価
	 * @return boolean 編集が成功したかを指す論理値
	 * @exception SQLException
	 */
	public static boolean updateProductData(int productCode, String productName, int price) throws SQLException {
		return ProductDao.editProductData(true, productCode, productName, price);
	}

	/**
	 * 商品名変更
	 * @param 商品コード、商品名
	 * @return boolean 編集が成功したかを指す論理値
	 * @exception SQLException
	 */
	public static boolean updateProductName(int productCode, String productName) throws SQLException {
		return ProductDao.editProductData(true, productCode, productName, 0);
	}

	/**
	 * 商品マスタのデータを削除
	 * @param 商品コード、商品名、単価
	 * @param 変更か削除かを判定するbooleanが型論理値、商品コード、商品名、単価（削除の場合null）
	 * @return boolean 編集が成功したかを指す論理値
	 * @exception SQLException
	 */
	public static boolean deleteProductData(int productCode) throws SQLException {
		return ProductDao.editProductData(false, productCode, null, 0);
	}

	/**
	 * 商品マスタのデータを編集または削除
	 * @param 変更か削除かを判定するbooleanが型論理値、商品コード、商品名、単価（削除の場合null,0）
	 * @return boolean 編集が成功したかを指す論理値
	 * @exception SQLException
	 */
	private static boolean editProductData(boolean isUpdate, int productCode, String productName, int price)
			throws SQLException {

		boolean isEdited = false;
		String editQuery = "UPDATE m_product SET delete_datetime=?,update_datetime=?,version=? WHERE product_code=? AND version=?";
		// 削除の命令か判定する、falseの場合論理削除のqueryに変更する
		if (isUpdate) {
			editQuery = "UPDATE m_product SET product_name=?,price=?,update_datetime=?,version=? WHERE product_code=? AND version=?";
			// 商品名のみ変更の場合
			if (price == 0)
				editQuery = "UPDATE m_product SET product_name=?,update_datetime=?,version=? WHERE product_code=? AND version=?";
		}

		int prevVersion = 0;
		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {

			// Transactionを管理するため、auto commitをfalseにする
			connection.setAutoCommit(false);
			try (PreparedStatement getVersionStatement = connection
					.prepareStatement("SELECT * FROM m_product WHERE product_code=?");
					PreparedStatement editStatement = connection.prepareStatement(editQuery)) {

				// product code
				getVersionStatement.setInt(1, productCode);

				try (ResultSet getVersionResult = getVersionStatement.executeQuery()) {
					if (getVersionResult.next())
						prevVersion = getVersionResult.getInt("version");
				} catch (SQLException e) {
					throw new SQLException("データベースへの接続の際、問題が発生しました。");
				}

				if (isUpdate) {
					// 商品名のみ変更の場合
					if (price == 0) {
						// product name
						editStatement.setString(1, productName);
						// update datetime
						String updateDatetime = LocalDate.now()
								.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
						editStatement.setString(2, updateDatetime);
						// new version
						editStatement.setInt(3, prevVersion + 1);
						// product code
						editStatement.setInt(4, productCode);
						// previous version
						editStatement.setInt(5, prevVersion);
					} else {
						// product name
						editStatement.setString(1, productName);
						// price
						editStatement.setInt(2, price);
						// update datetime
						String updateDatetime = LocalDate.now()
								.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
						editStatement.setString(3, updateDatetime);
						// new version
						editStatement.setInt(4, prevVersion + 1);
						// product code
						editStatement.setInt(5, productCode);
						// previous version
						editStatement.setInt(6, prevVersion);
					}

				} else {
					// delete parameters
					// delete datetime
					String deleteDatetime = LocalDate.now()
							.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
					editStatement.setString(1, deleteDatetime);
					// update datetime
					editStatement.setString(2, deleteDatetime);
					// new version
					editStatement.setInt(3, prevVersion + 1);
					// product code
					editStatement.setInt(4, productCode);
					// previous version
					editStatement.setInt(5, prevVersion);
				}

				// if rows were affected, do commit
				if ((isEdited = editStatement.executeUpdate() > 0)) {
					connection.commit();
				} else {
					connection.rollback();
					throw new SQLException("変更・削除の作業は失敗しました。(Rollback)");
				}
			} catch (SQLException e) {
				throw new SQLException(e.getMessage());
			}

		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
		return isEdited;
	}

	/**
	 * 商品は既にに売上が発生したかを判定する
	 * @param 商品コード
	 * @exception SQLException
	 */
	public static boolean isProductSold(int productCode) throws SQLException {
		String query = "SELECT COUNT(*) FROM t_sales WHERE product_code=" + productCode;
		boolean isSold = false;

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				Statement statement = connection.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				if (resultSet.getInt(1) > 0)
					isSold = true;
			}
		} catch (SQLException e) {
			throw new SQLException("データベースへの接続の際、問題が発生しました。");
		}
		return isSold;
	}

}
