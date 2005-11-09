<%@ page import="java.util.Date"%>
<table class="sventonEntriesTable">
  <tr>
    <th></th>
    <th>Name</th>
    <th>Original size</th>
    <th>Compressed size</th>
    <th>Date</th>
    <th>CRC</th>
  </tr>
  <%
    int rowCount = 0;
    long totalSize = 0;
    %>
  <c:forEach items="${entries}" var="zipEntry">
    <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <c:choose>
        <c:when test="${zipEntry.directory}">
          <td><img src="images/icon_dir.gif" alt="dir"/></td>
        </c:when>
        <c:otherwise>
          <td><img src="images/icon_file.gif" alt="file"/></td>
        </c:otherwise>
      </c:choose>
      <td>${zipEntry.name}</td>
      <td class="sventonColRightAlign">${zipEntry.size}</td>
      <td class="sventonColRightAlign">${zipEntry.compressedSize}</td>
      <c:set var="date" value="${zipEntry.time}" />
      <jsp:useBean id="date" type="java.lang.Long" />
      <td class="sventonColNoWrap"><%=new Date(date.longValue())%></td>
      <td>${zipEntry.crc}</td>
    </tr>
    <% rowCount++; %>
  </c:forEach>
</table>
