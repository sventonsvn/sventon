<p>
Welcome to the sventon project!
</p>

<p>
sventon is a Java web application written for browsing <a href="http://subversion.tigris.org">Subversion</a> repositories using an ordinary web browser.
</p>

<p>
sventon is really easy to install and you don't have to know anything about Java. All you need is a <a href="http://java.sun.com/j2se/1.5.0/">J2SE 5.0 runtime</a> and a Servlet 2.4/JSP 2.0 compliant webserver, such as <a href="http://tomcat.apache.org/">Tomcat 5.5</a>. It features most basic functionality you'd expect from a tool like this, and a few more, including:
</p>

<ul>
  <li>Easy installation</li>
  <li>Browse, download, view logs and diff your repository files, and their properties</li>
  <li>Support for multiple repositories</li>
  <li>Easily keep track of changes using the 'latest commit' feature</li>
  <li>Lightning fast search for instantly finding files or log messages, including CamelCaseSearch</li>
  <li>Flattening of directory structures for quickly finding the directory you're looking for</li>
  <li>Browse inside archive files</li>
  <li>View thumbnails of image files stored in the repository</li>
  <li>RSS feed support</li>
  <li>... and more!</li>
</ul>
<p>
  Have a look at the <a href="index.php?page=features">feature page</a> for the current feature set.
</p>

<p>
  <b>Latest news</b>
  <br/><b>

<?php
  $rss = fetch_rss('http://sventonblog.blogspot.com/atom.xml'); 
  $item = $rss->items[0];
  $published = parse_w3cdtf($item['published']);
  $title = $item['title'];
  $content = $item['atom_content'];
  echo date("Y-m-d", $published);
  echo " - $title</b><br/>\r";
  echo "$content\r";
?>

  <br/>
  <a href="index.php?page=news">More news...</a>
</p>

<p>
  <b>Acknowledgments</b>
  <br/>
  sventon uses several open source projects created by contributions from many individuals and organizations. Many thanks!
</p>

<p>
  <b>Contact</b>
  <br/>
  If you have any questions regarding sventon usage or development, use the <a href="http://groups.google.com/group/sventon-support/">sventon Google group</a>. Please do not write the developers directly in these matters (but <i>do</i> post in the forum, we'll try to answer).
</p>

<p>
  Developer e-mail addresses:
</p>
<p>
  <i>sventonproject at gmail.com</i><br/>
  <i>patrikfr at users.berlios.de</i><br/>
  <i>jesper at users.berlios.de</i><br/>
</p>
<p>
This site uses Google Analytics and cookies to track visitors.
</p>
