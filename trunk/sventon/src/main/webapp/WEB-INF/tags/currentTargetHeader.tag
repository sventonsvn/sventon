<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2010 sventon project. All rights reserved.
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
<%@ tag import="org.sventon.util.WebUtils" %>
<%@ tag import="java.util.Iterator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="target" required="true" type="java.lang.String" %>
<%@ attribute name="properties" required="true" type="org.tmatesoft.svn.core.SVNProperties" %>

<div id="sventonHeaderDiv">
  <table class="sventonHeader">
    <tr>
      <c:choose>
        <c:when test="${properties ne null}">
          <td><spring:message code='${title}'/> - ${target}&nbsp;
            <a class="sventonHeaderLink" href="#" onclick="Element.toggle('propertiesDiv'); toggleInnerHTML('propertiesLink', '<spring:message code='show'/>', '<spring:message code='hide'/>'); return false;">
              [<span id="propertiesLink"><spring:message code='show'/></span> properties]
            </a>
          </td>
        </c:when>
        <c:otherwise>
          <td><spring:message code='${title}'/> - ${target}</td>
        </c:otherwise>
      </c:choose>
    </tr>
  </table>
</div>
<c:if test="${properties ne null}">
  <div id="propertiesDiv" style="display:none" class="sventonPropertiesDiv">
    <br>
      <table class="sventonPropertiesTable">
        <c:set var="properties" value="${properties}"/>
        <jsp:useBean id="properties" type="org.tmatesoft.svn.core.SVNProperties" />
        <%
          for (Iterator it = properties.nameSet().iterator(); it.hasNext();) {
            final String name = (String) it.next();
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
