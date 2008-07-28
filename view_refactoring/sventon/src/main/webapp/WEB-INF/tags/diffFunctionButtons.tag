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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="command" required="true" type="org.sventon.web.command.SVNBaseCommand" %>
<%@ attribute name="diffCommand" required="true" type="org.sventon.web.command.DiffCommand" %>
<%@ attribute name="diffStyle" required="true" type="java.lang.String" %>
<%@ attribute name="pegrev" required="true" type="java.lang.Long" %>

<c:url var="showLogLinkUrl" value="/repos/${command.name}/showlog${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showFileUrl" value="/repos/${command.name}/view${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>

<input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" onmouseover="Tip('<spring:message code="showlog.button.tooltip" arguments="${command.target}"/>')" onclick="document.location.href='${showLogLinkUrl}';">
<input type="button" class="btn" value="<spring:message code="showfile.button.text"/>" onclick="document.location.href='${showFileUrl}';">

<c:url value="/repos/${command.name}/diff${command.path}" var="diffUrl">
  <c:param name="revision" value="${command.revision}" />
  <c:param name="entry" value="${diffCommand.toPath};;${diffCommand.toRevision}" />
  <c:param name="entry" value="${diffCommand.fromPath};;${diffCommand.fromRevision}" />
  <c:if test="${pegrev > 0}">
    <c:param name="pegrev" value="${pegrev}" />
  </c:if>
  <c:if test="${param.showlatestrevinfo}">
    <c:param name="showlatestrevinfo" value="true" />
  </c:if>
</c:url>
<input type="button" class="btn" value="<spring:message code="wrap-nowrap.button.text"/>" onclick="toggleWrap();">

<c:url value="/repos/${command.name}/diffprev${diffCommand.fromPath}" var="diffPreviousUrl">
  <c:param name="revision" value="${diffCommand.fromRevision}" />
</c:url>
<input type="button" class="btn" value="<spring:message code="diffprev.button.text"/>" onmouseover="Tip('<spring:message code="diffprev.button.tooltip" arguments="${diffCommand.fromPath},${diffCommand.fromRevision}"/>')" onclick="document.location.href='${diffPreviousUrl}';">

<select name="diffStyle" class="sventonSelect" onchange="document.location.href=this.options[this.selectedIndex].value;">
  <option value="${diffUrl}&style=sidebyside" ${diffStyle eq 'sidebyside' ? 'selected' : ''}>Side By Side</option>
  <option value="${diffUrl}&style=unified" ${diffStyle eq 'unified' ? 'selected' : ''}>Unified</option>
  <option value="${diffUrl}&style=inline" ${diffStyle eq 'inline' ? 'selected' : ''}>Inline</option>
</select>
