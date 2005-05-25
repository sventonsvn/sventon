<%@ page session="false"%>
<table width="100%" class="sventonHeader"><tr>
<td>sventon subversion web client</td>
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
<p>
  <span class="sventonLocation">
  Repository path: <a href="repobrowser.svn">
  <c:out value="${url}"/></a> /
  <c:forTokens items="${pathPart}" delims="/" var="pathSegment">
  	<c:set var="accuPath" scope="page" value="${accuPath}${pathSegment}/"/>
  	<a href="<c:out value="repobrowser.svn?path=${accuPath}"/>"><c:out value="${pathSegment}"/></a> /
  </c:forTokens>
   	<c:out value="${target}"/>
  <p> 
  Revision: <c:out value="${revision}" />
  </p>
  </span>
</p>
<p>
  <input class="sventonGoTo" type="text" name="goto_url" value="" />[GoTo]
</p>


