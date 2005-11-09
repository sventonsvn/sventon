<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>Logs view</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <%@ include file="/WEB-INF/jsp/top.jsp"%>

  <c:url value="get.svn" var="downloadUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <c:url value="repobrowser.svn" var="showDirUrl">
    <c:param name="path" value="${command.path}" />
    <c:param name="revision" value="${command.revision}" />
  </c:url>
  
  <c:url value="showfile.svn" var="showFileUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>

  <p>
    <table class="sventonHeader"><tr><td>
  Log Messages - <b><c:out value="${command.target}"/></b>&nbsp;<a href="javascript:toggleElementVisibility('propertiesDiv'); changeHideShowDisplay('propertiesLink');">[<span id="propertiesLink">show</span> properties]</a></td></tr></table>
  </p>
  <%@ include file="/WEB-INF/jsp/sventonheader.jsp"%>

  <br/>
  
  <table class="sventonFunctionLinksTable">
    <tr>
    <c:choose>
    <c:when test="${isFile}">
      <td><a href="<c:out value="${showFileUrl}&revision=${command.revision}"/>">[Show file]</a></td>
      </c:when>
      <c:otherwise>
      <td><a href="<c:out value="${showDirUrl}"/>">[Show directory]</a></td>
      </c:otherwise>
      </c:choose>
      <td><a href="<c:out value="${downloadUrl}&revision=${command.revision}"/>">[Download]</a></td>
    </tr>
  </table>

<form action="diff.svn" method="get" name="logForm" onsubmit="return doDiff(logForm);">

<!-- Needed by ASVNTC -->
<input type="hidden" name="path" value="${command.path}${entry.name}"/>
<input type="hidden" name="revision" value="${command.revision}"/>

<table class="sventonLogEntriesTable">
  <tr>
    <c:choose>
    <c:when test="${isFile}">
    <th style="width: 55px">&nbsp;</th>
    </c:when>
    </c:choose>
    <th>Revision</th>
    <th>Message</th>
    <th>&nbsp;</th>
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
        <td><input type="checkbox" name="rev" value="${entry.pathAtRevision};;${entry.svnLogEntry.revision}" onClick="javascript:verifyCheckBox(this)" /></td>
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
      <td><a href="#" onclick="toggleElementVisibility('logInfoEntry<%=rowCount%>'); changeLessMoreDisplay('hdr<%=rowCount%>');"><%= message.replace("\n", "<br/>\n") %></a></td>
      <td><a href="#" onclick="toggleElementVisibility('logInfoEntry<%=rowCount%>'); changeLessMoreDisplay('hdr<%=rowCount%>');">[<span id="hdr<%=rowCount%>">more</span>]</a></td>
      <td><c:out value="${entry.svnLogEntry.author}" /></td>
      <td nowrap><c:out value="${entry.svnLogEntry.date}" /></td>
    </tr>
    <tr id="logInfoEntry<%=rowCount%>" style="display:none" class="sventonEntryLogInfo">
    <td valign="top">Changed<br>paths</td><td colspan="5">
    <table width="100%">
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
    <c:url value="goto.svn" var="goToUrl">
      <c:param name="path" value="<%= logEntryPath.getPath() %>" />
      <c:param name="revision" value="${entry.svnLogEntry.revision}" />
    </c:url>
      <td><%= logEntryPath.getType() %></td>
      <% if ('D' != logEntryPath.getType()) { %>
      <td><a href="${goToUrl}"><%= logEntryPath.getPath() %></a></td>
      <% } else { %>
      <td><%= logEntryPath.getPath() %></td>
      <% } %>
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
  <td colspan="5" align="center">
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

  <tr>
    <td colspan="2">
    <c:choose>
    <c:when test="${isFile}">
      <input type="submit" name="actionSubmitButton" value="diff"/>
    </c:when>
    </c:choose>
    </td>
    <td colspan="3">&nbsp;</td>
  </tr>
</table>
</form>
<br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
</body>
</html>
