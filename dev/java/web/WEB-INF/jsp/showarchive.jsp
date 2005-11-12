<%@ page import="java.util.Date"%>
<%@ page import="de.berlios.sventon.util.ByteFormatter"%>

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
    long totalOrigSize = 0;
    long totalCompSize = 0;
    %>
  <c:forEach items="${entries}" var="zipEntry">
    <jsp:useBean id="zipEntry" type="java.util.zip.ZipEntry" />
    <jsp:useBean id="entryDate" class="java.util.Date" />

    <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
      <%
        entryDate = new Date(zipEntry.getTime());
        totalOrigSize += zipEntry.getSize();
        totalCompSize += zipEntry.getCompressedSize();
      %>
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
      <td class="sventonColNoWrap"><fmt:formatDate type="both" value="${entryDate}" dateStyle="short" timeStyle="short"/></td>
      <td>${zipEntry.crc}</td>
    </tr>
    <% rowCount++; %>
  </c:forEach>

  <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry1"); else out.print("sventonEntry2");%>">
    <td align="right"><b>Total:</b></td>
    <td><b><%=rowCount%> entries</b></td>
    <td align="right" title="<%=totalOrigSize%>&nbsp;bytes"><b><%if (totalOrigSize != 0) out.print(ByteFormatter.format(totalOrigSize, request.getLocale()));%></b></td>
    <td align="right" title="<%=totalCompSize%>&nbsp;bytes"><b><%if (totalCompSize != 0) out.print(ByteFormatter.format(totalCompSize, request.getLocale()));%></b></td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
</table>
