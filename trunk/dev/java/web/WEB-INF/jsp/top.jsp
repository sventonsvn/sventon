<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/sventonbar.jsp"%>
<p/>
<table class="sventonTopTable">
  <tr>
    <td class="sventonHeadlines">
     Repository path: <a href="repobrowser.svn">
      <c:out value="${url}"/></a> /
      <c:forTokens items="${command.pathPart}" delims="/" var="pathSegment">
        <c:set var="accuPath" scope="page" value="${accuPath}${pathSegment}/"/>
        <a href="<c:out value="repobrowser.svn?path=${accuPath}&revision=${command.revision}"/>"><c:out value="${pathSegment}"/></a> /
      </c:forTokens>
        <c:out value="${command.target}"/>
    </td>
    <td align="right">
      <form action="search.svn" method="get">
        Search:
        <input type="text" name="sventonSearchString" class="sventonSearchField" value=""/>
        <input type="submit" name="sventonSearchButton" value="go!"/>
      </form>
    </td>
  <tr>
    <td class="sventonHeadlines">
      Revision: <c:out value="${command.revision}" /> <c:if test="${!empty numrevision}">(<c:out value="${numrevision}"/>)</c:if></p>
      </span>
    </td>
  </tr>
 <p>
</table>
<p/>

 <table>
<tr><td><spring:hasBindErrors name="command"><font color="#FF0000"><spring:message code="${errors.globalError.code}" text="${errors.globalError.defaultMessage}"/></font> </spring:hasBindErrors></td></tr>
</table>

<form name="gotoForm" method="post" action="repobrowser.svn">
 <table>
<tr>
<td><font color="#FF0000"><spring:bind path="command.revision"><c:out value="${status.errorMessage}" /></spring:bind></font></td>
<td><font color="#FF0000"><spring:bind path="command.path"><c:out value="${status.errorMessage}" /></spring:bind></font></td>
</tr>
 <tr>
 <td>Go to Revision</td><td colspan="2">Go to path</td>
 </tr>
<tr>
<td><spring:bind path="command.revision"><input class="sventonRevision" type="text" name="revision" value="<c:out value="${status.value}"/>"/></spring:bind></td>
<td><spring:bind path="command.path"><input class="sventonGoTo" type="text" name="path" value="<c:out value="${status.value}"/>" /></spring:bind></td>
<td><input class="sventonGoToSubmit" type="submit" value="go to"/></td>
</tr>
</table>
</form>
</p>


