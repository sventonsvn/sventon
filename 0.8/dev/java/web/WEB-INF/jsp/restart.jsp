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
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<html>
<head>
  <title>sventon repository browser</title>
  <%@ include file="/WEB-INF/jsp/head.jsp" %>
</head>

<body>
  <p>
    <table class="sventonHeader">
      <tr>
        <td>sventon repository browser configuration</td>
      </tr>
    </table>
  </p>
  <p><b>Configuration done!</b></p>
  <p>
    Please restart the servlet container to startup the sventon application.<br/>
    Note that directly after restart sventon will index the <i>entire</i> subversion repository.<br/>
    <b>This can take a couple of minutes depending on the number of repository entries. Please be patient...</b> 
  </p>
  <%@ include file="/WEB-INF/jsp/foot.jsp" %>
</body>
</html>
