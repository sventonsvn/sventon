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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="command" required="true" type="org.sventon.web.command.BaseCommand" %>
<%@ attribute name="isUpdating" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isHead" required="true" type="java.lang.Boolean" %>

<c:choose>
  <c:when test="${isUpdating}">
    <input type="button" class="btn" value="<spring:message code="flatten.button.text"/>" disabled="disabled">
    <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="Tip('<spring:message code="flatten.button.isupdating.tooltip"/>')">
  </c:when>
  <c:when test="${!isHead}">
    <input type="button" class="btn" value="<spring:message code="flatten.button.text"/>" disabled="disabled">
    <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="Tip('<spring:message code="flatten.button.disabled.tooltip"/>')">
  </c:when>
  <c:otherwise>
    <input type="button" class="btn" value="<spring:message code="flatten.button.text"/>" onclick="return doFlatten('${command.encodedPath}', '${command.name}');" onmouseover="Tip('<spring:message code="flatten.button.tooltip"/>')">
  </c:otherwise>
</c:choose>
