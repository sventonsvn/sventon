<%
/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
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
  <title>Show File - ${command.target}</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jhighlight.css" >
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <c:choose>
    <c:when test="${archivedEntry ne null}">
      <sventon:currentTargetHeader title="show.file" target="${command.target} (${archivedEntry})" properties="${properties}"/>
    </c:when>
    <c:otherwise>
      <sventon:currentTargetHeader title="show.file" target="${command.target}" properties="${properties}"/>
    </c:otherwise>
  </c:choose>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
  <table class="sventonFunctionLinksTable">
    <tr>
      <td style="white-space: nowrap;">
        <sventon:textFileFunctionButtons command="${command}" isArchivedEntry="${archivedEntry ne null}"/>
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

  <div id="fileHistoryContainerDiv" class="fileHistoryContainer">
    <img src="images/spinner.gif" alt="spinner" style="border: 1px solid">
  </div>

  <table id="textFileTable" class="codeBlock" cellspacing="0">
    <c:forEach items="${file.rows}" var="row">
      <tr id="l${row.rowNumber}">
        <td class="lineNo">${row.rowNumber}&nbsp;</td>
        <td class="lineContent">${row.content}</td>
      </tr>
    </c:forEach>
  </table>

  <script type="text/javascript">
    new Draggable($('fileHistoryContainerDiv'));
    getFileHistory('${command.name}', '${command.path}', '${command.revision}', '${archivedEntry}');
  </script>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
