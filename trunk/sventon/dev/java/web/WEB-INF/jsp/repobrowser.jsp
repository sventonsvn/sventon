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
<%@ include file="/WEB-INF/jspf/include.jspf"%>
<%@ page import="de.berlios.sventon.util.ByteFormatter, java.util.Locale"%>
<%@ page import="de.berlios.sventon.ctrl.RepositoryEntry"%>

<html>
<head>
<title>sventon repository browser - ${url}</title>
<%@ include file="/WEB-INF/jspf/head.jspf"%>
</head>
<body>
  <%@ include file="/WEB-INF/jspf/top.jspf"%>

  <p>
    <table class="sventonHeader"><tr><td>

  <c:choose>
    <c:when test="${isSearch}">
      Search result for - <b>${searchString}</b> (directory '${startDir}' and below)
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${isFlatten}">
          Flattened structure - <b>${command.target}</b> (and below)
        </c:when>
        <c:otherwise>
          Repository Browser - <b>${command.target}</b>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>

  <c:if test="${!empty properties}">
    <a href="javascript:toggleElementVisibility('propertiesDiv'); changeHideShowDisplay('propertiesLink');">[<span id="propertiesLink">show</span> properties]</a>
  </c:if>
    </td></tr></table>
    <%@ include file="/WEB-INF/jspf/sventonheader.jspf"%>
  </p>

  <br/>
  <ui:functionLinks pageName="repobrowse"/> 

<div id="entriesDiv" class="sventonEntriesDiv">
<form method="get" action="#" name="entriesForm" onsubmit="return doAction(entriesForm);">
  <!-- Needed by ASVNTC -->
  <input type="hidden" name="path" value="${command.path}"/>
  <input type="hidden" name="revision" value="${command.revision}"/>

  <table class="sventonEntriesTable">
    <tr>
      <th></th>
      <th></th>
      <th>File</th>
      <th></th>
      <th>Size (bytes)</th>
      <th>Revision</th>
      <th>Author</th>
      <th>Date</th>
    </tr>
    <%
      int rowCount = 0;
      long totalSize = 0;
    %>

    <c:if test="${!empty command.pathNoLeaf && !isSearch && !isFlatten}">
      <c:url value="repobrowser.svn" var="backUrl">
        <c:param name="path" value="${command.pathNoLeaf}" />
      </c:url>

      <tr class="sventonEntry1">
        <td class="sventonCol1"></td>
        <td class="sventonCol2"><img src="images/icon_dir.gif" alt="dir" /></td>
        <td class="sventonCol3">
          <a href="${backUrl}">..&nbsp;&nbsp;&nbsp;</a>
        </td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <% rowCount++; %>
    </c:if>

    <c:forEach items="${svndir}" var="entry">
    <jsp:useBean id="entry" type="de.berlios.sventon.ctrl.RepositoryEntry" />
      <c:url value="repobrowser.svn" var="viewUrl">
        <c:param name="path" value="${entry.fullEntryName}" />
      </c:url>
      <c:url value="showlog.svn" var="showLogUrl">
        <c:param name="path" value="${entry.fullEntryName}" />
      </c:url>
      <c:url value="showfile.svn" var="showFileUrl">
        <c:param name="path" value="${entry.fullEntryName}" />
      </c:url>

      <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <%
        totalSize += entry.getSize();
      %>
        <td class="sventonCol1"><input type="checkbox" name="entry" value="${entry.fullEntryName}"/></td>
        <% if (RepositoryEntry.Kind.dir == entry.getKind()) { %>
        <td class="sventonCol2"><img src="images/icon_dir.gif" alt="dir" /></td>
        <td class="sventonCol3">
          <c:choose>
            <c:when test="${isSearch || isFlatten}">
              <a href="${viewUrl}/&revision=${command.revision}" onmouseover="this.T_WIDTH=1;return escape('${entry.fullEntryName}')">${entry.friendlyFullEntryName}</a>
            </c:when>
            <c:otherwise>
              <a href="${viewUrl}/&revision=${command.revision}">${entry.name}</a>
            </c:otherwise>
          </c:choose>
        </td>
        <% } else { %>
        <td class="sventonCol2"><img src="images/icon_file.gif" alt="file" /></td>
        <td class="sventonCol3">
          <c:choose>
            <c:when test="${isSearch || isFlatten}">
              <a href="${showFileUrl}&revision=${command.revision}" onmouseover="this.T_WIDTH=1;return escape('${entry.fullEntryName}')">
                ${entry.friendlyFullEntryName}
              </a>
            </c:when>
            <c:otherwise>
              <a href="${showFileUrl}&revision=${command.revision}">${entry.name}</a>
            </c:otherwise>
          </c:choose>
          </td>
        <% } %>
        <td class="sventonCol4" align="center">
          <c:if test="${!empty entry.lock}">
            <span onmouseover="this.T_WIDTH=1;return escape('<table><tr><td><b>Owner</b></td><td>${entry.lock.owner}</td></tr><tr><td><b>Comment</b></td><td style=\'white-space: nowrap\'>${entry.lock.comment}</td></tr><tr><td><b>Created</b></td><td style=\'white-space: nowrap\'><fmt:formatDate type="both" value="${entry.lock.creationDate}" dateStyle="short" timeStyle="short"/></td></tr><tr><td><b>Expires</b></td><td style=\'white-space: nowrap\'><fmt:formatDate type="both" value="${entry.lock.expirationDate}" dateStyle="short" timeStyle="short"/></td></tr></table>')"><img border="0" src="images/lock.gif"></span>
          </c:if>
        </td>
        <td class="sventonCol5"><% if (RepositoryEntry.Kind.file == entry.getKind()) { %>${entry.size}<% } %></td>
        <td class="sventonCol6">${entry.revision}</td>
        <td class="sventonCol7">${entry.author}</td>
        <td class="sventonCol8"><fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/></td>
      </tr>
      <% rowCount++; %>
    </c:forEach>

    <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <td colspan="2" align="right"><b>Total:</b></td>
      <td><b><%=rowCount%> entries</b></td>
      <td></td>
      <td align="right" title="<%=totalSize%>&nbsp;bytes"><b><%if (totalSize != 0) out.print(ByteFormatter.format(totalSize, request.getLocale()));%></b></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>

    <tr>
      <td colspan="2"><input type="button" class="btn" name="toggleButton" value="toggle" onClick="javascript:toggleEntryFields(this.form)"/></td>
      <td>
        <select class="sventonSelect" name="actionSelect">
          <option class="sventonSelectOption">Actions...</option>
          <option value="thumb">&nbsp;&nbsp;Show as thumbnails</option>
        </select><input type="submit" class="btn" value="go!"/>
      </td>
      <td colspan="5"></td>
    </tr>
  </table>
</form>
</div>
<br>
<script language="JavaScript" type="text/javascript" src="wz_tooltip.js"></script>
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
</body>
</html>
