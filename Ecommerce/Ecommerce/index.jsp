<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Product Management System</title>
<style>
header h1 {
	font-size: 60px;
	text-align: center;
}

button {
	width: 250px;
	height: 150px;
}

div {
	display: inline-block;
	margin: 20px;
}
</style>
</head>
<body>
	<% session.invalidate(); %>

	<header>
		<h1>商品管理システム</h1>
	</header>

	<main>
		<h1 style="text-align: center;">メニュー</h1>
		<article>

			<div>
				<form action="/Ecommerce/search" method="get">
					<button type="submit">商品検索</button>
				</form>
			</div>

			<div>
				<form action="/Ecommerce/register_product" method="get">
					<button type="submit">商品登録</button>
				</form>
			</div>

			<div>
				<form action="/Ecommerce/register_sales" method="get">
					<button type="submit">売上登録</button>
				</form>
			</div>

			<div>
				<form action="/Ecommerce/download_csv" method="get">
					<button type="submit">CSVダウンロード</button>
				</form>
			</div>

		</article>
	</main>

</body>
</html>