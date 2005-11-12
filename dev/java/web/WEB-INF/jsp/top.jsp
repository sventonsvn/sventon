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
      <td class="sventonHeadlines">
        Revision: <c:out value="${command.revision}" /> <c:if test="${!empty numrevision}">(<c:out value="${numrevision}"/>)</c:if>
      </td>
      <td align="right">Search:<input type="text" name="sventonSearchString" class="sventonSearchField" value=""/>
        <input type="submit" name="sventonSearchButton" value="go!"/>
      </td>
    </tr>
    <tr>
      <td><a href="javascript:toggleElementVisibility('latestCommitInfoDiv'); changeHideShowDisplay('latestCommitLink');">[<span id="latestCommitLink">show</span> latest commit info]</a></td>
    </tr>
    <tr>
      <td>
        <div id="latestCommitInfoDiv" style="display:none">
          <table class="sventonLatestCommitInfoTable">
            <tr><td><b>User:</b></td><td>${latestCommitInfo.author}</td></tr>
            <tr><td><b>Date:</b></td><td><fmt:formatDate type="both" value="${latestCommitInfo.date}" dateStyle="short" timeStyle="short"/></td></tr>
            <tr><td><b>Message:</b></td><td>${latestCommitInfo.message}</td></tr>
            <tr><td><b>Revision:</b></td><td>${latestCommitInfo.revision}</td></tr>
            <tr><td colspan="2" valign="top"><b>Changed paths:</b></td></tr>
              <c:set var="latestChangedPaths" value="${latestCommitInfo.changedPaths}" />
              <jsp:useBean id="latestChangedPaths" type="java.util.Map" />
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
                  <c:param name="revision" value="${entry.svnLogEntry.revision}" />
                </c:url>
                  <td><i><%= actionType.getDescription() %></i></td>
                  <% if (LogEntryActionType.D != actionType) { %>
                  <td><a href="${goToUrl}"><%= logEntryPath.getPath() %></a></td>
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
      </td>
    </tr>
    <c:set var="command" value="${command}"/>
    <jsp:useBean id="command" type="de.berlios.sventon.command.SVNBaseCommand" />
    <tr>
      <td class="sventonHeadlines" colspan="2">
       Repository path: <a href="<c:out value="repobrowser.svn?path=/&revision=${command.revision}"/>">
        <c:out value="${url}"/> <% if (!"".equals(command.getMountPoint(false))) { %>/ <%= command.getMountPoint(true) %><% } %></a> /
        <c:forTokens items="${command.pathPart}" delims="/" var="pathSegment">
          <c:set var="accuPath" scope="page" value="${accuPath}${pathSegment}/"/>
          <c:choose>
            <c:when test="${hasErrors}">
              <c:out value="${pathSegment}"/>
            </c:when>
            <c:otherwise>
          <a href="<c:out value="repobrowser.svn?path=/${accuPath}&revision=${command.revision}"/>"><c:out value="${pathSegment}"/></a>
        </c:otherwise>
          </c:choose>
           /
        </c:forTokens>
        <c:out value="${command.target}"/>
      </td>
    </tr>
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
<td><font color="#FF0000"><spring:bind path="command.revision"><c:out value="${status.errorMessage}" /></spring:bind></font></td>
<td><font color="#FF0000"><spring:bind path="command.path"><c:out value="${status.errorMessage}" /></spring:bind></font></td>
</tr>
 <tr>
 <td>Go to Revision</td><td colspan="2">Go to path <% if (!"".equals(command.getMountPoint(false))) { %>(from: <%= command.getMountPoint(false) %>)<% } %></td>
 </tr>
<tr>
<td><spring:bind path="command.revision"><input class="sventonRevision" type="text" name="revision" value="<c:out value="${status.value}"/>"/></spring:bind></td>
<td><spring:bind path="command.path"><input class="sventonGoTo" type="text" name="path" value="<c:out value="${status.value}"/>" /></spring:bind></td>
<td><input class="sventonGoToSubmit" type="submit" value="go to"/></td>
<td><input class="sventonFlattenSubmit" type="button" value="flatten dirs" onclick="javascript:location.href='flatten.svn?path=${command.path}';"/></td>

</tr>
</table>
</form>


