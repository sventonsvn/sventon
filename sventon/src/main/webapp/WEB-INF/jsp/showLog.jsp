<%
/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
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

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <title>Logs View - ${command.target}</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="Log Messages" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
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

  <form:form action="${pageContext.request.contextPath}/repos/${command.name}/diff${command.path}${entry.name}" method="get" name="logForm" onsubmit="return doDiff(this);">

    <!-- Needed by ASVNTC -->
    <input type="hidden" name="revision" value="${command.revision}">

    <c:set var="command" value="${command}"/>
    <jsp:useBean id="command" type="org.sventon.web.command.SVNBaseCommand" />

    <table class="sventonLogEntriesTable">
      <c:set var="rowCount" value="0"/>
      <tr>
        <c:if test="${isFile}">
          <th style="width: 10px">&nbsp;</th>
          <th style="width: 10px">&nbsp;</th>
        </c:if>
        <th><spring:message code="revision"/></th>
        <th><spring:message code="message"/></th>
        <th>&nbsp;</th>
        <th><spring:message code="author"/></th>
        <th><spring:message code="date"/></th>
      </tr>

      <c:set var="nextPath" value=""/>
      <c:set var="nextRev" value=""/>

      <% pageContext.setAttribute("br", "\n"); %>
      <c:forEach items="${logEntriesPage}" var="entry">
        <c:set var="nextPath" value="${entry.pathAtRevision}"/>
        <c:set var="nextRev" value="${entry.revision}"/>

        <jsp:useBean id="entry" type="org.sventon.model.LogEntryWrapper" />

        <c:url value="/ajax/${command.name}/entrytray${entry.pathAtRevision}" var="entryTrayAddUrl">
          <c:param name="pegrev" value="${entry.revision}" />
          <c:param name="action" value="add" />
        </c:url>

        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <c:choose>
            <c:when test="${isFile}">
              <c:url value="/repos/${command.name}/view${entry.pathAtRevision}" var="showUrl">
                <c:param name="revision" value="${entry.revision}" />
              </c:url>
              <td>
                <form:checkbox path="entries" value="${entry.pathAtRevision};;${entry.revision}" onclick="verifyCheckBox(this)"/>
              </td>
              <td class="sventonCol2">
                <div id="${entryTrayAddUrl}" class="entry">
                  <img src="images/icon_file.png" alt="file">
                </div>
              </td>
              <td><a href="${showUrl}">${entry.revision}</a></td>
            </c:when>
            <c:otherwise>
              <c:url value="/repos/${command.name}/revinfo" var="showRevInfoUrl">
                <c:param name="revision" value="${entry.revision}" />
              </c:url>
              <td><a href="${showRevInfoUrl}">${entry.revision}</a></td>
            </c:otherwise>
          </c:choose>
          <td><a href="#" onclick="Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', '<spring:message code="less.link"/>', '<spring:message code="more.link"/>'); return false;">${fn:replace(fn:escapeXml(entry.message), br, '<br>')}</a></td>
          <td><a href="#" onclick="Element.toggle('logInfoEntry${rowCount}'); toggleInnerHTML('hdr${rowCount}', '<spring:message code="less.link"/>', '<spring:message code="more.link"/>'); return false;"><span id="hdr${rowCount}"><spring:message code="more.link"/></span></a></td>
          <td>${entry.author}</td>
          <td nowrap>
            <span onmouseover="Tip('<sventon-ui:age date="${entry.date}"/>');">
              <fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>
            </span>
          </td>
        </tr>
        <tr id="logInfoEntry${rowCount}" style="display:none" class="sventonEntryLogInfo">
          <c:if test="${isFile}">
            <td colspan="2">&nbsp;</td>
          </c:if>
          <td valign="top"><b>Changed<br>paths</b></td><td colspan="4">
            <%=HTMLCreator.createChangedPathsTable(entry.getChangedPaths(), entry.getRevision(),
                entry.getPathAtRevision(), "", command.getName(), false, false, response)%>
          </td>
        </tr>
        <c:set var="rowCount" value="${rowCount + 1}"/>
      </c:forEach>
      <c:url value="/repos/${command.name}/showlog${command.path}" var="showNextLogUrl">
        <c:param name="nextPath" value="${nextPath}" />
        <c:param name="nextRevision" value="${nextRev}" />
        <c:param name="revision" value="${command.revision}"/>
        <c:param name="stopOnCopy" value="${stopOnCopy}"/>
      </c:url>

      <c:choose>
        <c:when test="${morePages}">
          <tr>
            <td colspan="5" align="center">
              <a href="${showNextLogUrl}"><spring:message code="next"/> ${pageSize}</a>&nbsp;
            </td>
          </tr>
        </c:when>
      </c:choose>
      <tr>
        <td colspan="2">
          <c:if test="${isFile}"><input type="submit" class="btn" value="<spring:message code='diff.show'/>"></c:if>
        </td>
        <td colspan="3">&nbsp;</td>
      </tr>
    </table>
  </form:form>

  <c:if test="${isEntryTrayEnabled}">
    <%@ include file="/WEB-INF/jspf/entryTray.jspf"%>
  </c:if>
  
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
