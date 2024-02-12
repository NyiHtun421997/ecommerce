<%@page import="com.sysystem.ecommerce.model.Product" %>
<%@page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Search Product</title>
<style>
	#search_bar {
		display:inline;
	}
	#productName {
		width:460px;
		height:30px;
		margin:20px;
	}
    #result_table {
        border-collapse: collapse;
        text-align: center;
        border:2px solid black;
    }
    th,td {
        border:2px solid black;
    }
	button {
		width:60px;
		height:30px;
	}
</style>
</head>
<body>
	<% 
		List<Product> allProducts = (List<Product>)request.getAttribute("allProducts");
		String message = (String)request.getAttribute("message");
	%>
	<header>
		<h1 style="font-size:35px">商品検索</h1>
	</header>
	
	<main>
		<section id="search_bar">
			<form action="/Ecommerce/search" method="get">
				<label for="productName" style="font-size:28px">商品名</label>
				<input type="text" name="productName" id="productName">
				<button type="submit">検索</button>
			</form>
		</section>	
		
		<section id="table">
			<table id="result_table">
				<thead>
					<tr>
						<th height="40" width="110">商品コード</th><th width="320">商品名</th><th width="100">単価</th><th width="120">操作</th>
					</tr>
				</thead>
				
				<tbody>
					<%for (Product product : allProducts) { %>
					<tr>
					<% String productCode = String.format("%03d", product.getCode());
						String price = String.format("%,d", product.getPrice());	
					%>
						<td height="35"><%= productCode %></td>
						<td><%=  product.getName() %></td>
						<td><%=  price %></td>
						<td>
							<a href="/Ecommerce/edit?product_code=<%= product.getCode() %>&product_name=<%= product.getName() %>">
							編集</a>
						</td>
					</tr>
					<% } %>
				</tbody>
			</table>
		</section>
		
		<%
		if (message != null) {%>
		<section>
			<p style="margin-left: 130px; color: green;">
			<%= message %>
			</p>
		</section>
		<%
		}
		%>
		
		
	</main>
	
	<footer>
    	<a href="/Ecommerce/index.jsp">Home</a>
    </footer>

</body>
</html>