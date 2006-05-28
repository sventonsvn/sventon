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
    <title>Show locks - ${command.target}</title>
  </head>

  <body>
    <%@ include file="/WEB-INF/jspf/top.jspf"%>

    <p>
      <table class="sventonHeader">
        <tr>
          <td>Show locks - <b>${command.target}</b>&nbsp;</td>
        </tr>
      </table>
      <%@ include file="/WEB-INF/jspf/sventonheader.jspf"%>
    </p>

    <br/>
    <ui:functionLinks pageName="showLock"/>

    <table class="sventonEntriesTable" border="0">
      <tr>
        <th></th>
        <th>Name</th>
        <th>Owner</th>
        <th>Comment</th>
        <th>Created</th>
        <th>Expires</th>
      </tr>
      <% int rowCount = 0; %>
      <c:forEach items="${currentLocks}" var="lock">
        <jsp:useBean id="lock" type="org.tmatesoft.svn.core.SVNLock" />

        <c:url value="showfile.svn" var="showUrl">
          <c:param name="path" value="${lock.path}" />
          <c:param name="revision" value="${command.revision}" />
        </c:url>

        <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry2"); else out.print("sventonEntry1");%>">
          <td><img src="images/icon_file.gif" alt="file"/></td>
          <td><a href="${showUrl}">${lock.path}</a></td>
          <td>${lock.owner}</td>
          <td>${lock.comment}</td>
          <td><fmt:formatDate type="both" value="${lock.creationDate}" dateStyle="short" timeStyle="short"/></td>
          <td><fmt:formatDate type="both" value="${lock.expirationDate}" dateStyle="short" timeStyle="short"/></td>
        </tr>
    <% rowCount++; %>
      </c:forEach>

      <tr class="<%if (rowCount % 2 == 0) out.print("sventonEntry2"); else out.print("sventonEntry1");%>">
        <td>&nbsp;</td>
        <td><b>Total: <%=rowCount%> entries</b></td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table>

    <br>
<%@ include file="/WEB-INF/jspf/foot.jspf"%>
  </body>
</html>
