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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sventon-ui" uri="/WEB-INF/sventon.tld" %>

<%@ attribute name="command" required="true" type="de.berlios.sventon.web.command.SVNBaseCommand" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>

<span id="clickableUrl">
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
          <a href="<sventon-ui:formatUrl url='${pathUrl}'/>">${pathSegment}</a>
        </c:otherwise>
      </c:choose>
    /
  </c:forTokens>
${command.target}
</span>