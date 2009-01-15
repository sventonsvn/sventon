<%
/*
 * ====================================================================
 * Copyright (c) 2005-2009 sventon project. All rights reserved.
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
  <title>sventon repository browser - ${repositoryURL}</title>
</head>
<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>
  <sventon:currentTargetHeader title="Flattened structure" target="${command.target} (and below)" properties="${properties}"/>

  <form name="searchForm" action="#" method="get" onsubmit="return doSearch(this, '${command.name}', '${command.path}');">
    <table class="sventonFunctionLinksTable">
      <tr>
        <td style="white-space: nowrap;">
          <sventon:flattenFunctionButtons command="${command}"/>
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

  <form:form method="post" action="#" name="entriesForm" onsubmit="return doAction(this, '${command.name}', '${command.path}');" commandName="command">
    <input type="hidden" name="revision" value="${command.revision}">
    <input type="hidden" name="pegrev" value="${command.revisionNumber}">

    <c:url value="/repos/${command.name}/flatten${command.path}" var="sortUrl">
      <c:param name="revision" value="${command.revision}" />
    </c:url>

    <table class="sventonEntriesTable">
      <%@ include file="/WEB-INF/jspf/sortableEntriesTableHeaderRow.jspf"%>
        <c:set var="rowCount" value="0"/>
        <c:forEach items="${svndir}" var="entry">
          <c:url value="/repos/${command.name}/list${entry.fullEntryName}/" var="viewUrl">
            <c:param name="revision" value="${command.revision}" />
          </c:url>
          <c:url value="/repos/${command.name}/info" var="showRevInfoUrl">
            <c:param name="revision" value="${entry.revision}" />
          </c:url>

          <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}" id="dir${rowCount}">
            <td class="sventonCol1">
              <form:checkbox path="entries" value="${entry.fullEntryName}@${entry.revision}"/>
            </td>
            <td class="sventonCol2">
              <a href="#" onclick="return listFiles('${rowCount}', '${command.name}', '${entry.fullEntryName}/');" onmouseover="Tip('<spring:message code="listfiles.link.tooltip"/>')">
                <img alt="Expand" src="images/icon_folder_go.png" id="dirIcon${rowCount}" />
              </a>
            </td>
            <td class="sventonCol3">
              <a href="${viewUrl}" onmouseover="Tip('<table><tr><td style=\'white-space: nowrap\'>${entry.fullEntryName}/</td></tr></table>')">
                ${entry.friendlyFullEntryName}/
              </a>
            </td>
            <td class="sventonCol4"/>
            <td class="sventonCol5"/>
            <td class="sventonCol6">
              <a href="${showRevInfoUrl}" onmouseover="getLogMessage(${entry.revision}, '${command.name}', '<fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>');">
                ${entry.revision}
              </a>
            </td>
            <td class="sventonCol7">${entry.author}</td>
            <td class="sventonCol8">
              <span onmouseover="Tip('<sventon-ui:age date="${entry.date}"/>');">
                <fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>
              </span>
            </td>
          </tr>
        <c:set var="rowCount" value="${rowCount + 1}"/>
      </c:forEach>

      <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
        <td colspan="2" align="right"><b>Total:</b></td>
        <td><b>${rowCount} entries</b></td>
        <td/>
        <td/>
        <td/>
        <td/>
        <td/>
      </tr>
      <tr>
        <%@ include file="/WEB-INF/jspf/actionSelectList.jspf"%>
        <td colspan="5"/>
      </tr>
    </table>
  </form:form>

  <c:if test="${isEntryTrayEnabled}">
    <%@ include file="/WEB-INF/jspf/entryTray.jspf"%>
  </c:if>
  
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
