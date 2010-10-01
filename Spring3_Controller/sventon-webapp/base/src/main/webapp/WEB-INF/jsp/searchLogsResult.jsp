<%
/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <%@ include file="/WEB-INF/jspf/pageHeadRssLink.jspf"%>
  <title><spring:message code="window.title.default"/> - ${repositoryURL}</title>
</head>
<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="search.result" target="${searchString} (directory '${startDir}' and below)" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td style="white-space: nowrap;">
          <sventon:searchResultFunctionButtons command="${command}"/>
          <sventon:flattenButton command="${command}" isHead="${isHead}" isUpdating="${isUpdating}"/>
        </td>
        <td style="text-align: right;">
          <c:if test="${useCache}">
            <sventon:searchField command="${command}" isUpdating="${isUpdating}" isHead="${isHead}"
                                 searchMode="${userRepositoryContext.searchMode}"/>
          </c:if>
        </td>
      </tr>
    </table>
    <!-- Needed by ASVNTC -->
    <input type="hidden" name="revision" value="${command.revision}">
  </form>

  <% pageContext.setAttribute("br", "\n"); %>
  <table class="sventonEntriesTable">
    <c:set var="rowCount" value="0"/>
    <tr>
      <th><spring:message code="date"/></th>
      <th><spring:message code="author"/></th>
      <th><spring:message code="revision"/></th>
      <th><spring:message code="logmessage"/></th>
    </tr>
    <c:forEach items="${logEntries}" var="logEntry">
      <c:url value="/repoz/${command.name}/info" var="showRevInfoUrl">
        <c:param name="revision" value="${logEntry.revision}" />
      </c:url>
      <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
        <td class="sventonColNoWrap">
          <span onmouseover="Tip('<sventon-ui:age date="${logEntry.date}"/>');">
            <fmt:formatDate type="both" value="${logEntry.date}" dateStyle="short" timeStyle="short"/>
          </span>
        </td>
        <td>${logEntry.author}</td>
        <td><a href="${showRevInfoUrl}" onmouseover="Tip('<spring:message code="showrevision.info.link.tooltip"/>')">${logEntry.revision}</a></td>
        <td>${fn:replace(logEntry.message, br, '<br>')}</td>
      </tr>
      <c:set var="rowCount" value="${rowCount + 1}"/>
    </c:forEach>
    <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
      <td><b><spring:message code="total"/>:&nbsp;${rowCount}&nbsp;<spring:message code="hits"/></b></td>
      <td/>
      <td/>
      <td/>
    </tr>
  </table>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
