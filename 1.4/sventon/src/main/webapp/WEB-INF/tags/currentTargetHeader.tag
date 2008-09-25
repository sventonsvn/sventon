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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8" %>
<%@ tag import="org.tmatesoft.svn.core.SVNPropertyValue" %>
<%@ tag import="de.berlios.sventon.util.WebUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="target" required="true" type="java.lang.String" %>
<%@ attribute name="hasProperties" required="true" type="java.lang.Boolean" %>

<div id="sventonHeaderDiv">
  <table class="sventonHeader">
    <tr>
      <c:choose>
        <c:when test="${hasProperties}">
          <td>${title} - ${target}&nbsp;
            <a class="sventonHeaderLink" href="#" onclick="Element.toggle('propertiesDiv'); toggleInnerHTML('propertiesLink', 'show', 'hide'); return false;">
              [<span id="propertiesLink">show</span> properties]
            </a>
          </td>
        </c:when>
        <c:otherwise>
          <td>${title} - ${target}</td>
        </c:otherwise>
      </c:choose>
    </tr>
  </table>
</div>
<c:if test="${hasProperties}">
  <div id="propertiesDiv" style="display:none" class="sventonPropertiesDiv">
    <br>
      <table class="sventonPropertiesTable">
        <c:set var="properties" value="${properties}"/>
        <jsp:useBean id="properties" type="org.tmatesoft.svn.core.SVNProperties" />
        <%
          for (Object o : properties.nameSet()) {
            final String name = (String) o;
            final SVNPropertyValue value = properties.getSVNPropertyValue(name);
        %>
          <tr>
            <td valign="top"><b><%=name%>:&nbsp;</b></td>
            <td><%=WebUtils.nl2br(value.getString())%></td>
          </tr>
        <%
          }
        %>
      </table>
  </div>
</c:if>
