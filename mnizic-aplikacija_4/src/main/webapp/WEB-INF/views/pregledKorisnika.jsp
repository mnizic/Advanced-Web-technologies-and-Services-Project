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
	<c:set var="mojeKorime" scope="request" value="${requestScope.mojeKorime}" />
	<h3>
		<a
			href="${pageContext.servletContext.contextPath}/mvc/admin/pregledKorisnika/${mojeKorime}">Obriši
		vlastiti žeton</a>
	</h3>
	<table border="1">
		<tr>
			<th>korime</th>
			<th>ime</th>
			<th>prezime</th>
			<th>lozinka</th>
			<th>email</th>
			<c:set var="admin" scope="request" value="${requestScope.admin}" />
			<c:if test="${admin == 1}">
				<th>brisanje zetona</th>
			</c:if>
		</tr>
		<c:forEach var="k" items="${requestScope.korisnici}">
			<tr>
				<td>${k.getKorIme()}</td>
				<td>${k.getIme()}</td>
				<td>${k.getPrezime()}</td>
				<td>${k.getLozinka() }</td>
				<td>${k.getEmail() }</td>
				<c:set var="admin" scope="request" value="${requestScope.admin}" />
				<c:if test="${admin == 1}">
					<td><a
						href="${pageContext.servletContext.contextPath}/mvc/admin/pregledKorisnika/${k.getKorIme()}">obriši</a></td>
				</c:if>
			</tr>
		</c:forEach>
	</table>
</body>
</html>