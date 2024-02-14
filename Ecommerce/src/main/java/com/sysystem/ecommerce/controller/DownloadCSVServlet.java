package com.sysystem.ecommerce.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sysystem.ecommerce.exception.CustomException;
import com.sysystem.ecommerce.repository.SalesDao;
import com.sysystem.ecommerce.service.SalesManager;

/**
 * Servlet implementation class DownloadCSVServlet
 */
@WebServlet(description = "商品別売上CSVダウンロードためのサーブレット", urlPatterns = { "/download_csv" })
public class DownloadCSVServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SalesManager salesManager;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SalesDao.loadDriver();
		salesManager = new SalesManager();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/download_csv.jsp");
		requestDispatcher.forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String filePath = getServletContext().getRealPath("/");

			if (request.getParameter("date") == null) {

				salesManager.createAllSalesRecordCsv(filePath);
				filePath = filePath + "resources" + File.separator + "sales_record" + File.separator;

				String fileName = "商品別売上集計.csv";
				String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/csv; charset=UTF-8");
				response.addHeader("Content-disposition", "attachment; filename=" + encodedFileName);

				// Read the file
				try (BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(filePath + fileName), "SJIS"));
						PrintWriter out = response.getWriter()) {

					String in;
					while ((in = bufferedReader.readLine()) != null) {
						out.println(in);
					}
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}

			} else {
				String date = request.getParameter("date");
				salesManager.createSalesRecordCsvSpecifiedDate(filePath, date);

				filePath = filePath + "resources" + File.separator + "sales_record" + File.separator;
				String fileName = "指定年月商品別売上集計_" + date + ".csv";
				String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

				response.setCharacterEncoding("UTF-8");
				response.setContentType("text/csv; charset=UTF-8");
				response.addHeader("Content-disposition", "attachment; filename=" + encodedFileName);

				// Read the file
				try (BufferedReader bufferedReader = new BufferedReader(
						new InputStreamReader(new FileInputStream(filePath + fileName), "SJIS"));
						PrintWriter out = response.getWriter()) {

					String in;
					while ((in = bufferedReader.readLine()) != null) {
						out.println(in);
					}
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage());
				}
			}

		} catch (CustomException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

}
