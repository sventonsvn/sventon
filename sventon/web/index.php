<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>sventon - subversion repository browser</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
    <meta name="description" content="sventon - the pure java subversion repository browser">
    <meta name="keywords" content="sventon, subversion, svn, repository, repositories, browse, browser, browsing, servlet, java, tomcat, web, search, track, log">
    <meta name="ROBOTS" content="INDEX,FOLLOW">
    <meta name="author" content="sventon project team">
    <meta name="copyright" content="Copyright (c) 2005-2007 sventon project.">
    <meta name="distribution" content="Global">
    <meta name="rating" content="General">
    <link rel="stylesheet" type="text/css" href="sventon.css">
    <link rel="alternate" type="application/atom+xml" title="sventon" href="http://sventonblog.blogspot.com/atom.xml" />
  </head>

  <body>
    <p>&nbsp;</p>

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
      break;
  }
?>

    <table width="100%" border="0" cellspacing="0">
      <tr>
        <td width="20%">&nbsp;</td>
        <td width="2%">&nbsp;</td>
        <td>&nbsp;</td>
        <td width="2%" style="background-color: #99adbd; border-right: black solid 1px;">&nbsp;</td>
        <td width="20%">&nbsp;</td>
       </tr>
      <tr>
        <td width="20%">&nbsp;</td>
        <td width="2%">&nbsp;</td>
        <td align="center"><img src="img/sventon.png" alt="sventon logo"/><br/>the pure java subversion repository browser</td>
        <td width="2%" style="background-color: #99adbd; border-right: black solid 1px;">&nbsp;</td>
        <td width="20%">&nbsp;</td>
       </tr>
      <tr style="background-color: #99adbd;">
        <td style="border-bottom: black solid 1px;">&nbsp;</td>
        <td style="border-bottom: black solid 1px;">&nbsp;</td>
        <td style="border-bottom: black solid 1px;">&nbsp;</td>
        <td style="background-color: #99adbd;">&nbsp;</td>
        <td style="border-bottom: black solid 1px; border-right: black solid 1px;">&nbsp;</td>
       </tr>
      <tr>
        <td align="right" valign="top">
          <table border="0" cellspacing="1" cellpadding="1">
            <tr>
              <td align="right">&nbsp;</td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=main">[main]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=news">[news]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=history">[change history]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=features">[features]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=screenshots">[screenshots]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=downloads">[downloads]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=faq">[faq]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=links">[links]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="index.php?page=roadmap">[roadmap]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="http://groups.google.com/group/sventon-support/">[forum and mailinglist]</a>
              </td>
            </tr>
            <tr>
              <td align="right">
                <a href="http://developer.berlios.de/projects/sventon/">[project page]</a>
              </td>
            </tr>
          </table>
        </td>
        <td>&nbsp;</td>
        <td valign="top"><?php require($includepage); ?></td>
        <td style="background-color: #99adbd; border-right: black solid 1px;">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td align="right" valign="bottom">
          <br/>
          <br/>
          <a href="http://sventonblog.blogspot.com/atom.xml" title="Sventon Atom news feed">
          	<img src="img/feed-icon-16x16.png" alt="feed icon" border="0">
          	</a>
          <br/>
          <br/>
          <a href="http://developer.berlios.de" title="BerliOS Developer">
            <img src="http://developer.berlios.de/bslogo.php?group_id=3670" width="124px" height="32px" border="0" alt="BerliOS Developer Logo">
          </a>
          <br/>
        </td>
        <td>&nbsp;</td>
        <td>&nbsp;</td>
        <td style="background-color: #99adbd; border-bottom: black solid 1px; border-right: black solid 1px;">&nbsp;</td>
        <td>&nbsp;</td>
      </tr>
    </table>
<script src="http://www.google-analytics.com/urchin.js" type="text/javascript">
</script>
<script type="text/javascript">
_uacct = "UA-206174-3";
urchinTracker();
</script>
</body>
</html>
