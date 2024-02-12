package com.sysystem.ecommerce.repository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.sysystem.ecommerce.model.Product;
import com.sysystem.ecommerce.model.Sales;

/**
 * Dao Class 売上データを扱うDataAccessObjectクラス
 * 
 * @author NyiNyiHtun
 */
public class SalesDao {

	private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";
	private static String jdbcUrl = "jdbc:mysql://localhost:3306/exercise_b";
	private static String jdbcUsername = "nnhsyshd";
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
	 * 売上テーブルにデータを登録
	 * 
	 * @param 売上データオブジェクト
	 * @return Enum データベースへの登録が成功したか、登録しようとする商品が既に削除されたか 商品が当日に既に登録されたかを判定する
	 * @exception SQLException
	 */
	public static Status persistSalesData(Sales sales) throws SQLException {

		String getProductCodeQuery = "SELECT product_code FROM m_product WHERE delete_datetime IS NULL AND product_name='"
				+ sales.getProduct().getName() + "'";
		String checkSalesExistTodayQuery = "SELECT * FROM t_sales WHERE product_code=? AND register_datetime=?";
		String insertQuery = "INSERT INTO t_sales" + "(product_code,quantity,register_datetime,update_datetime)"
				+ " VALUES((SELECT product_code FROM m_product WHERE product_name=?),?,?,?)";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {

			int productCode;
			// Transactionを管理するため、auto commitをfalseにする
			connection.setAutoCommit(false);
			try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
					PreparedStatement getProductCodeStatement = connection.prepareStatement(getProductCodeQuery);
					PreparedStatement checkSalesExistTodayStatement = connection
							.prepareStatement(checkSalesExistTodayQuery);
					ResultSet productCodeResult = getProductCodeStatement.executeQuery()) {

				// データベースにPersist処理をする前、Userが選定した商品は削除されていないものを最終確認する処理
				// product_codeは0ではなければまだ削除されていない
				// SELECT product_code FROM m_product WHERE delete_datetime IS NULL AND
				// product_name=?
				if (!productCodeResult.next()) {
					// 商品が削除されたので処理を終了する
					return Status.PRODUCT_DELETED;

				} else {
					productCode = productCodeResult.getInt(1);
					// SELECT * FROM t_sales WHERE product_code=? AND register_datetime=?
					// 当日商品が売上テーブルに存在するか確認する
					checkSalesExistTodayStatement.setInt(1, productCode);
					checkSalesExistTodayStatement.setString(2,
							LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

					try (ResultSet checkSalesExistTodayResult = checkSalesExistTodayStatement.executeQuery()) {
						if (checkSalesExistTodayResult.next()) {
							// 商品が当日すでに売上が発生したのでINSERT処理をやめてUPDATE処理する
							return Status.SALES_EXIST;

						} else {
							// 登録処理を無難に続ける
							String registerDatetime = sales.getRegisterDatetime()
									.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

							String updateDatetime = sales.getUpdateDatetime()
									.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

							insertStatement.setString(1, sales.getProduct().getName());
							insertStatement.setInt(2, sales.getQuantity());
							insertStatement.setString(3, registerDatetime);
							insertStatement.setString(4, updateDatetime);

							if (insertStatement.executeUpdate() > 0) {
								connection.commit();
								return Status.SALES_REGISTERED;
							} else {
								connection.rollback();
								return null;
							}
						}
					} catch (SQLException e) {
						throw new SQLException("売上テーブルにデータを登録しようとする際、予期しない問題が発生しました。");
					}
				}

			} catch (SQLException e) {
				throw new SQLException(e.getMessage());
			}

		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
	}

	/**
	 * データベースから現在発生した既存の売上データを取得する
	 * 
	 * @exception SQLException
	 */
	public static List<Sales> getExistingSales(String registerDatetime) throws SQLException {

		return SalesDao.getAllSalesList("SELECT * FROM t_sales WHERE register_datetime='" + registerDatetime + "'");
	}

	/**
	 * 売上テーブルの全件を取得する
	 * 
	 * @return List of Sales 売上データオブジェクトが格納されたリスト
	 * @exception SQLException
	 */
	public static List<Sales> getAllSalesList() throws SQLException {
		return SalesDao.getAllSalesList("SELECT * FROM t_sales");
	}

	/**
	 * 売上テーブルの全件を取得する
	 * 
	 * @param 種類の違うQuery文字列
	 * @return List of Sales 売上データオブジェクトが格納されたリスト
	 * @exception SQLException
	 */
	private static List<Sales> getAllSalesList(String selectQuery) throws SQLException {

		List<Sales> allSales = new ArrayList<>();

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
				ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				int id = resultSet.getInt("sales_id");
				int productCode = resultSet.getInt("product_code");
				int quantity = resultSet.getInt("quantity");
				String registerDatetimeText = resultSet.getString("register_datetime");
				String updateDatetimeText = resultSet.getString("update_datetime");

				LocalDate registerDatetime = null, updateDatetime = null;
				if (registerDatetimeText != null && registerDatetimeText != "")
					registerDatetime = LocalDate.parse(registerDatetimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				if (updateDatetimeText != null && updateDatetimeText != "")
					updateDatetime = LocalDate.parse(updateDatetimeText, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

				Sales sales = new Sales(id, null, quantity, registerDatetime, updateDatetime);

				// この売上データに関連する商品のオブジェクトを検索して作成する
				Product product = ProductDao.getProduct(productCode);
				sales.setProduct(product);

				allSales.add(sales);
			}

		} catch (SQLException e) {
			throw new SQLException("売上テーブルからデータを取得しようとする際、予期しない問題が発生しました。");
		}
		return allSales;
	}

	/**
	 * 売上レコードを更新する
	 * 
	 * @param 商品名と数量
	 * @return 更新処理が成功したかを判定できる論理値
	 * @exception SQLException
	 */
	public static boolean updateSalesData(String productName, Integer quantity) throws SQLException {

		boolean isUpdated = false;
		String updateQuery = "UPDATE t_sales SET quantity=?,update_datetime=?,version=? WHERE sales_id=?"
				+ " AND version=?";

		String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

		String getExistingSalesQuery = "SELECT * FROM t_sales WHERE product_code="
				+ "(SELECT product_code FROM m_product WHERE product_name='" + productName
				+ "') AND register_datetime='" + todayDate + "'";

		try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {

			// Transactionを管理するため、auto commitをfalseにする
			connection.setAutoCommit(false);
			try (PreparedStatement getExistingSalesStatement = connection.prepareStatement(getExistingSalesQuery);
					PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
					ResultSet getExistingSalesResult = getExistingSalesStatement.executeQuery()) {

				int existingSalesId = 0, existingQuantity = 0, prevVersion = 0;

				if (getExistingSalesResult.next()) {

					existingSalesId = getExistingSalesResult.getInt("sales_id");
					existingQuantity = getExistingSalesResult.getInt("quantity");
					prevVersion = getExistingSalesResult.getInt("version");
				}

				// quantity
				updateStatement.setInt(1, quantity + existingQuantity);
				// update_datetime
				updateStatement.setString(2, todayDate);
				// new version
				updateStatement.setInt(3, prevVersion + 1);
				// sales_id
				updateStatement.setInt(4, existingSalesId);
				// previous version
				updateStatement.setInt(5, prevVersion);

				// if rows were affected, do commit
				if ((isUpdated = updateStatement.executeUpdate() > 0))
					connection.commit();
				else
					connection.rollback();

			} catch (SQLException e) {
				throw new SQLException("データベースへの接続の際、問題が発生しました。");
			}
		} catch (SQLException e) {
			throw new SQLException(e.getMessage());
		}
		return isUpdated;
	}

	/**
	 * 商品ごとの売上集計。売上のない商品も含める売上レコードも
	 * 指定した年月に売上のある商品の売上集計。売上のない商品は含めない
	 * 両方のCsvファイルを作成する
	 * @param 種類の違うQuery文字列、ファイルPath
	 * @return void
	 * @exception SQLException,IOException
	 */
	public static void createCSV(String query, File path) throws SQLException {

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path)), true);
				Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword);
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			out.append("商品コード,商品名,単価,数量,金額\n");
			while (rs.next()) {
				out.append(rs.getInt("product_code") + "," + rs.getString("product_name") + "," + rs.getInt("price")
						+ "," + rs.getInt("quantity") + "," + rs.getInt("total_amount") + "\n");
			}

		} catch (IOException | SQLException e) {
			if (e instanceof IOException)
				throw new RuntimeException("ファイル作成の際予期しない問題が発生しました。");
			else
				throw new SQLException("データベースへの接続の際、問題が発生しました。");
		}
	}
}
