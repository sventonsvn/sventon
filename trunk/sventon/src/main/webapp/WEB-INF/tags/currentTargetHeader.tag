<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
  *
  * This software is licensed as described in the file LICENSE, which
  * you should have received as part of this distribution. The terms
  * are also available at http://sventon.berlios.de.
  * If newer versions of this license are posted there, you may use a
  * newer version instead, at your option.
  * ====================================================================
  */
%>
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8" %>
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
        <% application.setAttribute("br", "\n"); %>
        <c:forEach items="${properties}" var="property">
          <tr>
            <td valign="top"><b>${property.key}:&nbsp;</b></td>
            <td>${fn:replace(property.value, br, '<br>')}</td>
          </tr>
        </c:forEach>
      </table>
  </div>
</c:if>
