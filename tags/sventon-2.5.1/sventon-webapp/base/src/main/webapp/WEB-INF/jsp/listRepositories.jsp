<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2011 sventon project. All rights reserved.
  *
  * This software is licensed as described in the file LICENSE, which
  * you should have received as part of this distribution. The terms
  * are also available at http://www.sventon.org.
  * If newer versions of this license are posted there, you may use a
  * newer version instead, at your option.
  * ====================================================================
  */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf" %>
  <title>Repositories</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>

  <sventon:topHeaderTable repositoryName="${command.name}" repositoryNames="${repositoryNames}"
                          isEditableConfig="${isEditableConfig}" isLoggedIn="${userRepositoryContext.isLoggedIn}"/>

  <h1><spring:message code="repositories"/></h1>

  <ol>
    <c:forEach items="${repositoryNames}" var="repositoryName">
      <s:url value="/repos/${repositoryName}/list/" var="repositoryUrl" />
      <li>
        <a href="${repositoryUrl}">${repositoryName}</a>
      </li>
    </c:forEach>
  </ol>

<%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf"%>
</body>
</html>
