<%
/*
 * ====================================================================
 * Copyright (c) 2005 Sventon Project. All rights reserved.
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
<c:url var="showDirLinkUrl" value="repobrowser.svn">
  <c:param name="path" value="${command.path}" />
  <c:param name="revision" value="${command.revision}" />
</c:url>
<c:url var="showFileLinkUrl" value="showfile.svn">
  <c:param name="path" value="${command.path}${entry.name}" />
  <c:param name="revision" value="${command.revision}" />
</c:url>

<c:choose>
  <c:when test="${pageName == 'showFile'}">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td><a href="${showLogLinkUrl}">[Show log]</a></td>
        <td><a href="${downloadLinkUrl}">[Download]</a></td>
      </tr>
    </table>
  </c:when>

  <c:when test="${pageName == 'showLog'}">
    <table class="sventonFunctionLinksTable">
      <tr>
      <c:choose>
      <c:when test="${isFile}">
        <td><a href="${showFileLinkUrl}">[Show file]</a></td>
        </c:when>
        <c:otherwise>
        <td><a href="${showDirLinkUrl}">[Show directory]</a></td>
        </c:otherwise>
        </c:choose>
        <td><a href="${downloadLinkUrl}">[Download]</a></td>
      </tr>
    </table>
  </c:when>

  <c:when test="${pageName == 'showRevInfo'}">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td><a href="${showDirLinkUrl}">[Show directory]</a></td>
      </tr>
    </table>
  </c:when>

  <c:when test="${pageName == 'showDiff'}">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td><a href="${showLogLinkUrl}">[Show log]</a></td>
      </tr>
    </table>
  </c:when>

  <c:when test="${pageName == 'showBlame'}">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td><a href="${showFileLinkUrl}">[Show file]</a></td>
        <td><a href="${downloadLinkUrl}">[Download]</a></td>
        <td><a href="${showLogLinkUrl}">[Show log]</a></td>
      </tr>
    </table>
  </c:when>

  <c:otherwise>
No function link style for pagename ${pagename}
  </c:otherwise>
</c:choose>


