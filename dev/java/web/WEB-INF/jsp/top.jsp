<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/sventonbar.jsp"%>
<spring:hasBindErrors name="command"><c:set var="hasErrors" scope="page" value="true"/></spring:hasBindErrors>
<p/>
<table class="sventonTopTable">
  <tr>
    <td class="sventonHeadlines">
      Revision: <c:out value="${command.revision}" /> <c:if test="${!empty numrevision}">(<c:out value="${numrevision}"/>)</c:if>
    </td>
    <td align="right">
      <form name="searchForm" action="search.svn" method="get" onsubmit="return doSearch(searchForm);">
        Search:
        <input type="text" name="sventonSearchString" class="sventonSearchField" value=""/>
        <input type="submit" name="sventonSearchButton" value="go!"/>
      </form>
    </td>
  </tr>
  <c:set var="command" value="${command}"/> 
  <jsp:useBean id="command" type="de.berlios.sventon.ctrl.SVNBaseCommand" />
  <tr>
    <td class="sventonHeadlines">
     Repository path: <a href="repobrowser.svn">
      <c:out value="${url}"/> / <%= command.getMountPoint(true) %></a> /
      <c:forTokens items="${command.pathPart}" delims="/" var="pathSegment">
        <c:set var="accuPath" scope="page" value="${accuPath}${pathSegment}/"/>
        <c:choose>
          <c:when test="${hasErrors}">
            <c:out value="${pathSegment}"/>
          </c:when>
          <c:otherwise>
		    <a href="<c:out value="repobrowser.svn?path=/${accuPath}&revision=${command.revision}"/>"><c:out value="${pathSegment}"/></a>
		  </c:otherwise>
        </c:choose>
         /
      </c:forTokens>
      <c:out value="${command.target}"/>
    </td>
  </tr>
</table>
<p/>

<table class="sventonSpringErrorMessageTable">
<tr><td><spring:hasBindErrors name="command"><font color="#FF0000"><spring:message code="${errors.globalError.code}" text="${errors.globalError.defaultMessage}"/></font> </spring:hasBindErrors></td></tr>
</table>

<form name="gotoForm" method="post" action="repobrowser.svn">
<table class="sventonRepositoryFunctionsTable">
<tr>
<td><font color="#FF0000"><spring:bind path="command.revision"><c:out value="${status.errorMessage}" /></spring:bind></font></td>
<td><font color="#FF0000"><spring:bind path="command.path"><c:out value="${status.errorMessage}" /></spring:bind></font></td>
</tr>
 <tr>
 <td>Go to Revision</td><td colspan="2">Go to path <% if (command.getMountPoint(false) != null) { %>(from: <%= command.getMountPoint(false) %>)<% } %></td>
 </tr>
<tr>
<td><spring:bind path="command.revision"><input class="sventonRevision" type="text" name="revision" value="<c:out value="${status.value}"/>"/></spring:bind></td>
<td><spring:bind path="command.path"><input class="sventonGoTo" type="text" name="path" value="<c:out value="${status.value}"/>" /></spring:bind></td>
<td><input class="sventonGoToSubmit" type="submit" value="go to"/></td>
<td><input class="sventonFlattenSubmit" type="button" value="flatten dirs" onclick="javascript:location.href='flatten.svn?path=${command.path}';"/></td>

</tr>
</table>
</form>


