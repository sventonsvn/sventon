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

  <p><sventon:currentTargetHeader title="Search result for" target="${searchString}" hasProperties="false"/></p>

  <br/>
  <sventon:functionLinks pageName="repobrowser"/>

  <div id="logMessagesDiv" class="sventonEntriesDiv">
    <table class="sventonEntriesTable">
      <c:set var="rowCount" value="0"/>
      <tr>
        <th>Revision</th>
        <th>Log Message</th>
      </tr>
      <c:forEach items="${logMessages}" var="logMessage">
        <c:url value="revinfo.svn" var="showRevInfoUrl">
          <c:param name="revision" value="${logMessage.revision}" />
          <c:param name="name" value="${command.name}" />
        </c:url>
        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <td><a href="${showRevInfoUrl}" onmouseover="this.T_WIDTH=1;return escape('<spring:message code="showrevinfo.link.tooltip"/>')">${logMessage.revision}</a></td>
          <td>${logMessage.message}</td>
        </tr>
        <c:set var="rowCount" value="${rowCount + 1}"/>
      </c:forEach>
      <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
        <td><b>Total: ${rowCount} hits</b></td>
        <td></td>
      </tr>
    </table>
  </div>

<br>
<script language="JavaScript" type="text/javascript" src="js/wz_tooltip.js"></script>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
