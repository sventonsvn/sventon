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
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sventon" tagdir="/WEB-INF/tags" %>

<select class="sventonSelect" name="latestRevisionsSelect" onChange="javascript:getLatestRevisions('${command.name}', this.options[this.selectedIndex].value);">
  <option class="sventonSelectOption">1</option>
  <c:forEach var="i" begin="${2}" end="${maxRevisionsCount}">
    <option ${userContext.latestRevisionsDisplayCount == i ? 'selected' : ''} value="${i}">${i}</option>
  </c:forEach>
</select>
<table class="sventonLatestCommitInfoTable">
  <tr>
    <td>
      <c:forEach var="revision" items="${revisions}">
        <sventon:revisionInfo details="${revision}" keepVisible="true" linkToHead="${revision.revision == headRevision ? 'true' : 'false'}" />
      </c:forEach>
    </td>
  </tr>
</table>