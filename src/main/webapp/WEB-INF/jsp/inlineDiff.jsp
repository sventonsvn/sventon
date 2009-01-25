<%@ page import="org.sventon.model.SideBySideDiffRow" %>
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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <title>Diff View</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jhighlight.css" >
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="inline.diff.view" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td style="white-space: nowrap;">
          <sventon:diffFunctionButtons command="${command}" pegrev="${pegrev}"/>
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
      <p><b><spring:message code="diff.error.identical.files" arguments="${command.fromTarget},${command.fromRevision},${command.toTarget},${command.toRevision}"/></b></p>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${!isBinary}">
          <table id="diffTable" class="sventonDiffTable" cellspacing="0">
            <tr>
              <th style="background-color: white;">
                <a href="javascript:;" onclick="document.location.hash='#diff0';">
                  <img src="images/icon_nextdiff.png" alt="Next diff" title="Next diff">
                </a>
              </th>
              <th class="lineNo">&nbsp;</th>
              <th class="lineNo">&nbsp;</th>
              <th style="background-color: white;">&nbsp;</th>              
              <th width="100%" style="background-color: white;">${command.fromPath} @ revision ${command.fromRevision}</th>
            </tr>
            <tr>
              <th style="background-color: white;">&nbsp;</th>
              <th class="lineNo">&nbsp;</th>
              <th class="lineNo">&nbsp;</th>
              <th style="background-color: white;">&nbsp;</th>
              <th width="100%" style="background-color: white; border-bottom: 1px solid black">${command.toPath} @ revision ${command.toRevision}</th>
            </tr>
            <c:set var="diffCount" value="0"/>
            <c:forEach items="${diffResult}" var="row">
              <jsp:useBean id="row" type="org.sventon.model.InlineDiffRow"/>
              <tr <%= row.getRowNumberRight() != null ? "id=\"l" + row.getRowNumberRight().toString() + "\"" : "" %>>
                <td style="background-color: white;">
                  <c:if test="${!row.isUnchanged}">
                    <a name="diff${diffCount}"></a>
                    <c:set var="diffCount" value="${diffCount + 1}"/>
                    <a href="javascript:;" onclick="document.location.hash='#diff${diffCount}';">
                      <img src="images/icon_nextdiff.png" alt="Next diff" title="Next diff">
                    </a>
                  </c:if>
                </td>
                <td class="lineNo">
                  <%= row.getRowNumberLeft() != null ? row.getRowNumberLeft().toString() : "" %>
                </td>
                <td class="lineNo">
                  <%= row.getRowNumberRight() != null ? row.getRowNumberRight().toString() : "" %>
                </td>
                <td style="text-align: center; background-color: white;">
                  <b><%= row.getAction().getSymbol() %></b>
                </td>
                <td class="<%= row.getAction().getCSSClass() %>">
                  <span title="<%= row.getAction().getDescription() %>">
                    <%
                      String line = row.getLine();
                      out.print("".equals(line) ? "&nbsp;" : line);
                    %>
                  </span>
                </td>
              </tr>
            </c:forEach>
          </table>
          <a name="diff${diffCount}"/>
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
