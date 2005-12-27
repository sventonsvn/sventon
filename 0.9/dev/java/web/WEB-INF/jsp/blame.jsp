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
<%@ include file="/WEB-INF/jspf/include.jsp"%>

<html>
  <head>
    <title>sventon blame - ${url}</title>
    <%@ include file="/WEB-INF/jspf/head.jsp"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

<body>
  <%@ include file="/WEB-INF/jspf/top.jsp"%>

  <c:url value="showlog.svn" var="showLogUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <c:url value="get.svn" var="downloadUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <c:url value="showfile.svn" var="showFileUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>

  <p>
    <table class="sventonHeader">
      <tr><td>
  Blame - <b>${command.target}</b>&nbsp;<a href="javascript:toggleElementVisibility('propertiesDiv'); changeHideShowDisplay('propertiesLink');">[<span id="propertiesLink">show</span> properties]</a></td></tr></table>
    <%@ include file="/WEB-INF/jsp/sventonheader.jsp"%>
  </p>
  
  <br/>
  
  <table class="sventonFunctionLinksTable">
    <tr>
      <td><a href="${showFileUrl}&revision=${command.revision}">[Show file]</a></td>
      <td><a href="${downloadUrl}&revision=${command.revision}">[Download]</a></td>
      <td><a href="#">[Diff with previous]</a></td>
      <td><a href="${showLogUrl}&revision=${command.revision}">[Show log]</a></td>
    </tr>
  </table>
<p>
<br/>
Blame support disabled.
<br/>
</p>
<!-- 
<table class="sventonBlameTable">
  <tr>
    <th>Revision</th>
    <th>Author</th>
    <th>Line</th>
  </tr>
  <tr>
    <td valign="top">
      <pre>
<c:forEach items="${handler.blameLines}" var="line"><a href="#">${line.revision}</a>
</c:forEach></pre>
    </td>
    <td valign="top">
      <pre>
<c:forEach items="${handler.blameLines}" var="line">${line.author}
</c:forEach></pre>
    </td>
    <td valign="top">
      <pre>
<c:out value="${handler.blameContent}" escapeXml="false"/></pre>
    </td>
  </tr>
</table>
-->
<br>
<%@ include file="/WEB-INF/jspf/foot.jsp"%>
</body>
</html>
