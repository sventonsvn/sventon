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
<%@ include file="/WEB-INF/jspf/include.jspf"%>

<html>
<head>
<title>sventon repository browser</title>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
</head>
<body>
  <p class="sventonHeader">
  An authentication failure occured when connecting to the Subversion server.
  </p>
  <p>
  This is typically the symptom of incorrect or missing user id and password configuration 
  in sventon when anonymous access to the repository is not allowed.
  </p>
  <p>
  Peruse sventon log for further information.
  </p>

<%@ include file="/WEB-INF/jspf/foot.jspf"%>
</body>
</html>
