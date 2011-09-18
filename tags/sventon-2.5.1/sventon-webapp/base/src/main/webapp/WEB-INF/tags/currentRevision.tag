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
<%@ attribute name="headRevision" required="true" type="java.lang.Long" %>
<%@ attribute name="clickable" required="true" type="java.lang.Boolean" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<spring:message code="revision.rev"/>:
<c:choose>
  <c:when test="${(headRevision == command.revisionNumber)}">
    <spring:message code="revision.head"/> (${command.revisionNumber})
  </c:when>
  <c:otherwise>
    <s:url value="/repos/${command.name}/info" var="showRevInfoUrl">
      <s:param name="revision" value="${command.revision}"/>
    </s:url>
    <c:choose>
      <c:when test="${clickable}">
        <a href="${showRevInfoUrl}"><span class="exclamationText">${command.revision}</span></a>
      </c:when>
      <c:otherwise>
        <span class="exclamationText">${command.revision}</span>
      </c:otherwise>
    </c:choose>
  </c:otherwise>
</c:choose>

