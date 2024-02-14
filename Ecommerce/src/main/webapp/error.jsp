<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ERROR!</title>
</head>
<body>
<h1 style="color:red">問題が発生しました!</h1>
<span style="font-size:25px;"><%= exception.getMessage() %></span><br>
<a href="/Ecommerce/index.jsp">Home</a>
<a></a>
</body>
</html>