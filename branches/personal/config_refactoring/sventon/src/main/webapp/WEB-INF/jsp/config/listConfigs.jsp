<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2008 sventon project. All rights reserved.
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
</head>

<body>
<sventon:currentTargetHeader title="sventon repository browser" target="configuration" properties="${properties}"/>

<br>

<div id="config_page">

  <c:url value="/repos/deleteconfig" var="deleteConfigUrl">
    <c:param name="name" value="${repos}" />
  </c:url>

  <div id="configured_repos">
    <ul>
      <c:forEach var="repos" items="${addedRepositories}">
        <li>
          ${repos}
          <c:url value="/repos/deleteconfig" var="deleteConfigUrl">
            <c:param name="name" value="${repos}" />
          </c:url>
          <a href="${deleteConfigUrl}">[delete]</a>
        </li>
      </c:forEach>
    </ul>
  </div>

  <div id="config_confirmation">

    <c:if test="${latestAddedRepository ne null}">
      <div id="config_success">
        <h1>
          Repository successfully configured!
        </h1>
        <p>
          The repository <b>${latestAddedRepository}</b> has been successfully added to sventon.
        </p>
      </div>
    </c:if>

    <div id="config_navigation">
      <form action="${pageContext.request.contextPath}/repos/config">
        <input value="Add another repository" class="cfgbtn" type="submit">
      </form>

      <form action="${pageContext.request.contextPath}/repos/submitconfig">
        <input value="Complete setup and start browsing" class="cfgbtn" type="submit">
      </form>
    </div>

    <div id="config_faq">
      <p>
        Configuration FAQ
      </p>

      <div class="config_faq_entry">
        <div class="config_faq_question" onclick="new Element.toggle('faqa1');">
          <a href="#" onclick="return false;">Where are the log files stored?</a>
        </div>
        <div class="config_faq_answer" id="faqa1" style="display: none;">
          By default, the temporary files created by sventon, including the <b>sventon.log</b> file and
          repository configurations will be stored in a directory called <code>sventon_temp</code> below the servlet
          container's temporary directory, <b><%=System.getProperty("java.io.tmpdir")%>
          </b>.
        </div>
      </div>

      <div class="config_faq_entry">
        <div class="config_faq_question" onclick="new Element.toggle('faqa2');">
          <a href="#" onclick="return false;">What if I want to change logging granularity or log file location?</a>
        </div>
        <div class="config_faq_answer" id="faqa2" style="display:none;">
          Logging properties and log file location can be changed by customizing the properties in the file <b>svn/WEB-INF/classes/log4j.properties</b>
        </div>
      </div>

      <div class="config_faq_entry">
        <div class="config_faq_question" onclick="new Element.toggle('faqa3');">
          <a href="#" onclick="return false;">What about UTF-8 support?</a>
        </div>
        <div class="config_faq_answer" id="faqa3" style="display:none;">
          If this sventon instance will be used with <a href="http://tomcat.apache.org/">Tomcat</a> in a
          non-US-ASCII environment, making fully use of Subversion's UTF-8 support, modifications must be made to the
          Coyote HTTP/1.1 connector: In <code>server.xml</code>, either set attribute <code>URIEncoding="UTF-8"</code>
          and/or set <code>useBodyEncodingForURI="true"</code>.
        </div>
      </div>
    </div>
  </div>

  <br>
  <br>

  <%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf" %>
</body>
</html>
