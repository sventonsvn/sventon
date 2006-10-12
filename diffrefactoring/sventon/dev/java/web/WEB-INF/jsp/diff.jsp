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
    
    <br/>
    <sventon:functionLinks pageName="showDiff"/>

    <c:choose>
      <c:when test="${!empty diffException}">
        <p><b>${diffException}</b></p>
      </c:when>
      <c:otherwise>
        <c:choose>
          <c:when test="${!isBinary}">
            <table id="diffTable" class="sventonDiffTable" cellspacing="0" border="0">
              <tr>
                <th>
                  <a href="#diff0">
                    <img src="images/icon_nextdiff.gif" border="0" alt="Next diff" title="Next diff"/>
                  </a>
                </th>
                <th>&nbsp;</th>
                <th width="50%" colspan="2">
                  ${diffCommand.fromPath} @ revision ${diffCommand.fromRevision}
                </th>
                <th>&nbsp;</th>
                <th width="50%">
                  ${diffCommand.toPath} @ revision ${diffCommand.toRevision}
                </th>
              </tr>
              <c:forEach items="${tableRows}" var="tableRow">
                <tr>
                  <td>
                    <c:if test="${!empty tableRow.nextDiffAnchor}">
                      <a name="${tableRow.diffAnchor}"/>
                      <a href="#${tableRow.nextDiffAnchor}">
                        <img src="images/icon_nextdiff.gif" border="0" alt="Next diff" title="Next diff"/>
                      </a>
                    </c:if>
                  </td>
                  <td><b>${tableRow.diffSymbol}</b></td>
                  <td class="sventonLineNo">${tableRow.rowNumber}</td>
                  <td>
                    <span title="${tableRow.description}">
                      <code class="${tableRow.cssClass}">${tableRow.left}</code>
                    </span>
                  </td>
                  <td class="sventonLineNo">${tableRow.rowNumber}</td>
                  <td>
                    <span title="${tableRow.description}">
                      <code class="${tableRow.cssClass}">${tableRow.right}</code>
                    </span>
                  </td>
                </tr>
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
