<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Show file</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>
<h3><c:out value="${url}${path}" /> Rev: <c:out
	value="${revision}" />
<hr />
<pre>
<c:out value="${fileContents}" />
</pre>
</body>
</html>
