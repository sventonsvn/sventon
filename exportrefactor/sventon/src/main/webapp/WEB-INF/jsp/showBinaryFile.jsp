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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <title>Show Binary File - ${command.target}</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <c:choose>
    <c:when test="${archivedEntry ne null}">
      <c:set var="newTarget" value="${command.target} (${archivedEntry})"/>
      <sventon:currentTargetHeader title="Show Binary File" target="${newTarget}" hasProperties="false"/>
    </c:when>
    <c:otherwise>
      <sventon:currentTargetHeader title="Show Binary File" target="${command.target}" hasProperties="true"/>
    </c:otherwise>
  </c:choose>
  <sventon:functionLinks pageName="showBinaryFile"/>

  <p>File is in binary format.</p>

<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
