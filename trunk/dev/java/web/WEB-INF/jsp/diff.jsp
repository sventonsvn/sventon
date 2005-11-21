<%
/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="de.berlios.sventon.diff.DiffAction"%>
<%@ page import="de.berlios.sventon.diff.SourceLine"%>

<html>
  <head>
    <title>Diff view</title>
    <%@ include file="/WEB-INF/jsp/head.jsp"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jsp/top.jsp"%>

    <c:url value="showlog.svn" var="showLogUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
    </c:url>

    <p>
      <table class="sventonHeader"><tr><td>
    Diff view - <b><c:out value="${command.target}"/></b></td></tr></table>
    </p>

    <br/>

    <table class="sventonFunctionLinksTable">
      <tr>
        <td><a href="<c:out value="${showLogUrl}&revision=${command.revision}"/>">[Show log]</a></td>
      </tr>
    </table>

    <c:choose>
      <c:when test="${!empty diffException}">
        <p><b><c:out value="${diffException}"/></b></p>
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${!isBinary}">
            <c:set var="leftLines" value="${leftFileContents}" />
            <c:set var="rightLines" value="${rightFileContents}" />
            <jsp:useBean id="leftLines" type="de.berlios.sventon.svnsupport.CustomArrayList" />
            <jsp:useBean id="rightLines" type="de.berlios.sventon.svnsupport.CustomArrayList" />

            <table class="sventonDiffTable" cellspacing="0">
              <tr>
                <th>&nbsp;</th>
                <th width="50%">Revision <c:out value="${fromRevision}"/></th>
                <th>&nbsp;</th>
                <th width="50%">Revision <c:out value="${toRevision}"/></th>
              </tr>
          <%
            SourceLine line;
            for (int i = 0; i < leftLines.size(); i++) {
          %>
              <tr>
            <%
                line = (SourceLine) leftLines.get(i);
                final String css;
                final String sign;
                if (DiffAction.ADD_ACTION.equals(line.getAction())) {
                  css = "srcAdd";
                  sign = "+";
                } else if (DiffAction.DELETE_ACTION.equals(line.getAction())) {
                  css = "srcDel";
                  sign = "-";
                } else if (DiffAction.CHANGE_ACTION.equals(line.getAction())) {
                  css = "srcChg";
                  sign = "+";
                } else {
                  css = "src";
                  sign = "&nbsp;";
                }
            %>
                <td><b><%=sign%></b></td>
                <td class="<%=css%>"><% if ("".equals(line.getLine())) out.print("&nbsp;"); else out.print(line.getLine());%></td>
            <% line = (SourceLine) rightLines.get(i); %>
                <td><b><%=sign%></b></td>
                <td class="<%=css%>"><% if ("".equals(line.getLine())) out.print("&nbsp;"); else out.print(line.getLine());%></td>
              </tr>
        <%
          }
        %>
            </table>
          </c:when>
          <c:otherwise>
            <p><b>One or both files selected for diff is in binary format.</b></p>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>      
    <br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
  </body>
</html>
