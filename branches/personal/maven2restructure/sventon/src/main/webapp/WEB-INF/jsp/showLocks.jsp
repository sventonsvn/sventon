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
    <%@ include file="/WEB-INF/jspf/pageHead.jspf"%>
    <title>Show Locks - ${command.target}</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/pageTop.jspf"%>

    <sventon:currentTargetHeader title="Show Locks" target="${command.target}" hasProperties="false"/>
    <sventon:functionLinks pageName="showLock"/>

    <table class="sventonEntriesTable" border="0">
      <c:set var="rowCount" value="0"/>
      <tr>
        <th></th>
        <th>Name</th>
        <th>Owner</th>
        <th>Comment</th>
        <th>Created</th>
        <th>Expires</th>
      </tr>
      <c:forEach items="${currentLocks}" var="lock">
        <jsp:useBean id="lock" type="org.tmatesoft.svn.core.SVNLock" />

        <c:url value="showfile.svn" var="showUrl">
          <c:param name="path" value="${lock.path}" />
          <c:param name="revision" value="${command.revision}" />
          <c:param name="name" value="${command.name}" />
        </c:url>

        <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
          <td><sventon-ui:fileTypeIcon filename="${entry.name}"/></td>
          <td><a href="${showUrl}">${lock.path}</a></td>
          <td>${lock.owner}</td>
          <td>${lock.comment}</td>
          <td><fmt:formatDate type="both" value="${lock.creationDate}" dateStyle="short" timeStyle="short"/></td>
          <td><fmt:formatDate type="both" value="${lock.expirationDate}" dateStyle="short" timeStyle="short"/></td>
        </tr>
        <c:set var="rowCount" value="${rowCount + 1}"/>
      </c:forEach>

      <tr class="${rowCount mod 2 == 0 ? 'sventonEntryEven' : 'sventonEntryOdd'}">
        <td>&nbsp;</td>
        <td><b>Total: ${rowCount} entries</b></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table>

    <br>
<%@ include file="/WEB-INF/jspf/rssLink.jspf"%>
<%@ include file="/WEB-INF/jspf/pageFoot.jspf"%>
  </body>
</html>
