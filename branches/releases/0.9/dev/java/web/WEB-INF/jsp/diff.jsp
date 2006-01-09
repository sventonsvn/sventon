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
<%@ include file="/WEB-INF/jspf/include.jspf"%>
<%@ page import="de.berlios.sventon.diff.DiffAction"%>
<%@ page import="de.berlios.sventon.diff.SourceLine"%>

<html>
  <head>
    <title>Diff view</title>
    <%@ include file="/WEB-INF/jspf/head.jspf"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/top.jspf"%>

    <p>
      <table class="sventonHeader">
        <tr>
          <td>Diff view - <b>${command.target}</b></td>
        </tr>
      </table>
    </p>

    <br/>
    <ui:functionLinks pageName="showDiff"/>

    <c:choose>
      <c:when test="${!empty diffException}">
        <p><b>${diffException}</b></p>
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
                <th><a href="#diff0"><img src="images/icon_nextdiff.gif" border="0" alt="Next diff" title="Next diff"/></a></th>
                <th>&nbsp;</th>
                <th width="50%">Revision ${fromRevision}</th>
                <th>&nbsp;</th>
                <th width="50%">Revision ${toRevision}</th>
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
                  <% if (DiffAction.u != line.getAction()) { %>
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
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
  </body>
</html>
