<?php
  $rss = fetch_rss('http://sventonblog.blogspot.com/atom.xml'); 
	echo "<ul>\r";
	foreach ($rss->items as $item) {
    $modified = parse_w3cdtf($item['modified']);
		$title = $item['title'];
		echo "<li><b>";
    echo date("Y-m-d", $modified);
    echo " - $title</b></li>\r";
    $content = $item['atom_content'];
    echo "$content\r<br/>\r";
	}
	echo "</ul>\r"; 
?> 
