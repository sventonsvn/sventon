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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="command" required="true" type="org.sventon.web.command.SVNBaseCommand" %>

<c:url var="showLogLinkUrl" value="/repos/${command.name}/log${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showLockLinkUrl" value="/repos/${command.name}/showlocks${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showDirLinkUrl" value="/repos/${command.name}/browse${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>

<input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" onmouseover="Tip('<spring:message code="showlog.button.tooltip" arguments="${command.target eq '' ? '/' : command.target}"/>')" onclick="document.location.href='${showLogLinkUrl}';">
<input type="button" class="btn" value="<spring:message code="showlocks.button.text"/>" onclick="document.location.href='${showLockLinkUrl}';">

