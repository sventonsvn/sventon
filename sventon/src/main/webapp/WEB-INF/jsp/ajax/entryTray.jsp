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
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sventon-ui" uri="/WEB-INF/sventon.tld" %>

<form method="post" action="#" name="entryTrayForm" onsubmit="return doAction(this);">
  <!-- Needed by ASVNTC -->
  <input type="hidden" name="path" value="${command.path}">
  <!-- The last dragged entry's peg revision will rule -->
  <input type="hidden" name="revision" value="${pegrev}">
  <input type="hidden" name="name" value="${command.name}">

  <table class="entryTrayTable">
    <tr>
      <td class="droparea" colspan="4">
        <spring:message code="entrytray.dragdrop.text"/>
      </td>
    </tr>
    <tr>
      <td/>
      <td/>
      <td><b>Name</b></td>
      <td/>
    </tr>
    <c:set var="rowCount" value="0"/>
    <c:forEach var="peggedEntry" items="${userRepositoryContext.repositoryEntryTray.unmodifiableEntries}">
      <c:set var="trayEntry" value="${peggedEntry.entry}" />
      <jsp:useBean id="trayEntry" type="de.berlios.sventon.repository.RepositoryEntry"/>
      
      <c:url value="repobrowser.svn" var="entryTrayViewUrl">
        <c:param name="path" value="${trayEntry.fullEntryName}" />
        <c:param name="revision" value="${peggedEntry.pegRevision}" />
        <c:param name="name" value="${command.name}" />
      </c:url>
      <c:url value="showfile.svn" var="entryTrayShowFileUrl">
        <c:param name="path" value="${trayEntry.fullEntryName}" />
        <c:param name="revision" value="${peggedEntry.pegRevision}" />
        <c:param name="name" value="${command.name}" />
      </c:url>
      <c:url value="revinfo.svn" var="entryTrayShowRevInfoUrl">
        <c:param name="revision" value="${trayEntry.revision}" />
        <c:param name="name" value="${command.name}" />
      </c:url>
      <c:url value="entrytray.ajax" var="entryTrayRemoveUrl">
        <c:param name="path" value="${trayEntry.fullEntryName}" />
        <c:param name="revision" value="${peggedEntry.pegRevision}" />
        <c:param name="name" value="${command.name}" />
        <c:param name="action" value="remove" />
      </c:url>
    <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
      <td><input type="checkbox" name="entry" value="${trayEntry.fullEntryName};;${peggedEntry.pegRevision}"></td>
      <c:choose>
        <c:when test="${'dir' eq trayEntry.kind}">
          <td>
            <img src="images/icon_folder.png" alt="dir">
          </td>
          <td>
            <a href="<sventon-ui:formatUrl url='${entryTrayViewUrl}'/>">${trayEntry.name}</a>&nbsp;
        </c:when>
        <c:otherwise>
          <td>
            <sventon-ui:fileTypeIcon filename="${trayEntry.name}"/>
          </td>
          <td>
            <a href="<sventon-ui:formatUrl url='${entryTrayShowFileUrl}'/>">${trayEntry.name}</a>&nbsp;
        </c:otherwise>
      </c:choose>
            (<a href="${entryTrayShowRevInfoUrl}" onmouseover="getLogMessage(${trayEntry.revision}, '${command.name}', '<fmt:formatDate type="both" value="${trayEntry.date}" dateStyle="short" timeStyle="short"/>');">${trayEntry.revision}</a>)
          </td>
      <td>
        <a href="#" onclick="removeEntryFromTray('<sventon-ui:formatUrl url='${entryTrayRemoveUrl}'/>'); return false;">
          <img align="middle" src="images/delete.png" alt="<spring:message code="delete.tooltip"/>" title="<spring:message code="delete.tooltip"/>">
        </a>
      </td>
    </tr>
    <c:set var="rowCount" value="${rowCount + 1}"/>
    </c:forEach>
    <tr>
      <%@ include file="/WEB-INF/jspf/actionSelectList.jspf" %>
      <td/>
    </tr>
  </table>
</form>