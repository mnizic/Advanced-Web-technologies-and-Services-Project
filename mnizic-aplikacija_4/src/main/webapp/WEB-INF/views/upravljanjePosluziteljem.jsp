<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pregled svih korisnika</title>
</head>
<body>
	<h1>Pregled svih korisnika</h1>
	<a href="${pageContext.servletContext.contextPath}/mvc/izbornik">
		Početna stranica </a>
	<br>
	<br>
	<c:set var="s" scope="request" value="${requestScope.s}" />
	<h3>
		STATUS:
		<c:out value="${s}" />
	</h3>

	<h2>Komande:</h2>


	<form action="" method="post">
		Odaberi komandu: <select name="komanda">
			<option value="INIT">Inicijalizacija poslužitelja</option>
			<option value="QUIT">Prekid rada poslužitelja</option>
			<option value="LOAD">Učitavanje podataka</option>
			<option value="CLEAR">Brisanje podataka</option>

		</select> <br />
		<h4>Kod komande LOAD:</h4>
		<textarea name="loadInput" id="loadInput" cols="40" rows="30"></textarea>
		<br /> <br /> <br /> <input type="submit" value="Pošalji komandu" />

	</form>
</body>
</html>