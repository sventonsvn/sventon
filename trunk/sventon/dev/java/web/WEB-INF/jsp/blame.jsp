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
    <title>Blame - ${command.target}</title>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <sventon:currentTargetHeader title="Blame" target="${command.target}" hasProperties="false"/>
    <sventon:functionLinks pageName="showBlame"/>

    <table id="blameTable" class="codeBlock" cellspacing="0">
      <tr>
        <th style="width: 50px;">Revision</th>
        <th width="50px">Author</th>
        <th width="50px">Line</th>
        <th>&nbsp;</th>
      </tr>

      <c:forEach items="${annotatedFile.unmodifiableRows}" var="row">
        <tr>
          <td valign="top" style="text-align:right;" title="${row.date}">${row.revision}</td>
          <td valign="top">${row.author}</td>
          <td valign="top" class="sventonLineNo">${row.rowNumber}</td>
          <td valign="top">${row.content}</td>
        </tr>
      </c:forEach>
    </table>

    <br>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>