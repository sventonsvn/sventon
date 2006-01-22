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
    <input type="button" class="btn" value="Show log" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
    <input type="button" class="btn" value="Download" onclick="javascript:parent.location='${downloadLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'repobrowse'}">
    <input type="button" class="btn" value="Flatten dir" onclick="javascript:return doFlatten('${command.path}');"/>
    <input type="button" class="btn" value="Show log" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
    <input type="button" class="btn" value="Show locks" onclick="javascript:parent.location='${showLockLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showLog'}">
    <c:choose>
      <c:when test="${isFile}">
        <input type="button" class="btn" value="Show file" onclick="javascript:parent.location='${showFileLinkUrl}';"/>
      </c:when>
      <c:otherwise>
        <input type="button" class="btn" value="Show directory" onclick="javascript:parent.location='${showDirLinkUrl}';"/>
      </c:otherwise>
    </c:choose>
    <input type="button" class="btn" value="Download" onclick="javascript:parent.location='${downloadLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showRevInfo'}">
    <input type="button" class="btn" value="Show directory" onclick="javascript:parent.location='${showDirLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showDiff'}">
    <input type="button" class="btn" value="Show log" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
    <input type="button" class="btn" value="Show file" onclick="javascript:parent.location='${showFileLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showBlame'}">
    <input type="button" class="btn" value="Show file" onclick="javascript:parent.location='${showFileLinkUrl}';"/>
    <input type="button" class="btn" value="Download" onclick="javascript:parent.location='${downloadLinkUrl}';"/>
    <input type="button" class="btn" value="Show log" onclick="javascript:parent.location='${showLogLinkUrl}';"/>
  </c:when>

  <c:when test="${pageName == 'showLock'}">
    <input type="button" class="btn" value="Show directory" onclick="javascript:parent.location='${showDirLinkUrl}';"/>
  </c:when>

  <c:otherwise>
No function link style for pagename ${pagename}
  </c:otherwise>
</c:choose>

    </td>
    <td align="right" style="white-space: nowrap;">Search current directory and below <input type="text" name="sventonSearchString" class="sventonSearchField" value=""/><input type="submit" value="go!" class="btn"/><input type="hidden" name="startDir" value="${command.pathPart}"/></td>
  </tr>
</table>
  <!-- Needed by ASVNTC -->
  <input type="hidden" name="path" value="${command.path}${entry.name}"/>
  <input type="hidden" name="revision" value="${command.revision}"/>
</form>
