<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
  <title>sventon repository browser - <c:out value="${url}" /></title>
  <%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <h1>sventon repository browser configuration page</h1>
  <form method="post" action="config.svn">
    <p>
      <table width="650">
        <tr>
          <td valign="top" width="50%">
            Enter subversion repository root url:
            <p>
              Example:
            </p>
            <p>
              svn://svn.berlios.de/sventon/<br>
              svn+ssh://domain/project/<br>
              http://domain/project/trunk/<br>

            </p>
          </td>
          <td valign="top" width="50%">
            <input type="text" name="url" size="30" value=""/></td>
        </tr>
        <tr>
          <td valign="top" width="50%">
            Enter user name (leave blank for anonymous):
          </td>
          <td valign="top" width="50%">
            <input type="text" name="usr" size="30" value=""/></td>
        </tr>
        <tr>
          <td valign="top" width="50%">
            Enter user password (leave blank for anonymous):
          </td>
          <td valign="top" width="50%">
            <input type="text" name="pwd" size="30" value=""/></td>
        </tr>
      </table>
    </p>
    <p/>
    <p>
      <input type="button" name="urlcheck" value="check connection">&nbsp;&nbsp;
      <input type="submit" name="submit" value="submit configuration">
    </p>
  </form>
  <%@ include file="/WEB-INF/jsp/foot.jsp"%>
</body>
</html>
