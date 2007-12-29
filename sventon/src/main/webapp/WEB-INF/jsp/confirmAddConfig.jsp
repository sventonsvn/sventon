<%
  /*
  * ====================================================================
  * Copyright (c) 2005-2007 Sventon Project. All rights reserved.
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
  <p>
  Instance <b>${latestAddedInstance}</b> successfully added.
</p>
</c:if>
<p>Added repositories:
  <c:forEach var="instance" items="${addedInstances}">
    <li>${instance}</li>
  </c:forEach>

</p>

<br>
You can now:
<br>
<br>

<form action="config.svn">
  <input type="hidden" value="addnew" name="addnew">
  <input type="submit" value="add another repository" class="btn"> <a href="#" title="what is this?">?</a>
</form>
<br>
- or -
<br>
<br>

<form action="submitconfig.svn">
  <input type="submit" value="start browsing" class="btn"> <a href="#" title="what is this?">?</a>
</form>

<p>
<i>Where are the configuration and log files stored?</i><br>
By default, the temporary files created by sventon, including the <b>sventon.log</b> file and repository configurations will be stored in
<b><%=System.getProperty("java.io.tmpdir")%></b>.
</p>
<p>
<i>What if I want to change logging granularity or log file location?</i><br>
Logging properties and log file location can be changed by customizing the properties in the file <b>svn/WEB-INF/classes/log4j.properties</b>
</p>
<p>
<i>What about UTF-8 support?</i><br>
If this sventon instance will be used with Tomcat in a non-US-ASCII environment, making fully use of Subversion's
UTF-8 support, modifications must be made to the Coyote HTTP/1.1 connector: In server.xml, either set attribute
<code>URIEncoding="UTF-8"</code> and/or set <code>useBodyEncodingForURI="true"</code>.
</p>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf" %>
</body>
</html>
