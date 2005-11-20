<%
/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="de.berlios.sventon.svnsupport.LogEntryActionType"%>

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
  Log Messages - <b><c:out value="${command.target}"/></b>&nbsp;</td></tr></table>
  </p>
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
  <c:set var="nextPath" value=""/>
  <c:set var="nextRev" value=""/>
  
  <c:forEach items="${logEntriesPage}" var="entry">
    <c:url value="showfile.svn" var="showUrl">
      <c:param name="path" value="${entry.pathAtRevision}" />
      <c:param name="revision" value="${entry.svnLogEntry.revision}" />
    </c:url>
    <c:set var="nextPath" value="${entry.pathAtRevision}"/>
    <c:set var="nextRev" value="${entry.svnLogEntry.revision}"/>
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
      <td nowrap><fmt:formatDate type="both" value="${entry.svnLogEntry.date}" dateStyle="short" timeStyle="short"/></td>
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
    	  LogEntryActionType actionType = LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()));
    %>
    <tr>
    <c:url value="goto.svn" var="goToUrl">
      <c:param name="path" value="<%= logEntryPath.getPath() %>" />
      <c:param name="revision" value="${entry.svnLogEntry.revision}" />
    </c:url>
      <td><i><%= actionType.getDescription() %></i></td>
      <% if (LogEntryActionType.D != actionType) { %>
      <td><a href="${goToUrl}"><%= command.getPath().equals(logEntryPath.getPath()) ? "<i>" + logEntryPath.getPath() + "</i>" : logEntryPath.getPath() %></a></td>
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
  <c:url value="showlog.svn" var="showNextLogUrl">
    <c:param name="nextPath" value="${nextPath}" />
    <c:param name="nextRevision" value="${nextRev}" />
    <c:param name="path" value="${command.completePath}"/>
    <c:param name="rev" value="${command.revision}"/>
  </c:url>

  <c:choose>
    <c:when test="${morePages}">
  <tr>
    <td colspan="5" align="center">
  	     <a href="<c:out value="${showNextLogUrl}"/>">Next <c:out value="${pageSize}"/></a>&nbsp;
    </td>
  </tr>
  </c:when>
  </c:choose>
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
