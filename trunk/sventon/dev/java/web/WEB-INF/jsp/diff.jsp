<%
/*
 * ====================================================================
 * Copyright (c) 2005-2006 Sventon Project. All rights reserved.
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
    <title>Diff View</title>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <p><sventon:currentTargetHeader title="Diff View" target="${command.target}" hasProperties="false"/></p>
    
    <sventon:functionLinks pageName="showDiff"/>

    <c:choose>
      <c:when test="${!empty diffException}">
        <p><b>${diffException}</b></p>
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${!isBinary}">
            <c:set var="leftLines" value="${leftFileContent}" />
            <c:set var="rightLines" value="${rightFileContent}" />
            <jsp:useBean id="leftLines" type="java.util.ArrayList" />
            <jsp:useBean id="rightLines" type="java.util.ArrayList" />

            <table id="diffTable" class="sventonDiffTable" cellspacing="0">
              <tr>
                <th>
                  <a href="#diff0">
                    <img src="images/icon_nextdiff.gif" border="0" alt="Next diff" title="Next diff"/>
                  </a>
                </th>
                <th>&nbsp;</th>
                <th width="50%">${diffCommand.fromTarget} @ revision ${diffCommand.fromRevision}</th>
                <th>&nbsp;</th>
                <th width="50%">${diffCommand.toTarget} @ revision ${diffCommand.toRevision}</th>
              </tr>
              <c:set var="diffCount" value="0"/>
              <c:set var="lineCount" value="0"/>
              <c:forEach items="${leftLines}" var="line">
                <tr>
                  <td>
                    <c:if test="${'Unchanged' != line.action.description}">
                      <a name="diff${diffCount}"/>
                      <c:set var="diffCount" value="${diffCount + 1}"/>
                      <a href="#diff${diffCount}">
                        <img src="images/icon_nextdiff.gif" border="0" alt="Next diff" title="Next diff"/>
                      </a>
                    </c:if>
                  </td>
                  <td><b>${line.action.symbol}</b></td>
                  <td class="${line.action.CSSClass}">
                    <span title="${line.action.description}">
                      ${line.line eq '' ? '&nbsp;' : line.line}
                    </span>
                  </td>
                  <td><b>${righLines[lineCount].action.symbol}</b></td>
                  <td class="${rightLines[lineCount].action.CSSClass}">
                    <span title="${rightLines[lineCount].action.description}">
                      ${rightLines[lineCount].line eq '' ? '&nbsp;' : rightLines[lineCount].line}
                    </span>
                  </td>
                </tr>
                <c:set var="lineCount" value="${lineCount + 1}"/>
              </c:forEach>
            </table>
            <a name="diff${diffCount}"/>
          </c:when>
          <c:otherwise>
            <p><b>One or both files selected for diff is in binary format.</b></p>
          </c:otherwise>
        </c:choose>
      </c:otherwise>
    </c:choose>
    <br>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
