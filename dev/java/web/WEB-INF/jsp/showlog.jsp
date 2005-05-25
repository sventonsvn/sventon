<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Logs view</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
<%@ include file="/WEB-INF/jsp/top.jsp"%>

<table class="sventonLogEntriesTable">
	<tr>
		<th>Rev</th>
		<th>Message</th>
		<th>Author</th>
		<th>Date</th>
	</tr>
	<c:forEach items="${logEntries}" var="entry">
		<c:url value="showfile.svn" var="showUrl">
			<c:param name="path" value="${path}" />
			<c:param name="revision" value="${entry.revision}" />
		</c:url>
		<tr>
			<c:choose>
				<c:when test="${isFile}">
					<td><a href="<c:out value="${showUrl}"/>"><c:out
						value="${entry.revision}" /></a></td>
				</c:when>
				<c:otherwise>
					<td><c:out
						value="${entry.revision}" /></td>
				</c:otherwise>
			</c:choose>
			<td><c:out value="${entry.message}" /></td>
			<td><c:out value="${entry.author}" /></td>
			<td><c:out value="${entry.date}" /></td>
		</tr>
	</c:forEach>
</table>
</body>
</html>
