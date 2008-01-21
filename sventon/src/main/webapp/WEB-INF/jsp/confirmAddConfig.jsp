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
</head>

<body>
<sventon:currentTargetHeader title="sventon repository browser" target="configuration" hasProperties="false"/>

<br>

<c:if test="${latestAddedInstance ne null}">
  <p class="configHeaderBig">
    Repository added!
  </p>

  <p class="configHeaderSmall">
    The repository <b>${latestAddedInstance}</b> has been successfully added to sventon.
    The configuration parameters will be stored in the file <b>${configFile.absolutePath}</b>.
  </p>
</c:if>

<br>
<br>

<c:if test="${fn:length(addedInstances) > 1}">
  <p class="configHeaderSmall"><b>Repositories added so far:</b>
    <ul>
      <c:forEach var="instance" items="${addedInstances}">
        <li><i><span class="configHeaderSmall">${instance}</span></i></li>
      </c:forEach>
    </ul>
  </p>
</c:if>

<br>
<br>

<hr>

<table class="configFormTable">
  <tr>
    <td class="configHeaderBig">Where are the log files stored?</td>
  </tr>
  <tr>
    <td class="configHeaderSmall">By default, the temporary files created by sventon, including the <b>sventon.log</b> file and
      repository configurations will be stored in the servlet container's temporary directory,
      <b><%=System.getProperty("java.io.tmpdir")%></b>.
    </td>
  </tr>
  <tr>
    <td>
      <hr>
    </td>
  </tr>
  <tr>
    <td class="configHeaderBig">What if I want to change logging granularity or log file location?</td>
  </tr>
  <tr>
    <td class="configHeaderSmall">Logging properties and log file location can be changed by customizing the properties in the file <b>svn/WEB-INF/classes/log4j.properties</b>
    </td>
  </tr>
  <tr>
    <td>
      <hr>
    </td>
  </tr>
  <tr>
    <td class="configHeaderBig">What about UTF-8 support?</td>
  </tr>
  <tr>
    <td class="configHeaderSmall">If this sventon instance will be used with <a href="http://tomcat.apache.org/">Tomcat</a> in a
      non-US-ASCII environment, making fully use of Subversion's UTF-8 support, modifications must be made to the
      Coyote HTTP/1.1 connector: In <code>server.xml</code>, either set attribute <code>URIEncoding="UTF-8"</code>
      and/or set <code>useBodyEncodingForURI="true"</code>.
    </td>
  </tr>
</table>

<hr>

<br>
<br>

<div align="center" class="configHeaderSmall">
  <table>
    <tr>
      <td align="right">
        <form action="config.svn">
          <input type="hidden" value="addnew" name="addnew">
          <input type="submit" value="Add another repository to sventon" class="cfgbtn">
        </form>
      </td>
      <td>or</td>
      <td>
        <form action="submitconfig.svn">
          <input type="submit" value="Complete setup and start browsing" class="cfgbtn">
        </form>
      </td>
    </tr>
  </table>
</div>

<br>
<br>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf" %>
</body>
</html>
