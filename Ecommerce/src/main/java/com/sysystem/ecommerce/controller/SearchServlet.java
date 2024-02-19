package com.sysystem.ecommerce.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sysystem.ecommerce.exception.CustomException;
import com.sysystem.ecommerce.model.Product;
import com.sysystem.ecommerce.repository.ProductDao;

/**
 * Servlet implementation class Search
 */
@WebServlet(description = "商品を検索するサーブレット", urlPatterns = { "/search" })
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private List<Product> allProducts;

	public void init(ServletConfig config) throws ServletException {
		ProductDao.loadDriver();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RequestDispatcher requestDispatcher;
		request.setCharacterEncoding("UTF-8");
		
		String productName = (String) request.getParameter("productName");
		String message = "";
		
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals("message"))
				message = cookie.getValue();
		}
		
		// 編集画面から検索画面に遷移する場合
		if (productName == null) 
			productName = "";

		try {
			allProducts = ProductDao.searchActiveProductsOrderByCode(productName);
			
		} catch (CustomException e) {
			throw new RuntimeException(e.getMessage());
		}

		request.setAttribute("allProducts", allProducts);
		request.setAttribute("message", message);
		requestDispatcher = request.getRequestDispatcher("/search.jsp");
		requestDispatcher.forward(request, response);
	}
}
