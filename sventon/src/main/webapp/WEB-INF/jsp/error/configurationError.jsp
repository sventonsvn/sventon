<%
/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/config.css">
  <title><spring:message code="window.title.configerror"/></title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>

  <sventon:topHeaderTable repositoryName="${command.name}" repositoryNames="${repositoryNames}"
                          isEditableConfig="${isEditableConfig}" isLoggedIn="${userRepositoryContext.isLoggedIn}"/>

  <h3>
    No repository has been added.
    Make sure to <i>add</i> at least one configuration before <i>submitting</i>.
  </h3>

  <p>
    <a href="#" onclick="history.go(-1); return false;">Back...</a>
  </p>

<%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf" %>
</body>
</html>