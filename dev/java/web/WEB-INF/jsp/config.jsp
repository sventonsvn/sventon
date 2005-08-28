<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
  <title>sventon repository browser</title>
  <%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <h1>sventon repository browser configuration</h1>
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
              <b>svn://svn.berlios.de/sventon/</b><br>
              <b>svn+ssh://domain/project/</b><br>
              <b>http://domain/project/trunk/</b><br>

            </p>
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.repositoryURL">
              <input type="text" name="repositoryURL" size="30" value=""/></td>
            </spring:bind>
        </tr>
        <tr>
          <td valign="top" width="50%">
            Enter user name (leave blank for anonymous):
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.username">
              <input type="text" name="username" size="30" value=""/></td>
            </spring:bind>
        </tr>
        <tr>
          <td valign="top" width="50%">
            Enter user password (leave blank for anonymous):
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.password">
              <input type="text" name="password" size="30" value=""/></td>
            </spring:bind>
        </tr>
        <tr>
          <td valign="top" width="50%">
            Enter sventon config path:
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.configPath">
              <input type="text" name="configPath" size="30" value=""/></td>
            </spring:bind>
        </tr>
        <tr>
          <td colspan="2">
The file <b>sventon.log</b> will be stored in current directory, currently that is <b>${command.currentDir}</b>.
Logging properties can be customized by changing properties in the file <b>svn/WEB-INF/classes/log4j.properties</b>
          </td>
        </tr>
        <tr>
          <td colspan="2"><spring:hasBindErrors name="command"><font color="#FF0000">Illegal repository URL!</font></spring:hasBindErrors></td>          
        </tr>
      </table>
    <p>
      <input type="submit" name="submit" value="submit configuration">
    </p>
  </form>
  <%@ include file="/WEB-INF/jsp/foot.jsp"%>
</body>
</html>
