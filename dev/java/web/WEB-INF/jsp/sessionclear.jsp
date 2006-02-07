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
<%@ include file="/WEB-INF/jspf/include.jspf" %>

<html>
  <head>
    <title>Logged out</title>
    <%@ include file="/WEB-INF/jspf/head.jspf" %>
  </head>

  <body>

    <%@ include file="/WEB-INF/jspf/sventonbar.jspf" %>
    <p>
The session has been cleared.
    </p>
    <a href="repobrowser.svn">sventon</a>
  </body>
</html>
