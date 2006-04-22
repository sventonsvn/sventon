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
<%@ include file="/WEB-INF/jspf/include.jspf"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/head.jspf"%>
    <title>Revision information details</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/top.jspf"%>

    <p>
      <table class="sventonHeader"><tr><td>Revision information</td></tr></table>
    </p>

    <br/>
    <ui:functionLinks pageName="showRevInfo"/>

    <ui:revisionInfo details="${revisionInfo}" keepVisible="false"/>
    <br>
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
  </body>
</html>
