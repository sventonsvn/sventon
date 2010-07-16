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
<%@ attribute name="command" required="true" type="org.sventon.web.command.BaseCommand" %>
<%@ attribute name="archivedEntry" required="true" type="java.lang.String" %>

<c:url var="showLogLinkUrl" value="/repos/${command.name}/log${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="downloadLinkUrl" value="/repos/${command.name}/get${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showFileUrl" value="/repos/${command.name}/show${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showArchivedFileLinkUrl" value="/repos/${command.name}/show${command.path}">
  <c:param name="revision" value="${command.revision}"/>
  <c:param name="archivedEntry" value="${archivedEntry}"/>
  <c:param name="forceDisplay" value="true"/>
</c:url>

<c:choose>
  <c:when test="${empty archivedEntry}">
    <input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" onmouseover="Tip('<spring:message code="showlog.button.tooltip" arguments="${command.target}"/>')" onclick="document.location.href='${showLogLinkUrl}';">
    <input type="button" class="btn" value="<spring:message code="download.button.text"/>" onclick="document.location.href='${downloadLinkUrl}';">
  </c:when>
  <c:otherwise>
    <input type="button" class="btn" value="<spring:message code="showarchivefile.button.text"/>" onclick="document.location.href='${showFileUrl}';">
    <input type="button" class="btn" value="<spring:message code="force-display.button.text"/>" onclick="document.location.href='${showArchivedFileLinkUrl}';">
  </c:otherwise>
</c:choose>

