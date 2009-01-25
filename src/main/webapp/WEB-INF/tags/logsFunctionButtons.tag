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
<%@ attribute name="isFile" required="true" type="java.lang.Boolean" %>

<c:url var="showFileUrl" value="/repos/${command.name}/show${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showDirLinkUrl" value="/repos/${command.name}/list${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="downloadLinkUrl" value="/repos/${command.name}/get${command.path}">
  <c:param name="name" value="${command.name}" />
</c:url>
<c:url var="showLogUrl" value="/repos/${command.name}/log${command.path}">
  <c:param name="revision" value="${command.revision}"/>
  <c:param name="stopOnCopy" value="${!stopOnCopy}"/>
  <c:if test="${nextPath ne null}">
    <c:param name="nextPath" value="${nextPath}" />
  </c:if>
  <c:if test="${nextRevision ne null}">
    <c:param name="nextRevision" value="${nextRevision}" />
  </c:if>
</c:url>

<c:choose>
  <c:when test="${isFile}">
    <input type="button" class="btn" value="<spring:message code="showfile.button.text"/>" onclick="document.location.href='${showFileUrl}';">
    <input type="button" class="btn" value="<spring:message code="download.button.text"/>" onclick="document.location.href='${downloadLinkUrl}';">
  </c:when>
  <c:otherwise>
    <input type="button" class="btn" value="<spring:message code="showdir.button.text"/>" onclick="document.location.href='${showDirLinkUrl}';" onmouseover="Tip('<spring:message code="showdir.button.tooltip" arguments="${command.path}"/>')">
  </c:otherwise>
</c:choose>
<input type="checkbox" ${stopOnCopy ? 'checked="checked"' : ''} onclick="document.location.href='${showLogUrl}';"/>
<spring:message code="stop-on-copy.button.text"/>
