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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ tag import="java.util.*" %>
<%@ tag import="org.tmatesoft.svn.core.SVNLogEntry" %>
<%@ tag import="org.tmatesoft.svn.core.SVNLogEntryPath" %>
<%@ tag import="de.berlios.sventon.svnsupport.LogEntryActionType" %>

<%@ attribute name="details" required="true" type="org.tmatesoft.svn.core.SVNLogEntry" %>
<%@ attribute name="keepVisible" required="true" type="java.lang.Boolean" %>

<table class="sventonLatestCommitInfoTable">
  <tr><td><b>User:</b></td><td>${details.author}</td></tr>
  <tr><td><b>Date:</b></td><td><fmt:formatDate type="both" value="${details.date}" dateStyle="short" timeStyle="short"/></td></tr>
  <tr><td><b>Message:</b></td><td>${details.message}</td></tr>
  <tr><td><b>Revision:</b></td><td>${details.revision}</td></tr>
  <tr><td colspan="2" valign="top"><b>Changed paths:</b></td></tr>
  <c:set var="latestChangedPaths" value="${details.changedPaths}" />
  <jsp:useBean id="latestChangedPaths" type="java.util.Map" />
  <c:set var="details" value="${details}" />
  <c:set var="keepVisible" value="${keepVisible}" />
  <jsp:useBean id="details" type="SVNLogEntry" />

  <tr><td colspan="2">
      <table border="0">
        <tr>
          <th>Action</th>
          <th>Path</th>
          <th>Copy From Path</th>
          <th>Revision</th>
        </tr>
      <%
        List latestPathsList = new ArrayList(latestChangedPaths.keySet());
        Collections.sort(latestPathsList);
        Iterator latestLogIterator = latestPathsList.iterator();
        while (latestLogIterator.hasNext()) {
          SVNLogEntryPath logEntryPath = (SVNLogEntryPath) latestChangedPaths.get(latestLogIterator.next());
          LogEntryActionType actionType = LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()));
      %>
      <tr>
        <c:url value="goto.svn" var="goToUrl">
          <c:param name="path" value="<%= logEntryPath.getPath() %>" />
          <c:param name="revision" value="${details.revision}" />
          <c:if test="${keepVisible}">
            <c:param name="showlatestinfo" value="true" />
          </c:if>
        </c:url>

        <c:url value="diff.svn" var="diffUrl">
          <c:param name="path" value="<%= logEntryPath.getPath() %>" />
          <c:param name="revision" value="head" />
          <c:if test="${keepVisible}">
            <c:param name="showlatestinfo" value="true" />
          </c:if>
        </c:url>

        <td><i><%= actionType %></i></td>
        <% if (LogEntryActionType.A == actionType || LogEntryActionType.R == actionType) { %>
        <td><a href="${goToUrl}" title="Show file"><%= logEntryPath.getPath() %></a></td>
        <% } else if (LogEntryActionType.M == actionType) { %>
        <td><a href="${diffUrl}&rev=<%= logEntryPath.getPath() %>;;<%= details.getRevision() %>&rev=<%= logEntryPath.getPath() %>;;<%= details.getRevision() - 1 %>" title="Diff with previous version"><%= logEntryPath.getPath() %></a></td>
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
</table>
