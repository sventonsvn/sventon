<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
  <title>sventon repository browser</title>
  <%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <p>
    <table class="sventonHeader">
      <tr>
        <td>sventon repository browser configuration</td>
      </tr>
    </table>
  </p>

  <br/>
  <form method="post" action="config.svn">
    <p>
      <table width="650">
        <tr>
          <td valign="top" width="50%">
            Enter subversion repository root url:
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.repositoryURL">
              <input type="text" name="repositoryURL" size="30" value=""/></td>
            </spring:bind>
          <td valign="top">
            Example:

            <p>
              <b>svn://svn.berlios.de/sventon/</b><br>
              <b>svn+ssh://domain/project/</b><br>
              <b>http://domain/project/trunk/</b><br>
            </p>
          </td>
        </tr>
        
        <tr>
          <td valign="top" width="50%">
            Enter mount point:
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.mountPoint">
              <input type="text" name="mountPoint" size="30" value=""/>
            </spring:bind>
          </td>
          <td valign="top">
            (leave blank for browsing root)
          </td>
        </tr>
        
        <tr>
          <td valign="top" width="50%">
            Enter user name:
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.username">
              <input type="text" name="username" size="30" value=""/>
            </spring:bind>
          </td>
          <td valign="top">
            (leave blank for anonymous)
          </td>
        </tr>
        <tr>
          <td valign="top" width="50%">
            Enter user password:
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.password">
              <input type="text" name="password" size="30" value=""/>
            </spring:bind>
          </td>
          <td valign="top">
            (leave blank for anonymous)
          </td>
        </tr>
        <tr>
          <td valign="top" width="50%">
            Enter Subversion config path:
          </td>
          <td valign="top" width="50%">
            <spring:bind path="command.configPath">
              <input type="text" name="configPath" size="30" value=""/>
            </spring:bind>
          </td>
          <td valign="top">
            This is the path to where subversion configuration files and the index are stored. </br>
            The web server running sventon must have read/write access to this directory
          </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr>
          <td colspan="2">
The file <b>sventon.log</b> will be stored in current directory, currently that is <b>${command.currentDir}</b>.<br>
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
