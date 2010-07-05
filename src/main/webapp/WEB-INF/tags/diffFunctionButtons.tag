<%
/*
 * ====================================================================
 * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
<%@ attribute name="command" required="true" type="org.sventon.web.command.DiffCommand" %>

<c:url var="showLogLinkUrl" value="/repos/${command.name}/log${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showFileUrl" value="/repos/${command.name}/show${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>

<input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" onmouseover="Tip('<spring:message code="showlog.button.tooltip" arguments="${command.target}"/>')" onclick="document.location.href='${showLogLinkUrl}';">
<input type="button" class="btn" value="<spring:message code="showfile.button.text"/>" onclick="document.location.href='${showFileUrl}';">

<c:url value="/repos/${command.name}/diff${command.path}" var="diffUrl">
  <c:param name="revision" value="${command.revision}" />
  <c:param name="entries" value="${command.toPath}@${command.toRevision}" />
  <c:param name="entries" value="${command.fromPath}@${command.fromRevision}" />
  <c:if test="${command.pegRevision > 0}">
    <c:param name="pegRevision" value="${command.pegRevision}" />
  </c:if>
  <c:if test="${param.showlatestrevinfo}">
    <c:param name="showlatestrevinfo" value="true" />
  </c:if>
</c:url>
<input type="button" class="btn" value="<spring:message code="wrap-nowrap.button.text"/>" onclick="toggleWrap();">

<c:url value="/repos/${command.name}/diff${command.fromPath}" var="diffPreviousUrl">
  <c:param name="revision" value="${command.fromRevision}" />
  <c:param name="style" value="${command.style}" />
</c:url>
<input type="button" class="btn" value="<spring:message code="diffprev.button.text"/>" onmouseover="Tip('<spring:message code="diffprev.button.tooltip" arguments="${command.fromPath},${command.fromRevision}"/>')" onclick="document.location.href='${diffPreviousUrl}';">

<select id="diffStyle" class="sventonSelect" onchange="document.location.href=this.options[this.selectedIndex].value;">
  <option value="${diffUrl}&style=sidebyside" ${command.style eq 'sidebyside' ? 'selected' : ''}>Side By Side</option>
  <option value="${diffUrl}&style=unified" ${command.style eq 'unified' ? 'selected' : ''}>Unified</option>
  <option value="${diffUrl}&style=inline" ${command.style eq 'inline' ? 'selected' : ''}>Inline</option>
</select>
