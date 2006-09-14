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

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
    <title>sventon repository browser - ${url}</title>
  </head>
<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <p><ui:currentTargetHeader title="Search result for" target="${searchString}" hasProperties="false"/></p>

  <br/>
  <ui:functionLinks pageName="repobrowse"/>

  <div id="logMessagesDiv" class="sventonEntriesDiv">
    <table class="sventonEntriesTable">
      <tr>
        <th>Revision</th>
        <th>Log Message</th>
      </tr>
      <%
        int hitCount = 0;
      %>
      <c:forEach items="${logMessages}" var="logMessage">
        <c:url value="revinfo.svn" var="showRevInfoUrl">
          <c:param name="revision" value="${logMessage.revision}" />
          <c:param name="name" value="${command.name}" />
        </c:url>
        <tr class="<%if (hitCount % 2 == 0) out.print("sventonEntryEven"); else out.print("sventonEntryOdd");%>">
          <td><a href="${showRevInfoUrl}" onmouseover="this.T_WIDTH=1;return escape('<spring:message code="showrevinfo.link.tooltip"/>')">${logMessage.revision}</a></td>
          <td>${logMessage.message}</td>
        </tr>
        <% hitCount++; %>
      </c:forEach>
      <tr class="<%if (hitCount % 2 == 0) out.print("sventonEntryEven"); else out.print("sventonEntryOdd");%>">
        <td><b>Total: <%=hitCount%> hits</b></td>
        <td></td>
      </tr>
    </table>
  </div>

<br>
<script language="JavaScript" type="text/javascript" src="wz_tooltip.js"></script>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
