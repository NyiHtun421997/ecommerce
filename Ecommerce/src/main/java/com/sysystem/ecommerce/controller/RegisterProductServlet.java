package com.sysystem.ecommerce.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sysystem.ecommerce.exception.CustomException;
import com.sysystem.ecommerce.repository.ProductDao;
import com.sysystem.ecommerce.service.ProductManager;

/**
 * Servlet implementation class RegisterProduct
 */
@WebServlet(description = "商品マスタに商品を登録するサーブレット", urlPatterns = { "/register_product" })
public class RegisterProductServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		ProductDao.loadDriver();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/register_product.jsp");
		requestDispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		String productName = (String) request.getParameter("productName");
		String priceText = (String) request.getParameter("price");
		String message = "";
		boolean isRegistered = false;
		RequestDispatcher requestDispatcher;

		// 入力された値に無効な文字が入っているかチェックする
		if (productName == "" || priceText == ""
				|| productName.matches(".*[!！@＠#＃%％^＾&＆*＊(（)）＿_＋+"
						+ "＝=［］\\[\\]｛｝{}｜|；;’'：:”\"，,．.／/"
						+ "＜＞<>？?～~‘`－-].*")
				|| !priceText.matches("[0-9]+")) {
			message = "商品名または価格に無効な値が入力されています。";
			request.setAttribute("message", message);
			request.setAttribute("isRegistered", isRegistered);
			requestDispatcher = request.getRequestDispatcher("/register_product.jsp");
			requestDispatcher.forward(request, response);
		} else {
			int productPrice = Integer.parseInt(priceText);
			ProductManager productManager = null;
			try {
				productManager = ProductManager.getInstance();
				isRegistered = productManager.registerProduct(productName, productPrice);
			} catch (CustomException e) {
				throw new RuntimeException(e.getMessage());
			}

			message = "商品の登録が成功しました。";
			request.setAttribute("message", message);
			request.setAttribute("isRegistered", isRegistered);
			requestDispatcher = request.getRequestDispatcher("/register_product.jsp");
			requestDispatcher.forward(request, response);
		}
	}

}
