<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2011 sventon project. All rights reserved.
  *
  * This software is licensed as described in the file LICENSE, which
  * you should have received as part of this distribution. The terms
  * are also available at http://www.sventon.org.
  * If newer versions of this license are posted there, you may use a
  * newer version instead, at your option.
  * ====================================================================
  */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHead.jspf" %>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/config.css">
  <title><spring:message code="window.title.default"/></title>
</head>

<body>

<script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/js/wz_tooltip.js"></script>

<sventon:currentTargetHeader title="sventon.header" target="configuration" properties="${properties}"/>

<br>

<form name="loginForm" action="${pageContext.request.contextPath}/repos/configlogin" method="post">
  <table>
    <spring:bind path="command.userPassword">
      <c:if test="${status.error}">
        <tr>
          <td>&nbsp;</td>
          <td>
            <span class="exclamationText">${status.errorMessage}</span>
          </td>
        </tr>
      </c:if>
      <tr>
        <td>Password</td>
        <td>
          <input name="userPassword" type="password" nocache/>
        </td>
      </tr>
    </spring:bind>
    <tr>
      <td colspan="2" align="right">
        <input type="submit" value="log in">
      </td>
    </tr>
  </table>

  <script language="JavaScript" type="text/javascript">document.loginForm.userPassword.focus();</script>

  <%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf" %>
</body>
</html>
