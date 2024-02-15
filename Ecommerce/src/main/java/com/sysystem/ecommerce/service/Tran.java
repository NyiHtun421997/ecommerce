package com.sysystem.ecommerce.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import com.sysystem.ecommerce.exception.CustomException;
import com.sysystem.ecommerce.model.Product;
import com.sysystem.ecommerce.model.Sales;
import com.sysystem.ecommerce.repository.ProductDao;
import com.sysystem.ecommerce.repository.SalesDao;
import com.sysystem.ecommerce.repository.Status;

/**
 * TranClass
 * データベースを初期化する
 * 
 * @author NyiNyiHtun
 */
public class Tran {
	public static void main(String[] args) {
		List<Product> allProducts = null;
		List<Sales> allSales = null;
		ProductDao.loadDriver();

		// 商品マスタにデータを登録
		Product product1 = new Product(1, "パソコンＰＣ", 130000, null, null, null);
		product1.setRegisterDatetime(LocalDate.now());
		product1.setUpdateDatetime(LocalDate.now());

		Product product2 = new Product(2, "デスクトップＰＣ", 150000, null, null, null);
		product2.setRegisterDatetime(LocalDate.of(2017, 3, 13));
		product2.setUpdateDatetime(LocalDate.of(2022, 6, 12));

		Product product3 = new Product(3, "モニター", 80000, null, null, null);
		product3.setRegisterDatetime(LocalDate.of(2019, 11, 23));
		product3.setUpdateDatetime(LocalDate.of(2022, 7, 12));

		Product product4 = new Product(4, "キーボード", 10000, null, null, null);
		product4.setRegisterDatetime(LocalDate.of(2021, 1, 19));
		product4.setUpdateDatetime(LocalDate.of(2023, 7, 18));

		Product product5 = new Product(5, "グラフィックカード", 90000, null, null, null);
		product5.setRegisterDatetime(LocalDate.of(2024, 1, 7));
		product5.setUpdateDatetime(LocalDate.of(2024, 1, 22));

		// 売上テーブルにデータを登録
		Sales sales1 = new Sales(LocalDate.of(2023, 11, 30),product1, 5, 
				LocalDate.of(2023, 11, 30), LocalDate.of(2024, 1, 4));

		Sales sales2 = new Sales(LocalDate.of(2023, 10, 12), product1, 7, 
				LocalDate.of(2023, 10, 12), LocalDate.of(2024, 1, 14));

		Sales sales3 = new Sales(LocalDate.of(2022, 8, 27), product2, 6, 
				LocalDate.of(2022, 8, 27), LocalDate.of(2024, 1, 6));

		Sales sales4 = new Sales(LocalDate.of(2023, 10, 11),product2, 2, 
				LocalDate.of(2023, 11, 30), LocalDate.of(2024, 1, 4));

		Sales sales5 = new Sales(LocalDate.of(2023, 10, 11),product3, 5, 
				LocalDate.of(2023, 10, 11), LocalDate.of(2024, 1, 17));

		Sales sales6 = new Sales(LocalDate.of(2023, 12, 22), product3, 7, 
				LocalDate.of(2023, 12, 22), LocalDate.of(2024, 1, 9));

		Sales sales7 = new Sales(LocalDate.now(), product3, 4, 
				LocalDate.now(), LocalDate.now());

		Sales sales8 = new Sales(LocalDate.of(2023, 7, 9), product4, 8, 
				LocalDate.of(2023, 7, 9), LocalDate.of(2023, 12, 14));

		Sales sales9 = new Sales(LocalDate.of(2023, 10, 11), product4, 5, 
				LocalDate.of(2023, 10, 11), LocalDate.of(2023, 11, 26));

		Sales sales10 = new Sales(LocalDate.of(2023, 10, 11), product5, 13, 
				LocalDate.of(2023, 10, 11), LocalDate.of(2023, 11, 30));

		try {
			// 商品マスタのデータを全件削除
			ProductDao.deleteAllData();
			// 売上テーブルにデータを登録
			if (ProductDao.registerProductData(product1)
					&& ProductDao.registerProductData(product2)
					&& ProductDao.registerProductData(product3)
					&& ProductDao.registerProductData(product4)
					&& ProductDao.registerProductData(product5)) {

				System.out.println("\n商品マスタにデータを登録しました。");
			}
			// 売上テーブルにデータを登録
			if ((SalesDao.persistSalesData(sales1) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales2) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales3) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales4) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales5) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales6) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales7) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales8) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales9) == Status.SALES_REGISTERED
					&& SalesDao.persistSalesData(sales10)== Status.SALES_REGISTERED)) {

				System.out.println("\n売上テーブルにデータを登録しました。");
			}
			// 商品テーブルのデータを全件を取得
			allProducts = ProductDao.getAllProducts();
			// 売上テーブルのデータを全件取得
			allSales = SalesDao.getAllSalesList();

		} catch (CustomException e) {
			System.out.println(e.getMessage());
		}

		if (allProducts != null) {
			// 商品テーブルのデータを全件表示
			System.out.println("\n商品テーブルのデータを表示します。");

			System.out.printf("%-14s%-18s%-18s%-18s%-18s%s%n", "商品コード", "商品名", "単価", "登録日", "更新日", "削除日");
			for (Product product : allProducts) {

				int name_padding = 22 - product.getName().length();
				// 926 has 3 digits,let k = 3
				// n number satisfies 10^k-1 <= n < 10^k
				// e.g., n = 926, k = 3, 10^2 = 100, 10^3 = 1000, 100 <= 926 < 1000
				// if n = 100, k = 3, 10^2 = 100, 10^3 = 1000, 100 <= 100 < 1000
				// if n = 999, k = 3, 10^2 = 100, 10^3 = 1000, 100 <= 999 < 1000
				// by multiplying both sides of eqn with log10, k-1 <= log10 n < k
				// hence, k = log10 n + 1
				int price_padding = 18 - (int) Math.log10(product.getPrice());
				System.out.printf("%03d%14s%-" + name_padding + "s%,d", product.getCode(), " ", product.getName(),
						product.getPrice());
				if (product.getRegisterDatetime() != null) {

					Date registerDatetime = Date.valueOf(product.getRegisterDatetime());
					System.out.printf("%2$" + price_padding + "s%1$tY/%1$tm/%1$td", registerDatetime, " ");
				} else {
					System.out.printf("%-10s", " ");
				}

				if (product.getUpdateDatetime() != null) {

					Date updateDatetime = Date.valueOf(product.getUpdateDatetime());
					System.out.printf("%2$11s%1$tY/%1$tm/%1$td", updateDatetime, " ");
				} else {
					System.out.printf("%-10s", " ");
				}

				if (product.getDeleteDatetime() != null) {

					Date deleteDatetime = Date.valueOf(product.getDeleteDatetime());
					System.out.printf("%2$6s%1$tY/%1$tm/%1$td%n", deleteDatetime, " ");
				} else {
					System.out.printf("%n");
				}
			}
		}

		if (allSales != null) {
			// 売上テーブルのデータを全件表示
			System.out.println("\n売上テーブルのデータを表示します。");

			System.out.printf("%-18s%-18s%-18s%n", "売上日", "商品コード", "数量");
			for (Sales sales : allSales) {

				if (sales.getRegisterDatetime() != null) {

					Date registerDatetime = Date.valueOf(sales.getSalesDate());
					System.out.printf("%1$tY/%1$tm/%1$td%2$-11s", registerDatetime, " ");

				} else {
					System.out.printf("%-14s", " ");
				}

				int name_padding = sales.getProduct().getName().length() - 23;
				System.out.printf("%" + name_padding + "s%d%n", sales.getProduct().getName(), sales.getQuantity());
			}
		}
	}
}
