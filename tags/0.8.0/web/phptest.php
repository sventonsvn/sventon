<html>
<body>

<?php
// Show all PHP information
//phpinfo();
?>

<?php
// Show only the general information
//phpinfo(INFO_GENERAL);
?>

<?php
  // get the value of the request parameter 'page'
  $pageparameter=$_GET['page'];

  switch ($pageparameter) {
    case "history":
      $includefile="history.php";
      break;
    case "downloads":
      $includefile="downloads.php";
      break;
    case "faq":
      $includefile="faq.php";
      break;
    case "roadmap":
      $includefile="roadmap.php";
      break;
    case "links":
      $includefile="links.php";
      break;
    default:
      $includefile="about.php";
      break;
  }
?>


</body>
</html>