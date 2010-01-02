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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf" %>

<html>
<head>
  <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf" %>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/config.css">
  <title><spring:message code="window.title.default"/></title>

  <script type="text/javascript">
    function toggleAccessControl() {
      var shared = $('shared_btn');
      var user = $('user_btn');
      var useCache = $('caching');

      ['access_method_user', 'access_method_shared', 'cache_settings', 'access_credentials', 'access_method_user_and_cache_enabled'].each(Element.hide);

      if (user.checked) {
        ['access_method_user', 'access_credentials'].each(Element.show);
      } else if (shared.checked) {
        ['access_method_shared', 'access_credentials'].each(Element.show);      
      }

      if (useCache.checked) {
        ['cache_settings'].each(Element.show);
        if (user.checked) {
          ['access_method_user_and_cache_enabled'].each(Element.show);
        }
      }
    }
    window.onload = toggleAccessControl;
  </script>
</head>

<body>

<script language="JavaScript" type="text/javascript" src="${pageContext.request.contextPath}/js/wz_tooltip.js"></script>

<sventon:currentTargetHeader title="sventon.header" target="configuration" properties="${properties}"/>

<br>

<div id="config_page">

  <div id="configured_repos">
    <ul>
      <c:forEach var="repos" items="${addedRepositories}">
        <li>${repos}</li>
      </c:forEach>
    </ul>
  </div>

<div id="config_main">
  <form id="config_form" name="configForm" method="post" action="${pageContext.request.contextPath}/repos/config" onsubmit="return validateUrl(this);">
    <div class="config_group">
      <div id="repository_location">
        <img alt="Step 1" src="images/config_step1.png"/>

        <div class="config_settings">
          <p class="config_key">Repository name:
            <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('conf_repository_name_help');">
          </p>

          <spring:bind path="command.name">
            <p>
              <input type="text" name="name" size="50" value="${status.value}" class="configHeaderSmall">
            </p>
            <c:if test="${status.error}">
              <p>
                <span class="exclamationText">${status.errorMessage}</span>
              </p>
            </c:if>
          </spring:bind>

          <p>
            <b>Example:</b> <i>local</i>, <i>myrepos</i>, or <i>project1</i>
          </p>

          <p class="config_key"> Subversion repository root URL:
            <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('conf_repository_url_help');">
          </p>

          <spring:bind path="command.repositoryUrl">
            <p>
              <input type="text" name="repositoryUrl" size="50" value="${status.value}" class="configHeaderSmall">
            </p>
            <c:if test="${status.error}">
              <p>
                <span class="exclamationText">${status.errorMessage}</span>
              </p>
            </c:if>
          </spring:bind>

          <p>
            <b>Example:</b> <i>http://domain/project/</i>, <i>svn://domain/project/</i> or <i>svn+ssh://domain/project/</i>
          </p>
        </div>

      </div>
    </div>

    <div class="config_group">
      <div id="access_method">
        <img alt="Step 2" src="images/config_step2.png"/>

        <div class="config_settings">
          <spring:bind path="command.accessMethod">
            <p class="config_key">
              <input id="anon_btn" type=radio name="accessMethod" value="ANONYMOUS"
              <c:if test="${status.value eq 'ANONYMOUS'}"> checked</c:if> onclick="toggleAccessControl();">
              <label for="anon_btn">anonymous</label>
              <input id="shared_btn" type=radio name="accessMethod" value="SHARED"
              <c:if test="${status.value eq 'SHARED'}"> checked</c:if> onclick="toggleAccessControl();">
              <label for="shared_btn">shared</label>
              <input id="user_btn" type=radio name="accessMethod" value="USER"
              <c:if test="${status.value eq 'USER'}"> checked</c:if> onclick="toggleAccessControl();">
              <label for="user_btn">user</label>
              <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('conf_access_method_help');">
            </p>
            <c:if test="${status.error}">
              <p>
                <span class="exclamationText">${status.errorMessage}</span>
              </p>
            </c:if>
          </spring:bind>
        </div>
      </div>

      <div id="access_method_shared" style="display: none;">
        <div class="config_settings">
          <p class="config_key">Username and password:</p>
        </div>
      </div>

      <div id="access_method_user" style="display: none;">
        <div class="config_settings">
          <p class="config_key">Username for connection test:</p>
        </div>
      </div>

      <div id="access_credentials" style="display: none;">
        <div class="config_settings">
          <spring:bind path="command.userName">
            <p>
              <input id="shared-uid" type="text" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
              <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('conf_shared_uid_help');">
            </p>
          </spring:bind>
          <spring:bind path="command.userPassword">
            <p>
              <input id="shared-pwd" type="password" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
            </p>
          </spring:bind>
        </div>
      </div>

    </div>

    <div class="config_group">
      <div id="application_features">
        <img alt="Step 3" src="images/config_step3.png"/>

        <div id="downloads">
          <div class="config_settings">
            <p class="config_key">
              <label for="zip_cbx">Allow download as compressed ZIP: </label>
              <spring:bind path="command.zippedDownloadsAllowed">
                <input id="zip_cbx" type="checkbox" name="${status.expression}"
                <c:if test="${status.value}"> checked</c:if>>
              </spring:bind>
              <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('conf_zipped_downloads_help');">
            </p>
          </div>
        </div>

        <div id="cache">
          <div class="config_settings">
            <spring:bind path="command.cacheUsed">
              <p class="config_key">
                <label for="caching">Use repository caching: </label>
                <input id="caching" type="checkbox" name="${status.expression}"
                <c:if test="${status.value}"> checked</c:if> onclick="toggleAccessControl();">
                <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('conf_repository_caching_help');">
              </p>
              <c:if test="${status.error}"><p><span class="exclamationText">${status.errorMessage}</span></p></c:if>
            </spring:bind>
          </div>
        </div>

        <div id="cache_settings" style="display: none;">
          <div class="config_settings">
            <p class="config_key">Username for cache: </p>
            <p>
              <spring:bind path="command.cacheUserName">
              <input id="cache-uid" type="text" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
              </spring:bind>
              <img class="helpIcon" src="images/icon_help.png" alt="Help" onmouseover="return getHelpText('conf_repository_cache_config_help');">
            </p>

            <p class="config_key">Password for cache: </p>
            <spring:bind path="command.cacheUserPassword">
              <p>
                <input id="cache-pwd" type="password" name="${status.expression}" size="30" value="${status.value}" class="configHeaderSmall">
              </p>
            </spring:bind>
          </div>
        </div>

        <div id="access_method_user_and_cache_enabled" style="display: none; width: 50%;">
          <div class="config_settings">
            <p class="config_key">
              <img src="images/icon_exclamation.png" alt="Warning"/>
              <span class="exclamationText">
                <b><spring:message code="config.warning.header"/></b>
              </span>
            </p>
            <p class="config_key">
              <span class="exclamationText"><spring:message code="config.warning.cached-files-visibility"/></span>
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="config_group">
      <div class="config_settings">
        <input value="Continue" class="cfgbtn" type="submit">
        <c:if test="${not empty addedRepositories}">
          <input value="Cancel" class="cfgbtn" type="button" onclick="document.location.href='${pageContext.request.contextPath}/repos/listconfigs'">
        </c:if>
      </div>
    </div>
</form>

</div>

<script language="JavaScript" type="text/javascript">
  document.configForm.name.focus();
</script>

<%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf" %>
</body>
</html>
