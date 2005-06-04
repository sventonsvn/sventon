<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Logged out</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
  <link href="colorizer.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<table width="100%" class="sventonHeader"><tr>
<td>sventon subversion web client</td>
<td align="right">
<c:choose>
  <c:when test="${empty uid}" > 
You are not logged in
  </c:when> 
  <c:otherwise> 
You are logged in as: <c:out value="${uid}" /> - <a href="clearsession.svn">Log out</a>
  </c:otherwise> 
</c:choose>  
</td></tr></table>
<p/>
You have been logged out.
<p/>
<a href="repobrowser.svn">sventon</a>
</body>
</html>
