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
<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
  <head>
    <title>Show thumbnails</title>
    <%@ include file="/WEB-INF/jsp/head.jsp"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jsp/top.jsp"%>

    <c:url value="get.svn" var="downloadUrl" />

    <p>
      <table class="sventonHeader"><tr><td>Thumbnail view</td></tr></table>
      <%@ include file="/WEB-INF/jsp/sventonheader.jsp"%>
    </p>

    <br/>

    <c:forEach items="${thumbnailentries}" var="entry">
      <p>
        <b><c:out value="${entry}"/></b><br/>
        <a href="<c:out value="${downloadUrl}?path=${entry}&revision=${command.revision}"/>&disp=inline">
          <img src="<c:out value="${downloadUrl}?path=${entry}&revision=${command.revision}"/>&disp=thumb" alt="Thumbnail" border="0"/>
        </a>
      </p>
    </c:forEach>

<br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
  </body>
</html>
