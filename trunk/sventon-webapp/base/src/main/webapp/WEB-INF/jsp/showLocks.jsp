<%
/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
  <title>Show Locks - ${command.target}</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="show.locks" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.encodedPath}');">
  <table class="sventonFunctionLinksTable">
    <tr>
      <td style="white-space: nowrap;">
        <sventon:locksFunctionButtons command="${command}"/>
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

  <table class="sventonEntriesTable">
    <c:set var="rowCount" value="0"/>
    <tr>
      <th/>
      <th><spring:message code="name"/></th>
      <th><spring:message code="owner"/></th>
      <th><spring:message code="comment"/></th>
      <th><spring:message code="createDate"/></th>
      <th><spring:message code="expireDate"/></th>
    </tr>
    <c:forEach items="${currentLocks}" var="lock">
      <jsp:useBean id="lock" type="org.sventon.model.DirEntryLock" />

      <s:url value="/repos/${command.name}/show${lock.path}" var="showUrl">
        <s:param name="revision" value="${command.revision}" />
      </s:url>

      <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
        <td><s:fileTypeIcon filename="${entry.name}"/></td>
        <td><a href="${showUrl}">${lock.path}</a></td>
        <td>${lock.owner}</td>
        <td>${lock.comment}</td>
        <td>
          <span onmouseover="Tip('<s:age date="${lock.creationDate}"/>');">
            <fmt:formatDate type="both" value="${lock.creationDate}" dateStyle="short" timeStyle="short"/>
          </span>
        </td>
        <td>
          <span onmouseover="Tip('<s:age date="${lock.creationDate}"/>');">
            <fmt:formatDate type="both" value="${lock.creationDate}" dateStyle="short" timeStyle="short"/>
          </span>
        </td>
      </tr>
      <c:set var="rowCount" value="${rowCount + 1}"/>
    </c:forEach>

    <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
      <td>&nbsp;</td>
      <td><b><spring:message code="total"/>: ${rowCount} <spring:message code="entries"/></b></td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
  </table>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
