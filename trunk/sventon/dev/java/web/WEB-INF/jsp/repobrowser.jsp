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
<%@ page import="de.berlios.sventon.util.ByteFormatter"%>
<%@ page import="de.berlios.sventon.repository.RepositoryEntry"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/head.jspf"%>
    <title>sventon repository browser - ${url}</title>
  </head>
<body>
  <%@ include file="/WEB-INF/jspf/top.jspf"%>

  <p>
    <table class="sventonHeader"><tr><td>

  <c:choose>
    <c:when test="${isLogSearch}">Search result for - <b>${searchString}</b></c:when>
    <c:when test="${isEntrySearch}">Search result for - <b>${searchString}</b> (directory '${startDir}' and below)</c:when>
    <c:when test="${isFlatten}">Flattened structure - <b>${command.target}</b> (and below)</c:when>
    <c:otherwise>Repository Browser - <b>${command.target}</b></c:otherwise>
  </c:choose>

  <c:if test="${!empty properties}">
    <a class="sventonHeader" href="javascript:toggleElementVisibility('propertiesDiv'); changeHideShowDisplay('propertiesLink');">[<span id="propertiesLink">show</span> properties]</a>
  </c:if>
    </td></tr></table>
    <%@ include file="/WEB-INF/jspf/sventonheader.jspf"%>
  </p>

  <br/>
  <ui:functionLinks pageName="repobrowse"/> 

  <c:choose>
    <c:when test="${!isLogSearch}">
      <div id="entriesDiv" class="sventonEntriesDiv">
        <form method="post" action="#" name="entriesForm" onsubmit="return doAction(entriesForm);">
      <!-- Needed by ASVNTC -->
      <input type="hidden" name="path" value="${command.path}"/>
      <input type="hidden" name="revision" value="${command.revision}"/>

      <table class="sventonEntriesTable">
        <tr>
          <th></th>
          <th></th>

          <c:choose>
            <c:when test="${isEntrySearch}">
              <c:url value="searchentries.svn" var="sortUrl">
                <c:param name="path" value="${command.path}" />
                <c:param name="revision" value="${command.revision}" />
                <c:param name="searchString" value="${searchString}" />
                <c:param name="startDir" value="${startDir}" />
              </c:url>
            </c:when>
            <c:when test="${isFlatten}">
              <c:url value="flatten.svn" var="sortUrl">
                <c:param name="path" value="${command.path}" />
                <c:param name="revision" value="${command.revision}" />
              </c:url>
            </c:when>
            <c:otherwise>
              <c:url value="repobrowser.svn" var="sortUrl">
                <c:param name="path" value="${command.path}" />
                <c:param name="revision" value="${command.revision}" />
              </c:url>
            </c:otherwise>
          </c:choose>

          <c:set var="sortModeArrow" value="${command.sortMode eq 'ASC' ? '&uArr;' : '&dArr;'}"/>
          <c:set var="opositeSortType" value="${command.sortMode eq 'ASC' ? 'DESC' : 'ASC'}"/>

          <th><a href="${sortUrl}&sortType=NAME&sortMode=${opositeSortType}" style="color: #000000;">
              <c:if test="${command.sortType eq 'NAME'}">${sortModeArrow}</c:if>File</a>
          </th>
          <th></th>
          <th><a href="${sortUrl}&sortType=SIZE&sortMode=${opositeSortType}" style="color: #000000;">
            <c:if test="${command.sortType eq 'SIZE'}">${sortModeArrow}</c:if>Size (bytes)</a>
          </th>
          <th><a href="${sortUrl}&sortType=REVISION&sortMode=${opositeSortType}" style="color: #000000;">
            <c:if test="${command.sortType eq 'REVISION'}">${sortModeArrow}</c:if>Revision</a>
          </th>
          <th><a href="${sortUrl}&sortType=AUTHOR&sortMode=${opositeSortType}" style="color: #000000;">
            <c:if test="${command.sortType eq 'AUTHOR'}">${sortModeArrow}</c:if>Author</a>
          </th>
          <th><a href="${sortUrl}&sortType=DATE&sortMode=${opositeSortType}" style="color: #000000;">
            <c:if test="${command.sortType eq 'DATE'}">${sortModeArrow}</c:if>Date</a>
          </th>
        </tr>
    <%
          int rowCount = 0;
          long totalSize = 0;
    %>

        <c:if test="${!empty command.pathNoLeaf && !isEntrySearch && !isLogSearch && !isFlatten}">
          <c:url value="repobrowser.svn" var="backUrl">
            <c:param name="path" value="${command.pathNoLeaf}" />
          </c:url>

          <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntryEven"); else out.print("sventonEntryOdd");%>">
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
          <jsp:useBean id="entry" type="RepositoryEntry" />
          <c:url value="repobrowser.svn" var="viewUrl">
            <c:param name="path" value="${entry.fullEntryName}" />
            <c:param name="revision" value="${command.revision}" />
          </c:url>
          <c:url value="showfile.svn" var="showFileUrl">
            <c:param name="path" value="${entry.fullEntryName}" />
            <c:param name="revision" value="${command.revision}" />
          </c:url>
          <c:url value="revinfo.svn" var="showRevInfoUrl">
            <c:param name="revision" value="${entry.revision}" />
          </c:url>

          <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntryEven"); else out.print("sventonEntryOdd");%>">
      <%
            totalSize += entry.getSize();
      %>
            <td class="sventonCol1"><input type="checkbox" name="entry" value="${entry.fullEntryName}"/></td>
        <% if (RepositoryEntry.Kind.dir == entry.getKind()) { %>
            <td class="sventonCol2"><img src="images/icon_dir.gif" alt="dir" /></td>
            <td class="sventonCol3">
              <c:choose>
                <c:when test="${isLogSearch || isEntrySearch || isFlatten}">
                  <a href="${viewUrl}" onmouseover="this.T_WIDTH=1;return escape('<table><tr><td style=\'white-space: nowrap\'>${entry.fullEntryName}</td></tr></table>')">${entry.friendlyFullEntryName}</a>
                </c:when>
                <c:otherwise>
                  <a href="${viewUrl}">${entry.name}</a>
                </c:otherwise>
              </c:choose>
            </td>
        <% } else { %>
            <td class="sventonCol2"><img src="images/icon_file.gif" alt="file" /></td>
            <td class="sventonCol3">
              <c:choose>
                <c:when test="${isLogSearch || isEntrySearch || isFlatten}">
                  <a href="${showFileUrl}" onmouseover="this.T_WIDTH=1;return escape('<table><tr><td style=\'white-space: nowrap\'>${entry.fullEntryName}</td></tr></table>')">
                ${entry.friendlyFullEntryName}
                  </a>
                </c:when>
                <c:otherwise>
                  <a href="${showFileUrl}">${entry.name}</a>
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
            <td class="sventonCol6"><a href="${showRevInfoUrl}" onmouseover="this.T_WIDTH=1;return escape('<spring:message code="showrevinfo.link.tooltip"/>')">${entry.revision}</a></td>
            <td class="sventonCol7">${entry.author}</td>
            <td class="sventonCol8"><fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/></td>
          </tr>
      <% rowCount++; %>
        </c:forEach>

        <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntryEven"); else out.print("sventonEntryOdd");%>">
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
              <option value="diff">&nbsp;&nbsp;Diff entries</option>
              <c:if test="${isZipDownloadsAllowed}">
                <option value="zip">&nbsp;&nbsp;Download as zip</option>
              </c:if>
              <option value="thumb">&nbsp;&nbsp;Show as thumbnails</option>
            </select><input type="submit" class="btn" value="go!"/>
          </td>
          <td colspan="5"></td>
        </tr>
      </table>
    </form>
      </div>
    </c:when>
    <c:otherwise>
      <div id="logMessagesDiv" class="sventonEntriesDiv">
        <table class="sventonEntriesTable">
          <tr>
            <th>Revision</th>
            <th>Log Message</th>
          </tr>
          <%
            int hitCount = 0;
          %>
          <c:forEach items="${logMessages}" var="logMessage">
            <c:url value="revinfo.svn" var="showRevInfoUrl">
              <c:param name="revision" value="${logMessage.revision}" />
            </c:url>
            <tr class="<%if (hitCount % 2 == 0) out.print("sventonEntryEven"); else out.print("sventonEntryOdd");%>">
              <td><a href="${showRevInfoUrl}" onmouseover="this.T_WIDTH=1;return escape('<spring:message code="showrevinfo.link.tooltip"/>')">${logMessage.revision}</a></td>
              <td>${logMessage.message}</td>
            </tr>
            <% hitCount++; %>
          </c:forEach>
          <tr class="<%if (hitCount % 2 == 0) out.print("sventonEntryEven"); else out.print("sventonEntryOdd");%>">
            <td><b>Total: <%=hitCount%> hits</b></td>
            <td></td>
          </tr>
        </table>
      </div>
    </c:otherwise>
  </c:choose>

<br>
<script language="JavaScript" type="text/javascript" src="wz_tooltip.js"></script>
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
</body>
</html>
