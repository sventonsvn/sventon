<%
/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>
<%@ page import="de.berlios.sventon.diff.DiffAction"%>
<%@ page import="de.berlios.sventon.diff.SourceLine"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
    <title>Diff View</title>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <p><ui:currentTargetHeader title="Diff View" target="${command.target}" hasProperties="false"/></p>
    
    <br/>
    <ui:functionLinks pageName="showDiff"/>

    <c:choose>
      <c:when test="${!empty diffException}">
        <p><b>${diffException}</b></p>
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${!isBinary}">
            <c:set var="leftLines" value="${leftFileContent}" />
            <c:set var="rightLines" value="${rightFileContent}" />
            <jsp:useBean id="leftLines" type="java.util.ArrayList" />
            <jsp:useBean id="rightLines" type="java.util.ArrayList" />

            <table id="diffTable" class="sventonDiffTable" cellspacing="0">
              <tr>
                <th><a href="#diff0"><img src="images/icon_nextdiff.gif" border="0" alt="Next diff" title="Next diff"/></a></th>
                <th>&nbsp;</th>
                <th width="50%">${diffCommand.fromPath} @ revision ${diffCommand.fromRevision}</th>
                <th>&nbsp;</th>
                <th width="50%">${diffCommand.toPath} @ revision ${diffCommand.toRevision}</th>
              </tr>
              <%
                int diffCount = 0;
                for (int i = 0; i < leftLines.size(); i++) {
              %>
              <tr>
                <%
                  SourceLine line = (SourceLine) leftLines.get(i);
                %>
                <td>
                  <% if (DiffAction.UNCHANGED != line.getAction()) { %>
                    <a name="diff<%=diffCount%>"/><a href="#diff<%=++diffCount%>">
                      <img src="images/icon_nextdiff.gif" border="0" alt="Next diff" title="Next diff"/>
                    </a>
                  <%}%>
                </td>
                <td><b><%= line.getAction().getSymbol() %></b></td>
                <td class="<%= line.getAction().getCSSClass() %>"><span title="<%= line.getAction().getDescription() %>"><% if ("".equals(line.getLine())) out.print("&nbsp;"); else out.print(line.getLine());%></span></td>
                <% line = (SourceLine) rightLines.get(i); %>
                <td><b><%= line.getAction().getSymbol() %></b></td>
                <td class="<%= line.getAction().getCSSClass() %>"><span title="<%= line.getAction().getDescription() %>"><% if ("".equals(line.getLine())) out.print("&nbsp;"); else out.print(line.getLine());%></span></td>
              </tr>
              <%
                }
              %>
            </table>
            <a name="diff<%=diffCount%>"/>
          </c:when>
          <c:otherwise>
            <p><b>One or both files selected for diff is in binary format.</b></p>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>
    <br>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
