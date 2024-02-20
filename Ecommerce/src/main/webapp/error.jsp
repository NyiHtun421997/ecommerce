<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ERROR!</title>
</head>
<body>
<header>
	<a href="/Ecommerce/index.jsp" style="text-decoration: none; color: black; font-size: 25px; text-align: center;">
		<h1>商品管理システム</h1>
	</a>
	<h2 style="color:red">問題が発生しました!</h2>
</header>

<span style="font-size:25px;"><%= exception.getMessage() %></span><br>
<a href="/Ecommerce/index.jsp">Home</a>
<a></a>
</body>
</html>