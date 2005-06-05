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
			<c:set var="message" value="${entry.svnLogEntry.message}" />
			<jsp:useBean id="message" type="java.lang.String" />
			<% 
				//fn:replace(entry.svnLogEntry.message, '\\\n', '<br/>' simply refused to work... 
            	//Perhaps other replacements have to be made for this to work for all types of line breaks?
			%>
			<td><%= message.replace("\n", "<br/>\n") %></td>
			<td><c:out value="${entry.svnLogEntry.author}" /></td>
			<td><c:out value="${entry.svnLogEntry.date}" /></td>
		</tr>
	</c:forEach>
</table>
</body>
</html>
