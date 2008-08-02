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
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>

<%!
   final org.apache.commons.logging.Log logger = 
   org.apache.commons.logging.LogFactory.getLog(getClass());
%>

<html>
  <head>
    <title>404 page not found</title>
    <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf"%>
  </head>
  <body>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>
  <sventon:topHeaderTable command="${command}" repositoryNames="${repositoryNames}" hasCredentials="${userRepositoryContext.uid ne null}"/>

  <h1>The requested view does not exist</h1>
  <p/>
  Go to <a href="${pageContext.request.contextPath}/index.jsp">sventon start page</a>
  <%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf"%>
  </body>
</html>
