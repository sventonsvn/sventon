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
<%@ taglib prefix="s" uri="/WEB-INF/sventon.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="command" required="true" type="org.sventon.web.command.BaseCommand" %>

<s:url var="showLogLinkUrl" value="/repos/${command.name}/log${command.path}">
  <s:param name="revision" value="${command.revision}" />
</s:url>
<s:url var="showLockLinkUrl" value="/repos/${command.name}/showlocks${command.path}">
  <s:param name="revision" value="${command.revision}" />
</s:url>
<s:url var="showDirLinkUrl" value="/repos/${command.name}/list${command.path}">
  <s:param name="revision" value="${command.revision}" />
</s:url>

<input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" onmouseover="Tip('<spring:message code="showlog.button.tooltip" arguments="${command.target eq '' ? '/' : command.target}"/>')" onclick="document.location.href='${showLogLinkUrl}';">
<input type="button" class="btn" value="<spring:message code="showlocks.button.text"/>" onclick="document.location.href='${showLockLinkUrl}';">

