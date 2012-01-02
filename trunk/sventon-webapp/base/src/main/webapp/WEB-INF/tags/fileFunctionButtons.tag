<%
/*
 * ====================================================================
 * Copyright (c) 2005-2012 sventon project. All rights reserved.
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
<%@ attribute name="command" required="true" type="org.sventon.web.command.BaseCommand" %>
<%@ attribute name="archivedEntry" required="true" type="java.lang.String" %>

<s:url var="showLogLinkUrl" value="/repos/${command.name}/log${command.path}">
  <s:param name="revision" value="${command.revision}" />
</s:url>
<s:url var="downloadLinkUrl" value="/repos/${command.name}/get${command.path}">
  <s:param name="revision" value="${command.revision}" />
</s:url>
<s:url var="showFileUrl" value="/repos/${command.name}/show${command.path}">
  <s:param name="revision" value="${command.revision}" />
</s:url>
<s:url var="showArchivedFileLinkUrl" value="/repos/${command.name}/show${command.path}">
  <s:param name="revision" value="${command.revision}"/>
  <s:param name="archivedEntry" value="${archivedEntry}"/>
  <s:param name="forceDisplay" value="true"/>
</s:url>

<c:choose>
  <c:when test="${empty archivedEntry}">
    <input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" onmouseover="Tip('<spring:message code="showlog.button.tooltip" arguments="${command.encodedTarget}"/>')" onclick="document.location.href='${showLogLinkUrl}';">
    <input type="button" class="btn" value="<spring:message code="download.button.text"/>" onclick="document.location.href='${downloadLinkUrl}';">
  </c:when>
  <c:otherwise>
    <input type="button" class="btn" value="<spring:message code="showarchivefile.button.text"/>" onclick="document.location.href='${showFileUrl}';">
    <input type="button" class="btn" value="<spring:message code="force-display.button.text"/>" onclick="document.location.href='${showArchivedFileLinkUrl}';">
  </c:otherwise>
</c:choose>

