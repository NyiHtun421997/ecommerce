<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.time.LocalDate" import="java.time.format.DateTimeFormatter" %>
<%@page import="java.util.List" import="java.util.Map" import="java.util.Map.Entry"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Confirmation</title>
<style>
	button {
        margin-left: 20px;
		width:84px;
		height:42px;
		font-size:12px
	}
	#update_sales_table {
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
		
		Map<String, Integer> salesToUpdate = (Map<String, Integer>) session.getAttribute("salesToUpdate");
		String message = (String)session.getAttribute("message");
		String productDeletedMessage = (String)session.getAttribute("productDeletedMessage");		
	%>
	<header><h1 style="font-size:35px">売上更新の確認</h1></header>
	
	<main>
		<section>
			<p style="font-size: 25px;">
                売上日<span style="margin-left: 50px;"><%= currentDate %></span>
            </p style="font-size: 25px;">
            	<span style="color:red"><%= productDeletedMessage %></span><br>
            	<span style="color:green"><%= message %></span> 
            <p>
            	<form action="/Ecommerce/update_sales" method="post">
            		<table id="update_sales_table">
            			<thead>
            				<tr>
            					<th height="40" width="400">商品名</th><th width="80">数量</th>
            				</tr>
            			</thead>
            		
            			<tbody>
            			<% for (Entry<String, Integer> sales : salesToUpdate.entrySet()) { %>
            				<tr>
            					<td height="40"><%= sales.getKey() %></td>
                        		<td><%=  sales.getValue() %></td>
            				</tr>
            			<% } %>
            			</tbody>
            		</table>
            		
            		<p style="margin-top: 20px; font-size: 25px; color: black">
            			このテーブル内の売上は本日既に売上発生しています。更新する場合は継続をクリックしてください。
            		</p>
            		
            		<br>
            		
            		<button style="margin-left: 30px" name="submit_type" type="submit" value="update">継続</button>
            		<button name="submit_type" type="submit" value="cancel">キャンセル</button>
            	</form>
            </p>
		</section>
	</main>

</body>
</html>