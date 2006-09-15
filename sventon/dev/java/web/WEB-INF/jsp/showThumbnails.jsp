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
    <title>Show Thumbnails</title>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <p><ui:currentTargetHeader title="Show Thumbnails" target="${command.target}" hasProperties="false"/></p>

    <br/>
    <ui:functionLinks pageName="showThumbs"/>

    <br/>

    <c:forEach items="${thumbnailentries}" var="entry">

      <c:url value="get.svn" var="downloadUrl" >
        <c:param name="revision" value="${command.revision}" />
        <c:param name="path" value="${entry}" />
        <c:param name="name" value="${command.name}" />
        <c:param name="disp" value="inline" />
      </c:url>
  
      <c:url value="getthumb.svn" var="getThumbUrl" >
        <c:param name="revision" value="${command.revision}" />
        <c:param name="path" value="${entry}" />
        <c:param name="name" value="${command.name}" />

      </c:url>

      <p>
        <b>${entry}</b><br/>
        <a href="${downloadUrl}"><img src="${getThumbUrl}" alt="Thumbnail of ${entry}" border="0"/></a>
      </p>
    </c:forEach>

    <br>

    <p>&nbsp;</p>
    
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
