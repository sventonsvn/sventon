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
			<c:param name="path" value="${entry.pathAtRevision}" />
			<c:param name="revision" value="${entry.svnLogEntry.revision}" />
		</c:url>
		<tr>
			<c:choose>
				<c:when test="${isFile}">
					<td><a href="<c:out value="${showUrl}"/>"><c:out
						value="${entry.svnLogEntry.revision}" /></a></td>
				</c:when>
				<c:otherwise>
					<td><c:out
						value="${entry.svnLogEntry.revision}" /></td>
				</c:otherwise>
			</c:choose>
			<td><c:out value="${entry.svnLogEntry.message}" /></td>
			<td><c:out value="${entry.svnLogEntry.author}" /></td>
			<td><c:out value="${entry.svnLogEntry.date}" /></td>
		</tr>
	</c:forEach>
</table>
</body>
</html>
