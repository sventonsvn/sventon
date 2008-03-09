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
  <title>sventon repository browser - ${repositoryURL}</title>
</head>
<body>
  <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

  <sventon:currentTargetHeader title="Flattened structure" target="${command.target} (and below)" hasProperties="false"/>
  <sventon:functionLinks pageName="repobrowser"/>

  <form method="post" action="#" name="entriesForm" onsubmit="return doAction(this);">
    <input type="hidden" name="path" value="${command.path}">
    <input type="hidden" name="revision" value="${command.revision}">
    <input type="hidden" name="name" value="${command.name}">
    <input type="hidden" name="pegrev" value="${!empty numrevision ? numrevision : command.revision}">

    <c:url value="flatten.svn" var="sortUrl">
      <c:param name="path" value="${command.path}" />
      <c:param name="revision" value="${command.revision}" />
      <c:param name="name" value="${command.name}" />
    </c:url>

    <table class="sventonEntriesTable">
      <%@ include file="/WEB-INF/jspf/sortableEntriesTableHeaderRow.jspf"%>
        <c:set var="rowCount" value="0"/>
        <c:forEach items="${svndir}" var="entry">
          <c:url value="repobrowser.svn" var="viewUrl">
            <c:param name="path" value="${entry.fullEntryName}" />
            <c:param name="revision" value="${command.revision}" />
            <c:param name="name" value="${command.name}" />
          </c:url>
          <c:url value="revinfo.svn" var="showRevInfoUrl">
            <c:param name="revision" value="${entry.revision}" />
            <c:param name="name" value="${command.name}" />
          </c:url>

          <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}" id="dir${rowCount}">
            <td class="sventonCol1"><input type="checkbox" name="entry" value="${entry.fullEntryName};;${entry.revision}"></td>
            <td class="sventonCol2">
              <a href="#" onclick="return listFiles('${rowCount}', '${entry.fullEntryName}', '${command.name}');" onmouseover="Tip('<spring:message code="listfiles.link.tooltip"/>')">
                <img alt="Expand" src="images/icon_folder_go.png" id="dirIcon${rowCount}" />
              </a>
            </td>
            <td class="sventonCol3">
              <a href="<sventon-ui:formatUrl url='${viewUrl}'/>" onmouseover="Tip('<table><tr><td style=\'white-space: nowrap\'>${entry.fullEntryName}</td></tr></table>')">
                ${entry.friendlyFullEntryName}
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
              <fmt:formatDate type="both" value="${entry.date}" dateStyle="short" timeStyle="short"/>
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
  </form>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
</body>
</html>
