<%
/*
 * ====================================================================
 * Copyright (c) 2005-2008 sventon project. All rights reserved.
 *
 * This software is licensed as described in the file LICENSE, which
 * you should have received as part of this distribution. The terms
 * are also available at http://sventon.berlios.de.
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
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <sventon:currentTargetHeader title="Revision Information" target="${command.revision}" hasProperties="false"/>
    <sventon:functionLinks pageName="showRevInfo"/>

    <c:url value="revinfo.svn" var="showPrevRevInfoUrl">
      <c:param name="revision" value="${command.revision - 1}" />
      <c:param name="name" value="${command.name}" />
    </c:url>

    <c:url value="revinfo.svn" var="showNextRevInfoUrl">
      <c:param name="revision" value="${command.revision + 1}" />
      <c:param name="name" value="${command.name}" />
    </c:url>

    <c:if test="${command.revision - 1 gt 0}">
      <a href="${showPrevRevInfoUrl}"><img src="images/arrow_left.png" alt="Previous revision" title="<spring:message code="revinfo.previousrev"/>"></a>
    </c:if>
    <c:if test="${!(command.revision + 1 gt headRevision)}">
      <a href="${showNextRevInfoUrl}"><img src="images/arrow_right.png" alt="Previous revision" title="<spring:message code="revinfo.nextrev"/>"></a>
    </c:if>

    <br>

    <table class="sventonLatestCommitInfoTable">
      <tr>
        <td>
          <sventon:revisionInfo details="${revisionInfo}" keepVisible="false" linkToHead="false" />
        </td>
      </tr>
    </table>

    <br>

    <%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
