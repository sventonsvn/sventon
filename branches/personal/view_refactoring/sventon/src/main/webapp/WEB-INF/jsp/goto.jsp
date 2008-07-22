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
  <title>sventon - error</title>
</head>

<body>
  <script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/js/wz_tooltip.js"></script>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>
  <sventon:topHeaderTable command="${command}" repositoryNames="${repositoryNames}"/>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf" %>
</body>

<%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf" %>
</html>
