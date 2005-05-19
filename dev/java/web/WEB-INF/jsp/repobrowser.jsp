<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="org.tmatesoft.svn.core.io.*" %>

<html>
<head>
<title>Repository browser view</title>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp"%>
<h3><c:out value="${url}/${path}" /> Rev: <c:out
	value="${revision}" /></h3>
<table>
	<tr>
		<th>File</th>
		<th>Last changed rev</th>
		<th>File type</th>
		<th colspan="2">Options</th>
	</tr>
	<c:forEach items="${svndir}" var="entry">
		<c:url value="repobrowser.svn" var="viewUrl">
			<c:param name="path" value="${path}${entry.name}" />
		</c:url>
		<c:url value="showlog.svn" var="showLogUrl">
			<c:param name="path" value="${path}${entry.name}" />
		</c:url>
		<c:url value="showfile.svn" var="showFileUrl">
			<c:param name="path" value="${path}${entry.name}" />
		</c:url>
		<tr>


			<%
    SVNDirEntry type = (SVNDirEntry) pageContext.getAttribute("entry");
    SVNNodeKind nodeKind = type.getKind();

    %>
			<% if (nodeKind == SVNNodeKind.DIR) { %>
			<td><a href="<c:out value="${viewUrl}/"/>"><c:out
				value="${entry.name}" /></a></td>
			<% } else { %>
			<td><c:out value="${entry.name}" /></td>
			<% } %>
			<td><c:out value="${entry.revision}" /></td>
			<td><c:out value="${entry.kind}" /></td>
			<td><a href="<c:out value="${showLogUrl}"/>">Show log</a></td>
			<td><% if (nodeKind == SVNNodeKind.FILE) { %><a href="<c:out value="${showFileUrl}"/>">View file</a> <% } %></td>
		</tr>
	</c:forEach>
</table>
<br>
</body>
</html>
