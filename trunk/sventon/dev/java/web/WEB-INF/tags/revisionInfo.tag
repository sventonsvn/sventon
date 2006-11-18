<%
/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ tag import="java.util.*" %>
<%@ tag import="org.tmatesoft.svn.core.SVNLogEntry" %>
<%@ tag import="org.tmatesoft.svn.core.SVNLogEntryPath" %>
<%@ tag import="de.berlios.sventon.web.model.LogEntryActionType"%>

<%@ attribute name="details" required="true" type="org.tmatesoft.svn.core.SVNLogEntry" %>
<%@ attribute name="keepVisible" required="true" type="java.lang.Boolean" %>
<%@ attribute name="linkToHead" required="true" type="java.lang.Boolean" %>

<% application.setAttribute("br", "\n"); %>
<table class="sventonLatestCommitInfoTable" border="0">
  <tr><td><b>Revision:</b></td><td>${details.revision}</td></tr>
  <tr><td><b>Date:</b></td><td><fmt:formatDate type="both" value="${details.date}" dateStyle="short" timeStyle="short"/></td></tr>
  <tr><td><b>User:</b></td><td>${details.author}</td></tr>
  <tr><td valign="top"><b>Message:</b></td><td>${fn:replace(details.message, br, '<br/>')}</td></tr>
  <tr><td colspan="2" valign="top"><b>Changed paths:</b></td></tr>
  <c:set var="latestChangedPaths" value="${details.changedPaths}" />
  <jsp:useBean id="latestChangedPaths" type="java.util.Map" />
  <c:set var="details" value="${details}" />
  <c:set var="keepVisible" value="${keepVisible}" />
  <jsp:useBean id="details" type="org.tmatesoft.svn.core.SVNLogEntry" />

  <tr><td colspan="2">
      <table border="0">
        <tr>
          <th>Action</th>
          <th>Path</th>
        </tr>
      <%
        final List latestPathsList = new ArrayList(latestChangedPaths.keySet());
        Collections.sort(latestPathsList);
        final Iterator latestLogIterator = latestPathsList.iterator();
        while (latestLogIterator.hasNext()) {
          final SVNLogEntryPath logEntryPath = (SVNLogEntryPath) latestChangedPaths.get(latestLogIterator.next());
          final LogEntryActionType actionType = LogEntryActionType.parse(logEntryPath.getType());
      %>
      <tr>
        <c:url value="goto.svn" var="goToUrl">
          <c:param name="path" value="<%= logEntryPath.getPath() %>" />
          <c:param name="revision" value="${linkToHead ? 'head' : details.revision}" />
          <c:param name="name" value="${command.name}" />
          <c:if test="${keepVisible}">
            <c:param name="showlatestinfo" value="true" />
          </c:if>
        </c:url>

        <td valign="top"><i><%= actionType %></i></td>
        <% if (LogEntryActionType.ADDED == actionType || LogEntryActionType.REPLACED == actionType) { %>
        <td><a href="${goToUrl}" title="Show file"><%= logEntryPath.getPath() %></a>
        <% } else if (LogEntryActionType.MODIFIED == actionType) { %>

        <%
          String entry1 = logEntryPath.getPath() + ";;" + details.getRevision();
          String entry2 = logEntryPath.getPath() + ";;" + (details.getRevision() - 1);
        %>

          <c:url value="diff.svn" var="diffUrl">
            <c:param name="path" value="<%= logEntryPath.getPath() %>" />
            <c:param name="revision" value="${linkToHead ? 'head' : details.revision}" />
            <c:param name="name" value="${command.name}" />
            <c:param name="entry" value="<%= entry1 %>"/>
            <c:param name="entry" value="<%= entry2 %>"/>
            <c:if test="${keepVisible}">
              <c:param name="showlatestinfo" value="true" />
            </c:if>
          </c:url>



        <td><a href="${diffUrl}" title="Diff with previous version"><%= logEntryPath.getPath() %></a>
        <% } else if (LogEntryActionType.DELETED == actionType) { %>
        <td><strike><%= logEntryPath.getPath() %></strike>
        <% } %>
        <% if (logEntryPath.getCopyPath() != null) { %>
          <br/><b>Copy from</b> <%=logEntryPath.getCopyPath()%> @ <%=Long.toString(logEntryPath.getCopyRevision())%>
        <% } %>
        </td>
      </tr>
      <%
        }
      %>
      </table>
    </td>
  </tr>
</table>
