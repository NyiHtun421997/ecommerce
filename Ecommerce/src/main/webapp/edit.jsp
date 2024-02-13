<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="javax.servlet.http.Cookie"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Product</title>
<style>
label {
	font-size: 30px;
	vertical-align: middle;
}

#productName {
	margin-left: 105px;
	margin-top: 20px;
	width: 460px;
	height: 36px;
}

#price {
	margin-left: 135px;
	margin-top: 30px;
	width: 300px;
	height: 36px;
}

button {
	display: inline;
	margin-top: 150px;
	width: 80px;
	height: 40px;
}
</style>
</head>
<body>
	<%
		request.setCharacterEncoding("UTF-8");
		int productCode = (Integer) request.getAttribute("productCode");
		String productCodeText = String.format("%03d", productCode);
		String productName = ((productName = (String) request.getAttribute("productName")) == null) ? "" : productName;
	%>
	<!-- 検索画面から最初に編集画面へ遷移した場合はサーバーメッセージがnullであるため -->
	<!-- 表示するメッセージを空白のままする、その他は変更または削除処理後のサーバーメッセージを受け取る-->
	<%
		String message = ((message = (String) request.getAttribute("message")) == null) ? "" : message;
		boolean isEdited = (request.getAttribute("isEdited") == null) ? false : (boolean)request.getAttribute("isEdited");
	%>
	<header>
		<h1 style="font-size: 35px">商品変更・削除</h1>
	</header>

	<main>
		<p style="font-size: 30px;">
			商品コード<span style="margin-left: 50px;"><%=productCodeText%></span>
		</p>
		<form action="/Ecommerce/edit" method="post">
			<input type="hidden" name="productCode" value=<%=productCode%>>
			<p>
				<label for="productName">商品名</label> <input type="text"
					name="productName" id="productName" placeholder="<%= productName %>">
			</p>

			<p>
				<label for="price">単価</label> <input type="number" name="price"
					id="price">
			</p>

			<p>
				<%
					if (isEdited) {
				%>
				<!-- 変更・削除処理が成功したというメッセージを次の画面に表示するためCookieに保管する -->
				<%
						Cookie cookie = new Cookie("message", message);
						cookie.setMaxAge(30);
						response.addCookie(cookie);
						response.sendRedirect("/Ecommerce/search");
				} 	else {
				%>
				<span style="margin-left: 130px; color: red;"><%=message%></span>
				<%
					}
				%>
				<span style="margin-left: 500px;"><button name="edit"
						type="submit" value="delete">削除</button></span>
				<button name="edit" type="submit" value="update">変更</button>
			</p>
		</form>
	</main>
	
	<footer>
    	<a href="/Ecommerce/index.jsp">Home</a>
    </footer>

</body>
</html>