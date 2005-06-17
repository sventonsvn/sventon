<%@ include file="/WEB-INF/jsp/include.jsp"%>
<html>
<head>
<title><fmt:message key="authenticate.title" /></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
<link href="colorizer.css" rel="stylesheet" type="text/css" />
</head>
<body onLoad="document.loginForm.uid.focus();">
<%@ include file="/WEB-INF/jsp/sventonbar.jsp"%>

<p />

<h3><fmt:message key="authenticate.heading" /></h3>
<form name="loginForm" method="post">
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
