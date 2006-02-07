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
<%@ include file="/WEB-INF/jspf/include.jspf"%>

<html>
  <head>
    <title>Show file - ${command.target}</title>
    <%@ include file="/WEB-INF/jspf/head.jspf"%>
    <link rel="stylesheet" type="text/css" href="jhighlight.css" >
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/top.jspf"%>

    <p>
      <table class="sventonHeader">
        <tr>
          <td>Show File - <b>${command.target}</b>&nbsp;<a href="javascript:toggleElementVisibility('propertiesDiv'); changeHideShowDisplay('propertiesLink');">[<span id="propertiesLink">show</span> properties]</a></td>
        </tr>
      </table>
      <%@ include file="/WEB-INF/jspf/sventonheader.jspf"%>
    </p>

    <br/>
    <ui:functionLinks pageName="showFile"/>

    <c:url value="get.svn" var="showUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
      <c:param name="revision" value="${command.revision}" />
    </c:url>

    <c:choose>
      <c:when test="${isBinary}">
        <c:choose>
          <c:when test="${isImage}">
<p>
  <a href="${showUrl}&disp=inline">
    <img src="${showUrl}&disp=thumb" alt="Thumbnail" border="0"/>
  </a>
</p>
          </c:when>
          <c:otherwise>
            <c:choose>
              <c:when test="${isArchive}">
                <%@ include file="/WEB-INF/jspf/showarchive.jspf"%>
              </c:when>
              <c:otherwise>
<p>File is in binary format.</p>
              </c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>
      </c:when>
     	<c:otherwise>
<pre class="codeBlock"><c:out value="${fileContents}" escapeXml="false"/></pre>
      </c:otherwise>
    </c:choose>
    <br>
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
  </body>
</html>
