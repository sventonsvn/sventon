<%
/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>
<%@ page import="java.util.*"%>
<%@ page import="org.tmatesoft.svn.core.SVNLogEntryPath"%>
<%@ page import="de.berlios.sventon.model.LogEntryActionType" %>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
    <title>Logs View - ${command.target}</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <sventon:currentTargetHeader title="Log Messages" target="${command.target}" hasProperties="false"/>
    <sventon:functionLinks pageName="showLog"/>

    <form action="diff.svn" method="post" name="logForm" onsubmit="return doDiff(logForm);">

      <!-- Needed by ASVNTC -->
      <input type="hidden" name="path" value="${command.path}${entry.name}"/>
      <input type="hidden" name="revision" value="${command.revision}"/>
      <input type="hidden" name="name" value="${command.name}"/>

      <c:set var="command" value="${command}"/>
      <jsp:useBean id="command" type="de.berlios.sventon.web.command.SVNBaseCommand" />

      <table class="sventonLogEntriesTable">
        <c:set var="rowCount" value="0"/>
        <tr>
          <c:if test="${isFile}">
            <th style="width: 55px">&nbsp;</th>
          </c:if>
          <th>Revision</th>
          <th>Message</th>
          <th>&nbsp;</th>
          <th>Author</th>
          <th>Date</th>
        </tr>

        <c:set var="nextPath" value=""/>
        <c:set var="nextRev" value=""/>

        <% pageContext.setAttribute("br", "\n"); %>        
        <c:forEach items="${logEntriesPage}" var="entry">
          <c:url value="showfile.svn" var="showUrl">
            <c:param name="path" value="${entry.pathAtRevision}" />
            <c:param name="revision" value="${entry.svnLogEntry.revision}" />
            <c:param name="name" value="${command.name}" />
          </c:url>
          <c:set var="nextPath" value="${entry.pathAtRevision}"/>
          <c:set var="nextRev" value="${entry.svnLogEntry.revision}"/>

          <jsp:useBean id="entry" type="de.berlios.sventon.model.LogEntryBundle" />

          <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
            <c:choose>
              <c:when test="${isFile}">
                <td><input type="checkbox" name="entry" value="${entry.pathAtRevision};;${entry.svnLogEntry.revision}" onClick="javascript:verifyCheckBox(this)" /></td>
                <td><a href="${showUrl}">${entry.svnLogEntry.revision}</a></td>
              </c:when>
              <c:otherwise>
                <td>${entry.svnLogEntry.revision}</td>
              </c:otherwise>
            </c:choose>
            <td><a href="#" onclick="javascript: Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', 'less', 'more'); return false;">${fn:replace(entry.svnLogEntry.message, br, '<br/>')}</a></td>
            <td><a href="#" onclick="javascript: Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', 'less', 'more'); return false;">[<span id="hdr${rowCount}">more</span>]</a></td>
            <td>${entry.svnLogEntry.author}</td>
            <td nowrap><fmt:formatDate type="both" value="${entry.svnLogEntry.date}" dateStyle="short" timeStyle="short"/></td>
          </tr>
          <tr id="logInfoEntry${rowCount}" style="display:none" class="sventonEntryLogInfo">
            <td valign="top"><b>Changed<br/>paths</b></td><td colspan="5">
              <table width="100%" border="0">
                <tr>
                  <th>Action</th>
                  <th>Path</th>
                </tr>
                <c:set var="changedPaths" value="${entry.svnLogEntry.changedPaths}" />
                <jsp:useBean id="changedPaths" type="java.util.Map" />
                <%
                  final List pathsList = new ArrayList(changedPaths.keySet());
                  Collections.sort(pathsList);
                  final Iterator i = pathsList.iterator();
                  while (i.hasNext()) {
                    final SVNLogEntryPath logEntryPath = (SVNLogEntryPath) changedPaths.get(i.next());
                    final LogEntryActionType actionType = de.berlios.sventon.model.LogEntryActionType.parse(logEntryPath.getType());
                %>
                <tr>

                  <c:url value="goto.svn" var="goToUrl">
                    <c:param name="path" value="<%= logEntryPath.getPath() %>" />
                    <c:param name="revision" value="${entry.svnLogEntry.revision}" />
                    <c:param name="name" value="${command.name}" />
                  </c:url>

                  <c:url value="goto.svn" var="goToCopyUrl">
                    <c:param name="path" value="<%= logEntryPath.getCopyPath() %>" />
                    <c:param name="revision" value="<%= String.valueOf(logEntryPath.getCopyRevision()) %>"/>
                    <c:param name="name" value="${command.name}" />
                  </c:url>

                  <c:url value="revinfo.svn" var="showRevInfoCopyUrl">
                    <c:param name="name" value="${command.name}" />
                  </c:url>

                  <td valign="top" width="1"><i><%= actionType %></i></td>
                  <% if (LogEntryActionType.ADDED == actionType || LogEntryActionType.REPLACED == actionType) { %>
                  <td><a href="${goToUrl}" title="Show"><%= logEntryPath.getPath().startsWith(command.getPath()) ? "<i>" + logEntryPath.getPath() + "</i>" : logEntryPath.getPath() %></a>
                  <% } else if (LogEntryActionType.MODIFIED == actionType) { %>

                  <%
                      String entry1 = logEntryPath.getPath() + ";;" + entry.getSvnLogEntry().getRevision();
                      String entry2 = logEntryPath.getPath() + ";;" + (entry.getSvnLogEntry().getRevision() - 1);
                  %>

                  <c:url value="diff.svn" var="diffUrl">
                    <c:param name="path" value="<%= logEntryPath.getPath() %>" />
                    <c:param name="revision" value="${entry.svnLogEntry.revision}" />
                    <c:param name="name" value="${command.name}" />
                    <c:param name="entry" value="<%= entry1 %>"/>
                    <c:param name="entry" value="<%= entry2 %>"/>
                  </c:url>

                  <td><a href="${diffUrl}" title="Diff with previous version"><%= logEntryPath.getPath().startsWith(command.getPath()) ? "<i>" + logEntryPath.getPath() + "</i>" : logEntryPath.getPath() %></a>
                  <% } else if (LogEntryActionType.DELETED == actionType) { %>
                  <td><strike><%= logEntryPath.getPath() %></strike>
                  <% } %>
                  <% if (logEntryPath.getCopyPath() != null) { %>
                    <br/><b>Copy from</b> <a href="${goToCopyUrl}" title="Show"><%=logEntryPath.getCopyPath()%></a> @ <a href="${showRevInfoCopyUrl}&revision=<%=logEntryPath.getCopyRevision()%>"><%=Long.toString(logEntryPath.getCopyRevision())%></a>
                  <% } %>
                  </td>
                </tr>
              <%
                }
              %>
              </table>
            </td>
          </tr>
          <c:set var="rowCount" value="${rowCount + 1}"/>
        </c:forEach>
        <c:url value="showlog.svn" var="showNextLogUrl">
          <c:param name="nextPath" value="${nextPath}" />
          <c:param name="nextRevision" value="${nextRev}" />
          <c:param name="path" value="${command.path}"/>
          <c:param name="revision" value="${command.revision}"/>
          <c:param name="name" value="${command.name}" />
        </c:url>

        <c:choose>
          <c:when test="${morePages}">
            <tr>
              <td colspan="5" align="center">
                <a href="${showNextLogUrl}">Next ${pageSize}</a>&nbsp;
              </td>
            </tr>
          </c:when>
        </c:choose>
        <tr>
          <td colspan="2">
            <c:if test="${isFile}"><input type="submit" class="btn" value="diff"/></c:if>
          </td>
          <td colspan="3">&nbsp;</td>
        </tr>
      </table>
    </form>
    <br>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
