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
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="command" required="true" type="de.berlios.sventon.web.command.SVNBaseCommand" %>
<%@ attribute name="headRevision" required="true" type="java.lang.Long" %>

Rev:
<c:choose>
  <c:when test="${(headRevision == command.revisionNumber)}">
      HEAD (${command.revisionNumber})
  </c:when>
  <c:otherwise>
      <a href="revinfo.svn?revision=${command.revision}&name=${command.name}">
        <span class="exclamationText">${command.revision}</span>
      </a>
  </c:otherwise>
</c:choose>

