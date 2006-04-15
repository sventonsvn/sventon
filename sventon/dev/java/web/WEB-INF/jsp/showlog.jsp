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
<%@ include file="/WEB-INF/jspf/include.jspf"%>
<%@ page import="de.berlios.sventon.ctrl.LogEntryActionType"%>
<%@ page import="org.tmatesoft.svn.core.SVNLogEntryPath"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/head.jspf"%>
    <title>Logs view - ${command.target}</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/top.jspf"%>

    <p>
      <table class="sventonHeader">
        <tr>
          <td>Log Messages - <b>${command.target}</b>&nbsp;</td>
        </tr>
      </table>
    </p>

    <br/>
    <ui:functionLinks pageName="showLog"/>

    <form action="diff.svn" method="post" name="logForm" onsubmit="return doDiff(logForm);">

      <!-- Needed by ASVNTC -->
      <input type="hidden" name="path" value="${command.path}${entry.name}"/>
      <input type="hidden" name="revision" value="${command.revision}"/>

      <c:url value="diff.svn" var="diffUrl">
        <c:if test="${isFile}">
          <c:param name="path" value="${command.path}" />
        </c:if>
        <c:param name="revision" value="${command.revision}" />
      </c:url>

      <table class="sventonLogEntriesTable">
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
        <% int rowCount = 0; %>
        <c:set var="nextPath" value=""/>
        <c:set var="nextRev" value=""/>

        <% pageContext.setAttribute("br", "\n"); %>        
        <c:forEach items="${logEntriesPage}" var="entry">
          <c:url value="showfile.svn" var="showUrl">
            <c:param name="path" value="${entry.pathAtRevision}" />
            <c:param name="revision" value="${entry.svnLogEntry.revision}" />
          </c:url>
          <c:set var="nextPath" value="${entry.pathAtRevision}"/>
          <c:set var="nextRev" value="${entry.svnLogEntry.revision}"/>

          <jsp:useBean id="entry" type="de.berlios.sventon.ctrl.LogEntryBundle" />

          <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry2"); else out.print("sventonEntry1");%>">
            <c:choose>
              <c:when test="${isFile}">
                <td><input type="checkbox" name="entry" value="${entry.pathAtRevision};;${entry.svnLogEntry.revision}" onClick="javascript:verifyCheckBox(this)" /></td>
                <td><a href="${showUrl}">${entry.svnLogEntry.revision}</a></td>
              </c:when>
              <c:otherwise>
                <td>${entry.svnLogEntry.revision}</td>
              </c:otherwise>
            </c:choose>
            <td><a href="#" onclick="toggleElementVisibility('logInfoEntry<%=rowCount%>'); changeLessMoreDisplay('hdr<%=rowCount%>');">${fn:replace(entry.svnLogEntry.message, br, '<br/>')}</a></td>
            <td><a href="#" onclick="toggleElementVisibility('logInfoEntry<%=rowCount%>'); changeLessMoreDisplay('hdr<%=rowCount%>');">[<span id="hdr<%=rowCount%>">more</span>]</a></td>
            <td>${entry.svnLogEntry.author}</td>
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
                  final Set paths = changedPaths.keySet();
                  final List pathsList = new java.util.ArrayList(paths);
                  Collections.sort(pathsList);
                  final Iterator i = pathsList.iterator();
                  while (i.hasNext()) {
                    final SVNLogEntryPath logEntryPath =
                      (SVNLogEntryPath)changedPaths.get(i.next());
                    final LogEntryActionType actionType = LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()));
                %>
                <tr>

                  <c:url value="goto.svn" var="goToUrl">
                    <c:param name="path" value="<%= logEntryPath.getPath() %>" />
                    <c:param name="revision" value="${entry.svnLogEntry.revision}" />
                  </c:url>

                  <td><i><%= actionType %></i></td>
                  <% if (LogEntryActionType.A == actionType || LogEntryActionType.R == actionType) { %>
                  <td><a href="${goToUrl}" title="Show file"><%= logEntryPath.getPath().startsWith(command.getPath()) ? "<i>" + logEntryPath.getPath() + "</i>" : logEntryPath.getPath() %></a></td>
                  <% } else if (LogEntryActionType.M == actionType) { %>
                  <td><a href="${diffUrl}&path=<%= logEntryPath.getPath() %>&entry=<%= logEntryPath.getPath() %>;;${entry.svnLogEntry.revision}&entry=<%= logEntryPath.getPath() %>;;<%= entry.getSvnLogEntry().getRevision()-1 %>" title="Diff with previous version"><%= logEntryPath.getPath().startsWith(command.getPath()) ? "<i>" + logEntryPath.getPath() + "</i>" : logEntryPath.getPath() %></a></td>
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
          <c:param name="path" value="${command.path}"/>
          <c:param name="rev" value="${command.revision}"/>
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
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
  </body>
</html>
