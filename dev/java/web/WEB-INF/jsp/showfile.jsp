<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Show file</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
  <link href="colorizer.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<%@ include file="/WEB-INF/jsp/top.jsp"%>
<p>
 <span class="sventonLocation">
   <c:out value="${url}/${path}" /> Rev: <c:out value="${revision}" />
 </span>
</p>
<p>
  <input class="sventonGoTo" type="text" name="goto_url" value="" />[GoTo]
</p>
<pre>
<c:out value="${fileContents}" escapeXml="false" />
</pre>
</body>
</html>
