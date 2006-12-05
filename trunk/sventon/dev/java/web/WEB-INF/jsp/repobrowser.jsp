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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>
<%@ page import="de.berlios.sventon.util.ByteFormatter"%>
<%@ page import="de.berlios.sventon.repository.RepositoryEntry"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
    <title>sventon repository browser - ${url}</title>
  </head>
<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <p><sventon:currentTargetHeader title="Repository Browser" target="${command.target}" hasProperties="true"/></p>

  <sventon:functionLinks pageName="repobrowser"/>

  <div id="entriesDiv" class="sventonEntriesDiv">
    <form method="post" action="#" name="entriesForm" onsubmit="return doAction(entriesForm);">
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
    <%
          long totalSize = 0;
    %>

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

          <%
            totalSize += entry.getSize();
          %>

        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <td class="sventonCol1">
            <input type="checkbox" name="entry" value="${entry.fullEntryName}"/>
          </td>

        <% if (RepositoryEntry.Kind.dir == entry.getKind()) { %>
            <td class="sventonCol2">
              <img src="images/icon_folder.png" alt="dir"/>
            </td>
            <td class="sventonCol3">
              <a href="${viewUrl}">${entry.name}</a>
            </td>
        <% } else { %>
            <td class="sventonCol2">
              <img src="images/icon_file.png" alt="file"/>
            </td>
            <td class="sventonCol3">
              <a href="${showFileUrl}">${entry.name}</a>
            </td>
        <% } %>
            <td class="sventonCol4" align="center">
              <c:set var="lock" value="${locks[entry.fullEntryName]}" scope="page"/>
              <c:if test="${!empty lock}">
                <span onmouseover="this.T_WIDTH=1;return escape('<table><tr><td><b>Owner</b></td><td>${lock.owner}</td></tr><tr><td><b>Comment</b></td><td style=\'white-space: nowrap\'>${lock.comment}</td></tr><tr><td><b>Created</b></td><td style=\'white-space: nowrap\'><fmt:formatDate type="both" value="${lock.creationDate}" dateStyle="short" timeStyle="short"/></td></tr><tr><td><b>Expires</b></td><td style=\'white-space: nowrap\'><fmt:formatDate type="both" value="${lock.expirationDate}" dateStyle="short" timeStyle="short"/></td></tr></table>')"><img src="images/icon_lock.png"></span>
              </c:if>
            </td>
            <td class="sventonCol5"><% if (RepositoryEntry.Kind.file == entry.getKind()) { %>${entry.size}<% } %></td>
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
          <c:set var="rowCount" value="${rowCount + 1}"/>
        </c:forEach>

        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <td colspan="2" align="right"><b>Total:</b></td>
          <td><b>${rowCount} entries</b></td>
          <td></td>
          <td align="right" title="<%=totalSize%>&nbsp;bytes"><b><%if (totalSize != 0) out.print(ByteFormatter.format(totalSize, request.getLocale()));%></b></td>
          <td></td>
          <td></td>
          <td></td>
        </tr>

        <tr>
          <td colspan="2"><input type="button" class="btn" name="toggleButton" value="toggle" onClick="javascript:toggleEntryFields(this.form)"/></td>
          <td>
            <%@ include file="/WEB-INF/jspf/actionSelectList.jspf"%><input type="submit" class="btn" value="go!"/>
          </td>
          <td colspan="5"></td>
        </tr>
      </table>
    </form>
  </div>


<br>
<script language="JavaScript" type="text/javascript" src="js/wz_tooltip.js"></script>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
