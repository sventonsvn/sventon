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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>
<%@ page import="org.sventon.model.DirEntryChangeType" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf" %>
  <%@ include file="/WEB-INF/jspf/pageHeadRssLink.jspf"%>
  <title>Diff View</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="path.diff.view" target="${command.target}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td style="white-space: nowrap;">
          <sventon:pathDiffFunctionButtons command="${command}"/>
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
      <p><b><spring:message code="diff.error.identical.dirs"/></b></p>
    </c:when>
    <c:otherwise>
      <table id="diffTable" cellspacing="0" style="width:100%">
        <tr>
          <th width="100%" style="background-color: white;">${command.fromPath} @ revision ${command.fromRevision}</th>
        </tr>
        <tr>
          <th width="100%" style="background-color: white;">${command.toPath} @ revision ${command.toRevision}</th>
        </tr>
        <tr>
          <td>
            <table class="sventonPathDiffTable">
              <tr>
                <th align="left"><spring:message code="action"/></th>
                <th align="left"><spring:message code="path"/></th>
                <th align="left"><spring:message code="properties.changed"/></th>
              </tr>
              <c:set var="diffResult" value="${diffResult}" />
              <jsp:useBean id="diffResult" type="java.util.ArrayList" />

              <c:forEach items="${diffResult}" var="row">
                <jsp:useBean id="row" type="org.sventon.model.DiffStatus"/>
                <%
                  final DirEntryChangeType changeType;
                  final char actionCode = row.getModificationType().getCode();
                  if (actionCode == ' ') {
                    changeType = null;
                  } else {
                    changeType = DirEntryChangeType.parse(actionCode);
                  }
                %>
                <tr>
                  <c:url value="/repos/${command.name}/goto${command.toPath}/${row.path}" var="goToUrl"/>

                  <c:url value="/repos/${command.name}/diff${command.toPath}/${row.path}" var="diffUrl">
                    <c:param name="revision" value="${command.toRevision}"/>
                  </c:url>

                  <td valign="top"><i><%= changeType == null ? "" : changeType.toString() %></i></td>
                  <% if (DirEntryChangeType.ADDED == changeType || DirEntryChangeType.REPLACED == changeType) { %>
                  <td><a href="${goToUrl}?revision=${command.toRevision}" title="Show file">${row.path}</a></td>
                  <% } else if (DirEntryChangeType.DELETED == changeType) { %>
                  <td><a href="${goToUrl}?revision=${command.fromRevision}" title="Show file">${row.path}</a></td>
                  <% } else if (DirEntryChangeType.MODIFIED == changeType) { %>
                  <td><a href="${diffUrl}&entries=${command.fromPath}/${row.path}@${command.fromRevision}&entries=${command.toPath}/${row.path}@${command.toRevision}" title="Show Diff">${row.path}</a></td>
                  <% } else { %>
                  <td>${row.path}</td>
                  <% } %>
                  <td><%= BooleanUtils.toStringYesNo(row.isPropertyModified()) %></td>
                </tr>
              </c:forEach>
              <tr>
                <td align="right"><b><spring:message code="total"/>:</b></td>
                <td colspan="2"><b>${fn:length(diffResult)} <spring:message code="entries"/></b></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </c:otherwise>
  </c:choose>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
