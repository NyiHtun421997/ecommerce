<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register Product</title>
<style>
#productName {
	width: 500px;
	height: 30px;
	margin-left: 18px;
}

#price {
	width: 250px;
	height: 30px;
	margin-left: 42px;
	margin-top: 25px;
}

#register {
	margin-top: 50px;
}

label {
	vertical-align: middle;
}

button {
	margin-top: 50px;
	margin-left: 525px;
	width: 80px;
	height: 40px;
}
</style>
</head>
<body>

	<!-- メニュー画面から最初に編集画面へ遷移した場合はサーバーメッセージがnullであるため -->
	<!-- 表示するメッセージを空白のままする、その他は登録処理後のサーバーメッセージを受け取る-->
	<%
		String message = ((message = (String) request.getAttribute("message")) == null) ? "" : message;
		boolean isRegistered = (request.getAttribute("isRegistered") == null) ? false : (boolean)request.getAttribute("isRegistered");
	%>
	<header style="font-size: 35px; font-weight: bold;">商品登録</header>

	<main>
		<form action="/Ecommerce/register_product" method="post">
			<section id="register">
				<p>
					<label for="productName" style="font-size: 28px;">商品名</label> <input
						type="text" name="productName" id="productName">
				</p>

				<p>
					<label for="price" style="font-size: 28px;">単価</label> <input
						type="number" min="0" name="price" id="price">
				</p>
					<%
						if (isRegistered) {
					%>
						<span style="margin-left: 130px; color: green;"><%=message%></span>
					<%
						} else {
					%>
						<span style="margin-left: 130px; color: red;"><%=message%></span>
					<%
						}
					%>
				<p>
					<button type="submit">登録</button>
				</p>
			</section>
		</form>
	</main>

	<footer>
		<a href="/Ecommerce/index.jsp">Home</a>
	</footer>

</body>
</html>