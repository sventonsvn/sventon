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
<%@ page import="org.sventon.model.LogEntryActionType" %>
<%@ page import="org.apache.commons.lang.BooleanUtils" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf" %>
  <title>Diff View</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="Path Diff View" target="${command.target}" properties="${properties}"/>

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
      <table id="diffTable" cellspacing="0">
        <tr>
          <th width="100%" style="background-color: white;">${diffCommand.fromPath} @ revision ${diffCommand.fromRevision}</th>
        </tr>
        <tr>
          <th width="100%" style="background-color: white;">${diffCommand.toPath} @ revision ${diffCommand.toRevision}</th>
        </tr>
        <tr>
          <td>
            <table class="sventonPathDiffTable">
              <tr>
                <th align="left">Action</th>
                <th align="left">Path</th>
                <th align="left">Changed Properties</th>
              </tr>
              <c:set var="diffResult" value="${diffResult}" />
              <jsp:useBean id="diffResult" type="java.util.ArrayList" />

              <c:forEach items="${diffResult}" var="row">
                <jsp:useBean id="row" type="org.tmatesoft.svn.core.wc.SVNDiffStatus"/>
                <%
                  final LogEntryActionType actionType;
                  final char actionCode = row.getModificationType().getCode();
                  if (actionCode == ' ') {
                    actionType = null;
                  } else {
                    actionType = LogEntryActionType.parse(actionCode);
                  }
                %>
                <tr>
                  <c:url value="/repos/${command.name}/goto${diffCommand.toPath}/${row.path}" var="goToUrl"/>

                  <c:url value="/repos/${command.name}/diff${diffCommand.toPath}/${row.path}" var="diffUrl">
                    <c:param name="revision" value="${diffCommand.toRevision}"/>
                  </c:url>

                  <td valign="top"><i><%= actionType == null ? "" : actionType %></i></td>
                  <% if (LogEntryActionType.ADDED == actionType || LogEntryActionType.REPLACED == actionType) { %>
                  <td><a href="${goToUrl}?revision=${diffCommand.toRevision}" title="Show file">${row.path}</a></td>
                  <% } else if (LogEntryActionType.DELETED == actionType) { %>
                  <td><a href="${goToUrl}?revision=${diffCommand.fromRevision}" title="Show file">${row.path}</a></td>
                  <% } else if (LogEntryActionType.MODIFIED == actionType) { %>
                  <td><a href="${diffUrl}&entry=${diffCommand.fromPath}/${row.path};;${diffCommand.fromRevision}&entry=${diffCommand.toPath}/${row.path};;${diffCommand.toRevision}" title="Show Diff">${row.path}</a></td>
                  <% } else { %>
                  <td>${row.path}</td>
                  <% } %>
                  <td><%= BooleanUtils.toStringYesNo(row.isPropertiesModified()) %></td>
                </tr>
              </c:forEach>
              <tr>
                <td align="right"><b>Total:</b></td>
                <td colspan="2"><b>${fn:length(diffResult)} entries</b></td>
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
