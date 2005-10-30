<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
  <head>
    <title>Show thumbnails</title>
    <%@ include file="/WEB-INF/jsp/head.jsp"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jsp/top.jsp"%>

    <c:url value="get.svn" var="downloadUrl" />

    <c:url value="repobrowser.svn" var="showDirUrl">
      <c:param name="path" value="${command.path}" />
      <c:param name="revision" value="${command.revision}" />
    </c:url>

    <p>
      <table class="sventonHeader"><tr><td>
    Diff view - <b><c:out value="${command.target}"/></b></td></tr></table>
    </p>
    <%@ include file="/WEB-INF/jsp/sventonheader.jsp"%>

    <br/>

    <table class="sventonFunctionLinksTable">
      <tr>
        <td><a href="<c:out value="${showDirUrl}"/>">[Show directory]</a></td>
      </tr>
    </table>

    <c:forEach items="${thumbnailentries}" var="entry">
      <p>
        <b><c:out value="${entry}"/></b><br/>
        <a href="<c:out value="${downloadUrl}?path=${entry}&revision=${command.revision}"/>&disp=inline">
          <img src="<c:out value="${downloadUrl}?path=${entry}&revision=${command.revision}"/>&disp=thumb"/>
        </a>
      </p>
    </c:forEach>

<br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
  </body>
</html>
