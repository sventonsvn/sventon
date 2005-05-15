<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Show file</title>
  <link href="colorizer.css" rel="stylesheet" type="text/css"> 
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>
<h3>
  <c:out value="${url}${path}" /> Rev: <c:out	value="${revision}" />
</h3>
<hr />
<pre>
<c:out value="${fileContents}" escapeXml="false" />
</pre>
</body>
</html>
