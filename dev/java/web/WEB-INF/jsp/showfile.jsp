<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Show file</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
<%@ include file="/WEB-INF/jsp/top.jsp"%>
<pre>
<c:out value="${fileContents}" escapeXml="false" />
</pre>
</body>
</html>
