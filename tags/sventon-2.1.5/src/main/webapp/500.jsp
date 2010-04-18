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
<%@ page isErrorPage="true"%>
<%@ include file="/WEB-INF/jspf/pageInclude.jspf"%>

<%!
   final org.apache.commons.logging.Log logger = 
   org.apache.commons.logging.LogFactory.getLog(getClass());
%>

<html>
  <head>
    <title>Application Error</title>
    <%@ include file="/WEB-INF/jspf/pageHeadWithoutRssLink.jspf"%>
  </head>
  <body>
  <%@ include file="/WEB-INF/jspf/spinner.jspf"%>

  <sventon:topHeaderTable repositoryName="${command.name}" repositoryNames="${repositoryNames}"
                          isEditableConfig="${isEditableConfig}" isLoggedIn="${userRepositoryContext.isLoggedIn}"/>

  <h1>An unhandled internal application error has occured</h1>
  <p/>
  Sorry.
  <p/>
  <%
  logger.error("Unhandled internal error caught by default error page", exception);
  %>
  The error has been logged. If this error persists please ask the administator of 
  this sventon installation to file a bug report at <a href="http://www.sventon.org">http://www.sventon.org</a>.
  
  <%@ include file="/WEB-INF/jspf/pageFootWithoutRssLink.jspf"%>
  </body>
</html>
