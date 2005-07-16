<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>sventon blame - <c:out value="${url}" /></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <%@ include file="/WEB-INF/jsp/top.jsp"%>

  <c:url value="showlog.svn" var="showLogUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <c:url value="download.svn" var="downloadUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <c:url value="showfile.svn" var="showFileUrl">
    <c:param name="path" value="${command.path}${entry.name}" />
  </c:url>
  
  <table class="sventonFunctionLinks" cellspacing="3">
    <tr>
      <td><a href="<c:out value="${showFileUrl}&revision=${command.revision}"/>">[Show file]</a></td>
      <td><a href="<c:out value="${downloadUrl}&revision=${command.revision}"/>">[Download]</a></td>
      <td><a href="#">[Diff with previous]</a></td>
      <td><a href="<c:out value="${showLogUrl}&revision=${command.revision}"/>">[Show log]</a></td>
    </tr>
  </table>

<table>
  <tr>
    <th>Revision</th>
    <th>Author</th>
    <th>Line</th>
  </tr>
  <tr>
    <td>
      <c:forEach items="${handler.blameLines}" var="line">
        <a href="#"><c:out value="${line.revision}"/></a><br/>
      </c:forEach>
    </td>
    <td>
      <c:forEach items="${handler.blameLines}" var="line">
        <c:out value="${line.author}"/><br/>
      </c:forEach>
    </td>
    <td>
      <c:forEach items="${handler.blameLines}" var="line">
        <c:out value="${line.line}"/><br/>
      </c:forEach>
    </td>
  </tr>
</table>
</body>
</html>
