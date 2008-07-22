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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>
<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf" %>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/config.css">
  <title>Configuration error</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>
  <sventon:topHeaderTable command="${command}" repositoryNames="${repositoryNames}"/>

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