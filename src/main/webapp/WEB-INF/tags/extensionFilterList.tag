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
<%@ attribute name="existingExtensions" required="true" type="java.util.Set" %>
<%@ attribute name="filterExtension" required="true" type="java.lang.String" %>

<c:url var="showDirLinkUrl" value="/repos/${command.name}/list${command.path}">
  <c:param name="revision" value="${command.revision}" />
</c:url>

<select name="filterExtension" class="sventonSelect" onchange="document.location.href='${showDirLinkUrl}&filterExtension=' + this.form.filterExtension.options[this.form.filterExtension.selectedIndex].value;">
  <option value="all">&lt;<spring:message code="show.all"/>&gt;</option>
  <c:forEach items="${existingExtensions}" var="extension">
    <option value="${extension}" ${extension eq filterExtension ? "selected" : ""}>${extension}</option>
  </c:forEach>
</select>

