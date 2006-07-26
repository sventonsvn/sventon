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
  <%@ include file="/WEB-INF/jspf/head.jspf" %>
  <title>Diff view</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/top.jspf" %>

  <p>
    <table class="sventonHeader">
      <tr>
        <td>Unified diff view - <b>${command.target}</b></td>
      </tr>
    </table>
  </p>

  <br/>
  <ui:functionLinks pageName="showUnifiedDiff"/>

  <c:choose>
    <c:when test="${!empty diffException}">
      <p><b>${diffException}</b></p>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${!isBinary}">
          <b>
            <br>Unified diff between:
            <br>${diffCommand.fromPath} (revision ${diffCommand.fromRevision})
            <br>${diffCommand.toPath} (revision ${diffCommand.toRevision})
          </b>

          <pre class="codeBlock"><c:out value="${diffResult}" escapeXml="false"/></pre>
        </c:when>
        <c:otherwise>
          <p><b>One or both files selected for diff is in binary format.</b></p>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
  <br>
<%@ include file="/WEB-INF/jspf/foot.jspf" %>
</body>
</html>
