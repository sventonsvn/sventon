<%@ page import="de.berlios.sventon.model.SideBySideDiffRow" %>
<%
/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
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
  <link rel="stylesheet" type="text/css" href="jhighlight.css" >
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <sventon:currentTargetHeader title="Diff View" target="${command.target}" hasProperties="false"/>
  <sventon:functionLinks pageName="showDiff"/>

  <c:choose>
    <c:when test="${isIdentical}">
      <p><b><spring:message code="diff.error.identical"/></b></p>
    </c:when>
    <c:when test="${noHistory}">
      <p><b><spring:message code="diff.error.no-history"/></b></p>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${!isBinary}">
          <c:set var="diffResult" value="${diffResult}" />
          <jsp:useBean id="diffResult" type="java.util.ArrayList" />

          <table id="diffTable" class="sventonDiffTable" cellspacing="0">
            <tr>
              <th style="background-color: white;">
                <a href="#diff0">
                  <img src="images/icon_nextdiff.png" alt="Next diff" title="Next diff">
                </a>
              </th>
              <th style="background-color: white;">&nbsp;</th>
              <th class="lineNo">&nbsp;</th>
              <th width="50%">${diffCommand.fromTarget} @ revision ${diffCommand.fromRevision}</th>
              <th style="background-color: white;">&nbsp;</th>
              <th class="lineNo">&nbsp;</th>
              <th width="50%">${diffCommand.toTarget} @ revision ${diffCommand.toRevision}</th>
            </tr>
            <c:set var="diffCount" value="0"/>
            <c:forEach items="${diffResult}" var="row">
              <jsp:useBean id="row" type="de.berlios.sventon.model.SideBySideDiffRow"/>
              <tr>
                <td style="background-color: white;">
                  <c:if test="${!row.isUnchanged}">
                    <a name="diff${diffCount}"/>
                    <c:set var="diffCount" value="${diffCount + 1}"/>
                    <a href="#diff${diffCount}">
                      <img src="images/icon_nextdiff.png" alt="Next diff" title="Next diff">
                    </a>
                  </c:if>
                </td>

                <td style="background-color: white;">
                  <b><%= row.getSide(SideBySideDiffRow.Side.LEFT).getAction().getSymbol() %></b>
                </td>
                <td class="lineNo"><%= row.getSide(SideBySideDiffRow.Side.LEFT).getRowNumber()
                    != null ? row.getSide(SideBySideDiffRow.Side.LEFT).getRowNumber().toString() : "" %></td>
                <td class="<%= row.getSide(SideBySideDiffRow.Side.LEFT).getAction().getCSSClass() %>">
                  <span title="<%= row.getSide(SideBySideDiffRow.Side.LEFT).getAction().getDescription() %>">
                    <%
                      String line = row.getSide(SideBySideDiffRow.Side.LEFT).getLine();
                      out.print("".equals(line) ? "&nbsp;" : line);
                    %>
                  </span>
                </td>
                <td style="background-color: white;">
                  <b><%= row.getSide(SideBySideDiffRow.Side.RIGHT).getAction().getSymbol() %></b>
                </td>
                <td class="lineNo"><%= row.getSide(SideBySideDiffRow.Side.RIGHT).getRowNumber()
                    != null ? row.getSide(SideBySideDiffRow.Side.RIGHT).getRowNumber().toString() : "" %></td>
                <td class="<%= row.getSide(SideBySideDiffRow.Side.RIGHT).getAction().getCSSClass() %>">
                  <span title="<%= row.getSide(SideBySideDiffRow.Side.RIGHT).getAction().getDescription() %>">
                    <%
                      line = row.getSide(SideBySideDiffRow.Side.RIGHT).getLine();
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

<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
