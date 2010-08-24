<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2010 sventon project. All rights reserved.
  *
  * This software is licensed as described in the file LICENSE, which
  * you should have received as part of this distribution. The terms
  * are also available at http://www.sventon.org.
  * If newer versions of this license are posted there, you may use a
  * newer version instead, at your option.
  * ====================================================================
  */
%>
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sventon-ui" uri="/WEB-INF/sventon.tld" %>
<%@ tag import="org.sventon.util.HTMLCreator" %>

<%@ attribute name="name" required="true" type="org.sventon.model.RepositoryName" %>
<%@ attribute name="logEntry" required="true" type="org.sventon.model.LogEntry" %>
<%@ attribute name="keepVisible" required="true" type="java.lang.Boolean" %>
<%@ attribute name="linkToHead" required="true" type="java.lang.Boolean" %>

<table class="revisionInfoTable">
  <tr>
    <td>
      <table>
        <tr><td><b><spring:message code="revision"/>:</b></td><td>${logEntry.revision}</td></tr>
        <tr><td><b><spring:message code="date"/>:</b></td>
          <td>
            <span onmouseover="Tip('<sventon-ui:age date="${logEntry.date}"/>');">
              <fmt:formatDate type="both" value="${logEntry.date}" dateStyle="short" timeStyle="short"/>
            </span>
          </td>
        </tr>
        <tr><td><b><spring:message code="author"/>:</b></td><td>${logEntry.author}</td></tr>
        <tr>
          <td valign="top"><b><spring:message code="message"/>:</b></td>
          <td>${logEntry.webFormattedMessage}</td>
        </tr>
      </table>
    </td>
  </tr>

  <tr>
    <td colspan="2">
      <!-- Make sure not to rely on unboxing -->
      <!-- Cast of response variable needed by WAS -->
      <%=HTMLCreator.createChangedPathsTable(logEntry.getChangedPaths(), logEntry.getRevision(), null, "", name,
          keepVisible.booleanValue(), linkToHead.booleanValue(), (HttpServletResponse) response)%>
    </td>
  </tr>
</table>
