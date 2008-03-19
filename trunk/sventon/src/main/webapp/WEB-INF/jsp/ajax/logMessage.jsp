<%
/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<% pageContext.setAttribute("br", "\n"); %>
<c:choose>
  <c:when test="${empty logMessage.message}">
    <i><spring:message code="logmessage.empty"/></i>
  </c:when>
  <c:otherwise>
    ${fn:replace(fn:escapeXml(logMessage.message), br, '<br>')}
  </c:otherwise>
</c:choose>