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
    <%@ include file="/WEB-INF/jspf/head.jspf"%>
    <title>Show file - ${command.target}</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/top.jspf"%>

    <p>
      <table class="sventonHeader">
        <tr>
          <td>Show File - <b>${command.target}</b>&nbsp;<a class="sventonHeader" href="javascript:toggleElementVisibility('propertiesDiv'); changeHideShowDisplay('propertiesLink');">[<span id="propertiesLink">show</span> properties]</a></td>
        </tr>
      </table>
      <%@ include file="/WEB-INF/jspf/sventonheader.jspf"%>
    </p>

    <br/>
    <ui:functionLinks pageName="showImageFile"/>

    <c:url value="get.svn" var="showUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
      <c:param name="revision" value="${command.revision}" />
      <c:param name="name" value="${command.name}" />
    </c:url>

    <c:url value="getthumb.svn" var="getThumbUrl">
      <c:param name="path" value="${command.path}${entry.name}" />
      <c:param name="revision" value="${command.revision}" />
      <c:param name="name" value="${command.name}" />
    </c:url>

    <p>
      <a href="${showUrl}&disp=inline">
        <img src="${getThumbUrl}" alt="Thumbnail" border="0"/>
      </a>
    </p>

    <br>
<%@ include file="/WEB-INF/jspf/rss.jspf"%>
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
  </body>
</html>
