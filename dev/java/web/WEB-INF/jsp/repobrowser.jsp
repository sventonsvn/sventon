<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="org.tmatesoft.svn.core.io.*" %>

<html>
<head>
<title>Repository browser view</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
<%@ include file="/WEB-INF/jsp/top.jsp"%>

<table class="sventonEntriesTable">
	<tr>
    <th></th>
    <th></th>
		<th>File</th>
    <th>Size (bytes)</th>
		<th>Last changed rev</th>
		<th>Last changed by</th>
    <th>Date</th>
		<th>Options</th>
	</tr>
  <% int rowCount = 0; %>
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

    <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
    <%
    SVNDirEntry type = (SVNDirEntry) pageContext.getAttribute("entry");
    SVNNodeKind nodeKind = type.getKind();
    long entrySize = type.size();
    %>
      <td class="sventonCol1"><input type="checkbox" name="entries" value="<c:out value="${entry.name}" />"/></td>
      <% if (nodeKind == SVNNodeKind.DIR) { %>
      <td class="sventonCol2"><img src="images/icon_dir.gif"/></td>
      <td class="sventonCol3"><a href="<c:out value="${viewUrl}/&revision=${revision}"/>"><c:out
        value="${entry.name}" /></a></td>
      <% } else { %>
      <td class="sventonCol2"><img src="images/icon_file.gif"/></td>
      <td class="sventonCol3"><a href="<c:out value="${showFileUrl}&revision=${revision}"/>"><c:out value="${entry.name}"/></a></td>
      <% } %>
      <td class="sventonCol4"><%=entrySize%></td>
      <td class="sventonCol5"><c:out value="${entry.revision}" /></td>
      <td class="sventonCol6"><c:out value="${entry.author}" /></td>
      <td class="sventonCol7"><c:out value="${entry.date}" /></td>
      <td class="sventonCol8"><a href="<c:out value="${showLogUrl}&revision=${revision}"/>">[Show log]</a></td>
    </tr>
    <% rowCount++; %>
	</c:forEach>

  <tr>
    <td colspan="8">[Toggle]</td>
  </tr>
</table>
<br>
</body>
</html>
