<%@ include file="/WEB-INF/jsp/include.jsp"%>
<html>
<head>
<title><fmt:message key="${errorHeadingKey}"/></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<h3><fmt:message key="${errorHeadingKey}"/></h3>
<p/>
<table class="sventonErrorMessageTable">
<tr>
<td>
<fmt:message key="${errorMessageKey}"/>
</td>
</tr>
</table>
</html>