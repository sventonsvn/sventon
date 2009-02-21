<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2009 sventon project. All rights reserved.
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
<%@ attribute name="details" required="true" type="org.tmatesoft.svn.core.SVNLogEntry" %>
<%@ attribute name="keepVisible" required="true" type="java.lang.Boolean" %>
<%@ attribute name="linkToHead" required="true" type="java.lang.Boolean" %>

<% application.setAttribute("br", "\n"); %>
<table class="revisionInfoTable">
  <tr>
    <td>
      <table>
        <tr><td><b><spring:message code="revision"/>:</b></td><td>${details.revision}</td></tr>
        <tr><td><b><spring:message code="date"/>:</b></td>
          <td>
            <span onmouseover="Tip('<sventon-ui:age date="${details.date}"/>');">
              <fmt:formatDate type="both" value="${details.date}" dateStyle="short" timeStyle="short"/>
            </span>
          </td>
        </tr>
        <tr><td><b><spring:message code="author"/>:</b></td><td>${details.author}</td></tr>
        <tr><td valign="top"><b><spring:message code="message"/>:</b></td><td>${fn:replace(fn:escapeXml(details.message), br, '<br>')}</td></tr>
        <tr><td colspan="2" valign="top"><b><spring:message code="paths.changed"/>:</b></td></tr>
      </table>
    </td>
  </tr>

  <tr>
    <td colspan="2">
      <%=HTMLCreator.createChangedPathsTable(details.getChangedPaths(), details.getRevision(), null, "", name,
          keepVisible.booleanValue(), linkToHead.booleanValue(), response)%>
    </td>
  </tr>
</table>
