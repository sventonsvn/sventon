<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2008 sventon project. All rights reserved.
  *
  * This software is licensed as described in the file LICENSE, which
  * you should have received as part of this distribution. The terms
  * are also available at http://www.sventon.org.
  * If newer versions of this license are posted there, you may use a
  * newer version instead, at your option.
  * ====================================================================
  */
%>
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ attribute name="command" required="true" type="org.sventon.web.command.SVNBaseCommand" %>
<%@ attribute name="repositoryNames" required="true" type="java.util.Set" %>
<%@ attribute name="hasCredentials" required="true" type="java.lang.Boolean" %>

<table class="sventonHeader">
  <tr>
    <td>
      sventon subversion web client -
      <a class="sventonHeaderLink" href="http://www.sventon.org" target="page">http://www.sventon.org</a>
    </td>
    <td align="right">
      <c:url value="/repos/list" var="changeReposUrl"/>
      <c:if test="${not empty repositoryNames && fn:length(repositoryNames) > 1}">
        <a class="sventonHeaderLink" href="${changeReposUrl}">[change repository]</a>
      </c:if>
      <c:if test="${hasCredentials}">
        <c:url value="/repos/list" var="logout">
          <c:param name="logout" value="true"/>
          <c:param name="repositoryName" value="${command.name}"/>
        </c:url>
        <a class="sventonHeaderLink" href="${logout}">[logout]</a>
      </c:if>
    </td>
  </tr>
</table>
