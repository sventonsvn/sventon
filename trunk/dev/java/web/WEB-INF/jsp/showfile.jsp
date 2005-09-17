<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
  <head>
    <title>Show file</title>
    <%@ include file="/WEB-INF/jsp/head.jsp"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jsp/top.jsp"%>

    <c:url value="blame.svn" var="blameUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
    </c:url>

    <c:url value="get.svn" var="downloadUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
    </c:url>

    <c:url value="showlog.svn" var="showLogUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
    </c:url>
    
    <p class="sventonHeader">
    Show File - <b><c:out value="${command.target}"/></b>&nbsp;<a href="javascript:toggleElementVisibility('propertiesDiv');">[show/hide properties]</a>
    </p>
    <%@ include file="/WEB-INF/jsp/sventonheader.jsp"%>

  <br/>

    <table class="sventonFunctionLinksTable">
      <tr>
        <td><a href="<c:out value="${showLogUrl}&revision=${command.revision}"/>">[Show log]</a></td>
        <td><a href="<c:out value="${downloadUrl}&revision=${command.revision}"/>">[Download]</a></td>
        <td><a href="#">[Diff with previous]</a></td>
        <c:choose>
       <c:when test="${empty fileContents}">
       <td>[Blame]</td>
       </c:when>
       <c:otherwise>
        <td><a href="<c:out value="${blameUrl}&revision=${command.revision}"/>">[Blame]</a></td>
        </c:otherwise>
        </c:choose>
      </tr>
    </table>

    <c:choose>
      <c:when test="${isBinary}">
        <c:choose>
          <c:when test="${isImage}">
<p>
  <a href="<c:out value="${downloadUrl}&revision=${command.revision}"/>&disp=inline">
    <img src="<c:out value="${downloadUrl}&revision=${command.revision}"/>&disp=thumb"/>
  </a>
</p>
          </c:when>
          <c:otherwise>
<p>File is in binary format.</p>
          </c:otherwise>
        </c:choose>
      </c:when>
     	<c:otherwise>
<pre class="codeBlock">
<c:out value="${fileContents}" escapeXml="false"/>
</pre>
</c:otherwise>
</c:choose>
<br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
  </body>
</html>
