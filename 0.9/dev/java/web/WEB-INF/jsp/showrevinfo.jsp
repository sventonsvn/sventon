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

<html>
<head>
<title>Revision information details</title>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
</head>
<body>
  <%@ include file="/WEB-INF/jspf/top.jspf"%>

  <c:url value="repobrowser.svn" var="showDirUrl">
    <c:param name="path" value="${command.path}" />
    <c:param name="revision" value="${command.revision}" />
  </c:url>
  
  <p>
    <table class="sventonHeader"><tr><td>Revision information</td></tr></table>
  </p>
  <br/>
  
  <table class="sventonFunctionLinksTable">
    <tr>
      <td><a href="${showDirUrl}">[Show directory]</a></td>
    </tr>
  </table>

<ui:revisionInfo details="${revisionInfo}"/>

<br>
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
</body>
</html>
