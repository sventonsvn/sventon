<%@ include file="/WEB-INF/jsp/include.jsp"%>

<html>
<head>
<title>sventon repository browser</title>
<%@ include file="/WEB-INF/jsp/head.jsp"%>
</head>
<body>
  <p class="sventonHeader">
  An authentication failure occured when connecting to the Subversion server.
  </p>
  <p>
  This is typically the symptom of incorrect or missing user id and password configuration 
  in sventon when anonymous access to the repository is not allowed.
  </p>
  <p>
  Peruse sventon log for further information.
  </p>

<%@ include file="/WEB-INF/jsp/foot.jsp"%>
</body>
</html>
