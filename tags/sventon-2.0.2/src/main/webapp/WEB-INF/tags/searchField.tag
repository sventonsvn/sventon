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
<%@ attribute name="command" required="true" type="org.sventon.web.command.BaseCommand" %>
<%@ attribute name="isUpdating" required="true" type="java.lang.Boolean" %>
<%@ attribute name="isHead" required="true" type="java.lang.Boolean" %>
<%@ attribute name="searchMode" required="true" type="java.lang.String" %>

<span style="white-space: nowrap;">
  <spring:message code="search.text"/>
  <input type="radio" id="entrySearch" name="searchMode" class="rdo" value="entries" ${searchMode eq 'entries' ? 'checked' : ''}>
  <label for="entrySearch"><spring:message code="entries"/></label>
  <input type="radio" id="logSearch" name="searchMode" class="rdo" value="logMessages" ${searchMode eq 'logMessages' ? 'checked' : ''}>
  <label for="logSearch"><spring:message code="logs"/></label>
</span>
<input type="hidden" name="startDir" value="${command.pathPart}">

<c:choose>
  <c:when test="${isUpdating}">
    <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="Tip('<spring:message code="search.button.isupdating.tooltip"/>')">
    <input type="text" name="searchString" class="sventonSearchField" value="" disabled="disabled">
    <input type="submit" value="<spring:message code='go'/>" disabled="disabled" class="btn">
  </c:when>
  <c:when test="${!isHead}">
    <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="Tip('<spring:message code="search.button.disabled.tooltip"/>')">
    <input type="text" name="searchString" class="sventonSearchField" value="" disabled="disabled">
    <input type="submit" value="<spring:message code='go'/>" disabled="disabled" class="btn">
  </c:when>
  <c:otherwise>
    <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('search_help');">
    <input type="text" name="searchString" class="sventonSearchField" value="">
    <input type="submit" value="<spring:message code='go'/>" class="btn">
  </c:otherwise>
</c:choose>
