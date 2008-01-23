<%
/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
<%@ page import="de.berlios.sventon.util.HTMLCreator" %>

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
    <input type="hidden" name="path" value="${command.path}">
    <input type="hidden" name="revision" value="${command.revision}">
    <input type="hidden" name="name" value="${command.name}">

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
        <c:set var="nextPath" value="${entry.pathAtRevision}"/>
        <c:set var="nextRev" value="${entry.svnLogEntry.revision}"/>

        <jsp:useBean id="entry" type="de.berlios.sventon.model.LogEntryBundle" />

        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <c:choose>
            <c:when test="${isFile}">
              <c:url value="showfile.svn" var="showUrl">
                <c:param name="path" value="${entry.pathAtRevision}" />
                <c:param name="revision" value="${entry.svnLogEntry.revision}" />
                <c:param name="name" value="${command.name}" />
              </c:url>
              <td><input type="checkbox" name="entry" value="${entry.pathAtRevision};;${entry.svnLogEntry.revision}" onClick="verifyCheckBox(this)"></td>
              <td><a href="<sventon-ui:formatUrl url='${showUrl}'/>">${entry.svnLogEntry.revision}</a></td>
            </c:when>
            <c:otherwise>
              <c:url value="revinfo.svn" var="showRevInfoUrl">
                <c:param name="revision" value="${entry.svnLogEntry.revision}" />
                <c:param name="name" value="${command.name}" />
              </c:url>
              <td><a href="${showRevInfoUrl}">${entry.svnLogEntry.revision}</a></td>
            </c:otherwise>
          </c:choose>
          <td><a href="#" onclick="Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', 'less', 'more'); return false;">${fn:replace(fn:escapeXml(entry.svnLogEntry.message), br, '<br>')}</a></td>
          <td><a href="#" onclick="Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', 'less', 'more'); return false;">[<span id="hdr${rowCount}">more</span>]</a></td>
          <td>${entry.svnLogEntry.author}</td>
          <td nowrap><fmt:formatDate type="both" value="${entry.svnLogEntry.date}" dateStyle="short" timeStyle="short"/></td>
        </tr>
        <tr id="logInfoEntry${rowCount}" style="display:none" class="sventonEntryLogInfo">
          <c:if test="${isFile}">
            <td>&nbsp;</td>
          </c:if>
          <td valign="top"><b>Changed<br>paths</b></td><td colspan="4">
            <%=HTMLCreator.createChangedPathsTable(entry.getSvnLogEntry(), "", command.getName(), false, false, response)%>
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
              <a href="<sventon-ui:formatUrl url='${showNextLogUrl}'/>">Next ${pageSize}</a>&nbsp;
            </td>
          </tr>
        </c:when>
      </c:choose>
      <tr>
        <td colspan="2">
          <c:if test="${isFile}"><input type="submit" class="btn" value="diff"></c:if>
        </td>
        <td colspan="3">&nbsp;</td>
      </tr>
    </table>
  </form>

<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
