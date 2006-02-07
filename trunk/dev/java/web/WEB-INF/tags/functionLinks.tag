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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="/WEB-INF/spring.tld" %>
<%@ attribute name="pageName" required="true" type="java.lang.String" %>

<!-- Prepare link URLs -->

<c:url var="downloadLinkUrl" value="get.svn">
  <c:param name="path" value="${command.path}${entry.name}" />
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showLogLinkUrl" value="showlog.svn">
  <c:param name="path" value="${command.path}${entry.name}" />
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showLockLinkUrl" value="showlock.svn">
  <c:param name="path" value="${command.path}${entry.name}" />
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showDirLinkUrl" value="repobrowser.svn">
  <c:param name="path" value="${command.path}" />
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showFileLinkUrl" value="showfile.svn">
  <c:param name="path" value="${command.path}${entry.name}" />
  <c:param name="revision" value="${command.revision}" />
</c:url>

<form name="searchForm" action="search.svn" method="get" onsubmit="return doSearch(searchForm);">
<table class="sventonFunctionLinksTable" border="0">
  <tr><td>

<c:choose>
  <c:when test="${pageName == 'showFile'}">
    <input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" title="<spring:message code="showlog.button.tooltip" arguments="${command.target}"/>" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
    <input type="button" class="btn" value="<spring:message code="download.button.text"/>" onclick="javascript:parent.location='${downloadLinkUrl}';"/>

    <c:if test="${!empty committedRevision && !isBinary}">
      <c:url value="diffprev.svn" var="diffPreviousUrl">
        <c:param name="path" value="${command.path}${entry.name}" />
        <c:param name="revision" value="${command.revision}" />
        <c:param name="commitrev" value="${committedRevision}" />
      </c:url>
      <input type="button" class="btn" value="<spring:message code="diffprev.button.text"/>" title="<spring:message code="diffprev.button.tooltip" arguments="${committedRevision}"/>" onclick="javascript:parent.location='${diffPreviousUrl}';"/>
    </c:if>
  </c:when>

  <c:when test="${pageName == 'repobrowse'}">
    <input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" title="<spring:message code="showlog.button.tooltip" arguments="${command.target}"/>" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
    <input type="button" class="btn" value="<spring:message code="showlocks.button.text"/>" onclick="javascript:parent.location='${showLockLinkUrl}';"/>
    <input type="button" class="btn" value="<spring:message code="flatten.button.text"/>" onclick="javascript:return doFlatten('${command.path}');" ${isIndexing ? 'disabled title=Index is being updated!' : 'title="Flatten directory structure from current location"'} />

  </c:when>

  <c:when test="${pageName == 'showLog'}">
    <c:choose>
      <c:when test="${isFile}">
        <input type="button" class="btn" value="<spring:message code="showfile.button.text"/>" onclick="javascript:parent.location='${showFileLinkUrl}';"/>
        <input type="button" class="btn" value="<spring:message code="download.button.text"/>" onclick="javascript:parent.location='${downloadLinkUrl}';"/>
      </c:when>
      <c:otherwise>
        <input type="button" class="btn" value="<spring:message code="showdir.button.text"/>" onclick="javascript:parent.location='${showDirLinkUrl}';"/>
      </c:otherwise>
    </c:choose>
  </c:when>

  <c:when test="${pageName == 'showRevInfo'}">
    <input type="button" class="btn" value="<spring:message code="showdir.button.text"/>" onclick="javascript:parent.location='${showDirLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showDiff'}">
    <input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" title="<spring:message code="showlog.button.tooltip" arguments="${command.target}"/>" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
    <input type="button" class="btn" value="<spring:message code="showfile.button.text"/>" onclick="javascript:parent.location='${showFileLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showBlame'}">
    <input type="button" class="btn" value="<spring:message code="showfile.button.text"/>" onclick="javascript:parent.location='${showFileLinkUrl}';"/>
    <input type="button" class="btn" value="<spring:message code="download.button.text"/>" onclick="javascript:parent.location='${downloadLinkUrl}';"/>
    <input type="button" class="btn" value="<spring:message code="showlog.button.text"/>" title="<spring:message code="showlog.button.tooltip" arguments="${command.target}"/>" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showLock'}">
    <input type="button" class="btn" value="<spring:message code="showdir.button.text"/>" onclick="javascript:parent.location='${showDirLinkUrl}';"/>
  </c:when>

  <c:otherwise>
    <spring:message code="functionlinks.error.message" arguments="${pagename}"/>
  </c:otherwise>
</c:choose>

    </td>
    <td align="right" style="white-space: nowrap;"><spring:message code="search.text"/>
      <input type="text" name="searchString" class="sventonSearchField" value="" ${isIndexing ? 'disabled' : ''} />
      <input type="hidden" name="startDir" value="${command.pathPart}"/>
      <c:choose>
        <c:when test="${isIndexing}">
          <input type="submit" value="go!" disabled title="<spring:message code="search.button.disabled.tooltip"/>" class="btn"/>
        </c:when>
        <c:otherwise>
          <input type="submit" value="go!" title="<spring:message code="search.button.tooltip"/>" class="btn"/>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
</table>
  <!-- Needed by ASVNTC -->
  <input type="hidden" name="path" value="${command.path}${entry.name}"/>
  <input type="hidden" name="revision" value="${command.revision}"/>
</form>
