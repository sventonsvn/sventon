<%
/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
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
  <title>Configuration error</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/topHeaderTable.jspf"%>

  <h3>
    No instance configuration has been added.
    Make sure to <i>add</i> at least one configuration before <i>submitting</i>.
  </h3>

  <p>
    <a href="#" onclick="history.go(-1); return false;">Back...</a>
  </p>
</body>
</html>