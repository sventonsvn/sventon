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
<%@ taglib prefix="sventon" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="sventon-ui" uri="/WEB-INF/sventon.tld" %>
<%@ taglib prefix="spring" uri="/WEB-INF/spring.tld" %>

<c:url value="showfile.svn" var="showFileUrl">
  <c:param name="path" value="${command.path}" />
  <c:param name="name" value="${command.name}" />
</c:url>

<table class="fileRevisionsTable">
  <tr>
    <td colspan="2">
      <c:if test="${fn:length(allRevisions) > 1}">
        File Revisions
        <select class="sventonSelect" onchange="document.location='<sventon-ui:formatUrl url="${showFileUrl}"/>' + '&revision=' + this.options[this.selectedIndex].value;">
          <c:forEach var="revision" items="${allRevisions}">
            <option ${currentRevision == revision ? 'selected' : ''} value="${revision}">${revision}</option>
          </c:forEach>
        </select>
      </c:if>
    </td>
  </tr>
  <tr>
    <td>
      <c:if test="${previousRevision ne null}">
        <a href="<sventon-ui:formatUrl url="${showFileUrl}&revision=${previousRevision}"/>" accesskey="P">
          <img src="images/arrow_left.png" alt="Previous change" title="<spring:message code="revinfo.previousrev"/>">
        </a>
      </c:if>
    </td>
    <td align="right">
      <c:if test="${nextRevision ne null}">
        <a href="<sventon-ui:formatUrl url="${showFileUrl}&revision=${nextRevision}"/>" accesskey="N">
          <img src="images/arrow_right.png" alt="Next change" title="<spring:message code="revinfo.nextrev"/>">
        </a>
      </c:if>
    </td>
  </tr>
</table>
