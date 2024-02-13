<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.time.LocalDate" import="java.time.format.DateTimeFormatter" %>
<%@page import="java.util.List" import="java.util.Map" import="java.util.Map.Entry"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register Sales</title>
<style>
    #productName {
        margin-left: 48px;
        width: 200px;
        height: 40px;
        text-align: center;
    }
    #quantity {
        margin-left: 16px;
        width: 50px;
        height: 32px;
    }
    label {
        font-size: 25px;
    }
	
	button {
        margin-left: 20px;
		width:70px;
		height:35px;
	}
    #register_button {
        margin-left: 510px;
    }
    #product_table {
        border-collapse: collapse;
        text-align: center;
        font-size: 25px;
        border: 2px solid black;
    }
    th,td {
        border: 2px solid black;
    }
</style>
</head>
<body>
	<%
		request.setCharacterEncoding("UTF-8");
		String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		
		List<String> allProductNames = (List<String>)session.getAttribute("allProductNames");
		Map<String, Integer> existingSales = (Map<String, Integer>) session.getAttribute("existingSales");
		Map<String, Integer> newSales = (Map<String, Integer>) session.getAttribute("newSales");
	%>
	<!-- メニュー画面から最初にこの画面へ遷移した場合はサーバーメッセージがnullであるため -->
	<!-- 表示するメッセージを空白のままする、その他は登録処理後のサーバーメッセージを受け取る-->
	<%
		String message = ((message = (String) session.getAttribute("message")) == null) ? "" : message;
		boolean isUpdated = (session.getAttribute("isUpdated") == null) ? false : (boolean)session.getAttribute("isUpdated");
		String productDeletedMessage = ((productDeletedMessage = (String) session.getAttribute("productDeletedMessage")) == null) ? "" : productDeletedMessage;
	%>
	<header>
		<h1 style="font-size:35px">売上登録</h1>
	</header>
	
	<main>
        <section>
            <p style="font-size: 25px;">
                売上日<span style="margin-left: 50px;"><%= currentDate %></span>
            </p>
            <p>
            	<form action="/Ecommerce/register_sales" method="post">
	                <label for="productName">商品名</label>
	                <select name="productName" id="productName">
	                <% for (String productName : allProductNames) {%>
	                	<option value="<%= productName %>"><%= productName %></option>
	                <% } %>
	                </select>
	                <label for="quantity" style="margin-left: 26px;">数量</label>
	                <input type="number" min="1" name="quantity" id="quantity">
	                <button name="submit_type" type="submit">追加</button>
                </form>
            </p>
        </section>
        <hr>
	
		<section id="table">
		<form action="/Ecommerce/register_sales" method="post">
			<table id="product_table">
				<thead>
					<tr>
						<th height="40" width="400">商品名</th><th width="80">数量</th>
					</tr>
				</thead>
				
				<tbody>
				<% for (Entry<String, Integer> sales : existingSales.entrySet()) {%>
					<tr>
                        <td height="40"><%= sales.getKey() %></td>
                        <td><%=  sales.getValue() %></td>
                    </tr>
                <% } %>
                <% for (Entry<String, Integer> sales : newSales.entrySet()) {%>
                    <tr>
                    	<td height="40"><%= sales.getKey() %></td>
                    	<td><%= sales.getValue() %></td>
                    </tr>
                <% } %>
				</tbody>
			</table>

			
			<%
				if (isUpdated) {
			%>
				<span style="margin-left: 130px; color: green;"><%=message%></span>
			<%
				} else {
			%>
				<span style="margin-left: 130px; color: red;"><%=message%></span>
			<%
				}
			%>
						
			<br>
			
			<span style="margin-left: 130px; color: red;"><%=productDeletedMessage%></span>
			<br>
			
            <button name="submit_type" type="submit" id="register_button" value="register">登録</button>
            </form>
		</section>
	</main>
	
	<footer>
		<a href="/Ecommerce/index.jsp">Home</a>
	</footer>

</body>
</html>