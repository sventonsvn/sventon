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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>
<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf" %>
    <title>Repository instances</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/topHeaderTable.jspf"%>

    <h1>Repository instances</h1>

    <p>
      <ol>
        <c:forEach items="${instanceNames}" var="instanceName">
          <c:url value="repobrowser.svn" var="instanceUrl">
            <c:param name="name" value="${instanceName}" />
          </c:url>
          <li>
            <a href="${instanceUrl}">${instanceName}</a>
          </li>
        </c:forEach>
      </ol>
    </p>
  <%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>