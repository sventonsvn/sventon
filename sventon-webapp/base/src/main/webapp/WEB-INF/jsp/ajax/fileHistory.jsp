<%
    /*
    * ====================================================================
    * Copyright (c) 2005-2011 sventon project. All rights reserved.
    *
    * This software is licensed as described in the file LICENSE, which
    * you should have received as part of this distribution. The terms
    * are also available at http://www.sventon.org.
    * If newer versions of this license are posted there, you may use a
    * newer version instead, at your option.
    * ====================================================================
    */
%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/WEB-INF/sventon.tld" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sventon" tagdir="/WEB-INF/tags" %>
<%@ page import="java.util.Date" %>

<c:if test="${fn:length(logEntries) > 1}">
    <table class="fileHistoryTable">
        <tr>
            <td colspan="2">
                <spring:message code="previous-revisions"/>
                <select class="sventonSelect" onchange="document.location.href=this.options[this.selectedIndex].value;">
                    <c:forEach var="logEntry" items="${logEntries}">
                        <s:url value="/repos/${command.name}/show${logEntry.pathAtRevision}" var="showFileAtRevisionUrl">
                            <s:param name="revision" value="${logEntry.revision}"/>
                            <c:if test="${archivedEntry ne null}">
                                <s:param name="archivedEntry" value="${archivedEntry}"/>
                                <s:param name="forceDisplay" value="true"/>
                            </c:if>
                        </s:url>
                        <jsp:useBean id="logEntry" type="org.sventon.model.LogEntry"/>
                        <%
                            final Date revDate = logEntry.getDate();
                            final String authorString = logEntry.getAuthor();
                        %>
                        <option value="${showFileAtRevisionUrl}">
                            <fmt:formatDate type="both" value="<%=revDate%>" dateStyle="short" timeStyle="short"/>
                            <%
                                if (authorString != null) {
                                    out.print("(" + authorString + ")");
                                }
                            %>
                        </option>
                    </c:forEach>
                </select>
            </td>
        </tr>
    </table>
</c:if>
