<%
/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://www.sventon.org.
 * If newer versions of this license are posted there, you may use a
 * newer version instead, at your option.
 * ====================================================================
 */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
  <title>Revision information details</title>
</head>

<body>
  <script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/js/wz_tooltip.js"></script>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>
  <sventon:topHeaderTable command="${command}" repositoryNames="${repositoryNames}"/>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <sventon:currentTargetHeader title="Revision Information" target="${command.revision}" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
  <table class="sventonFunctionLinksTable">
    <tr>
      <td style="white-space: nowrap;">
        <sventon:revInfoFunctionButtons command="${command}"/>
      </td>
      <td style="text-align: right;">
        <c:if test="${useCache}">
          <sventon:searchField command="${command}" isUpdating="${isUpdating}" isHead="${isHead}" searchMode="${userRepositoryContext.searchMode}"/>
        </c:if>
      </td>
    </tr>
  </table>
    <!-- Needed by ASVNTC -->
    <input type="hidden" name="revision" value="${command.revision}">
  </form>

  <c:url value="/repos/${command.name}/revinfo" var="showPrevRevInfoUrl">
    <c:param name="revision" value="${command.revisionNumber - 1}" />
  </c:url>

  <c:url value="/repos/${command.name}/revinfo" var="showNextRevInfoUrl">
    <c:param name="revision" value="${command.revisionNumber + 1}" />
  </c:url>

  <c:if test="${command.revisionNumber - 1 gt 0}">
    <a href="${showPrevRevInfoUrl}"><img src="images/arrow_left.png" alt="Previous revision" title="<spring:message code="revinfo.previousrev"/>"></a>
  </c:if>
  <c:if test="${!(command.revisionNumber + 1 gt headRevision)}">
    <a href="${showNextRevInfoUrl}"><img src="images/arrow_right.png" alt="Next revision" title="<spring:message code="revinfo.nextrev"/>"></a>
  </c:if>

  <br>

  <table class="sventonLatestCommitInfoTable">
    <tr>
      <td>
        <sventon:revisionInfo name="${command.name}" details="${revisionInfo}" keepVisible="false" linkToHead="false" />
      </td>
    </tr>
  </table>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
