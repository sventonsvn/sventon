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
<%@ page import="de.berlios.sventon.svnsupport.LogEntryActionType"%>
<%@ page import="org.tmatesoft.svn.core.SVNLogEntryPath"%>
<%@ page import="org.tmatesoft.svn.core.SVNLogEntry"%>
<%@ page import="java.util.*"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/sventonbar.jsp"%>
<spring:hasBindErrors name="command"><c:set var="hasErrors" scope="page" value="true"/></spring:hasBindErrors>

<table class="sventonTopTable" border="0">
  <form name="searchForm" action="search.svn" method="get" onsubmit="return doSearch(searchForm);">
    <tr>
      <c:choose>
        <c:when test="${!empty numrevision}">
          <td class="sventonHeadlines">
            Revision: ${command.revision} (${numrevision})
          </td>
        </c:when>
        <c:otherwise>
          <td class="sventonHeadlines" style="color: #ff0000">
            Revision: ${command.revision}
          </td>
        </c:otherwise>
      </c:choose>

      <td align="right" style="white-space: nowrap;">Search current directory and below <input type="text" name="sventonSearchString" class="sventonSearchField" value=""/><input type="submit" value="go!"/><input type="hidden" name="startDir" value="${command.path}"/></td>
    </tr>
    <tr>
      <td><a href="javascript:toggleElementVisibility('latestCommitInfoDiv'); changeHideShowDisplay('latestCommitLink');">[<span id="latestCommitLink">show</span> latest commit info]</a></td>
    </tr>
    <tr>
      <td style="white-space: nowrap;">
        <c:if test="${!empty latestCommitInfo}">
          <div id="latestCommitInfoDiv" style="display:none">
            <table class="sventonLatestCommitInfoTable">
              <tr><td><b>User:</b></td><td>${latestCommitInfo.author}</td></tr>
              <tr><td><b>Date:</b></td><td><fmt:formatDate type="both" value="${latestCommitInfo.date}" dateStyle="short" timeStyle="short"/></td></tr>
              <tr><td><b>Message:</b></td><td>${latestCommitInfo.message}</td></tr>
              <tr><td><b>Revision:</b></td><td>${latestCommitInfo.revision}</td></tr>
              <tr><td colspan="2" valign="top"><b>Changed paths:</b></td></tr>
              <c:set var="latestChangedPaths" value="${latestCommitInfo.changedPaths}" />
              <jsp:useBean id="latestChangedPaths" type="java.util.Map" />
              <c:set var="latestCommitInfo" value="${latestCommitInfo}" />
              <jsp:useBean id="latestCommitInfo" type="SVNLogEntry" />
  
              <tr><td colspan="2">
                  <table border="0">
                    <tr>
                      <th>Action</th>
                      <th>Path</th>
                      <th>Copy From Path</th>
                      <th>Revision</th>
                    </tr>
                  <%
                    List latestPathsList = new ArrayList(latestChangedPaths.keySet());
                    Collections.sort(latestPathsList);
                    Iterator latestLogIterator = latestPathsList.iterator();
                    while (latestLogIterator.hasNext()) {
                      SVNLogEntryPath logEntryPath = (SVNLogEntryPath) latestChangedPaths.get(latestLogIterator.next());
                      LogEntryActionType actionType = LogEntryActionType.valueOf(String.valueOf(logEntryPath.getType()));
                  %>
                  <tr>
                    <c:url value="goto.svn" var="goToUrl">
                      <c:param name="path" value="<%= logEntryPath.getPath() %>" />
                      <c:param name="revision" value="head" />
                    </c:url>
  
                    <c:url value="diff.svn" var="diffUrl">
                      <c:param name="path" value="<%= logEntryPath.getPath() %>" />
                      <c:param name="revision" value="head" />
                    </c:url>
  
                    <td><i><%= actionType %></i></td>
                    <% if (LogEntryActionType.A == actionType || LogEntryActionType.R == actionType) { %>
                    <td><a href="${goToUrl}" title="Show file"><%= logEntryPath.getPath() %></a></td>
                    <% } else if (LogEntryActionType.M == actionType) { %>
                    <td><a href="${diffUrl}&rev=<%= logEntryPath.getPath() %>;;<%= latestCommitInfo.getRevision() %>&rev=<%= logEntryPath.getPath() %>;;<%= latestCommitInfo.getRevision() - 1 %>" title="Diff with previous version"><%= logEntryPath.getPath() %></a></td>
                    <% } else { %>
                    <td><%= logEntryPath.getPath() %></td>
                    <% } %>
                    <td><%= logEntryPath.getCopyPath() == null ? "" : logEntryPath.getCopyPath() %></td>
                    <td><%= logEntryPath.getCopyPath() == null ? "" : Long.toString(logEntryPath.getCopyRevision()) %></td>
                  </tr>
                  <%
                    }
                  %>
                  </table>
                </td>
              </tr>
            </table>
          </div>
        </c:if>
      </td>
    </tr>
    <c:set var="command" value="${command}"/>
    <jsp:useBean id="command" type="de.berlios.sventon.command.SVNBaseCommand" />
    <tr>
      <td class="sventonHeadlines" colspan="2">
       Repository path:<br/><a href="repobrowser.svn?path=/&revision=${command.revision}">
        ${url} <% if (!"".equals(command.getMountPoint(false))) { %>/ <%= command.getMountPoint(true) %><% } %></a> /
        <c:forTokens items="${command.pathPart}" delims="/" var="pathSegment">
          <c:set var="accuPath" scope="page" value="${accuPath}${pathSegment}/"/>
          <c:choose>
            <c:when test="${hasErrors}">
              ${pathSegment}
            </c:when>
            <c:otherwise>
          <a href="repobrowser.svn?path=/${accuPath}&revision=${command.revision}">${pathSegment}</a>
        </c:otherwise>
          </c:choose>
           /
        </c:forTokens>
        ${command.target}
      </td>
    </tr>
    <!-- Needed by ASVNTC -->
    <input type="hidden" name="path" value="${command.path}${entry.name}"/>
    <input type="hidden" name="revision" value="${command.revision}"/>
  </form>
</table>

<spring:hasBindErrors name="command">
  <table class="sventonSpringErrorMessageTable">
    <tr><td><font color="#FF0000"><spring:message code="${errors.globalError.code}" text="${errors.globalError.defaultMessage}"/></font></td></tr>
  </table>
</spring:hasBindErrors>

<form name="gotoForm" method="post" action="repobrowser.svn">
<table class="sventonRepositoryFunctionsTable">
<tr>
<td><font color="#FF0000"><spring:bind path="command.revision">${status.errorMessage}</spring:bind></font></td>
<td><font color="#FF0000"><spring:bind path="command.path">${status.errorMessage}</spring:bind></font></td>
</tr>
 <tr>
 <td>Go to revision</td><td colspan="2">Go to path <% if (!"".equals(command.getMountPoint(false))) { %>(from: <%= command.getMountPoint(false) %>)<% } %></td>
 </tr>
<tr>
<td><spring:bind path="command.revision"><input class="sventonRevision" type="text" name="revision" value="${status.value}"/></spring:bind></td>
<td><spring:bind path="command.path"><input class="sventonGoTo" id="goToPath" type="text" name="path" value="${status.value}" /></spring:bind></td>
<td><input class="sventonGoToSubmit" type="submit" value="go to"/></td>
<td><input class="sventonFlattenSubmit" type="button" value="flatten dirs" onclick="javascript: return doFlatten();"/></td>

</tr>
</table>
</form>


