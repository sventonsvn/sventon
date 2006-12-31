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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf"%>
    <title>sventon repository browser</title>
  </head>

  <body>
    <sventon:currentTargetHeader title="sventon repository browser" target="configuration" hasProperties="false"/>

    <br/>
      <p>
        <table width="700" border="0">
          <form name="configForm" method="post" action="config.svn" onsubmit="return validateUrl(configForm);">
            <tr>
              <td valign="top" align="right" style="white-space: nowrap;">Enter repository name:</td>
              <td valign="top">
                <spring:bind path="command.name">
                  <input type="text" name="name" size="30" value="${status.value}"/><c:if test="${status.error}"><span class="exclamationText">*</span></c:if></td>
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
                <spring:bind path="command.repositoryURL">
                  <input type="text" name="repositoryURL" size="30" value="${status.value}"/><c:if test="${status.error}"><span class="exclamationText">*</span></c:if></td>
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
              <td valign="top" align="right" style="white-space: nowrap;">Enter user name:</td>
              <td valign="top">
                <spring:bind path="command.username">
                  <input type="text" name="${status.expression}" size="30" value="${status.value}"/><c:if test="${status.error}"><span class="exclamationText">*</span></c:if></td>
                </spring:bind>
              <td valign="top">(leave blank for anonymous)</td>
            </tr>
            <tr>
              <td valign="top" align="right" style="white-space: nowrap;">Enter user password:</td>
              <td valign="top">
                <spring:bind path="command.password">
                  <input type="password" name="${status.expression}" size="30" value="${status.value}"/><c:if test="${status.error}"><span class="exclamationText">*</span></c:if></td>
                </spring:bind>
              <td valign="top">(leave blank for anonymous)</td>
            </tr>
            <tr>
              <td valign="top" align="right" style="white-space: nowrap;">Use repository caching feature:</td>
              <td valign="top">
                <spring:bind path="command.cacheUsed">
                  <input type="checkbox" name="${status.expression}" <c:if test="${status.value}">checked</c:if>/></td>
                </spring:bind>
              <td valign="top">
                Controls whether repository caching feature should be used. <br/>
                If enabled, the search and directory flattening features will be available, as well as the log message search <br/>
              </td>
            </tr>
            <tr>
              <td valign="top" align="right" style="white-space: nowrap;">Allow download as compressed ZIP:</td>
              <td valign="top">
                <spring:bind path="command.zippedDownloadsAllowed">
                  <input type="checkbox" name="${status.expression}" <c:if test="${status.value}">checked</c:if>/></td>
                </spring:bind>
              <td valign="top">
                Enable/disable the 'download as zip' function. <br/>
              </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
              <td colspan="3">
                By default, the temporary files created by sventon, including the <b>sventon.log</b> file will be stored in <b><%=System.getProperty("java.io.tmpdir")%></b>.<br>
                Logging properties and log file location can be changed by customizing the properties in the file <b>svn/WEB-INF/classes/log4j.properties</b>
                <br/>
                If this sventon instance will be used with Tomcat in a non-US-ASCII environment, making fully use of Subversion's
                UTF-8 support, modifications must be made to the Coyote HTTP/1.1 connector: In server.xml, either set attribute
                <code>URIEncoding="UTF-8"</code> and/or set <code>useBodyEncodingForURI="true"</code>.
              </td>
            </tr>
            <tr>
              <td colspan="2">
                <spring:hasBindErrors name="command">
                  <span class="exclamationText">
                    <c:forEach var="errMsgObj" items="${errors.allErrors}">
                      *&nbsp;<spring:message code="${errMsgObj.code}" text="${errMsgObj.defaultMessage}"/><br/>
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
          </form>
          <c:if test="${fn:length(addedInstances) > 0}">
            <tr>
              <td><b>Added instances</b></td>
            </tr>
            <c:forEach var="instance" items="${addedInstances}">
            <tr><td>${instance}</td></tr>
            </c:forEach>
          </c:if>
          <tr>
            <td colspan="2" align="right">
              <form action="submitconfig.svn">
                <input type="submit" value="submit configuration" class="btn">
              </form>
            </td>
          </tr>
        </table>
      </p>

    <script language="JavaScript" type="text/javascript">document.configForm.name.focus();</script>

  <%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
