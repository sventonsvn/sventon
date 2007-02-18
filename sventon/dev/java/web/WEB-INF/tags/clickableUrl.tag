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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="command" required="true" type="de.berlios.sventon.web.command.SVNBaseCommand" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>

<a href="repobrowser.svn?path=/&revision=${command.revision}&name=${command.name}"> ${url} </a> /
  <c:forTokens items="${command.pathNoLeaf}" delims="/" var="pathSegment">
    <c:set var="accuPath" scope="page" value="${accuPath}${pathSegment}/"/>
      <c:choose>
        <c:when test="${hasErrors}">${pathSegment}</c:when>
        <c:otherwise>
          <c:url value="repobrowser.svn" var="pathUrl">
            <c:param name="path" value="/${accuPath}" />
            <c:param name="revision" value="${command.revision}" />
            <c:param name="name" value="${command.name}" />
          </c:url>
          <a href="${pathUrl}">${pathSegment}</a>
        </c:otherwise>
      </c:choose>
    /
  </c:forTokens>
${command.target}
