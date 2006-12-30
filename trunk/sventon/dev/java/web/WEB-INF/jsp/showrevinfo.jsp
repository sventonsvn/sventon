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
    <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
    <title>Revision information details</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <sventon:currentTargetHeader title="Revision Information" target="${command.revision}" hasProperties="false"/>
    <sventon:functionLinks pageName="showRevInfo"/>

    <table class="sventonLatestCommitInfoTable">
      <tr>
        <td>
          <sventon:revisionInfo details="${revisionInfo}" keepVisible="false" linkToHead="false" />
        </td>
      </tr>
    </table>
    <br>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
