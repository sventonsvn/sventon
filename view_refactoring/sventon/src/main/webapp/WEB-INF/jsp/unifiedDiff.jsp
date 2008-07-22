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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf" %>
  <title>Diff View</title>
</head>

<body>
  <script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/js/wz_tooltip.js"></script>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>
  <sventon:topHeaderTable command="${command}" repositoryNames="${repositoryNames}"/>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf" %>

  <sventon:currentTargetHeader title="Unified Diff View" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
  <table class="sventonFunctionLinksTable">
    <tr>
      <td style="white-space: nowrap;">
        <sventon:diffFunctionButtons command="${command}" diffCommand="${diffCommand}"/>
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

  <c:choose>
    <c:when test="${isIdentical}">
      <p><b><spring:message code="diff.error.identical.files" arguments="${diffCommand.fromTarget},${diffCommand.fromRevision},${diffCommand.toTarget},${diffCommand.toRevision}"/></b></p>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${!isBinary}">
          <table id="diffTable" class="sventonUnifiedDiffTable" cellspacing="0">
            <tr>
              <th width="100%" style="background-color: white;">${diffCommand.fromPath} @ revision ${diffCommand.fromRevision}</th>
            </tr>
            <tr>
              <th width="100%" style="background-color: white; border-bottom: 1px solid black">${diffCommand.toPath} @ revision ${diffCommand.toRevision}</th>
            </tr>
            <tr>
              <td><c:out value="${diffResult}" escapeXml="false"/></td>
            </tr>
          </table>
        </c:when>
        <c:otherwise>
          <p><b><spring:message code="diff.error.binary"/></b></p>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
