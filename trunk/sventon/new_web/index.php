<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>sventon - subversion repository browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <meta name="description" content="sventon - the pure java subversion repository browser">
  <meta name="keywords"
        content="sventon, subversion, svn, repository, repositories, browse, browser, browsing, servlet, java, tomcat, web, search, track, log">
  <meta name="ROBOTS" content="INDEX,FOLLOW">
  <meta name="author" content="sventon project team">
  <meta name="copyright" content="Copyright (c) 2005-2007 sventon project.">
  <meta name="distribution" content="Global">
  <meta name="rating" content="General">
  <link rel="stylesheet" type="text/css" href="style.css">
  <link rel="alternate" type="application/atom+xml" title="sventon" href="http://sventonblog.blogspot.com/atom.xml"/>
  <style type="text/css">
    <!--
    .style1 {
      color: #000000;
    }

    .style3 {
      font-size: 10
    }
    -->
  </style>
</head>

<body>

<?php
  require 'magpierss/rss_utils.inc'; 
  require_once('magpierss/rss_fetch.inc'); 

  // get the value of the request parameter
  $pageparameter=$_GET['page'];

  switch ($pageparameter) {
    case "history":
      $includepage="history.php";
      break;
    case "downloads":
      $includepage="downloads.php";
      break;
    case "faq":
      $includepage="faq.php";
      break;
    case "roadmap":
      $includepage="roadmap.php";
      break;
    case "links":
      $includepage="links.php";
      break;
    case "features":
      $includepage="features.php";
      break;
    case "screenshots":
      $includepage="screenshots.php";
      break;
    case "news":
      $includepage="news.php";
      break;
    default:
      $includepage="main.php";
      $pageparameter="main";
      break;
  }
?>

<div align="center">
  <table width="700" height="676" border="0" cellpadding="0px" cellspacing="0px">
    <tr>
      <td height="64" colspan="2" valign="top">
        <table border="0" align="right" cellpadding="1" cellspacing="0" class="topMenu">
          <tr>
            <td width="94" <?php if ($pageparameter == "main") { ?>id="selectedTopMenuCell" <?php } else { ?> class="topMenuCell" <?php } ?>><a href="index.php?page=main" <?php if ($pageparameter == "main") { ?>class="style1"<?php } ?>>Main</a></td>
            <td width="94" <?php if ($pageparameter == "news") { ?>id="selectedTopMenuCell" <?php } else { ?> class="topMenuCell" <?php } ?>><a href="index.php?page=news" <?php if ($pageparameter == "news") { ?>class="style1"<?php } ?>>News</a></td>
            <td width="94" <?php if ($pageparameter == "features") { ?>id="selectedTopMenuCell" <?php } else { ?> class="topMenuCell" <?php } ?>><a href="index.php?page=features" <?php if ($pageparameter == "features") { ?>class="style1"<?php } ?>>Features</a></td>
            <td width="94" <?php if ($pageparameter == "screenshots") { ?>id="selectedTopMenuCell" <?php } else { ?> class="topMenuCell" <?php } ?>><a href="index.php?page=screenshots" <?php if ($pageparameter == "screenshots") { ?>class="style1"<?php } ?>>Screenshots</a></td>
            <td width="94" <?php if ($pageparameter == "faq") { ?>id="selectedTopMenuCell" <?php } else { ?> class="topMenuCell" <?php } ?>><a href="index.php?page=faq" <?php if ($pageparameter == "faq") { ?>class="style1"<?php } ?>>FAQ</a></td>
            <td width="94" class="topMenuCell"><a href="http://groups.google.com/group/sventon-support/">Forum</a></td>
            <td width="94" class="topMenuCell"><a href="http://developer.berlios.de/projects/sventon/">Project page</a></td>
          </tr>
        </table>
      </td>
    </tr>
    <tr>
      <td height="94" colspan="2" valign="top">
        <div align="right"><img src="images/logo.gif" width="216" height="54"/></div>
      </td>
    </tr>
    <tr>
      <td width="484" height="53" valign="top" style="padding-right:65px;padding-top:20px;" class="body">
        <?php require($includepage); ?>
      </td>

      <td width="216" valign="top">
        <!-- Dowload box -->
        <table width="100%" border="0" cellspacing="0" cellpadding="3" style="margin-top:20px">
          <tr>
            <td class="boxHeader">Downloads</td>
          </tr>
          <tr>
            <td class="downloadBody">
              <?php require('download.php'); ?>
            </td>
          </tr>
        </table>

        <table width="100%" border="0" cellspacing="0" cellpadding="3" style="margin-top:20px">
          <tr>
            <td class="boxHeader">
              <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="boxHeaderS">Latest news</td>
                  <td width="16px"><a href="http://sventonblog.blogspot.com/atom.xml"><img src="images/feed.png" alt="Add feed" width="16" height="16"/></a></td>
                </tr>
              </table>
            </td>
          </tr>
          <tr>
            <td class="boxBody">
              <ul style="list-style-image:url(images/bullet_orange.png);line-height:13px;">
                <?php
                  $rss = fetch_rss('http://sventonblog.blogspot.com/atom.xml');
                  echo "<ul>\r";
                  $counter = 0;
                  while ( $counter < 3 ) {
                    $item = $rss->items[$counter];
                    $published = parse_w3cdtf($item['published']);
                    $title = $item['title'];
                    echo "<li><b>";
                    echo date("Y-m-d", $published);
                    echo " - $title</b></li>\r";
                    $counter = $counter + 1;
                  }
                  echo "</ul>\r";
                ?> 
              </ul>
            </td>
          </tr>
        </table>

        <table width="100%" border="0" cellspacing="0" cellpadding="3" style="margin-top:20px">
          <tr>
            <td class="boxHeader">Contact</td>
          </tr>
          <tr>
            <td class="boxBody">
              <p>If you have any questions regarding sventon usage or development, use the
                <a href="http://groups.google.com/group/sventon-support/">sventon Google group</a>.
                Please do not write the developers directly in these matters (but <em>do</em> post in the forum,
                we'll try to answer).</p>

              <p><strong>Developer e-mail addresses:</strong><br/>
                <em>sventonproject at gmail.com</em><br/>
                <em>patrikfr at users.berlios.de</em><br/>
                <em>jesper at users.berlios.de</em>
              </p>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <p>&nbsp;</p>
</div>

<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
  _uacct = "UA-206174-3";
  urchinTracker();
</script>

</body>
</html>