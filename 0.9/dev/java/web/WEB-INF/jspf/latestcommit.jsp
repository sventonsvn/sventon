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
