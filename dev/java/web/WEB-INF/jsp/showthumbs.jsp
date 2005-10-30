<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
  <head>
    <title>Show thumbnails</title>
    <%@ include file="/WEB-INF/jsp/head.jsp"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jsp/top.jsp"%>

    <%@ include file="/WEB-INF/jsp/sventonheader.jsp"%>

  <br/>

    <table class="sventonFunctionLinksTable">
      <tr>
        <td>[Show log]</td>
        <td>[Download]</td>
      </tr>
    </table>
<p>
<%
//  <a href="<c:out value="${downloadUrl}&revision=${command.revision}"/>&disp=inline">
//    <img src="<c:out value="${downloadUrl}&revision=${command.revision}"/>&disp=thumb"/>
//  </a>
%>

</p>
<br>
<%@ include file="/WEB-INF/jsp/foot.jsp"%>
  </body>
</html>
