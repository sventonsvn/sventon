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
    <title>Unhandled error</title>
  </head>

  <body>
    <h3>An unhandled error has occured. Sorry. </h3>

    <p/>
    <table class="sventonStackTraceTable">
      <tr>
        <td>
<pre>${throwable}</pre>
        </td>
      </tr>
    </table>
  </body>
</html>