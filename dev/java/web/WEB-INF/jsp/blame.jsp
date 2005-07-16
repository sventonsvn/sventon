<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>sventon blame - <c:out value="${url}" /></title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
<%@ include file="/WEB-INF/jsp/top.jsp"%>

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
