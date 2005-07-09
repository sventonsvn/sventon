<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>sventon blame - <c:out value="${url}" /></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
<%@ include file="/WEB-INF/jsp/top.jsp"%>

Blame

<table>
	<tr>
		<th>Revision</th>
		<th>Author</th>
		<th>Line</th>
	</tr>
	<c:forEach items="${handler.blameLines}" var="line">
	<tr>
		<td><c:out value="${line.revision}" /></td>
		<td><c:out value="${line.author}" /></td>
		<td><pre><c:out value="${line.line}" /></pre></td>
	</tr>
	</c:forEach>
</table>
</body>
</html>
