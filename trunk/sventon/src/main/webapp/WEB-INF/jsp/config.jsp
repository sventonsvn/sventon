<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2008 sventon project. All rights reserved.
  *
  * This software is licensed as described in the file LICENSE, which
  * you should have received as part of this distribution. The terms
  * are also available at http://sventon.berlios.de.
  * If newer versions of this license are posted there, you may use a
  * newer version instead, at your option.
  * ====================================================================
  */
%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf" %>
  <title>sventon repository browser</title>
  <script type="text/javascript">
    function toggleAccessControl() {
      var accessControl = $('accessControlCheckbox');
      ['global-uid', 'global-pwd', 'caching'].each(accessControl.checked ? Element.hide : Element.show);
      ['connection-test-uid', 'connection-test-pwd'].each(accessControl.checked ? Element.show : Element.hide);
    }
    window.onload = toggleAccessControl;
  </script>
</head>

<body>
<sventon:currentTargetHeader title="sventon repository browser" target="configuration" hasProperties="false"/>

<br>

<p>
<form name="configForm" method="post" action="config.svn" onsubmit="return validateUrl(configForm);">
  <table width="800" border="0">
  <tr>
    <td valign="top" align="right" style="white-space: nowrap;">Enter repository name:</td>
    <td valign="top">
      <spring:bind path="command.name">
      <input type="text" name="name" size="30" value="${status.value}"><c:if test="${status.error}"><span
       class="exclamationText">*</span></c:if></td>
    </spring:bind>
    <td valign="top">
      Example:
      <p>
        <b>local</b><br>
        <b>myrepos</b><br>
        <b>project1</b><br>
      </p>
    </td>
  </tr>
  <tr>
    <td valign="top" align="right" style="white-space: nowrap;">Enter subversion repository root url:</td>
    <td valign="top">
      <spring:bind path="command.repositoryUrl">
      <input type="text" name="repositoryUrl" size="30" value="${status.value}"><c:if test="${status.error}"><span
       class="exclamationText">*</span></c:if></td>
    </spring:bind>
    <td valign="top">
      Example:
      <p>
        <b>http://domain/project/</b><br>
        <b>svn://domain/project/</b><br>
        <b>svn+ssh://domain/project/</b><br>
      </p>
    </td>
  </tr>
  <tr>
    <td valign="top" align="right" style="white-space: nowrap;">Allow download as compressed ZIP:</td>
    <td valign="top">
      <spring:bind path="command.zippedDownloadsAllowed">
      <input type="checkbox" name="${status.expression}"
             <c:if test="${status.value}">checked</c:if>></td>
    </spring:bind>
    <td valign="top">
      Enable/disable the 'download as zip' function. <br>
    </td>
  </tr>
  <tr>
    <td valign="top" align="right" style="white-space: nowrap;">Enable user based access control:</td>
    <td valign="top">
      <spring:bind path="command.enableAccessControl">
      <input type="checkbox" name="${status.expression}" id="accessControlCheckbox"
             <c:if test="${status.value}">checked</c:if> onclick="toggleAccessControl();"></td>
    </spring:bind>
    <td valign="top">
      Controls whether user based access control should be enabled. This should be used if access control is
      enabled on the repository and each sventon user should authenticate them self individually. Do not check
      this box if anonymus read access is enabled on the repository or if you have set a global user and
      password above.<br>
      If enabled, the search and directory flattening features, as well as the log message search, will be
      <i>disabled</i>. <br>
      Note that enabling user based access control stores user id and password in the user session, this could
      be considered a security problem.
    </td>
  </tr>
  <tr id="global-uid">
    <td valign="top" align="right" style="white-space: nowrap;">Enter user name:</td>
    <td valign="top">
      <spring:bind path="command.uid">
      <input type="text" name="${status.expression}" size="30" value="${status.value}"><c:if test="${status.error}"><span
       class="exclamationText">*</span></c:if></td>
    </spring:bind>
    <td valign="top">(leave blank for anonymous)</td>
  </tr>
  <tr id="global-pwd">
    <td valign="top" align="right" style="white-space: nowrap;">Enter user password:</td>
    <td valign="top">
      <spring:bind path="command.pwd">
      <input type="password" name="${status.expression}" size="30" value="${status.value}"><c:if
       test="${status.error}"><span class="exclamationText">*</span></c:if></td>
    </spring:bind>
    <td valign="top">(leave blank for anonymous)</td>
  </tr>
  <tr id="caching">
    <td valign="top" align="right" style="white-space: nowrap;">Use repository caching feature:</td>
    <td valign="top">
      <spring:bind path="command.cacheUsed">
      <input type="checkbox" name="${status.expression}"
             <c:if test="${status.value}">checked</c:if>></td>
    </spring:bind>
    <td valign="top">
      Controls whether repository caching feature should be used. <br>
      If enabled, the search and directory flattening features will be available, as well as the log message search <br>
    </td>
  </tr>
  <tr id="connection-test-uid">
    <td valign="top" align="right" style="white-space: nowrap;">Enter user name for connection test:</td>
    <td valign="top">
      <spring:bind path="command.connectionTestUid">
      <input type="text" name="${status.expression}" size="30" value="${status.value}"><c:if test="${status.error}"><span
       class="exclamationText">*</span></c:if></td>
    </spring:bind>
    <td valign="top"></td>
  </tr>
  <tr id="connection-test-pwd">
    <td valign="top" align="right" style="white-space: nowrap;">Enter password for connection test:</td>
    <td valign="top">
      <spring:bind path="command.connectionTestPwd">
      <input type="password" name="${status.expression}" size="30" value="${status.value}"><c:if
       test="${status.error}"><span class="exclamationText">*</span></c:if></td>
    </spring:bind>
    <td valign="top"></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td colspan="2">
      <spring:hasBindErrors name="command">
        <span class="exclamationText">
          <c:forEach var="errMsgObj" items="${errors.allErrors}">
            *&nbsp;<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/><br>
          </c:forEach>
        </span>
      </spring:hasBindErrors>
    </td>
  </tr>
  <tr>
    <td>
      <input type="submit" value="add repository" class="btn">
    <td>
  </tr>
  <c:if test="${fn:length(addedInstances) > 0}">
    <tr>
      <td><b>Added instances</b></td>
    </tr>
    <c:forEach var="instance" items="${addedInstances}">
      <tr>
        <td>${instance}</td>
      </tr>
    </c:forEach>
  </c:if>
  </table>
</form>
</p>
<script language="JavaScript" type="text/javascript">document.configForm.name.focus();</script>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf" %>
</body>
</html>
