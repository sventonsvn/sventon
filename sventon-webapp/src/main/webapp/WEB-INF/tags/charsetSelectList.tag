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
<%@ tag body-content="empty" language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="charsets" required="true" type="java.util.Set" %>
<%@ attribute name="currentCharset" required="true" type="java.lang.String" %>

<select class="sventonSelect" name="charsetSelect" onchange="updateCharsetParameter(this.form.charsetSelect.options[selectedIndex].value, '${pageContext.request.queryString}');">
  <c:forEach items="${charsets}" var="charset">
    <option ${charset eq currentCharset ? 'selected' : ''} value="${charset}">${charset}</option>
  </c:forEach>
</select>