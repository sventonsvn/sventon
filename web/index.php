<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>sventon - subversion repository browser</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <meta name="description" content="sventon - the pure java subversion repository browser">
  <meta name="keywords" content="sventon, subversion, svn, repository, repositories, browse, browser, browsing, servlet, java, tomcat, web, search, track, log, scm, version control, versioning, view, show, display, files, directories, path, compass, lucene, cache">
  <meta name="ROBOTS" content="INDEX,FOLLOW">
  <meta name="author" content="sventon project team">
  <meta name="copyright" content="Copyright (c) 2005-2009 sventon project.">
  <meta name="distribution" content="Global">
  <meta name="rating" content="General">

  <script type="text/javascript" src="js/prototype.js"></script>
  <script type="text/javascript" src="js/scriptaculous.js?load=effects,builder"></script>
  <script type="text/javascript" src="js/lightbox.js"></script>
  <link rel="stylesheet" href="css/lightbox.css" type="text/css" media="screen" />

  <link rel="stylesheet" type="text/css" href="css/sventon.css">
  <link rel="alternate" type="application/atom+xml" title="sventon" href="http://sventonblog.blogspot.com/atom.xml"/>
  <link rel="shortcut icon" href="/images/favicon.ico" />
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
    case "downloads":
      $includepage="downloads.php";
      break;
    case "faq":
      $includepage="faq.php";
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
      case "development":
      $includepage="development.php";
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
            <td width="94" class="topMenuCell"><a href="http://wiki.sventon.org/">Wiki</a></td>
            <td width="94" class="topMenuCell"><a href="http://groups.google.com/group/sventon-support/">Forum</a></td>
            <td width="94" <?php if ($pageparameter == "development") { ?>id="selectedTopMenuCell" <?php } else { ?> class="topMenuCell" <?php } ?>><a href="index.php?page=development" <?php if ($pageparameter == "development") { ?>class="style1"<?php } ?>>Development</a></td>
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
      <td width="484" height="53" valign="top" style="padding-right:65px;padding-top:20px;" class="body" align="left">
        <?php require($includepage); ?>
      </td>

      <td width="216" valign="top" align="left">
        <!-- Dowload box -->
        <table width="100%" border="0" cellspacing="0" cellpadding="3" style="margin-top:20px">
          <tr>
            <td class="boxHeader">Downloads</td>
          </tr>
          <tr>
            <td class="downloadBody">
              <?php require('downloads.php'); ?>
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
                  $counter = 0;
                  while ( $counter < 3 ) {
                    $item = $rss->items[$counter];
                    $published = parse_w3cdtf($item['published']);
                    $title = $item['title'];
                    echo "<li><b>";
                    echo date("Y-m-d", $published);
                    echo "<br/><a href=\"index.php?page=news#$counter\">$title</a></b></li>\r";
                    $counter++;
                  }
                ?>
              </ul>
            </td>
          </tr>
        </table>

        <table width="100%" border="0" cellspacing="0" cellpadding="3" style="margin-top:20px">
          <tr>
            <td class="boxHeader">Support</td>
          </tr>
          <tr>
            <td class="boxBody">
              <p>
              See the <a href="http://wiki.sventon.org/">sventon wiki</a> for user and developer documentation.
              </p>
              <p>If you have any questions regarding sventon usage or development, please check the 
                <a href="http://wiki.sventon.org/index.php?n=Main.FAQ">FAQ</a> or visit the
                <a href="http://groups.google.com/group/sventon-support/">sventon Google group</a> to
                discuss different topics with sventon committers and users.
                </p>
            </td>
          </tr>
        </table>

        <table width="100%" border="0" cellspacing="0" cellpadding="3" style="margin-top:20px">
          <tr>
            <td class="boxBody">
YourKit is kindly supporting open source projects with its full-featured Java Profiler.
YourKit, LLC is the creator of innovative and intelligent tools for profiling
Java and .NET applications. Take a look at YourKit's leading software products:
<a href="http://www.yourkit.com/java/profiler/index.jsp">YourKit Java Profiler</a> and
<a href="http://www.yourkit.com/.net/profiler/index.jsp">YourKit .NET Profiler</a>.
            </td>
          </tr>
        </table>

        <table width="100%" border="0" cellspacing="0" cellpadding="3" style="margin-top:20px">
          <tr>
            <td align="center" width="193px">
              <script type="text/javascript" src="https://www.ohloh.net/projects/9261/widgets/project_partner_badge"></script>
              <br>
              <script type="text/javascript" src="https://www.ohloh.net/projects/9261/widgets/project_users_logo"></script>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <p>&nbsp;</p>
</div>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-206174-3");
pageTracker._initData();
pageTracker._trackPageview();
</script>

</body>
</html>