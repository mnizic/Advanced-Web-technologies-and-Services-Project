<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Prijava korisnika</title>
</head>
<body>
	<h1>Prijava korisnika</h1>
	<a
		href="${pageContext.servletContext.contextPath}/mvc/izbornik">
		Početna </a>
	<br>
	<br>

	<h3>Unesite korisničke podatke:</h3>
	<form action="${pageContext.servletContext.contextPath}/mvc/admin/prijavljenKorisnik" method="POST">
		Korisničko ime:
		<input type="text" name="korime" id="korime" required></input><br/>
		Lozinka:
		<input type="password" name="lozinka" id="lozinka" required></input><br/>
		<button type="submit">Prijavi se</button>
	</form>
	<br>
	<br>

</body>
</html>