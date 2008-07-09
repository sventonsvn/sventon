<%
/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <title>Show Image File - ${command.target}</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <sventon:currentTargetHeader title="Show Image File" target="${command.target}" hasProperties="true"/>
  <sventon:functionLinks pageName="showImageFile"/>

  <c:url value="get.svn" var="showUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
    <c:param name="revision" value="${command.revision}" />
    <c:param name="name" value="${command.name}" />
    <c:param name="disp" value="inline" />
  </c:url>

  <c:url value="getthumb.svn" var="getThumbUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
    <c:param name="revision" value="${command.revision}" />
    <c:param name="name" value="${command.name}" />
  </c:url>

  <p>
    <a href="<sventon-ui:formatUrl url='${showUrl}'/>">
      <img src="<sventon-ui:formatUrl url='${getThumbUrl}'/>" alt="Thumbnail" style="border: 1px dashed black;">
    </a>
  </p>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
