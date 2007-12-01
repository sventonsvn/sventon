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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>

<html>
  <head>
    <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf"%>
    <title>sventon repository browser</title>
  </head>
  <body>
    <p class="sventonHeader">
      An authentication failure occured when connecting to the Subversion server.
    </p>
    <p>
      This is typically the symptom of incorrect or missing user id and password configuration
      in sventon when anonymous access to the repository is not allowed.
    </p>
    <p>
      Peruse sventon log for further information.
    </p>

    <form action="${action}" method="post">
      <p>UID: <input name="uid" type="text" nocache/></p>
      <p>PWD: <input name="pwd" type="password" nocache/></p>

      <c:forEach items="${parameters}" var="paramEntry">
        <c:forEach items="${paramEntry.value}" var="parameter">
          <input type="hidden" name="${paramEntry.key}" value="${parameter}"/>
        </c:forEach>
      </c:forEach>

      <input type="submit" value="log in">
    </form>

<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
