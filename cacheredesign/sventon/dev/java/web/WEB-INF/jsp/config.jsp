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
<%@ include file="/WEB-INF/jspf/include.jspf"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/head.jspf"%>
    <title>sventon repository browser</title>
  </head>

  <body>
    <p>
      <table class="sventonHeader">
        <tr>
          <td>sventon repository browser configuration</td>
        </tr>
      </table>
    </p>

    <br/>
    <form name="configForm" method="post" action="config.svn">
      <p>
        <table width="700">
          <tr>
            <td valign="top" align="right" style="white-space: nowrap;">Enter subversion repository root url:</td>
            <td valign="top">
              <spring:bind path="command.repositoryURL">
                <input type="text" name="repositoryURL" size="30" value="${status.value}"/><c:if test="${status.error}"><span class="exclamationText">*</span></c:if></td>
              </spring:bind>
            <td valign="top">
              Example:
              <p>
                <b>svn://domain/project/</b><br>
                <b>svn+ssh://domain/project/</b><br>
                <b>http://domain/project/</b><br>
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
              If enabled, the search and directory flattening features will be available, aswell as the commit message search <br/>
            </td>
          </tr>
          <tr>
            <td valign="top" align="right" style="white-space: nowrap;">Enter sventon config/temp path:</td>
            <td valign="top">
              <spring:bind path="command.configPath">
                <input type="text" name="${status.expression}" size="30" value="${status.value}"/><c:if test="${status.error}"><span class="exclamationText">*</span></c:if></td>
              </spring:bind>
            <td valign="top">
              This is the path where the disk persistent cache file is stored. <br/>
              The web server running sventon must have read/write access to this directory.
            </td>
          </tr>
          <tr><td>&nbsp;</td></tr>
          <tr>
            <td colspan="2">
  By default the <b>sventon.log</b> file will be stored in the temporary directory, <b><%=System.getProperty("java.io.tmpdir")%></b>.<br>
  Logging properties and log file location can be changed by customizing the properties in the file <b>svn/WEB-INF/classes/log4j.properties</b>
            </td>
          </tr>
          <tr>
          <td colspan="2">
  If this sventon instance will be used with Tomcat in a non-ISO-8859-1 environment, making fully use of Subversion's 
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
        </table>
      </p>
      <p>
        <input type="submit" value="submit configuration" class="btn">
      </p>
    </form>

    <script language="JavaScript">
      document.configForm.repositoryURL.focus();
    </script>

  <%@ include file="/WEB-INF/jspf/foot.jspf"%>
  </body>
</html>
