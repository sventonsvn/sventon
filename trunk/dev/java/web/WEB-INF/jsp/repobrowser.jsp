<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ page import="de.berlios.sventon.util.ByteFormatter"%>

<html>
<head>
<title>sventon repository browser - <c:out value="${url}" /></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <%@ include file="/WEB-INF/jsp/top.jsp"%>

  <p>
    <table class="sventonHeader"><tr><td>
  Repository Browser - <b><c:out value="${command.target}"/></b>&nbsp;
  <c:if test = "${!empty properties}">
    <a href="javascript:toggleElementVisibility('propertiesDiv');">[show/hide properties]</a>
  </c:if>
    </td></tr></table>
  </p>
  <%@ include file="/WEB-INF/jsp/sventonheader.jsp"%>

<br/>

<div id="entriesDiv" class="sventonEntriesDiv">
<form method="post" action="#" name="entriesForm" onsubmit="return doAction(entriesForm);">
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

    <c:forEach items="${svndir}" var="entry">
    <jsp:useBean id="entry" type="de.berlios.sventon.ctrl.RepositoryEntry" />
      <c:url value="repobrowser.svn" var="viewUrl">
        <c:param name="path" value="${entry.fullEntryNameStripMountPoint}" />
      </c:url>
      <c:url value="showlog.svn" var="showLogUrl">
        <c:param name="path" value="${entry.fullEntryNameStripMountPoint}" />
      </c:url>
      <c:url value="showfile.svn" var="showFileUrl">
        <c:param name="path" value="${entry.fullEntryNameStripMountPoint}" />
      </c:url>

      <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <%
        totalSize += entry.getSize();
      %>
        <td class="sventonCol1"><input type="checkbox" name="entry" value="<c:out value="${entry.fullEntryName}" />"/></td>
        <% if ("dir".equals(entry.getKind())) { %>
        <td class="sventonCol2"><img src="images/icon_dir.gif" alt="dir" /></td>
        <td class="sventonCol3">
          <c:choose>
            <c:when test="${isSearch}">
              <a href="<c:out value="${viewUrl}/&revision=${command.revision}"/>" onmouseover="this.T_WIDTH=1;return escape('<c:out value="${entry.fullEntryName}" />')">
                <c:out value="${entry.friendlyFullEntryName}" />
            </c:when>
            <c:otherwise>
              <a href="<c:out value="${viewUrl}/&revision=${command.revision}"/>"><c:out value="${entry.name}" /></c:otherwise>
          </c:choose>
          </a></td>
        <% } else { %>
        <td class="sventonCol2"><img src="images/icon_file.gif" alt="file" /></td>
        <td class="sventonCol3">
          <c:choose>
            <c:when test="${isSearch}">
              <a href="<c:out value="${showFileUrl}&revision=${command.revision}"/>" onmouseover="this.T_WIDTH=1;return escape('<c:out value="${entry.fullEntryName}" />')">
                <c:out value="${entry.friendlyFullEntryName}"/>
            </c:when>
            <c:otherwise>
              <a href="<c:out value="${showFileUrl}&revision=${command.revision}"/>"><c:out value="${entry.name}"/></c:otherwise>
          </c:choose>
          </a></td>
        <% } %>
        <td class="sventonCol4"><% if ("file".equals(entry.getKind())) { %><c:out value="${entry.size}" /><% } %></td>
        <td class="sventonCol5"><c:out value="${entry.revision}" /></td>
        <td class="sventonCol6"><c:out value="${entry.author}" /></td>
        <td class="sventonCol7"><c:out value="${entry.date}" /></td>
        <td class="sventonCol8"><a href="<c:out value="${showLogUrl}&revision=${command.revision}"/>">[Show log]</a></td>
      </tr>
      <% rowCount++; %>
    </c:forEach>

    <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <td colspan="2" align="right">Total:</td>
      <td><%=rowCount%> entries</td>
      <td align="right" title="<%=totalSize%>&nbsp;bytes"><%if (totalSize != 0) out.print(ByteFormatter.format(totalSize));%></td>
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
          <!--<option value="zip">&nbsp;&nbsp;Download as zip</option>-->
          <option value="thumb">&nbsp;&nbsp;Show as thumbnails</option>
        </select>
      </td>
      <td colspan="6"><input type="submit" name="actionSubmitButton" value="go!"/></td>
    </tr>
  </table>
</form>
</div>
<br>
<script language="JavaScript" type="text/javascript" src="wz_tooltip.js"></script>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
</body>
</html>
