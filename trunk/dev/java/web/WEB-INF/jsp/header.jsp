<%@ page session="false"%>
<table width="100%"><tr>
<td align="right">
<c:choose> 
  <c:when test="${empty uid}" > 
You are not logged in
  </c:when> 
  <c:otherwise> 
You are logged in as: <c:out value="${uid}" /> - <a href="clearsession.svn">Log out</a>
  </c:otherwise> 
</c:choose>  
</td></tr></table>
<hr/>

