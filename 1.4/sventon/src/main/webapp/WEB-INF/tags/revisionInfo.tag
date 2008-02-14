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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ tag import="de.berlios.sventon.util.HTMLCreator" %>
<%@ tag import="org.apache.commons.lang.StringEscapeUtils" %>

<%@ attribute name="details" required="true" type="org.tmatesoft.svn.core.SVNLogEntry" %>
<%@ attribute name="keepVisible" required="true" type="java.lang.Boolean" %>
<%@ attribute name="linkToHead" required="true" type="java.lang.Boolean" %>

<% application.setAttribute("br", "\n"); %>
<table class="revisionInfoTable">
  <tr>
    <td>
      <table>
        <tr><td><b>Revision:</b></td><td>${details.revision}</td></tr>
        <tr><td><b>Date:</b></td><td><fmt:formatDate type="both" value="${details.date}" dateStyle="short" timeStyle="short"/></td></tr>
        <tr><td><b>User:</b></td><td>${details.author}</td></tr>
        <tr><td valign="top"><b>Message:</b></td><td>${fn:replace(fn:escapeXml(details.message), br, '<br>')}</td></tr>
        <tr><td colspan="2" valign="top"><b>Changed paths:</b></td></tr>
      </table>
    </td>
  </tr>

  <c:set var="latestChangedPaths" value="${details.changedPaths}"/>
  <jsp:useBean id="latestChangedPaths" type="java.util.Map"/>

  <c:set var="instanceName" value="${command.name}"/>
  <jsp:useBean id="instanceName" type="java.lang.String"/>

  <tr>
    <td colspan="2">
      <%=HTMLCreator.createChangedPathsTable(details, null, "", instanceName, keepVisible.booleanValue(), linkToHead.booleanValue(), response)%>
    </td>
  </tr>
</table>
