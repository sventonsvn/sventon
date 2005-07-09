<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
  <head>
    <title>Show file</title>
    <%@ include file="/WEB-INF/jsp/head.jsp"%>
    <link href="syntax.css" rel="stylesheet" type="text/css"/>
  </head>

  <body>
    <%@ include file="/WEB-INF/jsp/top.jsp"%>
    <c:choose>
      <c:when test="${empty fileContents}">
This is a binary file.
      </c:when>
     	<c:otherwise>
<c:out value="${fileContents}" escapeXml="false"/>
      </c:otherwise>
    </c:choose>
  </body>
</html>
