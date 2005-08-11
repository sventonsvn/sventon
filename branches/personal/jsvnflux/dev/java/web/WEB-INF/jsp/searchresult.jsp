<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="org.tmatesoft.svn.core.*"%>
<%@ page import="de.berlios.sventon.util.ByteFormatter"%>

<html>
<head>
<title>sventon repository browser - <c:out value="${url}" /></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
<%@ include file="/WEB-INF/jsp/top.jsp"%>

  <p class="sventonHeader">
  Repository Browser - <c:out value="${command.target}"/>
  </p>

<div id="entriesDiv" class="sventonEntriesDiv">
<form method="post" name="entriesForm" onsubmit="return doAction(entriesForm);">
  <table class="sventonEntriesTable">
    <tr>
      <th></th>
      <th></th>
      <th>File</th>
      <th>Size (bytes)</th>
      <th>Revision</th>
      <th>Author</th>
      <th>Date</th>
      <th colspan="2">Options</th>
    </tr>
    <%
      int rowCount = 0;
      long totalSize = 0;
    %>
    <c:forEach items="${svndir}" var="indexentry">
    <jsp:useBean id="indexentry" type="de.berlios.sventon.index.IndexEntry" />
      <c:url value="repobrowser.svn" var="viewUrl">
        <c:param name="path" value="${indexentry.fullEntryName}" />
      </c:url>
      <c:url value="showlog.svn" var="showLogUrl">
        <c:param name="path" value="${indexentry.fullEntryName}" />
      </c:url>
      <c:url value="showfile.svn" var="showFileUrl">
        <c:param name="path" value="${indexentry.fullEntryName}" />
      </c:url>

      <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <%
      SVNDirEntry type = indexentry.getEntry();
      SVNNodeKind nodeKind = type.getKind();
      totalSize += type.size();
      %>
        <td class="sventonCol1"><input type="checkbox" name="entry" value="<c:out value="${indexentry.fullEntryName}" />"/></td>
        <% if (nodeKind == SVNNodeKind.DIR) { %>
        <td class="sventonCol2"><img src="images/icon_dir.gif"/></td>
        <td class="sventonCol3"><a href="<c:out value="${viewUrl}/&revision=${command.revision}"/>"><c:out value="${indexentry.friendlyFullEntryName}" /></a></td>
        <% } else { %>
        <td class="sventonCol2"><img src="images/icon_file.gif"/></td>
        <td class="sventonCol3"><a href="<c:out value="${showFileUrl}&revision=${command.revision}"/>"><c:out value="${indexentry.friendlyFullEntryName}"/></a></td>
        <% } %>
        <td class="sventonCol4"><% if (nodeKind == SVNNodeKind.FILE) { %><%=type.size()%><% } %></td>
        <td class="sventonCol5"><c:out value="${indexentry.entry.revision}" /></td>
        <td class="sventonCol6"><c:out value="${indexentry.entry.author}" /></td>
        <td class="sventonCol7"><c:out value="${indexentry.entry.date}" /></td>
        <td class="sventonCol8"><a href="<c:out value="${showLogUrl}&revision=${command.revision}"/>">[Show log]</a></td>
      </tr>
      <% rowCount++; %>
    </c:forEach>

    <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <td colspan="2" align="right">Total:</td>
      <td><%=rowCount%> entries</td>
      <td align="right" title="<%=totalSize%>&nbsp;bytes"><%=ByteFormatter.format(totalSize)%></td>
      <td></td>
      <td></td>
      <td></td>
      <td></td>
    </tr>

    <tr>
      <td><input type="button" name="toggleButton" value="toggle" onClick="javascript:toggleEntryFields(this.form)"/></td>
      <td colspan="2">
        <select class="sventonSelect" name="actionSelect">
          <option class="sventonSelectOption">Actions...</option>
          <option value="zip">&nbsp;&nbsp;Download as zip</option>
          <option value="thumb">&nbsp;&nbsp;Show as thumbnails</option>
        </select>
      </td>
      <td colspan="6"><input type="submit" name="actionSubmitButton" value="go!"/></td>
    </tr>
  </table>
</form>
</div>
<br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
</body>
</html>
