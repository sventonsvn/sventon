<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="/spring" %>

<html>
<head><title><fmt:message key="authenticate.title"/></title></head>
<body>
<h3><fmt:message key="authenticate.heading"/></h3>
<form method="post">
<table>
<tr>
<td>username</td>
  <spring:bind path="auth.uid">
<td>
    <font color="red"><c:out value="${status.errorMessage}"/></font><br>
	<input type="text" name="uid" value="<c:out value="${status.value}"/>">
</td>
</tr>
  </spring:bind>
<tr>
<td>password</td>
  <spring:bind path="auth.pwd">
<td>
    <font color="red"><c:out value="${status.errorMessage}"/></font><br>
    <input type="password" name="pwd" value="<c:out value="${status.value}"/>">
</td>
</tr>
  </spring:bind>
  <spring:hasBindErrors name="auth">
<tr>
<td colspan="2">
  <b>Authentication failed, please try again!</b>
</td>
</tr>
  </spring:hasBindErrors>
<tr>
<td colspan="2">
  <input type="submit" alignment="center" value="login">
</td>
</tr>
</table>
</form>
</body>
</html>