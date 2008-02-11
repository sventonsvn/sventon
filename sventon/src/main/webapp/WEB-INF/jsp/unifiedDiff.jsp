<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
  <%@ include file="/WEB-INF/jspf/pageHead.jspf" %>
  <title>Diff View</title>
</head>

<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf" %>

  <sventon:currentTargetHeader title="Unified Diff View" target="${command.target}" hasProperties="false"/>
  <sventon:functionLinks pageName="showUnifiedDiff"/>

  <c:choose>
    <c:when test="${isIdentical}">
      <p><b><spring:message code="diff.error.identical"/></b></p>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${!isBinary}">
          <b>
            <br>Unified diff between:
            <br>${diffCommand.fromPath} @ revision ${diffCommand.fromRevision}
            <br>${diffCommand.toPath} @ revision ${diffCommand.toRevision}
          </b>

          <pre class="codeBlock"><c:out value="${diffResult}" escapeXml="false"/></pre>
        </c:when>
        <c:otherwise>
          <p><b><spring:message code="diff.error.binary"/></b></p>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
