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
  <title>Blame - ${command.target}</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jhighlight.css" >
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="blame" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td style="white-space: nowrap;">
          <sventon:blameFunctionButtons command="${command}"/>
          <sventon:charsetSelectList charsets="${charsets}" currentCharset="${userRepositoryContext.charset}"/>
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

  <table id="blameTable" class="codeBlock" cellspacing="0">
    <tr>
      <th style="width: 50px;"><spring:message code="revision"/></th>
      <th width="50px"><spring:message code="author"/></th>
      <th width="50px"><spring:message code="line"/></th>
      <th>&nbsp;</th>
    </tr>

    <c:forEach items="${annotatedFile.unmodifiableRows}" var="row">
      <c:url value="/repos/${command.name}/info" var="showRevInfoUrl">
        <c:param name="revision" value="${row.revision}" />
      </c:url>

      <tr id="L${row.rowNumber}">
        <td valign="top" style="background-color: white; text-align:right;" class="blameRev_${row.revision}">
          <a href="${showRevInfoUrl}" onmouseover="highlightBlameRev(${row.revision}); getLogMessage(${row.revision}, '${command.name}', '<fmt:formatDate type="both" value="${row.date}" dateStyle="short" timeStyle="short"/>');" onmouseout="restoreBlameRev(${row.revision});">
            ${row.revision}
          </a>
        </td>
        <td class="blameAuthor">${row.author}</td>
        <td class="lineNo">${row.rowNumber}</td>
        <td class="lineContent">${row.content}</td>
      </tr>
    </c:forEach>
  </table>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
