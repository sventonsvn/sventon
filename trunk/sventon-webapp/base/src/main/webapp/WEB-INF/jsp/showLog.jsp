<%
/*
 * ====================================================================
 * Copyright (c) 2005-2011 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>
<%@ page import="org.sventon.util.HTMLCreator" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page import="org.sventon.util.WebUtils" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <%@ include file="/WEB-INF/jspf/pageHeadRssLink.jspf"%>
  <title>Logs View - ${command.target}</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="log.messages" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.encodedPath}');">
  <table class="sventonFunctionLinksTable">
    <tr>
      <td style="white-space: nowrap;">
        <sventon:logsFunctionButtons command="${command}" isFile="${isFile}"/>
      </td>
      <td style="text-align: right;">
        <c:if test="${useCache}">
          <sventon:searchField command="${command}" isUpdating="${isUpdating}" isHead="${isHead}" searchMode="${userRepositoryContext.searchMode}"/>
        </c:if>
      </td>
    </tr>
  </table>
    <!-- Needed by ASVNTC -->
    <input type="hidden" name="revision" value="${command.revision}">
  </form>

  <form:form action="${pageContext.request.contextPath}/repos/${command.name}/diff${command.encodedPath}${entry.name}" method="get" name="logForm" onsubmit="return doDiff(this);">

    <!-- Needed by ASVNTC -->
    <input type="hidden" name="revision" value="${command.revision}">

    <c:set var="command" value="${command}"/>
    <jsp:useBean id="command" type="org.sventon.web.command.BaseCommand" />

    <table class="sventonLogEntriesTable">
      <c:set var="rowCount" value="0"/>
      <tr>
        <th style="width: 10px; height: 22px;">&nbsp;</th>
        <th style="width: 10px;">&nbsp;</th>
        <th><spring:message code="date"/></th>
        <th><spring:message code="author"/></th>
        <th><spring:message code="revision"/></th>
        <th><spring:message code="message"/></th>
        <th>&nbsp;</th>
      </tr>

      <c:set var="nextPath" value=""/>
      <c:set var="nextRev" value=""/>

      <c:forEach items="${logEntriesPage}" var="entry">
        <c:set var="nextPath" value="${entry.pathAtRevision}"/>
        <c:set var="nextRev" value="${entry.revision}"/>

        <jsp:useBean id="entry" type="org.sventon.model.LogEntry" />

        <s:url value="/ajax/${command.name}/entrytray${entry.pathAtRevision}" var="entryTrayAddUrl">
          <s:param name="pegRevision" value="${entry.revision}" />
          <s:param name="action" value="add" />
        </s:url>

        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <c:choose>
            <c:when test="${isFile}">
              <td>
                <input type="checkbox" name="entries" value="${entry.pathAtRevision}@${entry.revision}" onclick="verifyCheckBox(this)"/>
              </td>
              <td class="sventonCol2">
                <div id="${entryTrayAddUrl}" class="entry">
                  <img src="images/icon_file.png" alt="file">
                </div>
              </td>
            </c:when>
            <c:otherwise>
              <td colspan="2" style="width: 10px; height: 22px;">&nbsp;</td>
            </c:otherwise>
          </c:choose>
          <td nowrap>
            <span onmouseover="Tip('<s:age date="${entry.date}"/>');">
              <fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>
            </span>
          </td>
          <td>${entry.author}</td>
          <c:choose>
            <c:when test="${isFile}">
              <s:url value="/repos/${command.name}/show${entry.pathAtRevision}" var="showUrl">
                <s:param name="revision" value="${entry.revision}" />
              </s:url>
              <td><a href="${showUrl}">${entry.revision}</a></td>
            </c:when>
            <c:otherwise>
              <s:url value="/repos/${command.name}/info" var="showRevInfoUrl">
                <s:param name="revision" value="${entry.revision}" />
              </s:url>
              <td><a href="${showRevInfoUrl}">${entry.revision}</a></td>
            </c:otherwise>
          </c:choose>
          <td><a href="#" onclick="Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', '<spring:message code="less.link"/>', '<spring:message code="more.link"/>'); return false;"><%=WebUtils.nl2br(StringEscapeUtils.escapeXml(entry.getMessage()))%></a></td>
          <td><a href="#" onclick="Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', '<spring:message code="less.link"/>', '<spring:message code="more.link"/>'); return false;"><span id="hdr${rowCount}"><spring:message code="more.link"/></span></a></td>
        </tr>
        <tr id="logInfoEntry${rowCount}" style="display:none" class="sventonEntryLogInfo">
          <td colspan="2">&nbsp;</td>
          <td colspan="5">
            <%=HTMLCreator.createChangedPathsTable(entry.getChangedPaths(), entry.getRevision(),
                entry.getPathAtRevision(), "", command.getName(), false, false, response)%>
          </td>
        </tr>
        <c:set var="rowCount" value="${rowCount + 1}"/>
      </c:forEach>
      <s:url value="/repos/${command.name}/log${command.path}" var="showNextLogUrl">
        <s:param name="nextPath" value="${nextPath}" />
        <s:param name="nextRevision" value="${nextRev}" />
        <s:param name="revision" value="${command.revision}"/>
        <s:param name="stopOnCopy" value="${stopOnCopy}"/>
      </s:url>

      <c:choose>
        <c:when test="${morePages}">
          <tr>
            <td colspan="7" align="center">
              <b><a href="${showNextLogUrl}"><spring:message code="next"/> ${pageSize}</a></b>
            </td>
          </tr>
        </c:when>
      </c:choose>
      <tr>
        <td colspan="2">
          <c:if test="${isFile}"><input type="submit" class="btn" value="<spring:message code='diff.show'/>"></c:if>
        </td>
        <td colspan="5">&nbsp;</td>
      </tr>
    </table>
  </form:form>

  <c:if test="${isEntryTrayEnabled}">
    <%@ include file="/WEB-INF/jspf/entryTray.jspf"%>
  </c:if>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
