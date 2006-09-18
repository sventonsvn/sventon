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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="command" required="true" type="de.berlios.sventon.web.command.SVNBaseCommand" %>
<%@ attribute name="revisionNumber" required="true" type="java.lang.String" %>

Rev:
<c:choose>
  <c:when test="${!empty revisionNumber}">
      ${command.revision} (${revisionNumber})
  </c:when>
  <c:otherwise>
      <a class="exclamationText" href="revinfo.svn?revision=${command.revision}&name=${command.name}">
        ${command.revision}
      </a>
  </c:otherwise>
</c:choose>

