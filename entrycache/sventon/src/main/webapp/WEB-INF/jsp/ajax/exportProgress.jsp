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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:url value="/ajax/${command.name}/exportprogress" var="exportDownloadUrl">
  <c:param name="uuid" value="${userRepositoryContext.exportUuid}" />
  <c:param name="download" value="true" />
</c:url>

<c:url value="/ajax/${command.name}/exportprogress" var="exportDeleteUrl">
  <c:param name="uuid" value="${userRepositoryContext.exportUuid}" />
  <c:param name="delete" value="true" />
</c:url>

<c:if test="${userRepositoryContext.isWaitingForExport eq true}">
<div style="text-align:center; position:relative; ">
  <c:choose>
    <c:when test="${exportFinished eq true}">
<a href="${exportDownloadUrl}"><span style="font-weight:bold; vertical-align:middle; color:green;">Download!</span></a>
<a href="#" onclick="deleteExportFile('${exportDeleteUrl}'); return false;">
  <img align="middle" src="images/delete.png" alt="<spring:message code="delete.tooltip"/>" title="<spring:message code="delete.tooltip"/>">
</a>
    </c:when>
    <c:otherwise>
<img src="images/spinner.gif" alt="spinner"><span style="vertical-align:40%; font-weight:bold; color:red;">Download in progress...</span>
    </c:otherwise>
  </c:choose>
</div>
</c:if>