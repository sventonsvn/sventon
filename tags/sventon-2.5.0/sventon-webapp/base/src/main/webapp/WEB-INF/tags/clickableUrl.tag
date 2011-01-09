<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2011 sventon project. All rights reserved.
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
<%@ taglib prefix="s" uri="/WEB-INF/sventon.tld" %>
<%@ attribute name="command" required="true" type="org.sventon.web.command.BaseCommand" %>
<%@ attribute name="url" required="true" type="java.lang.String" %>
<%@ attribute name="clickable" required="true" type="java.lang.Boolean" %>

<span id="clickableUrl">
  <s:url value="/repos/${command.name}/list/" var="basePathUrl">
    <s:param name="revision" value="${command.revision}" />
  </s:url>
  <a href="${basePathUrl}">${url}</a> /
  <c:set var="accuPath" value=""/>
  <c:forTokens items="${command.parentPath}" delims="/" var="pathSegment">
    <c:set var="accuPath" scope="page" value="${accuPath}${pathSegment}/"/>
      <c:choose>
        <c:when test="${clickable}">
          <s:url value="/repos/${command.name}/list/${accuPath}" var="pathUrl">
            <s:param name="revision" value="${command.revision}" />
          </s:url>
          <a href="${pathUrl}">${pathSegment}</a>
        </c:when>
        <c:otherwise>${pathSegment}</c:otherwise>
      </c:choose>
    /
  </c:forTokens>
${command.target}
</span>
