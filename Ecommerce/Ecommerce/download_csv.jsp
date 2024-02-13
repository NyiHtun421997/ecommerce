<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Download CSV</title>
<style>
    #button1 {
        width: 150px;
        height: 40px;
        margin-left: 315px;
    }
    #button2 {
        width: 200px;
        height: 40px;
    }
    #date {
        width: 140px;
        height: 30px;
    }
</style>
</head>
<body>
    <header style="font-size: 35px; font-weight: bold;">CSVダウンロード</header>
    <main style="font-size: 25px;">
	   <form action="/Ecommerce/download_csv" method="post">
		    <p>
		        <button type="submit" id="button1">商品別売上集計CSV</button>
		    </p>
		</form>
		<form action="/Ecommerce/download_csv" method="post">
		    <p>
		        <label for="date" style="margin-right: 30px; vertical-align: middle;">年月</label>
		        <input type="month" name="date" id="date" value="2024-02">
		
		        <button type="submit" id="button2" style="margin-left: 75px;">指定年月商品別売上集計CSV</button>
		    </p>
		 </form>
	 </main>
	 
	 <footer>
    	<a href="/Ecommerce/index.jsp">Home</a>
    </footer>
</body>
</html>