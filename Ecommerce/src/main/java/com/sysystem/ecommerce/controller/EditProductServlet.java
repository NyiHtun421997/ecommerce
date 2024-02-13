package com.sysystem.ecommerce.controller;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sysystem.ecommerce.repository.ProductDao;

/**
 * Servlet implementation class EditProduct
 */
@WebServlet(description = "商品を登録するサーブレット", urlPatterns = { "/edit" })
public class EditProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		int productCode = Integer.parseInt((String) request.getParameter("product_code"));
		String productName = (String) request.getParameter("product_name");
		request.setAttribute("productCode", productCode);
		request.setAttribute("productName", productName);
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/edit.jsp");
		requestDispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String productName = (String) request.getParameter("productName");
		String priceText = (String) request.getParameter("price");
		String edit = ((String) request.getParameter("edit"));
		String message = null;
		boolean isEdited = false;
		int productCode = Integer.parseInt((String) request.getParameter("productCode"));
		RequestDispatcher requestDispatcher;

		// 変更処理をする際入力された値に無効な文字が入っているかチェックする
		if (edit.equals("update")
				&& (productName == "" || priceText == ""
						|| productName.matches(".*[!@#%^&*()_+=\\[\\]{}|;':\",./<>?~`-].*")
						|| !priceText.matches("[0-9]+"))) {
			message = "商品名または価格に無効な値が入力されています。";
			request.setAttribute("message", message);
			request.setAttribute("isEdited", isEdited);
			request.setAttribute("productCode", productCode);
			requestDispatcher = request.getRequestDispatcher("/edit.jsp");
			requestDispatcher.forward(request, response);

		} else {
			// ここまで来たら入力値にspecial charactersか空白が入っていない為、編集処理を続ける
			// 検索ボタンと変更ボタンどっちが押されたかを判定する
			if (edit.equals("update")) {

				// 変更の処理
				int price = Integer.parseInt(priceText);
				// ここからこの商品は売上テーブルに関連しているかを検索する
				try {
					if (!ProductDao.isProductSold(productCode)) {
						// 商品が既に売上発生していない場合
						isEdited = ProductDao.updateProductData(productCode, productName, price);
						message = "商品の変更が成功しました。";
					} else {
						response.sendError(HttpServletResponse.SC_FORBIDDEN, "既に販売中の商品ですので変更処理が失敗しました。");
						return;
					}
				} catch (SQLException e) {
					throw new RuntimeException(e.getMessage());
				}
			} else {
				// 削除の処理
				try {
					isEdited = ProductDao.deleteProductData(productCode);
					message = "商品の削除が成功しました。";
				} catch (SQLException e) {
					throw new RuntimeException(e.getMessage());
				}
			}
			request.setAttribute("message", message);
			request.setAttribute("productCode", productCode);
			request.setAttribute("isEdited", isEdited);
			requestDispatcher = request.getRequestDispatcher("/edit.jsp");
			requestDispatcher.forward(request, response);
		}
	}
}
