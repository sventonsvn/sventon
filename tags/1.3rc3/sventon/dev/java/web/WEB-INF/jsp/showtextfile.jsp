<%
/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
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
    <title>Show File - ${command.target}</title>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <c:choose>
      <c:when test="${archivedEntry ne null}">
        <c:set var="newTarget" value="${command.target} (${archivedEntry})"/>
        <sventon:currentTargetHeader title="Show File" target="${newTarget}" hasProperties="false"/>
      </c:when>
      <c:otherwise>
        <sventon:currentTargetHeader title="Show File" target="${command.target}" hasProperties="true"/>
      </c:otherwise>
    </c:choose>
    <sventon:functionLinks pageName="showTextFile"/>

    <table id="textFileTable" class="codeBlock" cellspacing="0">
      <c:forEach items="${file.rows}" var="row">
        <tr>
          <td valign="top" class="sventonLineNo">${row.rowNumber}&nbsp;</td>
          <td valign="top">${row.content}</td>
        </tr>
      </c:forEach>
    </table>

    <br>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>