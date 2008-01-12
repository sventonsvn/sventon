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
  <title>Show Thumbnails</title>
  <link rel="stylesheet" type="text/css" href="jhighlight.css" >
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <sventon:currentTargetHeader title="Show Thumbnails" target="${command.target}" hasProperties="false"/>
  <sventon:functionLinks pageName="showThumbs"/>

  <br>

  <table border="1">
    <c:forEach items="${thumbnailentries}" var="entry">
      <tr height="160px">
        <c:url value="get.svn" var="downloadUrl" >
          <c:param name="revision" value="${command.revision}" />
          <c:param name="path" value="${entry.path}" />
          <c:param name="name" value="${command.name}" />
          <c:param name="disp" value="inline" />
        </c:url>
        <c:url value="getthumb.svn" var="getThumbUrl" >
          <c:param name="revision" value="${command.revision}" />
          <c:param name="path" value="${entry.path}" />
          <c:param name="name" value="${command.name}" />
        </c:url>
        <td valign="top">
          File: <b>${entry.path}</b><br>
          Revision: <b>${entry.revision}</b>
        </td>
        <td width="210px" style="text-align:center;">
          <a href="<sventon-ui:formatUrl url='${downloadUrl}'/>">
            <img src="<sventon-ui:formatUrl url='${getThumbUrl}'/>" alt="Thumbnail of ${entry.path} @ ${revision}"></a>
        </td>
      </tr>
    </c:forEach>
  </table>

<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
