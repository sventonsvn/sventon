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

<c:if test="${fn:length(fileRevisions) > 1}">
<table class="fileHistoryTable">
  <tr>
    <td colspan="2">
        File History
        <select class="sventonSelect" onchange="document.location=this.options[this.selectedIndex].value;">
          <c:forEach var="fileRevision" items="${fileRevisions}">
            <c:url value="showfile.svn" var="showFileAtRevisionUrl">
              <c:param name="path" value="${fileRevision.path}" />
              <c:param name="name" value="${command.name}" />
              <c:param name="revision" value="${fileRevision.revision}" />
              <c:if test="${archivedEntry ne null}">
                <c:param name="archivedEntry" value="${archivedEntry}" />
                <c:param name="forceDisplay" value="true" />
              </c:if>
            </c:url>
            <option ${currentRevision == fileRevision.revision ? 'selected' : ''}
                value="<sventon-ui:formatUrl url="${showFileAtRevisionUrl}"/>">${fileRevision.revision}</option>
          </c:forEach>
        </select>
    </td>
  </tr>
</table>
</c:if>