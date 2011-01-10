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
<%@ taglib prefix="s" uri="/WEB-INF/sventon.tld" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="command" required="true" type="org.sventon.web.command.DiffCommand" %>

<c:choose>
  <c:when test="${isFile}">
    <s:url var="showLogLinkUrl" value="/repos/${command.name}/log${command.path}">
      <s:param name="revision" value="${command.revision}" />
    </s:url>
    <s:url var="showFileUrl" value="/repos/${command.name}/show${command.path}">
      <s:param name="revision" value="${command.revision}" />
    </s:url>

    <input type="button" class="btn" value="<spring:message code="showlog.button.text"/>"
           onmouseover="Tip('<spring:message code="showlog.button.tooltip" arguments="${command.encodedTarget}"/>')"
           onclick="document.location.href='${showLogLinkUrl}';">

    <input type="button" class="btn" value="<spring:message code="showfile.button.text"/>" onclick="document.location.href='${showFileUrl}';">

    <s:url value="/repos/${command.name}/diff${command.fromPath}" var="diffPreviousUrl">
      <s:param name="revision" value="${command.fromRevision}" />
      <s:param name="style" value="${command.style}" />
    </s:url>

    <input type="button" class="btn"
           value="<spring:message code="diffprev.button.text"/>"
           onmouseover="Tip('<spring:message code="diffprev.button.tooltip" arguments="${command.fromTarget},${command.fromRevision}"/>')"
           onclick="document.location.href='${diffPreviousUrl}';">
  </c:when>
  <c:otherwise>
    <s:url var="showDirLinkUrl" value="/repos/${command.name}/list${command.path}">
      <s:param name="revision" value="${command.revision}" />
    </s:url>

    <input type="button" class="btn" value="<spring:message code="showdir.button.text"/>" onclick="document.location.href='${showDirLinkUrl}';" onmouseover="Tip('<spring:message code="showdir.button.tooltip" arguments="${command.encodedPath}"/>')">

  </c:otherwise>
</c:choose>

<input type="button" class="btn" value="<spring:message code="wrap-nowrap.button.text"/>" onclick="toggleWrap();">

<s:url value="/repos/${command.name}/diff${command.path}" var="diffUrl">
  <s:param name="revision" value="${command.revision}" />
  <s:param name="entries" value="${command.toPath}@${command.toRevision}" />
  <s:param name="entries" value="${command.fromPath}@${command.fromRevision}" />
  <c:if test="${command.pegRevision.number > 0}">
    <s:param name="pegRevision" value="${command.pegRevision.number}" />
  </c:if>
  <c:if test="${param.showlatestrevinfo}">
    <s:param name="showlatestrevinfo" value="true" />
  </c:if>
</s:url>

<select id="diffStyle" class="sventonSelect" onchange="document.location.href=this.options[this.selectedIndex].value;">
  <option value="${diffUrl}&style=sidebyside" ${command.style eq 'sidebyside' ? 'selected' : ''}>Side By Side</option>
  <option value="${diffUrl}&style=unified" ${command.style eq 'unified' ? 'selected' : ''}>Unified</option>
  <option value="${diffUrl}&style=inline" ${command.style eq 'inline' ? 'selected' : ''}>Inline</option>
</select>
