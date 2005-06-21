<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="org.tmatesoft.svn.core.io.*" %>

<html>
<head>
<title>sventon repository browser - <c:out value="${url}"/></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body onLoad="document.gotoForm.path.focus();">
<%@ include file="/WEB-INF/jsp/top.jsp"%>

<div id="entriesDiv" class="sventonEntriesDiv">
<form name="entriesForm">
  <table class="sventonEntriesTable">
    <tr>
      <th></th>
      <th></th>
      <th>File</th>
      <th>Size (bytes)</th>
      <th>Last changed rev</th>
      <th>Last changed by</th>
      <th>Date</th>
      <th>Options</th>
    </tr>
    <% int rowCount = 0; %>
    <c:forEach items="${svndir}" var="entry">
    <jsp:useBean id="entry" type="org.tmatesoft.svn.core.io.SVNDirEntry" />
      <c:url value="repobrowser.svn" var="viewUrl">
        <c:param name="path" value="${command.path}${entry.name}" />
      </c:url>
      <c:url value="showlog.svn" var="showLogUrl">
        <c:param name="path" value="${command.path}${entry.name}" />
      </c:url>
      <c:url value="showfile.svn" var="showFileUrl">
        <c:param name="path" value="${command.path}${entry.name}" />
      </c:url>

      <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <%
      SVNDirEntry type = entry;
      SVNNodeKind nodeKind = type.getKind();
      long entrySize = type.size();
      %>
        <td class="sventonCol1"><input type="checkbox" name="entry" value="<c:out value="${entry.name}" />"/></td>
        <% if (nodeKind == SVNNodeKind.DIR) { %>
        <td class="sventonCol2"><img src="images/icon_dir.gif"/></td>
        <td class="sventonCol3"><a href="<c:out value="${viewUrl}/&revision=${command.revision}"/>"><c:out
          value="${entry.name}" /></a></td>
        <% } else { %>
        <td class="sventonCol2"><img src="images/icon_file.gif"/></td>
        <td class="sventonCol3"><a href="<c:out value="${showFileUrl}&revision=${command.revision}"/>"><c:out value="${entry.name}"/></a></td>
        <% } %>
        <td class="sventonCol4"><% if (nodeKind == SVNNodeKind.FILE) { %><%= entrySize %><% } %></td>
        <td class="sventonCol5"><c:out value="${entry.revision}" /></td>
        <td class="sventonCol6"><c:out value="${entry.author}" /></td>
        <td class="sventonCol7"><c:out value="${entry.date}" /></td>
        <td class="sventonCol8"><a href="<c:out value="${showLogUrl}&revision=${command.revision}"/>">[Show log]</a></td>
      </tr>
      <% rowCount++; %>
    </c:forEach>

    <tr>
      <td><input type="button" name="toggleButton" value="toggle" onClick="javascript:toggleEntryFields(this.form)"/></td>
      <td colspan="2">

        <!--Just testing, move this to spring/jstl later -->
        <select class="sventonSelect">
          <option class="sventonSelectOption">Actions...</option>
          <option class="sventonSelectOption" disabled="disabled">Download as..</option>
          <option value="zip">&nbsp;&nbsp;&nbsp;Zip file</option>
        </select>

      </td>
      <td colspan="5">
        <input type="button" name="actionButton" value="go!" onClick="javascript:alert('Not implemented.');"/>
      </td>
    </tr>
  </table>
</form>
</div>
<br>
</body>
</html>
