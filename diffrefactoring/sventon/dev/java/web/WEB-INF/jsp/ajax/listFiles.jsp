<%
/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="/WEB-INF/spring.tld" %>

<%@ page import="de.berlios.sventon.repository.RepositoryEntry"%>

<c:forEach items="${svndir}" var="entry">
  <jsp:useBean id="entry" type="RepositoryEntry" />

  <c:url value="showfile.svn" var="showFileUrl">
    <c:param name="path" value="${entry.fullEntryName}" />
    <c:param name="revision" value="${command.revision}" />
    <c:param name="name" value="${command.name}" />
  </c:url>
  <c:url value="revinfo.svn" var="showRevInfoUrl">
    <c:param name="revision" value="${entry.revision}" />
    <c:param name="name" value="${command.name}" />
  </c:url>

  <tr class="sventonFileEntryTableRow expandedDir${rowNumber}">
    <td class="sventonCol1">
      <input type="checkbox" name="entry" value="${entry.fullEntryName}"/>
    </td>
    <td class="sventonCol2">
      <img src="images/icon_file.gif" alt="file"/>
    </td>
    <td class="sventonCol3">
      <a href="${showFileUrl}">${entry.name}</a>
    </td>
    <td class="sventonCol4"></td>
    <td class="sventonCol5">${entry.size}</td>
    <td class="sventonCol6">
      <a href="${showRevInfoUrl}" onmouseover="this.T_WIDTH=1;return escape('<spring:message code="showrevinfo.link.tooltip"/>')">
        ${entry.revision}
      </a>
    </td>
    <td class="sventonCol7">${entry.author}</td>
    <td class="sventonCol8">
      <fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>
    </td>
  </tr>
</c:forEach>
