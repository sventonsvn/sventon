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
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/config.css">
  <title>sventon repository browser</title>

  <script type="text/javascript">
    function toggleAccessControl() {
      var global = $('global_btn');
      var user = $('user_btn');

      ['access_method_user', 'access_method_global', 'access_method_anon', 'cache'].each(Element.show);

      if (user.checked) {
        ['access_method_anon', 'access_method_global', 'cache'].each(Element.hide);
      } else if (global.checked) {
        ['access_method_anon', 'access_method_user'].each(Element.hide);
      } else {
        ['access_method_user', 'access_method_global'].each(Element.hide);
      }
    }
    window.onload = toggleAccessControl;
  </script>
</head>

<body>
<sventon:currentTargetHeader title="sventon repository browser" target="configuration" hasProperties="false"/>

<br>

<div id="config_page">

<div id="configured_repos">

  <ul>
    <c:forEach var="instance" items="${addedInstances}">
      <li>${instance}</li>
    </c:forEach>
  </ul>

</div>

<div id="config_main">
<form id="config_form" name="configForm" method="post" action="config.svn" onsubmit="return validateUrl(this);">
<div class="config_group">
  <div id="repository_location">
    <img src="images/config_step1.png"/>

    <div class="config_settings">
      <p class="config_key">Repository name: <img class="helpIcon" src="images/icon_help.png"
                                                  alt="Help"
                                                  onmouseover="return getHelpText('conf_repository_name_help');">
      </p>

      <spring:bind path="command.name">
        <p><input type="text" name="name" size="50" value="${status.value}" class="configHeaderSmall"></p>
        <c:if test="${status.error}"><p><span class="exclamationText">${status.errorMessage}</span></p></c:if>
      </spring:bind>
      <p><b>Example:</b> <i>local</i>, <i>myrepos</i>, or <i>project1</i></p>

      <p class="config_key">Subversion repository root URL: <img class="helpIcon"
                                                                 src="images/icon_help.png"
                                                                 alt="Help"
                                                                 onmouseover="return getHelpText('conf_repository_url_help');">
      </p>

      <spring:bind path="command.repositoryUrl">
        <p><input type="text" name="repositoryUrl" size="50" value="${status.value}" class="configHeaderSmall"></p>
        <c:if test="${status.error}"><p><span class="exclamationText">${status.errorMessage}</span></p></c:if>
      </spring:bind>

      <p><b>Example:</b> <i>http://domain/project/</i>, <i>svn://domain/project/</i> or
        <i>svn+ssh://domain/project/</i></p>
    </div>

  </div>
</div>

<div class="config_group">
  <div id="access_method">
    <img src="images/config_step2.png"/>

    <div class="config_settings">
      <spring:bind path="command.accessMethod">
        <p class="config_key"><input id="anon_btn" type=radio name="accessMethod" value="ANONYMOUS"
        <c:if test="${status.value eq 'ANONYMOUS'}"> checked</c:if>
                                                     onclick="toggleAccessControl();"><label
           for="anon_btn">anonymous</label>
          <input id="global_btn" type=radio name="accessMethod" value="GLOBAL"
          <c:if test="${status.value eq 'GLOBAL'}"> checked</c:if> onclick="toggleAccessControl();"><label
           for="global_btn">sventon</label>
          <input id="user_btn" type=radio name="accessMethod" value="USER"
          <c:if test="${status.value eq 'USER'}"> checked</c:if> onclick="toggleAccessControl();"><label
           for="user_btn">user</label> <img class="helpIcon" src="images/icon_help.png" alt="Help"
                                            onmouseover="return getHelpText('conf_access_method_help');"></p>
        <c:if test="${status.error}"><p><span class="exclamationText">${status.errorMessage}</span></p></c:if>
      </spring:bind>
    </div>
  </div>

  <div id="access_method_global">
    <div class="config_settings">
      <p class="config_key">Username:</p>

      <spring:bind path="command.uid">
      <p><input id="global-uid" type="text" name="${status.expression}" size="30" value="${status.value}"
                class="configHeaderSmall">
        </spring:bind>
        <img class="helpIcon" src="images/icon_help.png" alt="Help"
             onmouseover="return getHelpText('conf_global_uid_help');"></p>

      <p class="config_key">Password:</p>
      <spring:bind path="command.pwd">
        <p><input id="global-pwd" type="password" name="${status.expression}" size="30" value="${status.value}"
                  class="configHeaderSmall"></p>
      </spring:bind>
    </div>
  </div>

  <div id="access_method_user">
    <div class="config_settings">
      <p class="config_key">Username for connection test:</p>
      <spring:bind path="command.connectionTestUid">
      <p><input type="text" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
        </spring:bind> <img
         class="helpIcon" src="images/icon_help.png" alt="Help"
         onmouseover="return getHelpText('conf_connection_test_uid_help');"></p>

      <p class="config_key">Password for connection test:</p>
      <spring:bind path="command.connectionTestPwd">
        <p><input type="password" name="${status.expression}" size="30" value="${status.value}"
                  class="configHeaderSmall"></p>
      </spring:bind>
    </div>
  </div>

  <div id="access_method_anon">

  </div>
</div>

<div class="config_group">
  <div id="application_features">
    <img src="images/config_step3.png"/>

    <div id="downloads">
      <div class="config_settings">
        <p class="config_key"><label for="zip_cbx">Allow download as compressed ZIP: </label>
          <spring:bind path="command.zippedDownloadsAllowed">
            <input id="zip_cbx" type="checkbox" name="${status.expression}"
            <c:if test="${status.value}"> checked</c:if>>
          </spring:bind>
          <img class="helpIcon" src="images/icon_help.png" alt="Help"
               onmouseover="return getHelpText('conf_zipped_downloads_help');"></p>
      </div>
    </div>

    <div id="cache">
      <div class="config_settings">
        <p class="config_key"><label for="caching">Use repository caching: </label>
          <spring:bind path="command.cacheUsed">
            <input id="caching" type="checkbox" name="${status.expression}"
            <c:if test="${status.value}"> checked</c:if>>
          </spring:bind> <img
           class="helpIcon" src="images/icon_help.png" alt="Help"
           onmouseover="return getHelpText('conf_repository_caching_help');"></p>
      </div>
    </div>
  </div>

</div>

<div class="config_group">
  <div class="config_settings">
    <input value="Continue" class="cfgbtn" type="submit">
  </div>
</div>
</form>
</div>

<script language="JavaScript" type="text/javascript">
  document.configForm.name.focus();
</script>
<script language="JavaScript" type="text/javascript" src="js/wz_tooltip.js"></script>

<%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf" %>
</body>
</html>
