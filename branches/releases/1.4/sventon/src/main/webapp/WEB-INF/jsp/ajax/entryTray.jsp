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
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sventon-ui" uri="/WEB-INF/sventon.tld" %>
<%@ taglib prefix="spring" uri="/WEB-INF/spring.tld" %>

<form method="post" action="#" name="entryTrayForm" onsubmit="return doAction(this);">
  <!-- Needed by ASVNTC -->
  <input type="hidden" name="path" value="${command.path}">
  <input type="hidden" name="revision" value="${command.revision}">
  <input type="hidden" name="name" value="${command.name}">

  <table class="entryTrayTable">
    <tr>
      <td class="droparea" colspan="4">
        <spring:message code="entrytray.dragdrop.text"/>
      </td>
    </tr>
    <tr>
      <td></td>
      <td></td>
      <td><b>Name</b></td>
      <td></td>
    </tr>
    <c:set var="rowCount" value="0"/>
    <c:forEach var="entry" items="${userRepositoryContext.repositoryEntryTray.unmodifiableEntries}">
      <c:url value="repobrowser.svn" var="entryTrayViewUrl">
        <c:param name="path" value="${entry.fullEntryName}" />
        <c:param name="revision" value="${command.revision}" />
        <c:param name="name" value="${command.name}" />
      </c:url>
      <c:url value="showfile.svn" var="entryTrayShowFileUrl">
        <c:param name="path" value="${entry.fullEntryName}" />
        <c:param name="revision" value="${command.revision}" />
        <c:param name="name" value="${command.name}" />
      </c:url>
      <c:url value="revinfo.svn" var="entryTrayShowRevInfoUrl">
        <c:param name="revision" value="${entry.revision}" />
        <c:param name="name" value="${command.name}" />
      </c:url>
      <c:url value="entrytray.ajax" var="entryTrayRemoveUrl">
        <c:param name="path" value="${entry.fullEntryName}" />
        <c:param name="revision" value="${entry.revision}" />
        <c:param name="name" value="${command.name}" />
        <c:param name="action" value="remove" />
      </c:url>
    <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
      <td><input type="checkbox" name="entry" value="${entry.fullEntryName};;${entry.revision}"></td>
      <c:choose>
        <c:when test="${'dir' eq entry.kind}">
          <td>
            <img src="images/icon_folder.png" alt="dir">
          </td>
          <td>
            <a href="<sventon-ui:formatUrl url='${entryTrayViewUrl}'/>">${entry.name}</a>&nbsp;
        </c:when>
        <c:otherwise>
          <td>
            <sventon-ui:fileTypeIcon filename="${entry.name}"/>
          </td>
          <td>
            <a href="<sventon-ui:formatUrl url='${entryTrayShowFileUrl}'/>">${entry.name}</a>&nbsp;
        </c:otherwise>
      </c:choose>
            (<a href="${showRevInfoUrl}" onmouseover="getLogMessage(${entry.revision}, '${command.name}', '<fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>');">${entry.revision}</a>)
          </td>
      <td>
        <a href="#" onclick="removeEntryFromTray('<sventon-ui:formatUrl url='${entryTrayRemoveUrl}'/>'); return false;">
          <img align="middle" src="images/delete.png" alt="<spring:message code="delete.tooltip"/>" title="<spring:message code="delete.tooltip"/>"/>
        </a>
      </td>
    </tr>
    <c:set var="rowCount" value="${rowCount + 1}"/>
    </c:forEach>
    <tr>
      <%@ include file="/WEB-INF/jspf/actionSelectList.jspf" %>
      <td></td>
    </tr>
  </table>
</form>