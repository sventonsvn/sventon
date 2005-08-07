<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Logs view</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <%@ include file="/WEB-INF/jsp/top.jsp"%>

  <c:url value="blame.svn" var="blameUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <c:url value="get.svn" var="downloadUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <c:url value="showfile.svn" var="showFileUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  <p class="sventonHeader">
  Log Messages - <c:out value="${command.target}"/>
  </p>
  
  <table class="sventonFunctionLinksTable">
    <tr>
      <td><a href="<c:out value="${showFileUrl}&revision=${command.revision}"/>">[Show file]</a></td>
      <td><a href="<c:out value="${downloadUrl}&revision=${command.revision}"/>">[Download]</a></td>
      <td><a href="#">[Diff with previous]</a></td>
      <td><a href="<c:out value="${blameUrl}&revision=${command.revision}"/>">[Blame]</a></td>
    </tr>
  </table>

<table class="sventonLogEntriesTable">
  <tr>
    <th>Revision</th>
    <th>Message</th>
    <th>Author</th>
    <th>Date</th>
  </tr>
  <% int rowCount = 0; %>
  <c:forEach items="${logEntriesPage}" var="entry">
    <c:url value="showfile.svn" var="showUrl">
      <c:param name="path" value="${entry.pathAtRevision}" />
      <c:param name="revision" value="${entry.svnLogEntry.revision}" />
    </c:url>
    <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
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
      <td><a href="#" onclick="toggleLogInfo('logInfoEntry<%=rowCount%>');"><%= message.replace("\n", "<br/>\n") %></a></td>
      <td><c:out value="${entry.svnLogEntry.author}" /></td>
      <td nowrap><c:out value="${entry.svnLogEntry.date}" /></td>
    </tr>
    <tr id="logInfoEntry<%=rowCount%>" style="display:none" class="sventonEntryLogInfo">
    <td valign="top">Changed paths</td><td colspan="3">
    <table>
    <tr>
      <th>Action</th>
      <th>Path</th>
      <th>Copy From Path</th>
      <th>Revision</th>
    </tr>
    <c:set var="changedPaths" value="${entry.svnLogEntry.changedPaths}" />
    <jsp:useBean id="changedPaths" type="java.util.Map" />
    <%
    	java.util.Set paths = changedPaths.keySet();
    	java.util.List pathsList = new java.util.ArrayList(paths);
    	java.util.Collections.sort(pathsList);
    	java.util.Iterator i = pathsList.iterator();
    	while (i.hasNext()) {
    	  org.tmatesoft.svn.core.SVNLogEntryPath logEntryPath = 
    	    (org.tmatesoft.svn.core.SVNLogEntryPath)changedPaths.get(i.next());
    	  
    %>
    <tr>
      <td><%= logEntryPath.getType() %></td>
      <td><%= logEntryPath.getPath() %></td>
      <td><%= logEntryPath.getCopyPath() == null ? "" : logEntryPath.getCopyPath() %></td>
      <td><%= logEntryPath.getCopyPath() == null ? "" : Long.toString(logEntryPath.getCopyRevision()) %></td>
    </tr>
    <%
    	}
    %>
    </table>
    </td>
    </tr>
    <% rowCount++; %>
  </c:forEach>
  <c:set var="count" value="${pageCount}" />
  <c:set var="pageNum" value="${pageNumber}" />
  <jsp:useBean id="count" type="java.lang.Integer" />
  <jsp:useBean id="pageNum" type="java.lang.Integer" />
  <c:url value="showlogpage.svn" var="showlogpageUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
    <c:param name="revision" value="${command.revision}" />
  </c:url>
  <tr>
  <td colspan="4" align="center">
  <% for (int j = 1; j <= count.intValue(); j++) { 
       if (j == pageNum.intValue()) { %>
  	     <%= j %>&nbsp;
  <%   } else { %>
    <c:url value="showlogpage.svn" var="showLogPageUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
      <c:param name="revision" value="${command.revision}" />
      <c:param name="page" value="<%= Integer.toString(j) %>" />
    </c:url>
  	     <a href="<c:out value="${showLogPageUrl}"/>"><%= j %></a>&nbsp;
  <%   }
     } %>
  </td>
  </tr>
</table>

<br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
</body>
</html>
