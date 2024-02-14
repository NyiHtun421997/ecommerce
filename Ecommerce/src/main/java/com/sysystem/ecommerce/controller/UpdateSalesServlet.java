package com.sysystem.ecommerce.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sysystem.ecommerce.exception.CustomException;
import com.sysystem.ecommerce.repository.SalesDao;
import com.sysystem.ecommerce.service.SalesManager;

/**
 * Servlet implementation class UpdateSalesServlet
 */
@WebServlet(description = "売上データの更新処理を扱うサーブレット", urlPatterns = { "/update_sales" })
public class UpdateSalesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {
		SalesDao.loadDriver();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);

		// 押したボタンがキャンセルボタンの場合
		if (((String) request.getParameter("submit_type")).equals("cancel")) {
			// sessionを無効にする
			session.invalidate();
			response.sendRedirect("/Ecommerce/register_sales");
		} else {
			// 継続ボタンの場合
			Map<String, Integer> salesToUpdate = (Map<String, Integer>) session.getAttribute("salesToUpdate");
			SalesManager salesManager = new SalesManager();
			try {
				boolean isUpdated = salesManager.updateSales(salesToUpdate);
				String message = (isUpdated) ? "売上データ更新処理が成功しました。"
						: "トランザクション競合が原因で一部の売上データ更新が失敗しました。";
				// Sessionを無効にする
				session.invalidate();
				// 新しいSessionを有効にする
				session = request.getSession(true);
				session.setAttribute("message", message);
				session.setAttribute("isUpdated", isUpdated);

				response.sendRedirect("/Ecommerce/register_sales");
			} catch (CustomException e) {
				new RuntimeException(e.getMessage());
			}
		}
	}

}
