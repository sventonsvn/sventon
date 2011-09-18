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

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <%@ include file="/WEB-INF/jspf/pageHeadRssLink.jspf"%>
  <title>Show Image File - ${command.target}</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="show.image.file" target="${command.target}" properties="${properties}"/>

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

  <s:url value="/repos/${command.name}/get${command.path}${entry.name}" var="showUrl">
    <s:param name="revision" value="${command.revision}" />
    <s:param name="disp" value="inline" />
  </s:url>

  <s:url value="/repos/${command.name}/get${command.path}${entry.name}" var="getThumbUrl">
    <s:param name="revision" value="${command.revision}" />
    <s:param name="disp" value="thumbnail" />
  </s:url>

  <p>
    <a href="${showUrl}">
      <img src="${getThumbUrl}" alt="Thumbnail" style="border: 1px dashed black;">
    </a>
  </p>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
