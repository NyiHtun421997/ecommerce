package com.sysystem.ecommerce.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
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
 * Servlet implementation class RegisterSalesServlet
 */
@WebServlet(description = "売上データを登録するサーブレット", urlPatterns = { "/register_sales" })
public class RegisterSalesServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private SalesManager salesManager;

	public void init(ServletConfig config) throws ServletException {
		SalesDao.loadDriver();
		salesManager = new SalesManager();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			HttpSession session = request.getSession(true);
			session.setAttribute("existingSales", salesManager.getExistingSales());
			session.setAttribute("allProductNames", salesManager.getActiveProductNames());
			session.setAttribute("newSales", new LinkedHashMap<>());
		} catch (CustomException e) {
			throw new RuntimeException(e.getMessage());
		}
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/register_sales.jsp");
		requestDispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		Map<String, Integer> newSales = (Map<String, Integer>) session.getAttribute("newSales");
		String message = "";
		
		
		// 押したボタンが追加ボタンの場合
		if (!((String) request.getParameter("submit_type")).equals("register")) {

			RequestDispatcher requestDispatcher = request.getRequestDispatcher("/register_sales.jsp");
			
			String productName = (String) request.getParameter("productName");
			
			// 数量に何も入力しなくて追加ボタンを押した場合の処理
			if (request.getParameter("quantity").equals("")) {
				message = "数量を入力してください。";
				session.setAttribute("message", message);
				session.setAttribute("isUpdated", false);
				requestDispatcher.forward(request, response);
				return; // 以降は続かない
			}
			// ここまで来たらユーザーの入力に問題ないのが確実
			// ユーザーに表示するエラーメッセージを消す
			session.removeAttribute("message");
			session.removeAttribute("isUpdated");
			int quantity = Integer.parseInt((String) request.getParameter("quantity"));

			newSales.computeIfPresent(productName, (name, oldQuantity) -> oldQuantity + quantity);
			newSales.putIfAbsent(productName, quantity);

			try {
				session.setAttribute("allProductNames", salesManager.getActiveProductNames());
				session.setAttribute("existingSales", salesManager.getExistingSales());
				session.setAttribute("newSales", newSales);
			} catch (CustomException e) {
				throw new RuntimeException("接続が失敗しました。");
			}
			requestDispatcher.forward(request, response);

		} else {
			// 登録ボタンの場合
			boolean isRegistered = false;
			String productDeletedMessage = "";
			
			try {
				// 今までユーザーが追加してきた商品名と数量を登録する
				// 登録の結果をStatusMapに受け取って更新処理が必要だったら続ける
				Map<String, Integer> salesRegisterStatusMap = salesManager.registerSales(newSales);
				Map<String, Integer> salesToUpdate = new LinkedHashMap<>();
				
				// 既に削除された商品が1つ以上存在する場合の処理
				// 商品が既に削除された場合警告メッセージを表示するように処理する
				if (salesRegisterStatusMap.containsKey("ProductAlreadyDeleted")) {
					salesRegisterStatusMap.remove("ProductAlreadyDeleted");
					productDeletedMessage = "既に削除された商品が含まれています。一部の商品売上登録が失敗しました。";
				}
				
				// 登録しようとする売上データのなかに1つ以上のデータが登録された場合の処理
				if (salesRegisterStatusMap.containsKey("ProductRegistered")) {
					message = "商品売上データ登録が成功しました。";
					salesRegisterStatusMap.remove("ProductRegistered");
					isRegistered = true;
				}
				
				// to confirmation page
				// StatusMapはEmptyではなければ、既に売上が発生したので登録ではなく更新処理が必要である商品が存在する
				if (!salesRegisterStatusMap.isEmpty()) {
					
					// 更新処理が必要な売上データが存在する上に一部の商品売上登録が成功したというメッセージを表示するように処理する
					if (isRegistered)
						message = " 一部の商品売上データ登録が成功しました。";
					
					// 更新処理が必要な商品名＋数量のみ含むMap
					salesToUpdate = salesRegisterStatusMap;
					
					// redirect to page that displays a confirmation dialogue prompt whether to continue updating sales data
					session.setAttribute("salesToUpdate", salesToUpdate);
					session.setAttribute("message", message);
					session.setAttribute("productDeletedMessage", productDeletedMessage);
					response.sendRedirect("/Ecommerce/confirm_update_sales.jsp");
					
				} else {					
					// StatusMapはEmptyですから登録処理が成功して更新処理の必要はない場合の処理								
					// Sessionを無効にする
					session.invalidate();
					// 新しいSessionを有効にする
					session = request.getSession(true);
					
					session.setAttribute("message", message);
					session.setAttribute("productDeletedMessage", productDeletedMessage);
					// 登録が成功したことをユーザー知らせる
					session.setAttribute("isUpdated", isRegistered);
					
					doGet(request, response);
				}
			} catch (CustomException e) {
				throw new RuntimeException("接続が失敗しました。");
			}
		}

	}

}
