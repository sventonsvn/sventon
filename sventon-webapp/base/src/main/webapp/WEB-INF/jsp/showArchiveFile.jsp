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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf" %>
  <%@ include file="/WEB-INF/jspf/pageHeadRssLink.jspf"%>
  <title>Show Archive File - ${command.target}</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="show.archive.file" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.encodedPath}');">
  <table class="sventonFunctionLinksTable">
    <tr>
      <td style="white-space: nowrap;">
        <sventon:fileFunctionButtons command="${command}" archivedEntry="${archivedEntry}"/>
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
    <c:set var="totalOrigSize" value="0"/>
    <c:set var="totalCompSize" value="0"/>
    <tr>
      <th/>
      <th><spring:message code="name"/></th>
      <th><spring:message code="size.original"/></th>
      <th><spring:message code="size.compressed"/></th>
      <th><spring:message code="date"/></th>
      <th><spring:message code="crc"/></th>
    </tr>
    <c:forEach items="${entries}" var="zipEntry">

      <s:url value="/repos/${command.name}/show${command.path}" var="showFileUrl">
        <s:param name="revision" value="${command.revision}" />
        <s:param name="archivedEntry" value="${zipEntry.name}" />
      </s:url>

      <jsp:useBean id="entryDate" class="java.util.Date" />
      <c:set target="${entryDate}" property="time" value="${zipEntry.time}"/>

      <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
        <c:choose>
          <c:when test="${zipEntry.directory}">
            <td><img src="images/icon_folder.png" alt="dir"></td>
            <td>${zipEntry.name}</td>
          </c:when>
          <c:otherwise>
            <td><s:fileTypeIcon filename="${entry.name}"/></td>
            <td><a href="${showFileUrl}">${zipEntry.name}</a></td>
          </c:otherwise>
        </c:choose>
        <td class="sventonColRightAlign">${zipEntry.size}</td>
        <td class="sventonColRightAlign">${zipEntry.compressedSize}</td>
        <td class="sventonColNoWrap">
          <span onmouseover="Tip('<s:age date="${entryDate}"/>');">
            <fmt:formatDate type="both" value="${entryDate}" dateStyle="short" timeStyle="short"/>
          </span>
        </td>
        <td>${zipEntry.crc}</td>
      </tr>
      <c:set var="totalOrigSize" value="${totalOrigSize + zipEntry.size}"/>
      <c:set var="totalCompSize" value="${totalCompSize + zipEntry.compressedSize}"/>

      <c:set var="rowCount" value="${rowCount + 1}"/>
    </c:forEach>

    <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
      <td class="sventonColRightAlign"><b><spring:message code="total"/>:</b></td>
      <td><b>${rowCount} <spring:message code="entries"/></b></td>
      <td class="sventonColRightAlign" title="${totalOrigSize} bytes">
        <b><s:formatBytes size="${totalOrigSize}" locale="${pageContext.request.locale}"/></b>
      </td>
      <td class="sventonColRightAlign" title="${totalCompSize} bytes">
        <b><s:formatBytes size="${totalCompSize}" locale="${pageContext.request.locale}"/></b>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
  </table>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf" %>
</body>
</html>
