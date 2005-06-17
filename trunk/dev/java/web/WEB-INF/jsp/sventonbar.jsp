<%@ page session="false"%>
<table width="100%" class="sventonHeader"><tr>
<td>sventon subversion web client - (<a href="http://sventon.berlios.de" target="page">sventon.berlios.de</a>)</td>
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