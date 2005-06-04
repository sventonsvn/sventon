<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="/spring"%>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<html>
<head>
<title><fmt:message key="authenticate.title" /></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
<link href="colorizer.css" rel="stylesheet" type="text/css" />
</head>
<body>
<table width="100%" class="sventonHeader">
	<tr>
		<td>sventon subversion web client</td>
		<td align="right"><c:choose>
			<c:when test="${empty uid}"> 
You are not logged in
  </c:when>
			<c:otherwise> 
You are logged in as: <c:out value="${uid}" /> - <a
					href="clearsession.svn">Log out</a>
			</c:otherwise>
		</c:choose></td>
	</tr>
</table>

<p />

<h3><fmt:message key="authenticate.heading" /></h3>
<form method="post">
<table>
   <spring:hasBindErrors name="auth">
		<tr>
			<td>Authentication failed, please try again!<br><br></td>
		</tr>
	</spring:hasBindErrors>

	<tr>
		<spring:bind path="auth.uid">
			<td>username <font color="red"><c:out value="${status.errorMessage}" /></font>
			</td>
	</tr>
	<tr>

		<td><input type="text" name="uid"
			value="<c:out value="${status.value}"/>"></td>
	</tr>
	</spring:bind>
	<tr>
		<td>password <font color="red"><c:out value="${status.errorMessage}" /></font>
		</td>
	</tr>
	<tr>
		<spring:bind path="auth.pwd">
			<td><input type="password" name="pwd"
				value="<c:out value="${status.value}"/>"></td>
	</tr>
	</spring:bind>
	<tr>
		<td colspan="2"><input type="submit" alignment="center" value="login"></td>
	</tr>
</table>
</form>
</body>
</html>
