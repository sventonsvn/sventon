<%
/*
 * ====================================================================
 * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
    <title>sventon repository browser - ${url}</title>
  </head>
<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <sventon:currentTargetHeader title="Repository Browser" target="${command.target}" hasProperties="true"/>
  <sventon:functionLinks pageName="repobrowser"/>

  <form method="post" action="#" name="entriesForm" onsubmit="return doAction(this);">
    <!-- Needed by ASVNTC -->
    <input type="hidden" name="path" value="${command.path}"/>
    <input type="hidden" name="revision" value="${command.revision}"/>
    <input type="hidden" name="name" value="${command.name}"/>

    <c:url value="repobrowser.svn" var="sortUrl">
      <c:param name="path" value="${command.path}" />
      <c:param name="revision" value="${command.revision}" />
      <c:param name="name" value="${command.name}" />
    </c:url>

    <table class="sventonEntriesTable">
      <%@ include file="/WEB-INF/jspf/sortableEntriesTableHeaderRow.jspf"%>
      <c:set var="rowCount" value="0"/>
      <c:set var="totalSize" value="0"/>

      <c:if test="${!empty command.pathNoLeaf}">
        <%@ include file="/WEB-INF/jspf/parentDirLinkTableRow.jspf"%>
        <c:set var="rowCount" value="${rowCount + 1}"/>
      </c:if>

      <c:forEach items="${svndir}" var="entry">
        <jsp:useBean id="entry" type="de.berlios.sventon.repository.RepositoryEntry" />
        <c:url value="repobrowser.svn" var="viewUrl">
          <c:param name="path" value="${entry.fullEntryName}" />
          <c:param name="revision" value="${command.revision}" />
          <c:param name="name" value="${command.name}" />
          <c:param name="bypassEmpty" value="true" />
        </c:url>
        <c:url value="showfile.svn" var="showFileUrl">
          <c:param name="path" value="${entry.fullEntryName}" />
          <c:param name="revision" value="${command.revision}" />
          <c:param name="name" value="${command.name}" />
        </c:url>
        <c:url value="revinfo.svn" var="showRevInfoUrl">
          <c:param name="revision" value="${entry.revision}" />
          <c:param name="name" value="${command.name}" />
        </c:url>
        <c:url value="entrytray.ajax" var="entryTrayAddUrl">
          <c:param name="path" value="${entry.fullEntryName}" />
          <c:param name="revision" value="${entry.revision}" />
          <c:param name="name" value="${command.name}" />
          <c:param name="action" value="add" />
        </c:url>

        <c:set var="totalSize" value="${totalSize + entry.size}"/>

        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <td class="sventonCol1">
            <input type="checkbox" name="entry" value="${entry.fullEntryName}"/>
          </td>
          <c:choose>
            <c:when test="${'dir' eq entry.kind}">
              <td class="sventonCol2">
                <div id="<sventon-ui:formatUrl url='${entryTrayAddUrl}'/>" class="entry">
                  <img src="images/icon_folder.png" alt="dir"/>
                </div>
              </td>
              <td class="sventonCol3"><a href="<sventon-ui:formatUrl url='${viewUrl}'/>">${entry.name}</a></td>
            </c:when>
            <c:otherwise>
              <td class="sventonCol2">
                <div id="${entryTrayAddUrl}" class="entry">
                  <sventon-ui:fileTypeIcon filename="${entry.name}"/>
                </div>
              </td>
              <td class="sventonCol3"><a href="<sventon-ui:formatUrl url="${showFileUrl}" />">${entry.name}</a></td>
            </c:otherwise>
          </c:choose>
          <td class="sventonCol4" align="center">
            <c:set var="lock" value="${locks[entry.fullEntryName]}" scope="page"/>
            <c:if test="${!empty lock}">
              <jsp:useBean id="lock" type="org.tmatesoft.svn.core.SVNLock" />
              <span onmouseover="Tip('<table><tr><td><b>Owner</b></td><td><%=StringEscapeUtils.escapeJavaScript(lock.getOwner())%></td></tr><tr><td><b>Comment</b></td><td style=\'white-space: nowrap\'>${lock.comment}</td></tr><tr><td><b>Created</b></td><td style=\'white-space: nowrap\'><fmt:formatDate type="both" value="${lock.creationDate}" dateStyle="short" timeStyle="short"/></td></tr><tr><td><b>Expires</b></td><td style=\'white-space: nowrap\'><fmt:formatDate type="both" value="${lock.expirationDate}" dateStyle="short" timeStyle="short"/></td></tr></table>')"><img src="images/icon_lock.png"></span>
            </c:if>
          </td>
          <td class="sventonCol5"><c:if test="${'file' eq entry.kind}">${entry.size}</c:if></td>
          <td class="sventonCol6">
            <a href="${showRevInfoUrl}" onmouseover="getLogMessage(${entry.revision}, '${command.name}', '<fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>');">
              ${entry.revision}
            </a>
          </td>
          <td class="sventonCol7">${entry.author}</td>
          <td class="sventonCol8">
            <fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>
          </td>
        </tr>
        <c:set var="rowCount" value="${rowCount + 1}"/>
      </c:forEach>

      <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
        <td colspan="2" class="sventonCol1" align="right"><b>Total:</b></td>
        <td><b>${rowCount} entries</b></td>
        <td></td>
        <td align="right" title="${totalSize} bytes"><b><sventon-ui:formatBytes size="${totalSize}" locale="${pageContext.request.locale}"/></b></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td colspan="2" class="sventonCol1">
          <input type="button" class="btn" name="toggleButton" value="toggle" onClick="toggleEntryFields(this.form)"/>
        </td>
        <td nowrap>
          <%@ include file="/WEB-INF/jspf/actionSelectList.jspf"%><input type="submit" class="btn" value="go!"/>
        </td>
        <td colspan="5"></td>
      </tr>
    </table>
  </form>

  <c:if test="${bypassed}">
    <script type="text/javascript">
      new Effect.Highlight($('clickableUrl'), {startcolor: '#ffff00', restorecolor: 'true', duration: 3});
    </script>
  </c:if>

  <div align="right" class="entryTrayContainerDiv" id="entryTrayContainer">
    <table class="entryTrayHeaderTable">
      <tr>
        <td>drag&amp;drop tray - <a class="sventonHeaderLink" href="#" onclick="toggleInnerHTML('hideShowTrayLink', '[hide]', '[show]'); showHideEntryTray(); return false;"><span id="hideShowTrayLink">[show]</span></a></td>
      </tr>
    </table>
    <div id="entryTrayWrapper" style="display: none">
      <div id="entryTray">
        <%@ include file="/WEB-INF/jsp/ajax/entryTray.jsp"%>
      </div>
    </div>
  </div>

  <script type="text/javascript">
    var entries = document.getElementsByClassName('entry');
    for (var i = 0; i < entries.length; i++) {
      new Draggable(entries[i].id, {revert:true})
    }
    Droppables.add('entryTrayContainer', {onDrop:addEntryToTray})
  </script>

  <%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
  <%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
