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
      ['global-uid', 'global-pwd', 'caching'].each(function(input) {
        return accessControl.checked ? Field.disable(input) : Field.enable(input);
      });
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
<table class="configFormTable">
<tr>
  <td class="configHeaderBig" colspan="3">Repository details</td>
</tr>
<tr>
  <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">Repository name:</td>
  <td>
    <spring:bind path="command.name">
      <input type="text" name="name" size="60" value="${status.value}" class="configHeaderSmall">
      <c:if test="${status.error}"><br><span class="exclamationText">${status.errorMessage}</span></c:if>
    </spring:bind>
    <p>
      <b>Example:</b> <i>local</i>, <i>myrepos</i>, or <i>project1</i>
    </p>
  </td>
</tr>
<tr>
  <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">Subversion repository root URL:</td>
  <td>
    <spring:bind path="command.repositoryUrl">
      <input type="text" name="repositoryUrl" size="60" value="${status.value}" class="configHeaderSmall">
      <c:if test="${status.error}"><br><span class="exclamationText">${status.errorMessage}</span></c:if>
    </spring:bind>
    <p>
      <b>Example:</b> <i>http://domain/project/</i>, <i>svn://domain/project/</i> or <i>svn+ssh://domain/project/</i>
    </p>
  </td>
</tr>
<tr>
  <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">User name:</td>
  <td>
    <spring:bind path="command.uid">
      <input id="global-uid" type="text" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
      <c:if test="${status.error}"><br><span class="exclamationText">${status.errorMessage}</span></c:if>
    </spring:bind>
    <p>
      Leave blank for anonymous
    </p>
  </td>
</tr>
<tr>
  <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">Password:</td>
  <td>
    <spring:bind path="command.pwd">
      <input id="global-pwd" type="password" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
      <c:if test="${status.error}"><br><span class="exclamationText">${status.errorMessage}</span></c:if>
    </spring:bind>
    <p>
      Leave blank for anonymous
    </p>
  </td>
</tr>
<tr>
  <td class="configHeaderBig" colspan="3">Application configuration</td>
</tr>
<tr>
  <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">Enable user based access control:</td>
  <td>
    <spring:bind path="command.enableAccessControl">
      <input type="checkbox" name="${status.expression}" id="accessControlCheckbox"
      <c:if test="${status.value}"> checked</c:if> onclick="toggleAccessControl();">
    </spring:bind>
    <p>
      Controls whether user based access control should be enabled. This should be used if access control is
      enabled on the repository and each sventon user should authenticate them self individually. Do not check
      this box if anonymus read access is enabled on the repository or if you have set a global user and
      password above.
    </p>

    <p>
      If enabled, all features relying on the repository cache, such as the search and directory flattening features and the log message
      search, will be <i>disabled</i>.
    </p>

    <p>
      Note that enabling user based access control stores user id and password in the user session, this could
      be considered a security problem.
    </p>
    <table class="configFormTable">
      <tr id="connection-test-uid">
        <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">User name for connection test:</td>
        <td>
          <spring:bind path="command.connectionTestUid">
            <input type="text" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
            <c:if test="${status.error}"><br><span class="exclamationText">${status.errorMessage}</span></c:if>
          </spring:bind>
        </td>
      </tr>
      <tr id="connection-test-pwd">
        <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">Password for connection test:</td>
        <td>
          <spring:bind path="command.connectionTestPwd">
            <input type="password" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
            <c:if test="${status.error}"><br><span class="exclamationText">${status.errorMessage}</span></c:if>
          </spring:bind>
        </td>
      </tr>
    </table>
  </td>
</tr>
<tr>
  <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">Allow download as compressed ZIP:</td>
  <td>
    <spring:bind path="command.zippedDownloadsAllowed">
      <input type="checkbox" name="${status.expression}"
      <c:if test="${status.value}"> checked</c:if>>
    </spring:bind>
    <p>
      Enable/disable the 'download as zip' function.
    </p>
  </td>
</tr>
<tr>
  <td valign="top" align="right" class="configHeaderSmall" nowrap="nowrap">Use repository caching:</td>
  <td>
    <spring:bind path="command.cacheUsed">
      <input id="caching" type="checkbox" name="${status.expression}"
      <c:if test="${status.value}"> checked</c:if>>
    </spring:bind>
    <p>
      Controls whether repository caching feature should be used. <br>
      If enabled, the search and directory flattening features will be available, as well as the log message search.
    </p>
  </td>
</tr>
<tr>
  <td></td>
  <td>
    <spring:hasBindErrors name="command">
      <span class="exclamationText">
        <b>Please fix the errors above!</b>
      </span>
    </spring:hasBindErrors>
  </td>
</tr>
<tr>
  <td></td>
  <td>
    <input type="submit" value="Continue" class="cfgbtn">
  </td>
</tr>
<c:if test="${fn:length(addedInstances) > 0}">
  <tr>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td></td>
    <td class="configHeaderSmall"><b>Already added instances</b></td>
  </tr>
  <c:forEach var="instance" items="${addedInstances}">
    <tr>
      <td></td>
      <td class="configHeaderSmall">${instance}</td>
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
