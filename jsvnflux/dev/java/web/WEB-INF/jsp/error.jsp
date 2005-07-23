<%@ include file="/WEB-INF/jsp/include.jsp"%>
<html>
<head>
<title>Unhandled error</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<h3>An unhandled error has occured. Sorry. </h3>
<p/>
<table class="sventonStackTraceTable">
<tr>
<td>
<pre>
<c:out value="${throwable}" /> 
</pre>
</td>
</tr>
</table>
</html>