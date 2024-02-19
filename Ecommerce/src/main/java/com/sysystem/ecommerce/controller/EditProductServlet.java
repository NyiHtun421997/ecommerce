package com.sysystem.ecommerce.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sysystem.ecommerce.exception.CustomException;
import com.sysystem.ecommerce.service.CleanseString;
import com.sysystem.ecommerce.service.ProductManager;

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
		String message = "";
		boolean isEdited = false;
		int productCode = Integer.parseInt((String) request.getParameter("productCode"));
		RequestDispatcher requestDispatcher;

		// 変更処理をする際入力された値に無効な文字が入っているかチェックする
		if (edit.equals("update") && (productName == "" || priceText == ""
				|| productName
						.matches(".*[!！@＠#＃%％^＾&＆*＊(（)）＿_＋+"
								+ "＝=［］\\[\\]｛｝{}｜|；;’'：:”\"，,．.／/"
								+ "＜＞<>？?～~‘`￥\\－-].*")
				|| !priceText.matches("[0-9]+"))) {

			message = "商品名または価格に無効な値が入力されています。";

		} else {
			// 全角英数字を半額へ変更する
			productName = CleanseString.apply(productName);

			try {
				ProductManager productManager = ProductManager.getInstance();
				// ここまで来たら入力値にspecial charactersか空白が入っていない為、編集処理を続ける
				// 検索ボタンと変更ボタンどっちが押されたかを判定する
				if (edit.equals("update")) {
					// 変更の処理
					int price = Integer.parseInt(priceText);
					message = productManager.updateProductData(productCode, productName, price);
					isEdited = (message.equals("商品の変更が成功しました。")) ? true : false;

				} else {
					// 削除の処理
					message = productManager.deleteProductData(productCode);
					isEdited = (message.equals("商品の削除が成功しました。")) ? true : false;
				}

			} catch (CustomException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		request.setAttribute("message", message);
		request.setAttribute("isEdited", isEdited);
		request.setAttribute("productCode", productCode);
		requestDispatcher = request.getRequestDispatcher("/edit.jsp");
		requestDispatcher.forward(request, response);
	}
}
